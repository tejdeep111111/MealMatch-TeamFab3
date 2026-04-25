package com.teamfab.meallmatch.person.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamfab.meallmatch.person.data.MealRepository
import com.teamfab.meallmatch.person.data.local.TokenStore
import com.teamfab.meallmatch.person.data.model.MealSkipResponse
import com.teamfab.meallmatch.person.data.model.SubscriptionResponse
import com.teamfab.meallmatch.person.data.model.UpcomingDelivery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class UpcomingDeliveriesState(
    val loading: Boolean = true,
    val upcoming: List<UpcomingDelivery> = emptyList(),
    val error: String? = null,
    val actionError: String? = null   // non-fatal error from skip/unskip
)

@HiltViewModel
class UpcomingDeliveriesViewModel @Inject constructor(
    private val repo: MealRepository,
    private val tokenStore: TokenStore
) : ViewModel() {

    private val _subscriptions = MutableStateFlow<List<SubscriptionResponse>>(emptyList())
    private val _skips        = MutableStateFlow<List<MealSkipResponse>>(emptyList())
    private val _loading      = MutableStateFlow(true)
    private val _error        = MutableStateFlow<String?>(null)
    private val _actionError  = MutableStateFlow<String?>(null)

    val state: StateFlow<UpcomingDeliveriesState> = combine(
        _loading, _subscriptions, _skips, _error, _actionError
    ) { loading, subs, skips, error, actionError ->
        UpcomingDeliveriesState(
            loading     = loading,
            upcoming    = if (!loading && error == null) computeUpcoming(subs, skips) else emptyList(),
            error       = error,
            actionError = actionError
        )
    }.stateIn(
        scope   = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UpcomingDeliveriesState()
    )

    init { load() }

    /** Load (or reload) subscriptions + their server-side skips. */
    fun load() {
        viewModelScope.launch {
            _loading.value = true
            _error.value   = null
            try {
                val token = tokenStore.tokenFlow.first().orEmpty()
                val subs  = repo.mySubscriptions(token)
                _subscriptions.value = subs

                // Fetch skips for all active subscriptions concurrently
                val activeIds = subs.filter { it.status?.uppercase() == "ACTIVE" }.map { it.id }
                val allSkips  = coroutineScope {
                    activeIds.map { id ->
                        async {
                            try { repo.getSkipsForSubscription(token, id) }
                            catch (_: Exception) { emptyList() }
                        }
                    }.awaitAll().flatten()
                }
                _skips.value = allSkips
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load subscriptions"
            } finally {
                _loading.value = false
            }
        }
    }

    /** Skip a single delivery date. [reason] is optional and shown to the provider. */
    fun skip(delivery: UpcomingDelivery, reason: String? = null) {
        viewModelScope.launch {
            _actionError.value = null
            try {
                val token = tokenStore.tokenFlow.first().orEmpty()
                val skip  = repo.skipDelivery(token, delivery.subscriptionId, delivery.dateStr, reason?.takeIf { it.isNotBlank() })
                // Optimistically add the new skip to avoid a full reload
                _skips.value = _skips.value + skip
            } catch (e: Exception) {
                _actionError.value = e.message ?: "Could not skip delivery"
            }
        }
    }

    /** Restore a previously skipped delivery by its server-side skip ID. */
    fun unskip(delivery: UpcomingDelivery) {
        val skipId = delivery.skipId ?: return
        viewModelScope.launch {
            _actionError.value = null
            try {
                val token = tokenStore.tokenFlow.first().orEmpty()
                repo.unskipDelivery(token, skipId)
                // Optimistically remove the skip from local state
                _skips.value = _skips.value.filterNot { it.id == skipId }
            } catch (e: Exception) {
                _actionError.value = e.message ?: "Could not restore delivery"
            }
        }
    }

    fun clearActionError() { _actionError.value = null }

    // ─── Delivery schedule computation ───────────────────────────────────────

    private fun computeUpcoming(
        subscriptions: List<SubscriptionResponse>,
        skips: List<MealSkipResponse>,
        count: Int = 3
    ): List<UpcomingDelivery> {
        val todayCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0);      set(Calendar.MILLISECOND, 0)
        }
        val todayMs  = todayCal.timeInMillis
        val dateFmt  = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dispFmt  = SimpleDateFormat("EEE, MMM d", Locale.getDefault())

        val allSlots = mutableListOf<Pair<Calendar, SubscriptionResponse>>()

        for (sub in subscriptions) {
            if (sub.status?.uppercase() != "ACTIVE") continue
            val activeDays = sub.daysOfWeek
                ?.split(",")
                ?.mapNotNull { abbrevToCalendarDay(it.trim()) }
                ?.toSet()
            if (activeDays.isNullOrEmpty()) continue

            val cursor = Calendar.getInstance().apply { timeInMillis = todayMs }
            repeat(90) {
                if (cursor.get(Calendar.DAY_OF_WEEK) in activeDays) {
                    allSlots.add(cursor.clone() as Calendar to sub)
                }
                cursor.add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        allSlots.sortWith(compareBy({ it.first.timeInMillis }, { it.second.id }))

        return allSlots.take(count).map { (cal, sub) ->
            val dateStr      = dateFmt.format(cal.time)
            val matchingSkip = skips.find { it.subscriptionId == sub.id && it.skipDate == dateStr }
            val daysUntil    = ((cal.timeInMillis - todayMs) / 86_400_000L).toInt()
            UpcomingDelivery(
                subscriptionId = sub.id,
                mealName       = sub.menuItemName ?: "Unknown meal",
                providerName   = sub.providerName,
                dateStr        = dateStr,
                displayDate    = dispFmt.format(cal.time),
                daysUntil      = daysUntil,
                deliveryTime   = sub.deliveryTime,
                deliveryAddress = sub.deliveryAddress,
                isSkipped      = matchingSkip != null,
                skipId         = matchingSkip?.id
            )
        }
    }

    private fun abbrevToCalendarDay(abbrev: String): Int? = when (abbrev.uppercase()) {
        "MON" -> Calendar.MONDAY
        "TUE" -> Calendar.TUESDAY
        "WED" -> Calendar.WEDNESDAY
        "THU" -> Calendar.THURSDAY
        "FRI" -> Calendar.FRIDAY
        "SAT" -> Calendar.SATURDAY
        "SUN" -> Calendar.SUNDAY
        else  -> null
    }
}

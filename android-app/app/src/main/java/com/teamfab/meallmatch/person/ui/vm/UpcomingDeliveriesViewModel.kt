package com.teamfab.meallmatch.person.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamfab.meallmatch.person.data.MealRepository
import com.teamfab.meallmatch.person.data.local.TokenStore
import com.teamfab.meallmatch.person.data.model.SubscriptionResponse
import com.teamfab.meallmatch.person.data.model.UpcomingDelivery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class UpcomingDeliveriesState(
    val loading: Boolean = true,
    val upcoming: List<UpcomingDelivery> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class UpcomingDeliveriesViewModel @Inject constructor(
    private val repo: MealRepository,
    private val tokenStore: TokenStore
) : ViewModel() {

    private val _subscriptions = MutableStateFlow<List<SubscriptionResponse>>(emptyList())
    private val _loading = MutableStateFlow(true)
    private val _error = MutableStateFlow<String?>(null)

    val state: StateFlow<UpcomingDeliveriesState> = combine(
        _loading,
        _subscriptions,
        repo.skippedDeliveriesFlow,
        _error
    ) { loading, subs, skipped, error ->
        UpcomingDeliveriesState(
            loading = loading,
            upcoming = if (!loading && error == null) computeUpcoming(subs, skipped) else emptyList(),
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UpcomingDeliveriesState()
    )

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val token = tokenStore.tokenFlow.first().orEmpty()
                _subscriptions.value = repo.mySubscriptions(token)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load subscriptions"
            } finally {
                _loading.value = false
            }
        }
    }

    fun skip(delivery: UpcomingDelivery) = viewModelScope.launch {
        repo.skipDelivery(delivery.skipKey)
    }

    fun unskip(delivery: UpcomingDelivery) = viewModelScope.launch {
        repo.unskipDelivery(delivery.skipKey)
    }

    // ─── Delivery computation ─────────────────────────────────────────────────

    private fun computeUpcoming(
        subscriptions: List<SubscriptionResponse>,
        skippedKeys: Set<String>,
        count: Int = 3
    ): List<UpcomingDelivery> {
        val todayCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val todayMs = todayCal.timeInMillis
        val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dispFmt = SimpleDateFormat("EEE, MMM d", Locale.getDefault())

        val allSlots = mutableListOf<Pair<Calendar, SubscriptionResponse>>()

        for (sub in subscriptions) {
            if (sub.status?.uppercase() != "ACTIVE") continue
            val activeDays = sub.daysOfWeek
                ?.split(",")
                ?.mapNotNull { abbrevToCalendarDay(it.trim()) }
                ?.toSet()
            if (activeDays.isNullOrEmpty()) continue

            // Scan the next 90 days looking for matching delivery days
            val cursor = Calendar.getInstance().apply { timeInMillis = todayMs }
            repeat(90) {
                if (cursor.get(Calendar.DAY_OF_WEEK) in activeDays) {
                    allSlots.add(cursor.clone() as Calendar to sub)
                }
                cursor.add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        // Sort chronologically; break ties by subscriptionId for determinism
        allSlots.sortWith(compareBy({ it.first.timeInMillis }, { it.second.id }))

        return allSlots.take(count).map { (cal, sub) ->
            val dateStr = dateFmt.format(cal.time)
            val skipKey = "${sub.id}::$dateStr"
            val daysUntil = ((cal.timeInMillis - todayMs) / 86_400_000L).toInt()
            UpcomingDelivery(
                subscriptionId = sub.id,
                mealName = sub.menuItemName ?: "Unknown meal",
                providerName = sub.providerName,
                dateStr = dateStr,
                displayDate = dispFmt.format(cal.time),
                daysUntil = daysUntil,
                deliveryTime = sub.deliveryTime,
                deliveryAddress = sub.deliveryAddress,
                isSkipped = skipKey in skippedKeys
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


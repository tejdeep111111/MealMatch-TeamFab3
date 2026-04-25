package com.teamfab.meallmatch.person.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamfab.meallmatch.person.data.MealRepository
import com.teamfab.meallmatch.person.data.local.TokenStore
import com.teamfab.meallmatch.person.data.model.MealSkipResponse
import com.teamfab.meallmatch.person.data.model.SubscriptionResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

// ─── Models ──────────────────────────────────────────────────────────────────

/** One day in the current Mon→Sun week. */
data class WeekDaySlot(
    val dayAbbrev: String,       // "M", "T", "W", "T", "F", "S", "S"
    val dayName: String,         // "Mon", "Tue", …
    val dateNum: String,         // day-of-month e.g. "28"
    val fullDate: String,        // "yyyy-MM-dd" — used to match skips
    val isToday: Boolean,
    val deliveries: List<String>,  // meal names scheduled for this day
    val skippedCount: Int           // how many of those deliveries are skipped
) {
    val hasDelivery: Boolean  get() = deliveries.isNotEmpty()
    val isFullySkipped: Boolean get() = hasDelivery && skippedCount >= deliveries.size
    val isPartlySkipped: Boolean get() = hasDelivery && skippedCount in 1 until deliveries.size
}

data class WeeklySummaryState(
    val loading: Boolean = true,
    val weekLabel: String = "",
    val weekDays: List<WeekDaySlot> = emptyList(),
    // Diet profile
    val dietType: String? = null,           // "VEG", "NON_VEG", "MIXED"
    val healthGoals: List<String> = emptyList(),  // human-readable goal labels
    val activeTags: List<String> = emptyList(),   // raw backend tags (for chips)
    // Stats
    val subscriptions: List<SubscriptionResponse> = emptyList(),
    val totalDeliveriesThisWeek: Int = 0,
    val skippedThisWeek: Int = 0,
    val compatibleMealsCount: Int = 0,
    val error: String? = null
)

// ─── ViewModel ───────────────────────────────────────────────────────────────

@HiltViewModel
class WeeklySummaryViewModel @Inject constructor(
    private val repo: MealRepository,
    private val tokenStore: TokenStore
) : ViewModel() {

    private val _state = MutableStateFlow(WeeklySummaryState())
    val state = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val token = tokenStore.tokenFlow.first().orEmpty()

                // Parallel fetches
                val (subs, tagsStr, compatible) = coroutineScope {
                    val subsD     = async { repo.mySubscriptions(token) }
                    val tagsD     = async { try { repo.getDietaryTags(token) } catch (_: Exception) { "" } }
                    val compatD   = async { try { repo.compatibleMeals(token).size } catch (_: Exception) { 0 } }
                    Triple(subsD.await(), tagsD.await(), compatD.await())
                }

                // Fetch skips for active subs (this week only) in parallel
                val (mondayCal, sundayCal) = weekBounds()
                val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val mondayStr = dateFmt.format(mondayCal.time)
                val sundayStr = dateFmt.format(sundayCal.time)

                val activeSubs = subs.filter { it.status?.uppercase() == "ACTIVE" }
                val weekSkips: List<MealSkipResponse> = coroutineScope {
                    activeSubs.map { sub ->
                        async {
                            try { repo.getSkipsForSubscription(token, sub.id) }
                            catch (_: Exception) { emptyList() }
                        }
                    }.awaitAll().flatten()
                }.filter { skip ->
                    // Keep only skips within this week's Mon–Sun window
                    skip.skipDate >= mondayStr && skip.skipDate <= sundayStr
                }

                // Build diet profile
                val tagList = tagsStr.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                val dietType = when {
                    "VEG" in tagList && "NON_VEG" !in tagList -> "VEG"
                    "NON_VEG" in tagList && "VEG" !in tagList -> "NON_VEG"
                    "VEG" in tagList && "NON_VEG" in tagList  -> "MIXED"
                    else -> null
                }
                val healthGoals = DietaryOptions.HEALTH_GOALS
                    .filter { (_, goalTags) -> goalTags.isNotEmpty() && goalTags.any { it in tagList } }
                    .keys.toList()

                // Build week calendar
                val weekDays = buildWeekDays(activeSubs, weekSkips)
                val totalDeliveries = weekDays.sumOf { it.deliveries.size }
                val totalSkipped    = weekDays.sumOf { it.skippedCount }

                val dispFmt = SimpleDateFormat("MMM d", Locale.getDefault())
                val weekLabel = "${dispFmt.format(mondayCal.time)} – ${dispFmt.format(sundayCal.time)}"

                _state.update {
                    it.copy(
                        loading = false,
                        weekLabel = weekLabel,
                        weekDays = weekDays,
                        dietType = dietType,
                        healthGoals = healthGoals,
                        activeTags = tagList,
                        subscriptions = activeSubs,
                        totalDeliveriesThisWeek = totalDeliveries,
                        skippedThisWeek = totalSkipped,
                        compatibleMealsCount = compatible
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message ?: "Failed to load summary") }
            }
        }
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    /** Returns (mondayCal, sundayCal) for the current week. */
    private fun weekBounds(): Pair<Calendar, Calendar> {
        val monday = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        val sunday = monday.clone() as Calendar
        sunday.add(Calendar.DAY_OF_YEAR, 6)
        return monday to sunday
    }

    private fun buildWeekDays(
        subs: List<SubscriptionResponse>,
        weekSkips: List<MealSkipResponse>
    ): List<WeekDaySlot> {
        val (mondayCal, _) = weekBounds()
        val today = Calendar.getInstance()
        val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val dayAbbrevs  = listOf("M",   "T",   "W",   "T",   "F",   "S",   "S")
        val dayNames    = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val backendDays = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")

        val cursor = mondayCal.clone() as Calendar
        return (0..6).map { i ->
            val fullDate = dateFmt.format(cursor.time)
            val isToday  = cursor.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) &&
                           cursor.get(Calendar.YEAR) == today.get(Calendar.YEAR)

            val deliveries = subs
                .filter { sub -> backendDays[i] in (sub.daysOfWeek?.split(",")?.map { it.trim().uppercase() } ?: emptyList()) }
                .mapNotNull { it.menuItemName }

            val skipped = subs
                .filter { sub -> backendDays[i] in (sub.daysOfWeek?.split(",")?.map { it.trim().uppercase() } ?: emptyList()) }
                .count { sub -> weekSkips.any { it.subscriptionId == sub.id && it.skipDate == fullDate } }

            val slot = WeekDaySlot(
                dayAbbrev    = dayAbbrevs[i],
                dayName      = dayNames[i],
                dateNum      = cursor.get(Calendar.DAY_OF_MONTH).toString(),
                fullDate     = fullDate,
                isToday      = isToday,
                deliveries   = deliveries,
                skippedCount = skipped
            )
            cursor.add(Calendar.DAY_OF_YEAR, 1)
            slot
        }
    }
}


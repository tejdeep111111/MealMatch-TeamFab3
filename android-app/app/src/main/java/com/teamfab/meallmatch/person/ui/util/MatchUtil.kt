package com.teamfab.meallmatch.person.ui.util

import com.teamfab.meallmatch.person.data.model.Meal

/**
 * Describes how well a meal matches the user's dietary preferences.
 */
data class MatchInfo(
    val matchPercent: Int,          // 0-100
    val matchedTags: List<String>,  // user tags found in this meal
    val label: String,              // friendly label
    val emoji: String               // visual indicator
)

/** Human-readable names for raw backend tags. */
private val TAG_LABELS = mapOf(
    "VEG" to "Vegetarian",
    "NON_VEG" to "Non-Vegetarian",
    "DIABETIC_CONTROL" to "Diabetic-friendly",
    "LOW_GI" to "Low Glycemic",
    "HYPERTENSION" to "BP-friendly",
    "LOW_SODIUM" to "Low Sodium",
    "FAT_LOSS" to "Fat Loss",
    "HIGH_PROTEIN" to "High Protein",
    "LOW_CAL" to "Low Calorie",
    "LOW_FAT" to "Low Fat",
    "MUSCLE_GAIN" to "Muscle Gain",
    "CALORIE_DENSE" to "Calorie Dense",
    "HIGH_FIBER" to "High Fiber",
    "WHOLE_WHEAT" to "Whole Wheat",
    "COMPLEX_CARBS" to "Complex Carbs",
    "HIGH_CARBS" to "High Carbs",
    "CONTROLLED_CARBS" to "Controlled Carbs",
    "LEAN" to "Lean",
    "HEALTHY_FATS" to "Healthy Fats",
    "PROBIOTIC" to "Probiotic",
    "COMFORT_FOOD" to "Comfort Food",
    "TIFFIN" to "Tiffin",
    "BREAKFAST" to "Breakfast",
    "SNACK" to "Snack",
    "DRINK" to "Drink",
    "SALAD" to "Salad",
    "FRUIT" to "Fruit"
)

fun prettyTag(raw: String): String = TAG_LABELS[raw.trim()] ?: raw.trim().lowercase()
    .replaceFirstChar { it.uppercase() }
    .replace("_", " ")

fun computeMatch(meal: Meal, userTags: List<String>): MatchInfo {
    if (userTags.isEmpty()) {
        return MatchInfo(0, emptyList(), "Set preferences to see match", "⚙️")
    }
    val itemTags = meal.dietaryTags?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
        ?: return MatchInfo(0, emptyList(), "No dietary info", "❓")

    val matched = userTags.filter { it in itemTags }
    val percent = if (userTags.isNotEmpty()) (matched.size * 100) / userTags.size else 0

    val label = when {
        percent >= 100 -> "Perfect match for you!"
        percent >= 75  -> "Great match for you"
        percent >= 50  -> "Good match"
        percent > 0    -> "Partial match"
        else           -> "Not matching your preferences"
    }
    val emoji = when {
        percent >= 100 -> "💚"
        percent >= 75  -> "👍"
        percent >= 50  -> "👌"
        percent > 0    -> "🔸"
        else           -> "⚪"
    }
    return MatchInfo(percent, matched, label, emoji)
}


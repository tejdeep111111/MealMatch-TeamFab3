package com.teamfab.meallmatch.person.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.skipDataStore by preferencesDataStore(name = "skip_prefs")

/**
 * Persists a set of "subscriptionId::yyyy-MM-dd" keys for deliveries the user
 * wants to skip on a specific date without cancelling the whole subscription.
 */
@Singleton
class SkipStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val KEY = stringPreferencesKey("skipped_keys")

    /** Emits the current set of skipped delivery keys. */
    val skippedFlow: Flow<Set<String>> = context.skipDataStore.data.map { prefs ->
        prefs[KEY]?.split("|||")?.filter { it.isNotBlank() }?.toSet() ?: emptySet()
    }

    suspend fun skip(key: String) = mutate { add(key) }
    suspend fun unskip(key: String) = mutate { remove(key) }

    private suspend fun mutate(block: MutableSet<String>.() -> Unit) {
        context.skipDataStore.edit { prefs ->
            val set = prefs[KEY]
                ?.split("|||")
                ?.filter { it.isNotBlank() }
                ?.toMutableSet()
                ?: mutableSetOf()
            set.block()
            prefs[KEY] = set.joinToString("|||")
        }
    }
}


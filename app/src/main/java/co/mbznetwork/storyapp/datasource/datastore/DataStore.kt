package co.mbznetwork.storyapp.datasource.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private const val DATA_STORE_NAME = "story_data_store"
private val Context.dataStore by preferencesDataStore(DATA_STORE_NAME)

class DataStore(
    context: Context,
    private val gson: Gson
) {
    private val dataStore = context.dataStore

    @Suppress("UNCHECKED_CAST")
    fun <T> getData(key: String, type: Class<T>): Flow<T?> = dataStore.data.map { preferences ->
        when (type) {
            String::class.java -> {
                preferences[stringPreferencesKey(key)] as T?
            }

            Int::class.java -> {
                preferences[intPreferencesKey(key)] as T?
            }

            Double::class.java -> {
                preferences[doublePreferencesKey(key)] as T?
            }

            Boolean::class.java -> {
                preferences[booleanPreferencesKey(key)] as T?
            }

            Float::class.java -> {
                preferences[floatPreferencesKey(key)] as T?
            }

            Long::class.java -> {
                preferences[longPreferencesKey(key)] as T?
            }

            Set::class.java -> {
                preferences[stringSetPreferencesKey(key)] as T?
            }

            else -> {
                gson.fromJson(preferences[stringPreferencesKey(key)], type)
            }
        }
    }.distinctUntilChanged()

    suspend fun <T> removeData(key: String, type: Class<T>) {
        dataStore.edit { preferences ->
            val preferencesKey = when (type) {
                String::class.java -> {
                    stringPreferencesKey(key)
                }

                Int::class.java -> {
                    intPreferencesKey(key)
                }

                Double::class.java -> {
                    doublePreferencesKey(key)
                }

                Boolean::class.java -> {
                    booleanPreferencesKey(key)
                }

                Float::class.java -> {
                    floatPreferencesKey(key)
                }

                Long::class.java -> {
                    longPreferencesKey(key)
                }

                Set::class.java -> {
                    stringSetPreferencesKey(key)
                }

                else -> {
                    stringPreferencesKey(key)
                }
            }

            preferences.remove(preferencesKey)
        }
    }

    suspend fun <T> isKeyExists(key: String, type: Class<T>): Boolean {
        return flowIsKeyExists(key, type).first()
    }

    fun <T> flowIsKeyExists(key: String, type: Class<T>): Flow<Boolean> {
        return when (type) {
            String::class.java -> {
                stringPreferencesKey(key)
            }

            Int::class.java -> {
                intPreferencesKey(key)
            }

            Double::class.java -> {
                doublePreferencesKey(key)
            }

            Boolean::class.java -> {
                booleanPreferencesKey(key)
            }

            Float::class.java -> {
                floatPreferencesKey(key)
            }

            Long::class.java -> {
                longPreferencesKey(key)
            }

            Set::class.java -> {
                stringSetPreferencesKey(key)
            }

            else -> {
                stringPreferencesKey(key)
            }
        }.let { preferenceKey ->
            dataStore.data.map {
                it.contains(preferenceKey)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun <T> editData(key: String, value: T) {
        dataStore.edit { preferences ->
            when (value) {
                is String -> {
                    preferences[stringPreferencesKey(key)] = value
                }

                is Int -> {
                    preferences[intPreferencesKey(key)] = value
                }

                is Double -> {
                    preferences[doublePreferencesKey(key)] = value
                }

                is Boolean -> {
                    preferences[booleanPreferencesKey(key)] = value
                }

                is Float -> {
                    preferences[floatPreferencesKey(key)] = value
                }

                is Long -> {
                    preferences[longPreferencesKey(key)] = value
                }

                is Set<*> -> {
                    preferences[stringSetPreferencesKey(key)] = value as Set<String>
                }

                else -> {
                    preferences[stringPreferencesKey(key)] = gson.toJson(value)
                }
            }
        }
    }

    suspend fun clear() {
        dataStore.edit {
            it.clear()
        }
    }
}
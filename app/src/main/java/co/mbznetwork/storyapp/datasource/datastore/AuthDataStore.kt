package co.mbznetwork.storyapp.datasource.datastore

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthDataStore @Inject constructor(
    dataStore: DataStore
): DataStoreAccess<String>(dataStore, "__auth_token__", String::class.java)

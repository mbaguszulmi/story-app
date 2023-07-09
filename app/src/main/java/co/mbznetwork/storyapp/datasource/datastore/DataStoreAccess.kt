package co.mbznetwork.storyapp.datasource.datastore

abstract class DataStoreAccess<T>(
    protected val dataStore: DataStore,
    protected val key: String,
    protected val type: Class<T>
) {
    suspend fun store(data: T) = dataStore.editData(key, data)

    suspend fun isExists() = dataStore.isKeyExists(key, type)

    fun get() = dataStore.getData(key, type)

    fun isExistsFlow() = dataStore.flowIsKeyExists(key, type)

    suspend fun remove() = dataStore.removeData(key, type)
}
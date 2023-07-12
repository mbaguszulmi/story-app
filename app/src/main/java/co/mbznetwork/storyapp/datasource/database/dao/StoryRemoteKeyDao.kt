package co.mbznetwork.storyapp.datasource.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import co.mbznetwork.storyapp.datasource.database.entity.StoryRemoteKey

@Dao
interface StoryRemoteKeyDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(remoteKeys: List<StoryRemoteKey>)

    @Query("DELETE FROM StoryRemoteKey")
    suspend fun deleteAll()

    @Query("SELECT * FROM StoryRemoteKey WHERE id = :id")
    suspend fun getRemoteKeyById(id: String): StoryRemoteKey?
}
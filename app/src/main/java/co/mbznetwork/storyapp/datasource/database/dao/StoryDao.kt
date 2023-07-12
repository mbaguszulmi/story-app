package co.mbznetwork.storyapp.datasource.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import co.mbznetwork.storyapp.datasource.database.entity.Story

@Dao
interface StoryDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(stories: List<Story>)

    @Query("DELETE FROM Story")
    suspend fun deleteAll()

    @Query("SELECT * FROM Story")
    fun getStories(): PagingSource<Int, Story>
}

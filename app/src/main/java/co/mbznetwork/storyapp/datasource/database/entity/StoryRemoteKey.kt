package co.mbznetwork.storyapp.datasource.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StoryRemoteKey(
    @PrimaryKey
    val id: String,
    val prevKey: Int? = null,
    val nextKey: Int? = null
)

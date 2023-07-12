package co.mbznetwork.storyapp.datasource.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import co.mbznetwork.storyapp.datasource.api.model.response.StoryResponse

@Entity
data class Story(
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val photoUrl: String = "",
    val createdAt: Long = 0L,
    val lat: Double? = null,
    val lon: Double? = null
) {
    companion object {
        fun fromStoryResponse(storyResponse: StoryResponse) = with(storyResponse) {
            Story(
                id, name, description, photoUrl, createdAt.time, lat, lon
            )
        }
    }
}

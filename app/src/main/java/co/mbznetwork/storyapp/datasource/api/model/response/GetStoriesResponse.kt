package co.mbznetwork.storyapp.datasource.api.model.response

import java.util.*

data class GetStoriesResponse(
    val listStory: List<StoryResponse> = emptyList(),
    override val error: Boolean = false,
    override val message: String = ""
): BaseResponse()

data class StoryResponse(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val photoUrl: String = "",
    val createdAt: Date = Date(),
    val lat: Double? = null,
    val lon: Double? = null
)

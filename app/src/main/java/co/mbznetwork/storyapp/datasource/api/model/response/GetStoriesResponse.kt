package co.mbznetwork.storyapp.datasource.api.model.response

import java.util.*

data class GetStoriesResponse(
    val listStory: List<Story> = emptyList(),
    override val error: Boolean = false,
    override val message: String = ""
): BaseResponse()

data class Story(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val photoUrl: String = "",
    val createdAt: Date = Date()
)

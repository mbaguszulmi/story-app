package co.mbznetwork.storyapp.model.ui

import co.mbznetwork.storyapp.datasource.database.entity.Story

data class StoryDisplay(
    val id: String,
    val name: String,
    val shortDesc: String,
    val photoUrl: String
) {
    companion object {
        fun fromStory(story: Story) = StoryDisplay(
            story.id,
            story.name,
            story.description.take(100),
            story.photoUrl
        )
    }
}

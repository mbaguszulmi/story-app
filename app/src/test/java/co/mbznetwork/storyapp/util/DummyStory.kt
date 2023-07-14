package co.mbznetwork.storyapp.util

import co.mbznetwork.storyapp.datasource.database.entity.Story
import java.util.*

object DummyStory {
    fun generateDummyStories() = mutableListOf<Story>().apply {
        for (i in 1..100) {
            add(Story(
                "$i",
                "Story $i",
                "Description of Story $i",
                "https://logowik.com/content/uploads/images/kotlin.jpg",
                createdAt = Date().time
            ))
        }
    }
}

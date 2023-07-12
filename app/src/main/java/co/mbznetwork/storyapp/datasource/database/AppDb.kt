package co.mbznetwork.storyapp.datasource.database

import androidx.room.Database
import androidx.room.RoomDatabase
import co.mbznetwork.storyapp.datasource.database.dao.StoryDao
import co.mbznetwork.storyapp.datasource.database.dao.StoryRemoteKeyDao
import co.mbznetwork.storyapp.datasource.database.entity.Story
import co.mbznetwork.storyapp.datasource.database.entity.StoryRemoteKey


const val APP_DATABASE_NAME = "app_db.db"

@Database(
    entities = [Story::class, StoryRemoteKey::class],
    version = 1,
    exportSchema = false
)
abstract class AppDb: RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun storyRemoteKeyDao(): StoryRemoteKeyDao
}
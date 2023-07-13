package co.mbznetwork.storyapp.repository.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import co.mbznetwork.storyapp.config.STORY_INITIAL_PAGE
import co.mbznetwork.storyapp.datasource.api.StoryApi
import co.mbznetwork.storyapp.datasource.database.AppDb
import co.mbznetwork.storyapp.datasource.database.dao.StoryDao
import co.mbznetwork.storyapp.datasource.database.dao.StoryRemoteKeyDao
import co.mbznetwork.storyapp.datasource.database.entity.Story
import co.mbznetwork.storyapp.datasource.database.entity.StoryRemoteKey
import co.mbznetwork.storyapp.model.network.NetworkResult
import co.mbznetwork.storyapp.util.ApiUtil
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalPagingApi::class)
@Singleton
class StoryRemoteMediator @Inject constructor(
    private val database: AppDb,
    private val storyDao: StoryDao,
    private val storyApi: StoryApi,
    private val gson: Gson,
    private val storyRemoteKeyDao: StoryRemoteKeyDao
): RemoteMediator<Int, Story>() {
    override suspend fun initialize() = InitializeAction.LAUNCH_INITIAL_REFRESH

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Story>): MediatorResult {
        val page = when(loadType) {
            LoadType.REFRESH -> {
                getRemoteKeyClosestToCurrentPosition(state)?.nextKey
                    ?.minus(1) ?: STORY_INITIAL_PAGE
            }
            LoadType.PREPEND -> {
                val remoteKey = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKey?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKey != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKey = getRemoteKeyForLastItem(state)
                val nextKey = remoteKey?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKey != null)
                nextKey
            }
        }

        return ApiUtil.finalize(gson) {
            storyApi.getStories(page, state.config.pageSize)
        }.let {
            when(it) {
                is NetworkResult.Success -> {
                    val stories = it.data.listStory.map {
                        Story.fromStoryResponse(it)
                    }
                    val endOfPaginationReached = stories.isEmpty()

                    database.withTransaction {
                        if (loadType == LoadType.REFRESH) {
                            storyRemoteKeyDao.deleteAll()
                            storyDao.deleteAll()
                        }

                        val prevKey = if (page == STORY_INITIAL_PAGE) null else page - 1
                        val nextKey = if (endOfPaginationReached) null else page + 1

                        storyRemoteKeyDao.insert(stories.map { story ->
                            StoryRemoteKey(story.id, prevKey, nextKey)
                        })
                        storyDao.insert(stories)
                    }
                    MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
                }
                is NetworkResult.Error -> {
                    MediatorResult.Error(Throwable(it.message))
                }
            }
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Story>) =
        state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let {
            storyRemoteKeyDao.getRemoteKeyById(it.id)
        }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Story>) =
        state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let {
            storyRemoteKeyDao.getRemoteKeyById(it.id)
        }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Story>) =
        with(state) {
            anchorPosition?.let { closestItemToPosition(it) }?.id?.let {
                storyRemoteKeyDao.getRemoteKeyById(it)
            }
        }
}
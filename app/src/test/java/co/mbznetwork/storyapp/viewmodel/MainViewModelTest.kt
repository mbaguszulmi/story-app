package co.mbznetwork.storyapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import co.mbznetwork.storyapp.datasource.database.entity.Story
import co.mbznetwork.storyapp.eventbus.AuthEventBus
import co.mbznetwork.storyapp.eventbus.UIStatusEventBus
import co.mbznetwork.storyapp.model.ui.StoryDisplay
import co.mbznetwork.storyapp.repository.StoryRepository
import co.mbznetwork.storyapp.util.DummyStory
import co.mbznetwork.storyapp.util.MainDispatcherRule
import co.mbznetwork.storyapp.view.adapter.DIFF_CALLBACK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `when stories is not empty, MainViewModel should return data`() = runBlocking{
        val dummyStories = DummyStory.generateDummyStories()
        val data = PagingData.from(dummyStories)
        val expectedStories = flow { emit(data) }
        Mockito.`when`(storyRepository.getStories()).thenReturn(expectedStories)

        val mainViewModel = MainViewModel(
            storyRepository,
            UIStatusEventBus(),
            AuthEventBus(),
            Dispatchers.Main
        )

        val actualStories = mainViewModel.stories.first()

        val differ = generateDiffer()
        differ.submitData(actualStories)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStories.size, differ.snapshot().size)
        assertEquals(StoryDisplay.fromStory(dummyStories.first()), differ.snapshot().first())
    }

    @Test
    fun `when stories is empty, MainViewModel should return empty data`() = runBlocking{
        val data = PagingData.empty<Story>()
        val expectedStories = flow { emit(data) }
        Mockito.`when`(storyRepository.getStories()).thenReturn(expectedStories)

        val mainViewModel = MainViewModel(
            storyRepository,
            UIStatusEventBus(),
            AuthEventBus(),
            Dispatchers.Main
        )

        val actualStories = mainViewModel.stories.first()

        val differ = generateDiffer()
        differ.submitData(actualStories)

        assertEquals(0, differ.snapshot().size)
    }

    private fun generateDiffer() = AsyncPagingDataDiffer(
        diffCallback = DIFF_CALLBACK,
        updateCallback = noopListUpdateCallback,
        workerDispatcher = Dispatchers.Main
    )

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

}
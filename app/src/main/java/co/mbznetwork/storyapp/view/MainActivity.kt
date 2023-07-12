package co.mbznetwork.storyapp.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import co.mbznetwork.storyapp.R
import co.mbznetwork.storyapp.databinding.ActivityMainBinding
import co.mbznetwork.storyapp.util.activityLifecycle
import co.mbznetwork.storyapp.view.adapter.StoryAdapter
import co.mbznetwork.storyapp.view.adapter.StoryLoadStateAdapter
import co.mbznetwork.storyapp.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val mainViewModel by viewModels<MainViewModel>()
    private val storyAdapter by lazy(LazyThreadSafetyMode.NONE) {
        StoryAdapter { it, iv ->
            startActivity(Intent(this, DetailActivity::class.java).putExtra(
                ARG_STORY_ID, it.id
            ), ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, iv, "photo"
            ).toBundle())
        }
    }

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        observeStories()
    }

    private fun initView() {
        binding.apply {
            rvStories.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = storyAdapter.withLoadStateFooter(
                    StoryLoadStateAdapter {
                        storyAdapter.retry()
                    }
                )
            }
            fabAdd.setOnClickListener {
                startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
            }
        }

        activityLifecycle {
            binding.rvStories.apply {
                post { scrollToPosition(0) }
            }
        }
    }

    private fun observeStories() {
        activityLifecycle {
            mainViewModel.stories.collectLatest {
                storyAdapter.submitData(it)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return menu?.let {
            menuInflater.inflate(R.menu.menu_main, it)
            true
        } ?: super.onCreateOptionsMenu(null)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_logout -> {
                mainViewModel.logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

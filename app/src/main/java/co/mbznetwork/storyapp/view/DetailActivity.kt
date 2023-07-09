package co.mbznetwork.storyapp.view

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import co.mbznetwork.storyapp.R
import co.mbznetwork.storyapp.databinding.ActivityDetailBinding
import co.mbznetwork.storyapp.viewmodel.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint

const val ARG_STORY_ID = "arg_story_id"

@AndroidEntryPoint
class DetailActivity : BaseActivity<ActivityDetailBinding>() {
    private val detailViewModel by viewModels<DetailViewModel>()

    override val layoutId: Int
        get() = R.layout.activity_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        supportActionBar?.apply {
            title = getString(R.string.story_detail)
            setDisplayHomeAsUpEnabled(true)
        }
        binding.apply {
            lifecycleOwner = this@DetailActivity
            vm = detailViewModel
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
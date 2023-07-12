package co.mbznetwork.storyapp.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import co.mbznetwork.storyapp.databinding.LoadStoryErrorBinding
import co.mbznetwork.storyapp.databinding.LoadStoryLoadingBinding

class StoryLoadStateAdapter(
    private val retry: () -> Unit
): LoadStateAdapter<StoryLoadStateAdapter.ViewHolder>() {
    sealed class ViewHolder(
        root: View
    ): RecyclerView.ViewHolder(root) {

        class LoadingViewHolder(
            binding: LoadStoryLoadingBinding
        ): ViewHolder(binding.root)

        class ErrorViewHolder(
            private val binding: LoadStoryErrorBinding
        ): ViewHolder(binding.root) {
            fun bind(state: LoadState.Error, retry: () -> Unit) {
                binding.apply {
                    error = state.error.localizedMessage
                    btnRetry.setOnClickListener { retry() }
                    executePendingBindings()
                }
            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        if (loadState is LoadState.Error) (holder as ViewHolder.ErrorViewHolder).bind(
            loadState, retry
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
        return LayoutInflater.from(parent.context).let {
            when(loadState) {
                is LoadState.Error -> ViewHolder.ErrorViewHolder(
                    LoadStoryErrorBinding.inflate(it, parent, false)
                )
                else -> ViewHolder.LoadingViewHolder(
                    LoadStoryLoadingBinding.inflate(it, parent, false)
                )
            }
        }
    }
}
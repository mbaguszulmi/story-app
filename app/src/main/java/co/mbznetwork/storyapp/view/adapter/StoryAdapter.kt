package co.mbznetwork.storyapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import co.mbznetwork.storyapp.databinding.ItemStoryBinding
import co.mbznetwork.storyapp.model.ui.StoryDisplay

class StoryAdapter(
    private val onItemClick: (StoryDisplay, ImageView) -> Unit
): PagingDataAdapter<StoryDisplay, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {
    inner class ViewHolder(
        private val binding: ItemStoryBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: StoryDisplay) {
            binding.apply {
                story = item
                root.setOnClickListener { onItemClick(item, binding.ivItemPhoto) }
                executePendingBindings()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemStoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }
}

private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryDisplay>() {
    override fun areItemsTheSame(oldItem: StoryDisplay, newItem: StoryDisplay) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: StoryDisplay, newItem: StoryDisplay) =
        oldItem == newItem

}

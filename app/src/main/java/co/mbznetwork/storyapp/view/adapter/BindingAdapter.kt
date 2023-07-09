package co.mbznetwork.storyapp.view.adapter

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import co.mbznetwork.storyapp.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

@BindingAdapter("imageUrl", "fitContents", requireAll = false)
fun setImageUrl(image: ImageView, url: String?, fitContents: Boolean = false) {
    if (url.isNullOrBlank()) {
        Glide.with(image.context)
            .load(R.drawable.ic_no_image)
            .centerCrop()
            .into(image)
    } else {
        Glide.with(image.context)
            .load(url).let {
                if (fitContents) it.fitCenter()
                else it.centerCrop()
            }.placeholder(R.drawable.ic_loading)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .error(R.drawable.ic_no_image)
            .into(image)
    }
}

@BindingAdapter("inputBitmap")
fun inputBitmap(imageView: ImageView, bitmap: Bitmap?) {
    bitmap?.let {
        imageView.setImageBitmap(it)
    }
}

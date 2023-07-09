package co.mbznetwork.storyapp.util

import android.content.Intent
import android.provider.MediaStore

object IntentChooserHelper {
    fun imageSelectCameraCapture(message: String) = Intent.createChooser(
        Intent().setType("image/*")
            .setAction(Intent.ACTION_GET_CONTENT),
        message
    ).putExtra(
        Intent.EXTRA_INITIAL_INTENTS, arrayOf(
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
        },
        Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    ))
}
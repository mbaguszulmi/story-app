package co.mbznetwork.storyapp.model.ui

import co.mbznetwork.storyapp.datasource.api.model.response.StoryResponse
import com.google.android.gms.maps.model.LatLng

data class StoryLocationDisplay(
    val id: String,
    val name: String,
    val shortDesc: String,
    val latLng: LatLng
) {
    companion object {
        fun fromStoryResponse(storyResponse: StoryResponse) = with(storyResponse) {
            StoryLocationDisplay(
                id, name, description.take(100), LatLng(lat ?: .0, lon ?: .0)
            )
        }
    }
}

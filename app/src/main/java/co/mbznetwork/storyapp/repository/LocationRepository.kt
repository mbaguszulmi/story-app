package co.mbznetwork.storyapp.repository

import android.location.Location
import android.os.Looper
import androidx.annotation.RequiresPermission
import co.mbznetwork.storyapp.BuildConfig
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.round

@Singleton
class LocationRepository @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
) {

    @RequiresPermission(anyOf = ["android.permission.ACCESS_FINE_LOCATION"])
    fun getLocation() = callbackFlow {
        val callback = object: LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let {
                    trySend(if (BuildConfig.USE_REAL_LOCATION) {
                        it
                    } else {
                        Location("").apply {
                            latitude = ((round(it.latitude * 100) / 100) + Math.random() / 100)
                            longitude = ((round(it.longitude * 100) / 100) + Math.random() / 100)
                        }
                    })
                }
            }
        }

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            TimeUnit.SECONDS.toMillis(1)
        ).build()
        fusedLocationProviderClient
            .requestLocationUpdates(request, callback, Looper.getMainLooper())

        awaitClose {
            fusedLocationProviderClient.removeLocationUpdates(callback)
        }
    }

}
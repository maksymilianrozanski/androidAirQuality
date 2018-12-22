package io.github.maksymilianrozanski.widget

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.content.ContextCompat
import com.google.android.gms.location.LocationServices
import io.github.maksymilianrozanski.utility.LocationSaver

class MyLocationProviderImpl(var context: Context) : MyLocationProvider {

    override fun getLocation(onFinishedListener: MyLocationProvider.OnFinishedListener, onFinishedToPass: MultipleStationWidgetContract.Model.OnFinishedListener) {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        if ((ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) || Build.VERSION.SDK_INT < 23) {
            fusedLocationProviderClient.lastLocation.addOnCompleteListener { result ->
                val locationSaver = LocationSaver(context)
                if (result.isSuccessful && result.result != null) {
                    locationSaver.saveLocation(result.result)
                    onFinishedListener.onLocationReceived(result.result
                            ?: locationSaver.location, onFinishedToPass)
                } else {
                    onFinishedListener.onLocationReceived(locationSaver.location, onFinishedToPass)
                }
            }
        } else {
            onFinishedListener.onLocationFailure(Throwable(access_to_location_not_granted), onFinishedToPass)
        }
    }
}
package io.github.maksymilianrozanski.widget

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.support.v4.content.ContextCompat
import com.google.android.gms.location.LocationServices
import io.github.maksymilianrozanski.utility.LocationSaver

class MyLocationProviderImpl(var context: Context) : MyLocationProvider {

    override fun getLocation(): Location {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val result = fusedLocationProviderClient.lastLocation
            val locationSaver = LocationSaver(context)

            return if (result.isSuccessful && result.result != null) {
                locationSaver.saveLocation(result.result)
                result.result ?: locationSaver.location
            } else {
                locationSaver.location
            }
        } else throw Exception("No permission")
    }
}
package io.github.maksymilianrozanski.widget

import android.location.Location

interface MyLocationProvider {

    fun getLocation(): Location
}
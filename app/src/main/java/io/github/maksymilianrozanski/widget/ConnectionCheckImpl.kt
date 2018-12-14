package io.github.maksymilianrozanski.widget

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class ConnectionCheckImpl(var context: Context) : ConnectionCheck {
    override fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo?
        try {
            activeNetwork = connectivityManager.activeNetworkInfo
        } catch (e: NullPointerException) {

            return false
        }

        return activeNetwork != null && activeNetwork.isConnected
    }
}
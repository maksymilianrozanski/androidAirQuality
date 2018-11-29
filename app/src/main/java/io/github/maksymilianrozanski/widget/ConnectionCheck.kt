package io.github.maksymilianrozanski.widget

import android.content.Context

interface ConnectionCheck {

    fun isConnected(context: Context): Boolean
}
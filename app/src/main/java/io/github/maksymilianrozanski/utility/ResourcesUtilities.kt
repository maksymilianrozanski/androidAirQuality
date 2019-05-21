package io.github.maksymilianrozanski.utility

import android.content.Context
import android.content.res.TypedArray
import android.util.TypedValue

fun getTextColorPrimary(context: Context): Int {
    val typedValue = TypedValue()
    val theme = context.theme
    theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
    val arr: TypedArray = context.obtainStyledAttributes(typedValue.data,
            intArrayOf(android.R.attr.textColorPrimary))
    try {
        return arr.getColor(0, -1)
    } finally {
        arr.recycle()
    }
}
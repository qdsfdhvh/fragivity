@file:JvmName("FragivityUtils")

package com.github.fragivity.util

import android.content.Context
import android.util.TypedValue

fun Float.dp(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        context.resources.displayMetrics
    ).toInt()
}

fun Int.dp(context: Context): Int = toFloat().dp(context)

// make hashCode > 0
internal inline val Any.positiveHashCode: Int
    get() = hashCode() and Int.MAX_VALUE
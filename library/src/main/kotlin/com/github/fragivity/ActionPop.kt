@file:JvmName("FragivityUtil")
@file:JvmMultifileClass

package com.github.fragivity

import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.popBackStack
import kotlin.reflect.KClass

/**
 * pop current fragment from back stack
 */
@JvmSynthetic
fun FragivityNavHost.pop(): Boolean {
    return navController.popBackStack()
}

/**
 * Pop back stack to [clazz]
 */
@JvmSynthetic
fun FragivityNavHost.popTo(clazz: KClass<out Fragment>, inclusive: Boolean = false): Boolean {
    return navController.popBackStack(createRoute(clazz), inclusive)
}

/**
 * Pop back stack to [deepRoute]
 * WARN: currentRoute should not same as route
 */
@JvmSynthetic
fun FragivityNavHost.popTo(deepRoute: String, inclusive: Boolean = false): Boolean {
    return navController.popBackStack(
        NavDeepLinkRequest.Builder.fromUri(createDeepRoute(deepRoute).toUri()).build(),
        inclusive
    )
}
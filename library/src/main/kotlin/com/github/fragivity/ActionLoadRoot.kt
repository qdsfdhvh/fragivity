@file:JvmName("FragivityUtil")
@file:JvmMultifileClass

package com.github.fragivity

import android.os.Bundle
import androidx.fragment.app.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.createGraph
import androidx.navigation.fragment.NavHostFragment
import kotlin.reflect.KClass

/**
 * Load root fragment
 */
@JvmSynthetic
fun NavHostFragment.loadRoot(clazz: KClass<out Fragment>) {
    loadRoot("root", clazz)
}

@JvmSynthetic
fun NavHostFragment.loadRoot(deepRoute: String, clazz: KClass<out Fragment>) {
    val route = createRoute(clazz)
    loadRootInternal(route, deepRoute) {
        navController.createNavDestination(route, clazz)
    }
}

@JvmSynthetic
fun NavHostFragment.loadRoot(deepRoute: String = "root", block: (Bundle) -> Fragment) {
    val route = deepRoute
    loadRootInternal(route, deepRoute) {
        navController.createNavDestination(route, block)
    }
}

private fun NavHostFragment.loadRootInternal(
    route: String,
    deepRoute: String,
    startDestinationFactory: () -> NavDestination
) = with(navController) {
    navigatorProvider.addNavigator(
        FragivityFragmentNavigator(requireContext(), childFragmentManager, id)
    )

    val nodeViewModel = ViewModelProvider(
        this@loadRootInternal,
        defaultViewModelProviderFactory
    ).get(FragivityNodeViewModel::class.java)

    val startDestination = startDestinationFactory.invoke()
    startDestination.addDeepLink(createDeepRoute(deepRoute))

    graph = createGraph(startDestination = route) {
        addDestination(startDestination)
        // restore destination from vm for NavController#mBackStackToRestore
        nodeViewModel.restoreDestination(this@with, this)
    }

    fragivityHostViewModel.setUpNavHost(nodeViewModel, this)
}

/**
 * navigation内部对route处理大致如[NavGraph.findNode]:
 *    val id = createRoute(route).hashCode()
 * 目前只能当id看待，也许后续navigation库会适配。
 */
@JvmSynthetic
internal fun createRoute(clazz: KClass<out Fragment>): String = clazz.toString()

/**
 * 当前真正用于路由跳转的route。
 */
@JvmSynthetic
internal fun createDeepRoute(route: String): String =
    "android-app://androidx.navigation.fragivity/$route"

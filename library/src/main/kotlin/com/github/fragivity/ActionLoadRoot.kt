@file:JvmName("FragivityUtil")
@file:JvmMultifileClass

package com.github.fragivity

import android.os.Bundle
import androidx.core.net.toUri
import androidx.fragment.app.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.*
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
) = setupGraph(route) {
    val startDestination = startDestinationFactory()
    startDestination.addDeepLink(wrapDeepRoute(deepRoute))
    addDestination(startDestination)
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
internal fun wrapDeepRoute(route: String): String =
    "android-app://androidx.navigation.fragivity/$route"

@JvmSynthetic
internal fun String.toRequest() =
    NavDeepLinkRequest.Builder.fromUri(wrapDeepRoute(this).toUri()).build()

@JvmSynthetic
internal fun NavHostFragment.setupGraph(
    startRoute: String,
    graphBuilder: NavGraphBuilder.() -> Unit
) {
    addFragivityNavigator()
    val nodeViewModel = getNodeViewModel()
    with(navController) {
        graph = createGraph(startDestination = startRoute) {
            graphBuilder()
            nodeViewModel.restoreDestination(this@with, this)
        }
        fragivityHostViewModel.setUpNavHost(nodeViewModel, this)
    }
}

@JvmSynthetic
private fun NavHostFragment.addFragivityNavigator() {
    navController.navigatorProvider.addNavigator(
        FragivityFragmentNavigator(requireContext(), childFragmentManager, id)
    )
}

@JvmSynthetic
private fun NavHostFragment.getNodeViewModel(): FragivityNodeViewModel {
    return ViewModelProvider(this, defaultViewModelProviderFactory)
        .get(FragivityNodeViewModel::class.java)
}


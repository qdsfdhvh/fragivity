@file:JvmName("FragivityUtil")
@file:JvmMultifileClass

package com.github.fragivity

import android.os.Bundle
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.*
import androidx.navigation.fragment.FragmentNavigator
import kotlin.reflect.KClass

@JvmSynthetic
fun FragivityNavHost.push(deepRoute: String, optionsBuilder: NavOptions.() -> Unit = {}) {
    push(deepRoute, navOptions(optionsBuilder))
}

@JvmSynthetic
fun FragivityNavHost.push(deepRoute: String, navOptions: NavOptions?) {
    val request = NavDeepLinkRequest.Builder.fromUri(wrapDeepRoute(deepRoute).toUri()).build()
    val (node, matchingArgs) = navController.findDestinationAndArgs(request) ?: return
    pushInternal(node, navOptions, matchingArgs)
}

/**
 * Navigates to fragment of [clazz] by pushing it to back stack
 */
@JvmSynthetic
fun FragivityNavHost.push(clazz: KClass<out Fragment>, optionsBuilder: NavOptions.() -> Unit = {}) {
    push(clazz, navOptions(optionsBuilder))
}

@JvmSynthetic
fun FragivityNavHost.push(clazz: KClass<out Fragment>, navOptions: NavOptions?) {
    pushInternal(putFragment(clazz), navOptions)
}

/**
 * Navigates to a fragment by its factory
 */
@JvmSynthetic
inline fun <reified T : Fragment> FragivityNavHost.push(
    noinline optionsBuilder: NavOptions.() -> Unit = {},
    noinline block: (Bundle) -> T
) {
    push(T::class, navOptions(optionsBuilder), block)
}

@JvmSynthetic
fun FragivityNavHost.push(
    clazz: KClass<out Fragment>,
    navOptions: NavOptions?,
    factory: (Bundle) -> Fragment
) {
    pushInternal(putFragment(clazz, factory), navOptions)
}

@JvmSynthetic
internal fun FragivityNavHost.putFragment(
    clazz: KClass<out Fragment>,
    factory: ((Bundle) -> Fragment)? = null
): FragmentNavigator.Destination {
    val graph = navController.graph

    val route = createRoute(clazz)
    var node = graph.findNode(route) as? FragmentNavigator.Destination
    if (node != null) {
        graph -= node
        removeDestination(node)
    }
    node = if (factory != null) {
        navController.createNavDestination(route, factory)
    } else {
        navController.createNavDestination(route, clazz)
    }
    graph += node
    saveDestination(node)
    return node
}

@JvmSynthetic
private fun FragivityNavHost.pushInternal(
    node: NavDestination,
    navOptions: NavOptions?,
    matchingArgs: Bundle? = null
) = with(navController) {
    if (navOptions == null) {
        navigate(node.id)
        return@with
    }

    // fix https://github.com/vitaviva/fragivity/issues/31
    // popSelf为true时， mFragmentManager.mBackStack需要与NavController.mBackStack同步更新，
    //  mFragmentManager在Navigator中会处理，NavController需要在此处处理，先删除当前Destination
//    if (navOptions.popSelf) {
//        navController.removeLastBackStackEntry()
//    }

    when (navOptions.launchMode) {
        LaunchMode.STANDARD,
        LaunchMode.SINGLE_TOP -> {
            navigate(
                node.id,
                navOptions.toBundle() + matchingArgs,
                navOptions.totOptions(node.id),
                navOptions.totExtras()
            )
        }
        LaunchMode.SINGLE_TASK -> {
            // curr == target || try pop to target
//            if (currentDestination?.id == node.id || popBackStack(node.id, false)) {
//                val navigator: Navigator<out NavDestination> =
//                    navigatorProvider.getNavigator(node.navigatorName)
//                if (navigator is FragivityFragmentNavigator) {
//                    navigator.restoreTopFragment(
//                        node.id,
//                        navOptions.toBundle() + matchingArgs
//                    )
//                }
//                return@with
//            }

            // create target
            navigate(
                node.id,
                navOptions.toBundle() + matchingArgs,
                navOptions.totOptions(node.id),
                navOptions.totExtras()
            )
        }
    }
}
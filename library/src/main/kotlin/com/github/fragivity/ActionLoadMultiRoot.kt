package com.github.fragivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import androidx.navigation.findDestinationAndArgs
import androidx.navigation.fragment.NavHostFragment
import kotlin.reflect.KClass

@JvmSynthetic
fun NavHostFragment.loadMultiRoot(vararg clazzArray: KClass<out Fragment>) {
    loadMultiRoot(0, *clazzArray)
}

@JvmSynthetic
fun NavHostFragment.loadMultiRoot(showPosition: Int, vararg clazzArray: KClass<out Fragment>) {
    loadMultiRootInternal(showPosition, clazzArray.map { clazz ->
        createRoute(clazz) to {
            val route = createRoute(clazz)
            navController.createNavDestination(route, clazz)
        }
    })
}

@JvmSynthetic
fun NavHostFragment.loadMultiRoot(vararg factoryArray: Pair<String, (Bundle) -> Fragment>) {
    loadMultiRoot(0, *factoryArray)
}

@JvmSynthetic
fun NavHostFragment.loadMultiRoot(
    showPosition: Int,
    vararg factoryArray: Pair<String, (Bundle) -> Fragment>
) {
    loadMultiRootInternal(showPosition, factoryArray.map { pair ->
        pair.first to {
            val route = pair.first
            navController.createNavDestination(route, pair.second)
        }
    })
}

private fun NavHostFragment.loadMultiRootInternal(
    showPosition: Int = 0,
    destinations: List<Pair<String, () -> NavDestination>>
) = setupGraph((100 + showPosition).toString()) {
    destinations.forEachIndexed { index, pair ->
        val deepRoute = pair.first
        val destination = pair.second()
        val childGraph = createGraph(startDestination = destination.route!!, (100 + index).toString()) {
            destination.addDeepLink(wrapDeepRoute(deepRoute))
            addDestination(destination)
        }
        addDestination(childGraph)
    }
}

// TODO rename
@JvmSynthetic
fun NavController.pushTo(position: Int): Boolean {
//    val (destination, args) = findDestinationAndArgs(.toRequest()) ?: return false
    val destination = findDestination((100 + position).toString()) ?: return false
    val builder = NavOptions.Builder().setLaunchSingleTop(true)
        .setRestoreState(true)
        .setPopUpTo(graph.findStartDestination().id, inclusive = false, saveState = true)
    return try {
        navigate(destination.id, null, builder.build())
        true
    } catch (e: IllegalArgumentException) {
        false
    }
}

@JvmSynthetic
    fun NavDestination.matchDestination(deepRoute: String): Boolean {
    return matchDeepLink(deepRoute.toRequest()) != null
}
@file:JvmName("FragivityUtil")
@file:JvmMultifileClass

package com.github.fragivity

import android.os.Bundle
import androidx.fragment.app.FragivityFragmentDestination
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment

@JvmSynthetic
fun NavHostFragment.composable(
    deepRoute: String,
    argument: NamedNavArgument,
    factory: (Bundle) -> Fragment
) {
    composable(deepRoute, listOf(argument), factory)
}

@JvmSynthetic
fun NavHostFragment.composable(
    deepRoute: String,
    arguments: List<NamedNavArgument> = emptyList(),
    factory: (Bundle) -> Fragment
) {
    composableInternal(deepRoute, arguments, factory)
}

private fun NavHostFragment.composableInternal(
    deepRoute: String,
    arguments: List<NamedNavArgument>,
    factory: (Bundle) -> Fragment
) = with(navController) {
    val route = deepRoute // = id

    var node = graph.findNode(route)
    if (node is FragivityFragmentDestination) {
        node.factory = factory
    } else {
        node = createNavDestination(route, factory)
        graph.addDestination(node)
    }

    node.apply {
        addDeepLink(wrapDeepRoute(deepRoute))
        arguments.forEach { (argumentName, argument) ->
            addArgument(argumentName, argument)
        }
    }

    // save destination for rebuild
    navigator.saveDestination(node)
}
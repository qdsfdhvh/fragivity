package com.github.fragivity

import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHost

/**
 * NavHost with viewModel to store NavDestination
 */
class FragivityNavHost(
    private val viewModel: FragivityNodeViewModel,
    override val navController: NavController
) : NavHost {

    @JvmSynthetic
    internal fun saveDestination(node: NavDestination) {
        viewModel.saveDestination(node)
    }

    @JvmSynthetic
    internal fun removeDestination(id: Int) {
        viewModel.removeDestination(id)
    }
}

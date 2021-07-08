/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.fragivity.example.multiback

import android.os.Bundle
import android.view.View
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.ui.*
import com.github.fragivity.*
import com.github.fragivity.example.multiback.formscreen.Register
import com.github.fragivity.example.multiback.homescreen.Title
import com.github.fragivity.example.multiback.listscreen.Leaderboard
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.lang.ref.WeakReference

/**
 * An activity that inflates a layout that has a [BottomNavigationView].
 */
class MultiBackFragment : Fragment(R.layout.activity_main) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragment = findOrCreateNavHostFragment(R.id.nav_host_container)
        val navController = navHostFragment.navController

//        navHostFragment.loadRoot()

        val array = arrayOf<Pair<String, (Bundle) -> Fragment>>(
            "home" to { Title() },
            "list" to { Leaderboard() },
            "form" to { Register() }
        )
        navHostFragment.loadMultiRoot(0, *array)

        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNavigationView.setOnItemSelectedListener { item ->
            val route = when (item.itemId) {
                R.id.home -> 0
                R.id.list -> 1
                R.id.form -> 2
                else -> return@setOnItemSelectedListener false
            }
            navController.pushTo(route)
//            onNavDestinationSelected(item, navController)
        }

        val weakReference = WeakReference(bottomNavigationView)
        navController.addOnDestinationChangedListener(object :
            NavController.OnDestinationChangedListener {
            override fun onDestinationChanged(
                controller: NavController,
                destination: NavDestination,
                arguments: Bundle?
            ) {
                val navigationView = weakReference.get()
                if (navigationView == null) {
                    navController.removeOnDestinationChangedListener(this)
                    return
                }

                navigationView.menu.forEach { item ->
                    val route = getRoute(item.itemId) ?: return@forEach
                    if (destination.matchDestination(route)) {
                        item.isChecked = true
                    }
                }
            }
        })
    }

    private fun getRoute(id: Int) = when (id) {
        R.id.home -> "home"
        R.id.list -> "list"
        R.id.form -> "form"
        else -> null
    }

//    private fun onNavDestinationSelected(item: MenuItem, navController: NavController): Boolean {
//        val builder = NavOptions.Builder().setLaunchSingleTop(true).setRestoreState(true)
//        if (item.order and Menu.CATEGORY_SECONDARY == 0) {
//            builder.setPopUpTo(
//                navController.graph.findStartDestination().id,
//                inclusive = false,
//                saveState = true
//            )
//        }
//        val options = builder.build()
//        return try {
//            navController.navigate(item.itemId, null, options)
//            true
//        } catch (e: IllegalArgumentException) {
//            false
//        }
//    }

//    fun NavDestination.matchDestination(@IdRes destId: Int): Boolean =
//        hierarchy.any { it.id == destId }

    companion object {
        fun newInstance() = MultiBackFragment()
    }
}

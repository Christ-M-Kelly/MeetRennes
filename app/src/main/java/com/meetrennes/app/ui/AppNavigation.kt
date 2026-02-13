package com.meetrennes.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.meetrennes.app.presentation.LieuVM
import com.meetrennes.app.ui.screens.DetailLieuScreen
import com.meetrennes.app.ui.screens.FavorisScreen
import com.meetrennes.app.ui.screens.ListeLieuxScreen
import com.meetrennes.app.ui.screens.MapScreen
import com.meetrennes.app.ui.screens.Screen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavigation(vm: LieuVM) {


    NavDisplay(
        backStack = vm.backStack,
        onBack = { vm.pop() },
        entryProvider = entryProvider {

            // --- ACCUEIL : Liste des lieux ---
            entry<Screen.Home> {
                ListeLieuxScreen(
                    vm = vm,
                    onEventClick = { lieu -> vm.push(Screen.LieuDetail(lieu.id)) },
                    onBottomNav = { tab -> vm.goTop(tab) }
                )
            }

            // --- DÉTAIL D'UN LIEU ---

            entry<Screen.LieuDetail> { detail ->
                DetailLieuScreen(
                    lieuId = detail.lieuId,
                    vm = vm,
                    onBack = { vm.pop() }
                )
            }

            // --- CARTE ---

            entry<Screen.Map> {
                MapScreen(
                    vm = vm,
                    onBack = { vm.pop() },
                    onBottomNav = { tab -> vm.goTop(tab) }
                )
            }

            // --- FAVORIS ---

            entry<Screen.Favorites> {
               FavorisScreen(
                    vm = vm,
                    onEventClick = { lieu -> vm.push(Screen.LieuDetail(lieu.id)) },
                    onBottomNav = { tab -> vm.goTop(tab) }
                )
            }


        }
    )
}

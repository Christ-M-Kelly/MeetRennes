package com.meetrennes.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.meetrennes.app.ui.screens.Screen

@Composable
fun MeetRennesBottomBar(
    selected: Int = 0,
    onNav: (Screen) -> Unit = {}
) {
    BottomAppBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Accueil") },
            label = { Text("Accueil") },
            selected = selected == 0,
            onClick = { onNav(Screen.Home) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Map, contentDescription = "Carte") },
            label = { Text("Carte") },
            selected = selected == 1,
            onClick = { onNav(Screen.Map) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Favoris") },
            label = { Text("Favoris") },
            selected = selected == 2,
            onClick = { onNav(Screen.Favorites) }
        )

    }
}

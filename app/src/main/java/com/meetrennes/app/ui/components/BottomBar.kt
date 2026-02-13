package com.meetrennes.app.ui.components

import androidx.compose.material3.BottomAppBar

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
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
            label = { Text("Profil") },
            selected = selected == 3,
            onClick = { onNav(Screen.Profile) }
        )
    }
}

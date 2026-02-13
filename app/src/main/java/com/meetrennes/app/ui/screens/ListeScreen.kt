package com.meetrennes.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.meetrennes.app.domain.CategorieLieu
import com.meetrennes.app.domain.Lieu
import com.meetrennes.app.presentation.LieuVM
import com.meetrennes.app.ui.components.MeetRennesBottomBar
import com.meetrennes.app.ui.theme.FavoriteRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListeLieuxScreen(
    vm: LieuVM,
    onEventClick: (Lieu) -> Unit = {},
    onBottomNav: (Screen) -> Unit = {}
) {
    // Collecter les données du ViewModel sous forme de state Compose
    val lieux by vm.lieux.collectAsState()
    val filtreActif by vm.filtreCategorie.collectAsState()

    // Filtrer les lieux selon la catégorie sélectionnée
    val lieuxFiltres = if (filtreActif != null) {
        lieux.filter { it.categorie == filtreActif }
    } else {
        lieux
    }

    Scaffold(
        // --- TOP BAR ---
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Explore,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "MeetRennes",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        // --- BOTTOM BAR ---
        bottomBar = {
            MeetRennesBottomBar(selected = 0, onNav = onBottomNav)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // --- FILTRES PAR CATÉGORIE ---
            LazyRow(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                // Chip "Tous"
                item {
                    FilterChip(
                        selected = filtreActif == null,
                        onClick = { vm.setFiltre(null) },
                        label = { Text("Tous") }
                    )
                }
                // Un chip par catégorie
                items(CategorieLieu.entries.toList()) { cat ->
                    FilterChip(
                        selected = filtreActif == cat,
                        onClick = {
                            vm.setFiltre(if (filtreActif == cat) null else cat)
                        },
                        label = { Text(cat.label()) }
                    )
                }
            }

            // --- LISTE DES LIEUX ---
            if (lieuxFiltres.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Aucun lieu trouvé",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(lieuxFiltres, key = { it.id }) { lieu ->
                        LieuItem(
                            lieu = lieu,
                            onClick = { onEventClick(lieu) },
                            onFavorite = { vm.toggleFavorite(lieu.id) }
                        )
                    }
                }
            }
        }
    }
}

// =====================================================================
// LieuItem — Carte d'un lieu dans la liste
// =====================================================================
@Composable
private fun LieuItem(
    lieu: Lieu,
    onClick: () -> Unit,
    onFavorite: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image du lieu (chargée depuis Internet via Coil)
            AsyncImage(
                model = lieu.imageUrl,
                contentDescription = lieu.nom,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(Modifier.width(12.dp))

            // Infos textuelles
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lieu.nom,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = lieu.categorie.label(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Place,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = lieu.adresse,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Bouton favori
            IconButton(onClick = onFavorite) {
                Icon(
                    imageVector = if (lieu.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favori",
                    tint = if (lieu.isFavorite) FavoriteRed else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}



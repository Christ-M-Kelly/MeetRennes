package com.meetrennes.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.meetrennes.app.domain.Lieu
import com.meetrennes.app.presentation.LieuVM
import com.meetrennes.app.ui.components.InfoCard
import com.meetrennes.app.ui.theme.FavoriteRed
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailLieuScreen(
    lieuId: String,
    vm: LieuVM,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current

    // Récupère le lieu depuis la liste du ViewModel
    val lieux by vm.lieux.collectAsState()
    val lieu = lieux.find { it.id == lieuId } ?: Lieu.DEFAULT

    // Mini-carte pour afficher la position du lieu
    val miniMapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(false)  // Pas de zoom/scroll
            controller.setZoom(16.0)      // Zoom serré sur le lieu
            controller.setCenter(GeoPoint(lieu.latitude, lieu.longitude))
        }
    }

    // Nettoyage de la mini-carte quand on quitte l'écran
    DisposableEffect(Unit) {
        onDispose {
            miniMapView.onPause()
            miniMapView.onDetach()
        }
    }

    Scaffold(
        // Top bar avec bouton retour
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        lieu.nom,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        // FAB (bouton flottant) pour les favoris
        floatingActionButton = {
            FloatingActionButton(
                onClick = { vm.toggleFavorite(lieu.id) },
                containerColor = if (lieu.isFavorite) FavoriteRed else MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    if (lieu.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favori",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // --- IMAGE PRINCIPALE ---
            AsyncImage(
                model = lieu.imageUrl,
                contentDescription = lieu.nom,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            )

            Spacer(Modifier.height(16.dp))

            // --- NOM ---
            Text(
                text = lieu.nom,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(8.dp))

            // --- INFOS EN CARTES ---
            InfoCard(
                icon = Icons.Default.Category,
                title = "Catégorie",
                lines = listOf(lieu.categorie.label())
            )

            InfoCard(
                icon = Icons.Default.Place,
                title = "Adresse",
                lines = listOf(lieu.adresse)
            )

            Spacer(Modifier.height(16.dp))

            // --- DESCRIPTION ---
            Text(
                text = lieu.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            // --- MINI-CARTE avec position du lieu ---
            // Utilise AndroidView pour intégrer osmdroid dans Compose
            Text(
                text = "📍 Localisation",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(8.dp))

            AndroidView(
                factory = {
                    miniMapView.apply {
                        // Désactive les interactions (carte en lecture seule)
                        isFocusable = false
                        isFocusableInTouchMode = false
                        isClickable = false

                        // Ajoute un marqueur sur la position du lieu
                        val marker = Marker(this)
                        marker.position = GeoPoint(lieu.latitude, lieu.longitude)
                        marker.title = lieu.nom
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        overlays.add(marker)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(Modifier.height(80.dp)) // Espace pour le FAB
        }
    }
}


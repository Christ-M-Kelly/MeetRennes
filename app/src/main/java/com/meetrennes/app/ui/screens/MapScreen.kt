package com.meetrennes.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.meetrennes.app.presentation.LieuVM
import com.meetrennes.app.ui.components.MeetRennesBottomBar
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    vm: LieuVM,
    onBack: () -> Unit = {},
    onBottomNav: (Screen) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val lieux by vm.lieux.collectAsState()

    // Configuration osmdroid (OBLIGATOIRE pour que les tuiles se chargent)

    val osmConfig = Configuration.getInstance()
    osmConfig.load(context, context.getSharedPreferences("osmdroid", 0))
    osmConfig.userAgentValue = context.packageName

    // Définit le dossier de cache pour les tuiles de la carte
    osmConfig.osmdroidBasePath = context.filesDir
    osmConfig.osmdroidTileCache = java.io.File(context.cacheDir, "osmdroid_tiles")

    // Création de la MapView (vue Android native)
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)          // Tuiles OpenStreetMap
            setMultiTouchControls(true) // Zoom avec deux doigts
            setTilesScaleFactor(2f)                           // Tuiles nettes sur écrans HD
            controller.setZoom(14.0)                          // Zoom initial
            controller.setCenter(GeoPoint(48.1173, -1.6778))  // Centre sur Rennes
        }
    }

    // Overlay de position GPS (Point de position)
    val myLocationOverlay = remember {
        MyLocationNewOverlay(GpsMyLocationProvider(context), mapView)
    }

    // Launcher pour demander la permission de localisation
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            myLocationOverlay.enableMyLocation()
            mapView.invalidate()
        }
    }

    // Vérifie la permission et la demande si nécessaire
    LaunchedEffect(Unit) {
        if (!mapView.overlays.contains(myLocationOverlay)) {
            mapView.overlays.add(myLocationOverlay)
        }

        val hasPerm = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPerm) {
            myLocationOverlay.enableMyLocation()
        } else {
            // Affiche le popup système de demande de permission
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // --- MARQUEURS : un par lieu ---

    val eventMarkers = remember { mutableStateListOf<Marker>() }

    LaunchedEffect(lieux) {
        // Supprime les anciens marqueurs
        eventMarkers.forEach { mapView.overlays.remove(it) }
        eventMarkers.clear()

        // Crée les nouveaux marqueurs
        val markers = lieux.map { lieu ->
            Marker(mapView).apply {
                position = GeoPoint(lieu.latitude, lieu.longitude)
                title = lieu.nom
                snippet = lieu.categorie.label()
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                setOnMarkerClickListener { m, _ ->
                    m.showInfoWindow()
                    true
                }
            }
        }

        markers.forEach { mapView.overlays.add(it) }
        eventMarkers.addAll(markers)
        mapView.invalidate() // Redessine la carte
    }

    // --- LIFECYCLE : forwarding vers la MapView ---
    // La carte doit être mise en pause quand l'écran n'est plus visible,
    // et reprise quand il redevient visible. Sinon = fuite mémoire.
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            myLocationOverlay.disableMyLocation()
            mapView.onPause()
            mapView.onDetach()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Carte de Rennes",
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
        bottomBar = {
            MeetRennesBottomBar(selected = 1, onNav = onBottomNav)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val loc = myLocationOverlay.myLocation
                    if (loc != null) {
                        mapView.controller.animateTo(loc)
                        mapView.controller.setZoom(17.0)
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Filled.MyLocation,
                    contentDescription = "Ma position",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // AndroidView intègre la vue native MapView dans Compose
            AndroidView(
                factory = {
                    mapView.apply {
                        isFocusable = false
                        isFocusableInTouchMode = false
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

package com.meetrennes.app.data

import com.meetrennes.app.data.local.LieuDao
import com.meetrennes.app.data.remote.OverpassService
import com.meetrennes.app.domain.Lieu
import com.meetrennes.app.domain.LieuRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext



class RoomLieuRepository(
    private val dao: LieuDao,
    private val overpassService: OverpassService,
    private val io: CoroutineDispatcher = Dispatchers.IO
) : LieuRepository {

    private val mutex = Mutex()
    private val scope = kotlinx.coroutines.CoroutineScope(io)
    private var isLoaded = false

    private val _lieux = MutableStateFlow<List<Lieu>>(emptyList())
    override val lieux: StateFlow<List<Lieu>> = _lieux.asStateFlow()

    /**
     * Initialisation : charge les données.
     * - Insère TOUJOURS les données hardcodées (pour avoir un fond de carte propre)
     * - Lance l'appel API Overpass en background pour enrichir
     * - Observe la DB via Flow
     */
    override suspend fun init() {
        if (isLoaded) return

        mutex.withLock {
            if (isLoaded) return@withLock
            isLoaded = true

            // 1. Insertion immédiate des données statiques (rapide & local)
            withContext(io) {
                // On insère d'abord les données statiques pour garantir un affichage immédiat
                // même si le réseau est lent ou absent.
                android.util.Log.d("LieuRepo", "→ Insertion des données statiques (LISTE_RENNES)")
                dao.insertAll(Lieu.LISTE_RENNES)
            }

            // 2. Lancement de la mise à jour API en arrière-plan (non bloquant)
            scope.launch {
                try {
                    android.util.Log.d("LieuRepo", "Lancement refresh API...")
                    val apiLieux = overpassService.fetchLieux()
                    if (apiLieux.isNotEmpty()) {
                        android.util.Log.d("LieuRepo", "API succès : ${apiLieux.size} lieux trouvés -> Insertion")
                        dao.insertAll(apiLieux)
                    } else {
                        android.util.Log.w("LieuRepo", "API retour vide")
                    }
                } catch (e: Exception) {
                    android.util.Log.e("LieuRepo", "Erreur API", e)
                }
            }

            // 3. Observation de la DB en arrière-plan (non bloquant)
            scope.launch {
                dao.getAll().collect { listeFromDb ->
                    android.util.Log.d("LieuRepo", "→ Mise à jour StateFlow : ${listeFromDb.size} lieux")
                    _lieux.value = listeFromDb
                }
            }
        }
    }

    override suspend fun toggleFavorite(id: String) {
        withContext(io) {
            val lieu = dao.getById(id) ?: return@withContext
            val updated = lieu.copy(isFavorite = !lieu.isFavorite)
            dao.update(updated)
        }
    }

    override suspend fun add(lieu: Lieu) {
        withContext(io) {
            dao.insert(lieu)
        }
    }
}

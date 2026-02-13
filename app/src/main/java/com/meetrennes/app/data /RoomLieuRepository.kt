package com.meetrennes.app.data

import com.meetrennes.app.data.local.LieuDao
import com.meetrennes.app.domain.Lieu
import com.meetrennes.app.domain.LieuRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

// =====================================================================
// RoomLieuRepository.kt — Implémentation du repository avec Room
// =====================================================================
// Cette classe fait le pont entre le ViewModel et la base Room.
// Elle implémente l'interface LieuRepository définie dans domain/.
//
// POINTS TECHNIQUES IMPORTANTS :
// - Mutex → empêche deux threads d'écrire en même temps (thread safety)
// - Dispatchers.IO → les opérations BDD se font sur un thread dédié
//   (pas le thread principal, sinon l'app freeze)
// - StateFlow → permet aux écrans Compose de se mettre à jour
//   automatiquement quand les données changent
//
// DANS L'ORAL : "Le repository utilise un Mutex pour éviter les conflits
// d'écriture et Dispatchers.IO pour ne pas bloquer le thread UI."
// =====================================================================

class RoomLieuRepository(
    private val dao: LieuDao,
    private val io: CoroutineDispatcher = Dispatchers.IO
) : LieuRepository {

    // Mutex (verrou) pour éviter les accès concurrents à la BDD
    private val mutex = Mutex()

    // Indicateur : est-ce que les données ont déjà été chargées ?
    private var isLoaded = false

    // Liste observable des lieux (les écrans Compose écoutent ce flow)
    private val _lieux = MutableStateFlow<List<Lieu>>(emptyList())
    override val lieux: StateFlow<List<Lieu>> = _lieux.asStateFlow()

    /**
     * Initialisation : charge les données depuis Room.
     * Si la base est vide (premier lancement), on insère les lieux par défaut.
     */
    override suspend fun init() {
        if (isLoaded) return

        mutex.withLock {
            if (isLoaded) return@withLock

            withContext(io) {
                // Vérifie si la base est vide
                val count = dao.count()
                if (count == 0) {
                    // Premier lancement → on pré-remplit avec les lieux de Rennes
                    dao.insertAll(Lieu.LISTE_RENNES)
                }

                // Collecte le Flow Room dans notre StateFlow
                dao.getAll().collect { listeFromDb ->
                    _lieux.value = listeFromDb
                    isLoaded = true
                }
            }
        }
    }

    /**
     * Ajoute ou retire un lieu des favoris.
     * On récupère le lieu, on inverse son statut, et on met à jour Room.
     */
    override suspend fun toggleFavorite(id: String) {
        withContext(io) {
            val lieu = dao.getById(id) ?: return@withContext
            // copy() crée une copie avec isFavorite inversé
            val updated = lieu.copy(isFavorite = !lieu.isFavorite)
            dao.update(updated)
        }
    }

    /**
     * Ajoute un nouveau lieu dans la base Room.
     */
    override suspend fun add(lieu: Lieu) {
        withContext(io) {
            dao.insert(lieu)
        }
    }
}

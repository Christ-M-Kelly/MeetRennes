package com.meetrennes.app.domain

import kotlinx.coroutines.flow.StateFlow

interface LieuRepository {

    val lieux: StateFlow<List<Lieu>>

    suspend fun init()

    suspend fun toggleFavorite(id: String)

    suspend fun add(lieu: Lieu)
}

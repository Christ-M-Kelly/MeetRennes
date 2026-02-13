package com.meetrennes.app.presentation

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meetrennes.app.domain.CategorieLieu
import com.meetrennes.app.domain.Lieu
import com.meetrennes.app.domain.LieuRepository
import com.meetrennes.app.ui.screens.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LieuVM(private val repo: LieuRepository) : ViewModel() {

    val backStack = mutableStateListOf<Screen>(Screen.Home)

    fun push(screen: Screen) {
        backStack.add(screen)
    }

    fun pop(): Boolean {
        return if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
            true
        } else {
            false
        }
    }

    fun goTop(tab: Screen) {
        backStack.clear()
        backStack.add(tab)
    }

    val lieux: StateFlow<List<Lieu>> = repo.lieux

    private val _filtreCategorie = MutableStateFlow<CategorieLieu?>(null)
    val filtreCategorie: StateFlow<CategorieLieu?> = _filtreCategorie.asStateFlow()

    fun setFiltre(categorie: CategorieLieu?) {
        _filtreCategorie.value = categorie
    }

    fun toggleFavorite(id: String) {
        viewModelScope.launch { repo.toggleFavorite(id) }
    }

    fun add(lieu: Lieu) {
        viewModelScope.launch { repo.add(lieu) }
    }

    init {
        viewModelScope.launch { repo.init() }
    }
}

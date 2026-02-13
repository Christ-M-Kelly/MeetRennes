package com.meetrennes.app.ui.screens

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


@Serializable
/* Sealed interface nous permet de lister tous les ecrans possibles de l'app
*  NavKey lui est nécessaire pour Navigation3
*  @Serializable permet à Navigation3 de sauvegarder et de restaurer l'état de navigation
*/
sealed interface Screen : NavKey {

    /** Écran d'accueil — Liste de tous les lieux */
    @Serializable
    data object Home : Screen

    /** Écran de détail d'un lieu — identifié par son ID */
    @Serializable
    data class LieuDetail(val lieuId: String) : Screen

    /** Écran carte */
    @Serializable
    data object Map : Screen

    /** Écran favoris */
    @Serializable
    data object Favorites : Screen


}

package com.meetrennes.app.di

import com.meetrennes.app.data.RoomLieuRepository
import com.meetrennes.app.data.local.MeetRennesDatabase
import com.meetrennes.app.domain.LieuRepository
import com.meetrennes.app.presentation.LieuVM
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Base de données Room — singleton (une seule instance)
    single { MeetRennesDatabase.create(get()) }

    // DAO — récupéré depuis la base de données
    single { get<MeetRennesDatabase>().lieuDao() }

    // Repository — implémentation Room injectée comme LieuRepository

    single<LieuRepository> { RoomLieuRepository(get()) }



    // ViewModels
    viewModel { LieuVM(get()) }

}

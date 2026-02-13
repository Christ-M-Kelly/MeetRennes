package com.meetrennes.app.di

import com.meetrennes.app.data.RoomLieuRepository
import com.meetrennes.app.data.local.MeetRennesDatabase
import com.meetrennes.app.data.remote.OverpassService
import com.meetrennes.app.domain.LieuRepository
import com.meetrennes.app.presentation.LieuVM
import okhttp3.OkHttpClient
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import java.util.concurrent.TimeUnit



val appModule = module {

    // --- Base de données Room (singleton) ---
    single { MeetRennesDatabase.create(get()) }

    // --- DAO ---
    single { get<MeetRennesDatabase>().lieuDao() }

    // --- Client HTTP (singleton) ---
    single {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // --- Service API Overpass ---
    single { OverpassService(get()) }

    // --- Repository : bridge entre API + Room ---
    single<LieuRepository> { RoomLieuRepository(get(), get()) }

    // --- ViewModel ---
    viewModel { LieuVM(get()) }
}

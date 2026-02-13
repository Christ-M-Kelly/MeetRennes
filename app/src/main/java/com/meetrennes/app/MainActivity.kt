package com.meetrennes.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.meetrennes.app.presentation.LieuVM
import com.meetrennes.app.ui.AppNavigation
import com.meetrennes.app.ui.theme.MeetRennesTheme
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MeetRennesTheme {
                // koinViewModel() → Koin crée et fournit le LieuVM
                val vm: LieuVM = koinViewModel()
                // Lance la navigation
                AppNavigation(vm)
            }
        }
    }
}


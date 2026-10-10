package com.techvipin130524.splitmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.techvipin130524.splitmate.core.navigation.AppNavHost
import com.techvipin130524.splitmate.feature.auth.presentation.AuthViewModel
import com.techvipin130524.splitmate.ui.theme.SplitMateTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplitMateTheme {
                val navController =
                    rememberNavController()

                val authViewModel: AuthViewModel =
                    hiltViewModel()

                AppNavHost(
                    navController = navController,
                    authViewModel = authViewModel
                )
            }
        }
    }
}

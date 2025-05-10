package com.emermelada.artcenter.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.emermelada.artcenter.MainActivity
import com.emermelada.artcenter.ui.navigation.AuthNavGraph
import com.emermelada.artcenter.ui.navigation.Destinations
import dagger.hilt.android.AndroidEntryPoint
import com.emermelada.artcenter.ui.theme.ArtCenterTheme

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {
    /**
     * Método que se ejecuta al crear la actividad.
     *
     * @param savedInstanceState Estado guardado de la actividad, si existe.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArtCenterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    val navController = rememberNavController()

                    val currentScreen = remember { mutableStateOf(Destinations.LOGIN) }

                    LaunchedEffect(navController) {
                        navController.addOnDestinationChangedListener { _, destination, _ ->
                            currentScreen.value = destination.route ?: Destinations.LOGIN
                        }
                    }
                    AuthNavGraph(
                        onClickLogIn = {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    },
                       navController,
                        onClickNav = { destination ->
                            navController.navigate(destination)
                        })
                }
            }
        }
    }}
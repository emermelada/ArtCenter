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

/**
 * Actividad que maneja la interfaz de autenticación de usuario.
 *
 * Se encarga de mostrar el flujo de pantallas de login y registro mediante navegación
 * Compose, y dirige al usuario a MainActivity tras un login exitoso.
 */
@AndroidEntryPoint
class AuthActivity : ComponentActivity() {

    /**
     * Configura el contenido de la actividad usando Jetpack Compose.
     *
     * Inicializa el tema de la aplicación, crea un controlador de navegación y actualiza el estado
     * de la pantalla actual al cambiar de destino. Al hacer login, inicia MainActivity y finaliza
     * la presente actividad.
     *
     * @param savedInstanceState Bundle con el estado previo de la actividad, si existe.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArtCenterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
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
                        navController = navController,
                        onClickNav = { destination ->
                            navController.navigate(destination)
                        }
                    )
                }
            }
        }
    }
}

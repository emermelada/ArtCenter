package com.emermelada.artcenter

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.emermelada.artcenter.data.repositories.PreferencesRepository
import com.emermelada.artcenter.ui.activities.AuthActivity
import com.emermelada.artcenter.ui.screens.MainScaffold
import com.emermelada.artcenter.ui.theme.ArtCenterTheme
import com.emermelada.artcenter.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Actividad principal de la aplicación ArtCenter.
 *
 * Muestra la interfaz principal si existe un token de sesión válido. En caso contrario,
 * redirige al usuario a la pantalla de autenticación.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * Repositorio de preferencias inyectado por Hilt, utilizado para almacenar y borrar datos de usuario.
     */
    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    /**
     * Método de ciclo de vida llamado cuando la actividad es creada.
     *
     * - Habilita el modo edge-to-edge para que la interfaz ocupe toda la pantalla.
     * - Configura el tema y la superficie de Compose.
     * - Verifica si existe un token de sesión:
     *   - Si existe, muestra el [MainScaffold] con la funcionalidad de cerrar sesión.
     *   - Si no existe, redirige al usuario a la actividad de autenticación ([AuthActivity]) y finaliza.
     *
     * @param savedInstanceState Estado previo de la actividad, si lo hubiera.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ArtCenterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (SessionManager.bearerToken != null) {
                        MainScaffold(onClickSignOut = {
                            lifecycleScope.launch {
                                preferencesRepository.clearUserData()
                                SessionManager.clearSession()
                                val intent = Intent(this@MainActivity, AuthActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        })
                    } else {
                        val intent = Intent(this, AuthActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }
}

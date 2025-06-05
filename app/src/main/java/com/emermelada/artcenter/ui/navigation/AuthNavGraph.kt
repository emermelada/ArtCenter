package com.emermelada.artcenter.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.emermelada.artcenter.ui.screens.auth.LoginScreen
import com.emermelada.artcenter.ui.screens.auth.RegisterScreen

/**
 * Grafo de navegación para las pantallas de autenticación (login y registro).
 *
 * @param onClickLogIn Lambda que se ejecuta cuando el usuario completa el login exitosamente,
 *                     permite navegar fuera del flujo de autenticación.
 * @param navController Controlador de navegación para gestionar las rutas de autenticación.
 * @param onClickNav Lambda que recibe una ruta como [String] y navega a dicha ruta dentro del flujo.
 */
@Composable
fun AuthNavGraph(
    onClickLogIn: () -> Unit,
    navController: NavHostController,
    onClickNav: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(navController = navController, startDestination = Destinations.LOGIN) {
            /**
             * Pantalla de registro de usuario.
             *
             * Navega a la ruta [Destinations.REGISTER].
             * @param onClickLogIn Lambda que se pasa a [RegisterScreen] para manejar login posterior al registro.
             * @param onClickNav Lambda para navegar dentro del flujo de autenticación.
             */
            composable(Destinations.REGISTER) {
                RegisterScreen(onClickLogIn = onClickLogIn, onClickNav = onClickNav)
            }
            /**
             * Pantalla de inicio de sesión de usuario.
             *
             * Navega a la ruta [Destinations.LOGIN].
             * @param onClickLogIn Lambda que se pasa a [LoginScreen] para manejar el login exitoso.
             * @param onClickNav Lambda para navegar dentro del flujo de autenticación.
             */
            composable(Destinations.LOGIN) {
                LoginScreen(onClickLogIn = onClickLogIn, onClickNav = onClickNav)
            }
        }
    }
}

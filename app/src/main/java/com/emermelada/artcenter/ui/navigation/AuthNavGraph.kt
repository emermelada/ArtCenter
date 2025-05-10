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

@Composable
fun AuthNavGraph(
    onClickLogIn: () -> Unit,
    navController: NavHostController,
    onClickNav: (String) -> Unit,

) {

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        NavHost(navController = navController, startDestination = Destinations.LOGIN) {
            composable(Destinations.REGISTER) {
                RegisterScreen(onClickLogIn = onClickLogIn, onClickNav = onClickNav)
            }
            composable(Destinations.LOGIN) {
                LoginScreen(onClickLogIn = onClickLogIn, onClickNav = onClickNav)
            }
        }
    }
}
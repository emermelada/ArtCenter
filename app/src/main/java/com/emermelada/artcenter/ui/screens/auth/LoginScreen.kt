package com.emermelada.artcenter.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emermelada.artcenter.R
import com.emermelada.artcenter.ui.UiState
import com.emermelada.artcenter.ui.components.auth.LogoArtCenter
import com.emermelada.artcenter.ui.components.auth.Title
import com.emermelada.artcenter.ui.navigation.Destinations
import com.emermelada.artcenter.ui.theme.LoraFontFamily

/**
 * Composable que muestra la pantalla de inicio de sesión.
 *
 * Presenta campos para introducir el correo electrónico y la contraseña, permite alternar la visibilidad
 * de la contraseña, valida la entrada y gestiona el flujo de autenticación mostrando un indicador de carga
 * o mensajes de error según el estado.
 *
 * @param onClickLogIn Lambda que se ejecuta cuando el usuario inicia sesión con éxito.
 * @param onClickNav Lambda que recibe la ruta de navegación al registrarse (por ejemplo, [Destinations.REGISTER]).
 * @param viewModel Instancia de [LoginViewModel] que contiene la lógica de autenticación y expone el estado UI.
 */
@Composable
fun LoginScreen(
    onClickLogIn: () -> Unit,
    onClickNav: (String) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val grayBackground = Color(0xFFD9D9D9)
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val loginResult by viewModel.loginResult.collectAsState()
    val uiStateLogin by viewModel.uiStatelogin.collectAsState()
    var intentoLogin by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(grayBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Title()
            LogoArtCenter()

            Column(
                modifier = Modifier
                    .background(Color(0xFFCCCCCC), shape = RoundedCornerShape(10.dp))
                    .border(2.dp, Color.Black, shape = RoundedCornerShape(10.dp))
                    .padding(12.dp)
                    .widthIn(max = 350.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                    label = {
                        Text(
                            stringResource(R.string.label_email),
                            fontSize = 15.sp,
                            color = Color.DarkGray
                        )
                    }
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                    label = {
                        Text(
                            stringResource(R.string.label_password),
                            fontSize = 15.sp,
                            color = Color.DarkGray
                        )
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (passwordVisible)
                            painterResource(R.drawable.visible_password)
                        else
                            painterResource(R.drawable.novisible_password)

                        Image(
                            painter = icon,
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)
                                .clickable { passwordVisible = !passwordVisible }
                        )
                    }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            fontSize = 13.sp,
                            maxLines = 2,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp)
                        )
                    }
                }

                Button(
                    onClick = {
                        when {
                            email.isBlank() || password.isBlank() -> {
                                errorMessage = "Por favor, completa todos los campos."
                            }
                            !isValidEmail(email) -> {
                                errorMessage = "Introduce un correo válido."
                            }
                            else -> {
                                errorMessage = ""
                                viewModel.login(email, password)
                                intentoLogin = true
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                ) {
                    Text(
                        text = stringResource(R.string.button_login),
                        color = Color.White,
                        fontFamily = LoraFontFamily
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.text_no_account),
                    color = Color.Black,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.text_register_here),
                    color = Color.Blue,
                    textDecoration = TextDecoration.Underline,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { onClickNav(Destinations.REGISTER) }
                )
            }

            if (intentoLogin) {
                when (uiStateLogin) {
                    is UiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                    is UiState.Success<*> -> {
                        loginResult?.let { isSuccess ->
                            if (isSuccess) {
                                onClickLogIn()
                            }
                        }
                    }
                    is UiState.Error -> {
                        intentoLogin = false
                        errorMessage = (uiStateLogin as UiState.Error).message
                    }
                    else -> { /* no-op */ }
                }
            }
        }
    }
}

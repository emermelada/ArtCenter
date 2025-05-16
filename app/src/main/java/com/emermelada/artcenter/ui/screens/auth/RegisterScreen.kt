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

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

@Composable
fun RegisterScreen(
    onClickLogIn: () -> Unit,
    onClickNav: (String) -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val grayBackground = Color(0xFFD9D9D9)
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var repeatPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var intentoRegistro by remember { mutableStateOf(false) }

    val registerResult by viewModel.registerResult.collectAsState()
    val uiStateRegister by viewModel.uiStateRegister.collectAsState()

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
                // Username
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                    label = { Text("Username", fontSize = 15.sp, color = Color.DarkGray) }
                )

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                    label = { Text("Correo", fontSize = 15.sp, color = Color.DarkGray) }
                )

                // Contraseña
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                    label = { Text("Contraseña", fontSize = 15.sp, color = Color.DarkGray) },
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

                // Repetir contraseña
                OutlinedTextField(
                    value = repeatPassword,
                    onValueChange = { repeatPassword = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                    label = { Text("Repetir contraseña", fontSize = 15.sp, color = Color.DarkGray) },
                    visualTransformation = if (repeatPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (repeatPasswordVisible)
                            painterResource(R.drawable.visible_password)
                        else
                            painterResource(R.drawable.novisible_password)

                        Image(
                            painter = icon,
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)
                                .clickable { repeatPasswordVisible = !repeatPasswordVisible }
                        )
                    }
                )

                // Espacio reservado para el error
                Box(modifier = Modifier.height(32.dp)) {
                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            fontSize = 13.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Botón registrar
                Button(
                    onClick = {
                        when {
                            username.isBlank() || email.isBlank() || password.isBlank() || repeatPassword.isBlank() -> {
                                errorMessage = "Por favor, completa todos los campos."
                            }
                            !isValidEmail(email) -> {
                                errorMessage = "Introduce un correo válido."
                            }
                            password != repeatPassword -> {
                                errorMessage = "Las contraseñas no coinciden."
                            }
                            else -> {
                                errorMessage = ""
                                viewModel.register(email, password, username)
                                intentoRegistro = true
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                ) {
                    Text("REGISTRAR", color = Color.White, fontFamily = LoraFontFamily )
                }
            }

            // Enlace para iniciar sesión
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "¿Ya tienes una cuenta? ", color = Color.Black, fontSize = 13.sp)
                Text(
                    text = "¡Inicia sesión aquí!",
                    color = Color.Blue,
                    textDecoration = TextDecoration.Underline,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { onClickNav(Destinations.LOGIN) }
                )
            }

            if (intentoRegistro) {
                when (uiStateRegister) {
                    is UiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                    is UiState.Success<*> -> {
                        registerResult?.let { isSuccess ->
                            if (isSuccess) {
                                onClickLogIn()
                            }
                        }
                    }
                    is UiState.Error -> {
                        intentoRegistro = false
                        errorMessage = (uiStateRegister as UiState.Error).message
                    }
                    else -> {  }
                }
            }
        }
    }
}

package com.emermelada.artcenter.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emermelada.artcenter.data.model.profile.User
import com.emermelada.artcenter.ui.UiState

@Composable
fun ProfileScreen(
    onClickNav: (String) -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val userInfoState by profileViewModel.userInfoState.collectAsState()
    var isEditing by remember { mutableStateOf(false) }  // Estado para controlar el modo de edición
    var newUsername by remember { mutableStateOf("") }  // Estado para el nuevo nombre de usuario
    var username by remember { mutableStateOf("") }  // Estado para el nombre de usuario actual

    // Cuando la información del usuario se obtiene, actualizamos el estado inicial del username
    LaunchedEffect(userInfoState) {
        if (userInfoState is UiState.Success<*>) {
            val user = (userInfoState as UiState.Success<User>).data
            username = user.username
            newUsername = user.username  // Llenamos el nuevo nombre con el actual
        }
    }

    // Cargar la información del usuario si no se ha cargado aún
    LaunchedEffect(Unit) {
        profileViewModel.fetchUserInfo()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (userInfoState) {
            is UiState.Loading -> {
                CircularProgressIndicator()
            }

            is UiState.Error -> {
                Text(
                    text = (userInfoState as UiState.Error).message,
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }

            is UiState.Success<*> -> {
                val user = (userInfoState as UiState.Success<User>).data

                // Imagen de perfil (con un icono por defecto si es nulo)
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color.LightGray, shape = MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PhotoCamera,
                        contentDescription = "Editar foto",
                        modifier = Modifier
                            .clickable { /* Lógica para cambiar la foto de perfil */ },
                        tint = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Nombre de usuario con icono para editar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isEditing) {
                        // Campo de texto editable
                        OutlinedTextField(
                            value = newUsername,
                            onValueChange = { newUsername = it },
                            label = { Text("Nuevo Nombre de Usuario", color = Color.DarkGray) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(0.7f),
                            textStyle = TextStyle(color = Color.DarkGray)
                        )
                        // Botón de confirmación (tick)
                        IconButton(
                            onClick = {
                                if (newUsername.isNotBlank()) {
                                    profileViewModel.updateUsername(newUsername)
                                    username = newUsername  // Actualizamos el nombre localmente en la UI
                                    isEditing = false  // Finalizamos la edición
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Confirmar nombre",
                                tint = Color.Green
                            )
                        }
                    } else {
                        // Mostrar el nombre de usuario y el botón de editar
                        Text(
                            text = username,  // Mostrar el nombre actualizado localmente
                            fontSize = 22.sp,
                            fontFamily = FontFamily.SansSerif,
                            color = Color.Black
                        )
                        IconButton(
                            onClick = {
                                isEditing = true  // Activamos el modo de edición
                                newUsername = username  // Rellenamos el campo de texto con el nombre actual
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Editar nombre de usuario"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botones para otras acciones (publicaciones y guardados)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {}) {
                        Text(text = "Tus publicaciones")
                    }
                    Button(onClick = {}) {
                        Text(text = "Guardados")
                    }
                }
            }
        }
    }
}


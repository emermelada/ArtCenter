package com.emermelada.artcenter.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.core.net.toFile
import coil.compose.rememberImagePainter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import android.content.Context
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.emermelada.artcenter.ui.theme.LoraFontFamily

@Composable
fun ProfileScreen(
    onClickNav: (String) -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val userInfoState by profileViewModel.userInfoState.collectAsState()
    val updateState by profileViewModel.updateState.collectAsState() // Obtener el estado de la actualización
    var isEditing by remember { mutableStateOf(false) }  // Estado para controlar el modo de edición
    var newUsername by remember { mutableStateOf("") }  // Estado para el nuevo nombre de usuario
    var username by remember { mutableStateOf("") }  // Estado para el nombre de usuario actual
    var profileImageUrl by remember { mutableStateOf<String?>(null) }  // Actualización a mutableStateOf para que sea reactivo

    // Lógica para seleccionar la imagen de la galería
    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { selectedUri ->
            try {
                // Crear un archivo temporal para almacenar la imagen seleccionada
                val inputStream = context.contentResolver.openInputStream(selectedUri)
                val tempFile = File(context.cacheDir, "profile_temp.jpg")
                val outputStream = FileOutputStream(tempFile)

                // Copiar el contenido de la imagen a un archivo temporal
                inputStream?.copyTo(outputStream)

                // Subir la imagen al backend utilizando MultipartBody
                val filePart = MultipartBody.Part.createFormData(
                    "file",
                    tempFile.name,
                    tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                )

                // Llamada al ViewModel para actualizar la foto de perfil
                profileViewModel.updateProfilePicture(filePart)

            } catch (e: Exception) {
                Log.e("ProfileScreen", "Error al manejar la URI de la imagen", e)
            }
        }
    }

    // Cuando la información del usuario se obtiene, actualizamos el estado inicial del username
    LaunchedEffect(userInfoState) {
        if (userInfoState is UiState.Success<*>) {
            val user = (userInfoState as UiState.Success<User>).data
            username = user.username
            newUsername = user.username  // Llenamos el nuevo nombre con el actual
            profileImageUrl = user.urlFotoPerfil  // Actualizamos la URL de la foto de perfil
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
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .background(Color.LightGray, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    // Si la URL de la foto de perfil está disponible, la mostramos
                    profileImageUrl?.let {
                        Image(
                            painter = rememberImagePainter(it),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .clickable { pickImage.launch("image/*") }
                                .size(160.dp)
                                .clip(CircleShape) // Recorta la imagen a un círculo
                                .fillMaxSize(), // Esto asegura que ocupe todo el tamaño del Box
                            contentScale = ContentScale.Crop // Recorta la imagen sin distorsionarla
                        )
                    } ?: run {
                        // Si no hay URL, mostramos el icono de la cámara
                        Icon(
                            imageVector = Icons.Filled.PhotoCamera,
                            contentDescription = "Editar foto",
                            modifier = Modifier
                                .clickable { pickImage.launch("image/*") }
                                .size(160.dp),
                            tint = Color.Black
                        )
                    }

                    // Contenedor adicional para crear espacio alrededor del círculo pequeño
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd) // Coloca el círculo en la parte inferior derecha
                            .padding(8.dp) // Espaciado adicional alrededor del círculo pequeño
                    ) {
                        // Círculo pequeño con el icono de la cámara en la esquina inferior derecha
                        Box(
                            modifier = Modifier
                                .size(30.dp) // Tamaño más pequeño del círculo
                                .background(Color.White, shape = CircleShape) // Fondo blanco
                                .border(2.dp, Color.Black, shape = CircleShape) // Borde negro y forma circular
                                .clip(CircleShape) // Recorta el Box a un círculo
                                .padding(4.dp) // Padding alrededor del círculo
                        ) {
                            // Agregar un espaciado extra dentro del círculo para que el icono de la cámara no esté tan pegado
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Editar foto",
                                modifier = Modifier
                                    .fillMaxSize(), // Asegura que el icono se ajuste al tamaño del círculo
                                tint = Color.Black
                            )
                        }
                    }
                }




                // Mostrar el mensaje de éxito debajo de la foto de perfil
                when (updateState) {
                    is UiState.Success<*> -> {
                        Text(
                            text = (updateState as UiState.Success<String>).data,
                            color = Color.Green,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    is UiState.Error -> {
                        Text(
                            text = (updateState as UiState.Error).message,
                            color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    else -> {}
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
                        Row {
                            Text(
                                text = "Nombre de usuario: ", // "Nombre de usuario:"
                                fontSize = 18.sp,
                                fontFamily = LoraFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = username, // El nombre de usuario
                                fontSize = 18.sp,
                                fontFamily = LoraFontFamily,
                                color = Color.Black
                            )
                        }
                        IconButton(
                            onClick = {
                                isEditing = true  // Activamos el modo de edición
                                newUsername = username  // Rellenamos el campo de texto con el nombre actual
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Editar nombre de usuario",
                                tint = Color.Black
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
                        Text(
                            text = "Tus publicaciones",
                            fontFamily = LoraFontFamily
                        )
                    }
                    Button(onClick = {}) {
                        Text(
                            text = "Guardados",
                            fontFamily = LoraFontFamily
                        )
                    }
                }
            }

            else -> {}
        }
    }
}



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
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.emermelada.artcenter.data.model.publications.PublicationSimple
import com.emermelada.artcenter.ui.components.publication.PublicationItem
import com.emermelada.artcenter.ui.theme.LoraFontFamily
import com.emermelada.artcenter.ui.theme.MutedBlue
import kotlinx.coroutines.flow.collectLatest

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

    // para tabs de publicaciones
    val myPubs by profileViewModel.myPublications.collectAsState()
    val savedPubs by profileViewModel.savedPublications.collectAsState()
    val isLoadingPubs by profileViewModel.isLoadingPublications.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

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

    // cuando cambie updateState y haya un mensaje, programa que desaparezca a los 3s
    LaunchedEffect(updateState) {
        if (updateState is UiState.Success<*> || updateState is UiState.Error) {
            kotlinx.coroutines.delay(3000)        // import kotlinx.coroutines.delay
            profileViewModel.clearUpdateState()  // vuelve a Loading, así no se muestra nada
        }
    }

    // load user and publications
    LaunchedEffect(Unit) {
        profileViewModel.fetchUserInfo()
        profileViewModel.loadMyPublications()
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
            .padding(0.dp),  // Quitamos el padding para que ocupe todo el espacio disponible
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
                // Contenedor para la parte superior con fondo oscuro
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFD3D3D3))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column{
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // --- FOTO A LA IZQUIERDA ---
                            Box(
                                modifier = Modifier
                                    .size(160.dp)
                                    .background(Color.LightGray, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                profileImageUrl?.let {
                                    Image(
                                        painter = rememberImagePainter(it),
                                        contentDescription = "Foto de perfil",
                                        modifier = Modifier
                                            .size(160.dp)
                                            .clip(CircleShape)
                                            .fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } ?: run {
                                    Icon(
                                        imageVector = Icons.Filled.PhotoCamera,
                                        contentDescription = "Editar foto",
                                        modifier = Modifier
                                            .clickable { pickImage.launch("image/*") }
                                            .size(160.dp),
                                        tint = Color.Black
                                    )
                                }
                                // Ícono de editar foto
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .background(Color.White, shape = CircleShape)
                                            .border(2.dp, Color.Black, shape = CircleShape)
                                            .clip(CircleShape)
                                            .padding(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Edit,
                                            contentDescription = "Editar foto",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clickable { pickImage.launch("image/*") },
                                            tint = Color.Black
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(24.dp))

                            // --- CONTENIDO A LA DERECHA ---
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier.weight(1f)
                            ) {
                                // Nombre de usuario y edición
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isEditing) {
                                        OutlinedTextField(
                                            value = newUsername,
                                            onValueChange = { newUsername = it },
                                            label = { Text("Nuevo Nombre de Usuario", color = Color.DarkGray) },
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth(0.7f),
                                            textStyle = TextStyle(color = Color.DarkGray)
                                        )
                                        IconButton(
                                            onClick = {
                                                if (newUsername.isNotBlank()) {
                                                    profileViewModel.updateUsername(newUsername)
                                                    username = newUsername
                                                    isEditing = false
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
                                        Column{
                                            Text(
                                                text = "Nombre de usuario: ",
                                                fontSize = 18.sp,
                                                fontFamily = LoraFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )
                                            Row(
                                                modifier = Modifier.padding(start = 10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ){
                                                Text(
                                                    text = username,
                                                    fontSize = 18.sp,
                                                    fontFamily = LoraFontFamily,
                                                    color = Color.Black
                                                )
                                                IconButton(
                                                    onClick = {
                                                        isEditing = true
                                                        newUsername = username
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
                                    }
                                }

                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth()){
                            // Mensaje de éxito o error
                            when (updateState) {
                                is UiState.Success<*> -> {
                                    Text(
                                        text = (updateState as UiState.Success<String>).data,
                                        color = Color.Green,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                                is UiState.Error -> {
                                    Text(
                                        text = (updateState as UiState.Error).message,
                                        color = Color.Red,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                                else -> {}
                            }
                        }
                    }
                }

                HorizontalDivider(
                    thickness = 2.dp,
                    color = Color.DarkGray,
                    modifier = Modifier.shadow(
                        elevation = 2.dp, // Ajusta la intensidad de la sombra
                        shape = RoundedCornerShape(4.dp),
                        clip = false
                    )
                )

                // --- TABS: Tus publicaciones / Guardados ---
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            selectedTab = 0
                            if (myPubs.isEmpty()) profileViewModel.loadMyPublications()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTab == 0) MutedBlue else Color.LightGray
                        )
                    ) {
                        Text("Tus publicaciones", fontFamily = LoraFontFamily)
                    }
                    Button(
                        onClick = {
                            selectedTab = 1
                            if (savedPubs.isEmpty()) profileViewModel.loadSavedPublications()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTab == 1) MutedBlue else Color.LightGray
                        )
                    ) {
                        Text("Guardados", fontFamily = LoraFontFamily)
                    }
                }

                // --- GRID: publicaciones según pestaña ---
                Spacer(Modifier.height(8.dp))
                val gridState = rememberLazyStaggeredGridState()
                LaunchedEffect(gridState, selectedTab) {
                    snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                        .collectLatest { lastIndex ->
                            val listSize = if (selectedTab == 0) myPubs.size else savedPubs.size
                            if (lastIndex == listSize - 1 && !isLoadingPubs) {
                                if (selectedTab == 0) profileViewModel.loadMyPublications()
                                else profileViewModel.loadSavedPublications()
                            }
                        }
                }

                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    state = gridState,
                    contentPadding = PaddingValues(8.dp),
                    verticalItemSpacing = 8.dp,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    val listToShow: List<PublicationSimple> =
                        if (selectedTab == 0) myPubs else savedPubs

                    items(listToShow) { pub ->
                        PublicationItem(
                            publication = pub,
                            userRole = "user",
                            onClickNav = onClickNav,
                            onSave = { profileViewModel.toggleSave(pub) },
                            onLike = { profileViewModel.toggleLike(pub) },
                            onDelete = {}
                        )
                    }
                    item(span = StaggeredGridItemSpan.FullLine) {
                        if (isLoadingPubs) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }

            else -> {}
        }
    }
}

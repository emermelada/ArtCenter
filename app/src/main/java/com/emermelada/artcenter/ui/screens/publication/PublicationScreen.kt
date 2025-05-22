package com.emermelada.artcenter.ui.screens.publication

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.emermelada.artcenter.data.model.tags.Tag
import com.emermelada.artcenter.ui.UiState
import com.emermelada.artcenter.ui.navigation.Destinations
import java.io.File
import java.io.FileOutputStream

@Composable
fun PublicationScreen(
    onClickNav: (String) -> Unit,
    viewModel: PublicationViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val tagsState by viewModel.tagsState.collectAsState()
    val uploadState by viewModel.uploadState.collectAsState()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedImageFile by remember { mutableStateOf<File?>(null) }
    var descripcion by remember { mutableStateOf("") }

    var expandedTags by remember { mutableStateOf(false) }
    var selectedTagName by remember { mutableStateOf<String?>(null) }

    // Selector de imagen usando ActivityResultLauncher
    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            // Guardar localmente para subir después
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val tempFile = File(context.cacheDir, "publication_temp.jpg")
                val outputStream = FileOutputStream(tempFile)
                inputStream?.copyTo(outputStream)
                selectedImageFile = tempFile
            } catch (e: Exception) {
                selectedImageFile = null
            }
        }
    }

    // Cargar etiquetas cuando la pantalla se inicia
    LaunchedEffect(Unit) {
        viewModel.fetchTags()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Crear Publicación",
                fontSize = 20.sp,
                color = Color.DarkGray
            )

            IconButton(
                onClick = { onClickNav(Destinations.FEED) },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.DarkGray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar imagen seleccionada o botón para seleccionar
        Box(
            modifier = Modifier
                .size(180.dp)
                .clickable { pickImage.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(selectedImageUri),
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = "Selecciona una imagen",
                    tint = Color.Gray,
                    modifier = Modifier.size(60.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Descripción
        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            textStyle = TextStyle(color = Color.DarkGray),
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Selector de etiqueta (opcional)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.TopStart)
        ) {
            Text(
                text = selectedTagName ?: "Selecciona una etiqueta (opcional)",
                modifier = Modifier
                    .clickable { expandedTags = !expandedTags }
                    .padding(12.dp),
                color = Color.DarkGray
            )
            DropdownMenu(
                expanded = expandedTags,
                onDismissRequest = { expandedTags = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                DropdownMenuItem(
                    text = { Text("Ninguna") },
                    onClick = {
                        selectedTagName = null
                        viewModel.selectedTagId = null
                        expandedTags = false
                    }
                )
                when (tagsState) {
                    is UiState.Success<*> -> {
                        val tags = (tagsState as UiState.Success<List<Tag>>).data
                        tags.forEach { tag ->
                            DropdownMenuItem(
                                text = { Text(tag.nombre) },
                                onClick = {
                                    selectedTagName = tag.nombre
                                    viewModel.selectedTagId = tag.id
                                    expandedTags = false
                                }
                            )
                        }
                    }
                    is UiState.Loading -> {
                        DropdownMenuItem(text = { Text("Cargando etiquetas...") }, onClick = {})
                    }
                    is UiState.Error -> {
                        DropdownMenuItem(text = { Text("Error cargando etiquetas") }, onClick = {})
                    }
                    else -> {}
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (selectedImageFile != null) {
                    viewModel.uploadPublication(selectedImageFile!!, descripcion)
                }
            },
            enabled = selectedImageFile != null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Crear publicación")
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (uploadState) {
            is UiState.Loading -> {
                CircularProgressIndicator()
            }
            is UiState.Success<*> -> {
                Text(
                    text = (uploadState as UiState.Success<String>).data,
                    color = Color(0xFF4CAF50),
                    fontSize = 14.sp
                )
            }
            is UiState.Error -> {
                Text(
                    text = (uploadState as UiState.Error).message,
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }
            else -> {}
        }
    }
}

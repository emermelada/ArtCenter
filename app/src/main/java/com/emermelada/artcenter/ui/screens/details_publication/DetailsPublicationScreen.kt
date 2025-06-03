// DetailsPublicationScreen.kt
package com.emermelada.artcenter.ui.screens.details_publication

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.emermelada.artcenter.data.model.comments.CommentSimple
import com.emermelada.artcenter.data.model.publications.Publication
import com.emermelada.artcenter.ui.UiState
import com.emermelada.artcenter.ui.theme.DarkBlue
import com.emermelada.artcenter.ui.theme.LightGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsPublicationScreen(
    idPublicacion: Int,
    onClickNav: (String) -> Unit,
    viewModel: DetailsPublicationViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // 1) Recolectamos los StateFlow del ViewModel
    val publicationState by viewModel.publicationState.collectAsState()
    val commentsState by viewModel.commentsState.collectAsState()
    val addCommentState by viewModel.addCommentState.collectAsState()

    // Estados de like, bookmark y contador
    val isLiked by viewModel.isLiked.collectAsState()
    val isSaved by viewModel.isSaved.collectAsState()
    val likesCount by viewModel.likesCount.collectAsState()

    var newCommentText by remember { mutableStateOf("") }
    var showComments by remember { mutableStateOf(false) }

    // Cargar datos al iniciar/componer
    LaunchedEffect(idPublicacion) {
        viewModel.loadPublication(idPublicacion)
        viewModel.loadComments(idPublicacion)
    }

    LaunchedEffect(addCommentState) {
        if (addCommentState is UiState.Success<*>) {
            newCommentText = ""
            viewModel.loadComments(idPublicacion)
        } else if (addCommentState is UiState.Error) {
            val msg = (addCommentState as UiState.Error).message
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Encabezado con texto "Detalles" y botón de retroceso
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Detalles",
                fontSize = 20.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.SemiBold
            )

            IconButton(
                onClick = { onClickNav("FEED") },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.DarkGray
                )
            }
        }
        // Card principal con imagen y detalles
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                // Cabecera: imagen de la publicación
                when (publicationState) {
                    is UiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = DarkBlue
                            )
                        }
                    }
                    is UiState.Error -> {
                        val msg = (publicationState as UiState.Error).message
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = msg,
                                color = Color.Red,
                                fontSize = 14.sp
                            )
                        }
                    }
                    is UiState.Success<*> -> {
                        val publication = (publicationState as UiState.Success<Publication>).data
                        PublicationHeader(publication = publication)
                    }
                    else -> { /* Idle */ }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Detalles (descripción, hashtag y fecha) justo debajo de la imagen
                if (publicationState is UiState.Success<*>) {
                    val publication = (publicationState as UiState.Success<Publication>).data
                    PublicationDetails(publication = publication)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botones de Like, Guardar y Comentarios (separados de la Card grande)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like: icono dinámico basado en isLiked y likesCount
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { viewModel.toggleLike(idPublicacion) }
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked) Color.Red else DarkBlue,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = likesCount.toString(),
                        color = Color.DarkGray,
                        fontSize = 14.sp
                    )
                }

                // Guardar: icono dinámico basado en isSaved
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { viewModel.toggleBookmark(idPublicacion) }
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        contentDescription = "Guardar",
                        tint = DarkBlue,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isSaved) "Guardado" else "Guardar",
                        color = Color.DarkGray,
                        fontSize = 14.sp
                    )
                }

                // Comentarios: solo icono
                IconButton(onClick = { showComments = true }) {
                    Icon(
                        imageVector = Icons.Filled.Comment,
                        contentDescription = "Comentarios",
                        tint = DarkBlue,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }

    // Overlay de comentarios (ocupa 3/4 de la pantalla desde abajo)
    if (showComments) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x88000000))
                .clickable { showComments = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.75f)
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                // Tirador superior
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(Color.LightGray, RoundedCornerShape(2.dp))
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Comentarios",
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Lista de comentarios
                when (commentsState) {
                    is UiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = DarkBlue)
                        }
                    }
                    is UiState.Error -> {
                        val msg = (commentsState as UiState.Error).message
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = msg,
                                color = Color.Red,
                                fontSize = 14.sp
                            )
                        }
                    }
                    is UiState.Success<*> -> {
                        val comments = (commentsState as UiState.Success<List<CommentSimple>>).data
                        if (comments.isEmpty()) {
                            Text(
                                text = "Sé el primero en comentar.",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                items(comments) { comment ->
                                    CommentItem(comment)
                                    Divider(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        color = LightGray,
                                        thickness = 0.5.dp
                                    )
                                }
                            }
                        }
                    }
                    else -> { /* Idle */ }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Campo para añadir comentario
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newCommentText,
                        onValueChange = { newCommentText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                text = "Escribe un comentario...",
                                fontSize = 14.sp,
                                color = Color.DarkGray
                            )
                        },
                        textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
                        maxLines = 3,
                        enabled = addCommentState !is UiState.Loading
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (newCommentText.isNotBlank()) {
                                viewModel.addComment(idPublicacion, newCommentText.trim())
                            }
                        },
                        enabled = newCommentText.isNotBlank() && addCommentState !is UiState.Loading
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Send,
                            contentDescription = "Enviar Comentario",
                            tint = DarkBlue
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PublicationHeader(
    publication: Publication
) {
    Image(
        painter = rememberAsyncImagePainter(publication.urlContenido),
        contentDescription = "Contenido Publicación",
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp)
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun PublicationDetails(
    publication: Publication,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        publication.descripcion?.let { desc ->
            Text(
                text = desc,
                style = TextStyle(fontSize = 16.sp, color = Color.DarkGray),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            publication.etiqueta?.let { tag ->
                Text(
                    text = "#$tag",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = DarkBlue,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
            Text(
                text = publication.fecha_publicacion,
                style = TextStyle(fontSize = 12.sp, color = Color.Gray)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun CommentItem(comment: CommentSimple) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = comment.username +": ",
                style = TextStyle(fontSize = 14.sp, color = DarkBlue,fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = " " + comment.contenido,
                style = TextStyle(fontSize = 14.sp, color = Color.DarkGray)
            )
        }
        Text(
            text = comment.fecha_publicacion,
            style = TextStyle(fontSize = 12.sp, color = Color.Gray),
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

package com.emermelada.artcenter.ui.screens.details_publication

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.emermelada.artcenter.data.model.comments.CommentSimple
import com.emermelada.artcenter.data.model.publications.Publication
import com.emermelada.artcenter.ui.UiState
import com.emermelada.artcenter.ui.components.shared.DeleteDialog
import com.emermelada.artcenter.ui.screens.MainScaffoldViewModel
import com.emermelada.artcenter.ui.theme.DarkBlue
import com.emermelada.artcenter.ui.theme.LightGray

@OptIn(ExperimentalMaterial3Api::class)
/**
 * Muestra los detalles de una publicación, permitiendo ver su contenido,
 * marcar como favorito, guardar, y gestionar comentarios.
 *
 * Para usuarios normales, muestra botones de Me gusta, Guardar y Comentarios.
 * Para administradores, solo muestra el acceso a comentarios.
 * Incluye diálogo de confirmación para eliminar comentarios.
 *
 * @param idPublicacion Identificador de la publicación a cargar.
 * @param onClickNav Lambda que maneja la navegación a otras rutas.
 * @param mainScaffoldViewModel ViewModel que proporciona información de usuario.
 * @param viewModel ViewModel que gestiona la lógica de detalle de publicación y comentarios.
 */
@Composable
fun DetailsPublicationScreen(
    idPublicacion: Int,
    onClickNav: (String) -> Unit,
    mainScaffoldViewModel: MainScaffoldViewModel = hiltViewModel(),
    viewModel: DetailsPublicationViewModel = hiltViewModel()
) {
    val userId by mainScaffoldViewModel.userId.collectAsState()
    val userIdInt = userId.toIntOrNull() ?: -1
    val userRole by mainScaffoldViewModel.userRol.collectAsState()

    val publicationState by viewModel.publicationState.collectAsState()
    val commentsState by viewModel.commentsState.collectAsState()
    val addCommentState by viewModel.addCommentState.collectAsState()

    val isLiked by viewModel.isLiked.collectAsState()
    val isSaved by viewModel.isSaved.collectAsState()
    val likesCount by viewModel.likesCount.collectAsState()

    var newCommentText by remember { mutableStateOf("") }
    var showComments by remember { mutableStateOf(false) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var commentToDelete: CommentSimple? by remember { mutableStateOf(null) }

    LaunchedEffect(addCommentState) {
        if (addCommentState is UiState.Success<*>) {
            newCommentText = ""
            viewModel.loadComments(idPublicacion)
        }
    }

    LaunchedEffect(idPublicacion) {
        viewModel.loadPublication(idPublicacion)
        viewModel.loadComments(idPublicacion)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
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
                when (publicationState) {
                    is UiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = DarkBlue)
                        }
                    }
                    is UiState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (publicationState as UiState.Error).message,
                                color = Color.Red,
                                fontSize = 14.sp
                            )
                        }
                    }
                    is UiState.Success<*> -> {
                        val publication = (publicationState as UiState.Success<Publication>).data
                        PublicationHeader(publication = publication)
                    }
                    else -> { }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (publicationState is UiState.Success<*>) {
                    val publication = (publicationState as UiState.Success<Publication>).data
                    PublicationDetails(publication = publication)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

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
                if (userRole == "admin") {
                    IconButton(onClick = { showComments = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Comment,
                            contentDescription = "Comentarios",
                            tint = DarkBlue,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                } else {
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

                    IconButton(onClick = { showComments = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Comment,
                            contentDescription = "Comentarios",
                            tint = DarkBlue,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }

    if (showComments) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        LaunchedEffect(showComments) {
            if (showComments) sheetState.show()
        }

        ModalBottomSheet(
            onDismissRequest = { showComments = false },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            tonalElevation = 8.dp,
            containerColor = Color.White,
            scrimColor = Color(0x88000000)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f)
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Comentarios",
                    fontSize = 18.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))

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
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (commentsState as UiState.Error).message,
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
                                    CommentItem(
                                        comment = comment,
                                        userRole = userRole,
                                        onDelete = {
                                            commentToDelete = comment
                                            showDeleteDialog = true
                                        },
                                        userId = userIdInt
                                    )
                                    HorizontalDivider(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        thickness = 0.5.dp,
                                        color = LightGray
                                    )
                                }
                            }
                        }
                    }
                    else -> { }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (userRole != "admin") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Enviar Comentario",
                                tint = DarkBlue
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog && commentToDelete != null) {
        DeleteDialog(
            text = "¿Estás seguro de que quieres eliminar este comentario?",
            onConfirm = {
                commentToDelete?.let { viewModel.deleteComment(it.id, idPublicacion) }
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

/**
 * Muestra la imagen principal de una publicación.
 *
 * @param publication Datos de la publicación a mostrar.
 */
@Composable
fun PublicationHeader(publication: Publication) {
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

/**
 * Muestra la descripción, etiqueta y fecha de publicación.
 *
 * @param publication Datos de la publicación a mostrar.
 */
@Composable
fun PublicationDetails(publication: Publication) {
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
                fontSize = 16.sp,
                color = Color.DarkGray,
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
                    fontSize = 14.sp,
                    color = DarkBlue,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = publication.fecha_publicacion,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

/**
 * Muestra un comentario individual con posibilidad de eliminación.
 *
 * @param comment Datos del comentario a mostrar.
 * @param userRole Rol del usuario actual.
 * @param onDelete Lambda que se ejecuta al solicitar eliminación.
 * @param userId Identificador del usuario actual.
 */
@Composable
fun CommentItem(
    comment: CommentSimple,
    userRole: String,
    onDelete: () -> Unit,
    userId: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${comment.username}: ",
                fontSize = 14.sp,
                color = DarkBlue,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = comment.contenido,
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }
        Text(
            text = comment.fecha_publicacion,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = 8.dp)
        )
        if (comment.id_usuario == userId || userRole == "admin") {
            IconButton(onClick = { onDelete() }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Eliminar comentario",
                    tint = Color.Gray
                )
            }
        }
    }
}

package com.emermelada.artcenter.ui.components.publication

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.emermelada.artcenter.data.model.publications.PublicationSimple
import com.emermelada.artcenter.ui.navigation.Destinations
import com.emermelada.artcenter.ui.theme.DarkBlue
import com.emermelada.artcenter.ui.theme.LoraFontFamily
import com.emermelada.artcenter.ui.theme.MutedBlue
import java.util.Locale

@Composable
fun PublicationItem(
    publication: PublicationSimple,
    userRole: String,
    isOwner: Boolean,
    onClickNav: (String) -> Unit,
    onSave: () -> Unit,
    onLike: () -> Unit,
    onDelete: () -> Unit
) {
    val label = publication.nombre_etiqueta
        .orEmpty()
        .uppercase(Locale.getDefault())

    var menuExpanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        // Etiqueta encima de la Card
        if (label.isNotBlank()) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = LoraFontFamily,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .background(color = MutedBlue, shape = RoundedCornerShape(4.dp))
                    .border(BorderStroke(1.dp, DarkBlue), shape = RoundedCornerShape(4.dp))
                    .clickable {
                        // navegación basada en categoría/subcategoría
                        val dest = if (publication.id_subcategoria == null)
                            "${Destinations.SUBCATEGORIES}/${publication.id_categoria}"
                        else
                            "${Destinations.SUBCATEGORY}/${publication.id_categoria}/${publication.id_subcategoria}"
                        onClickNav(dest)
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(2.dp, DarkBlue), shape = RoundedCornerShape(8.dp))
                .clickable {
                    onClickNav("${Destinations.DETAILS_PUBLICATION}/${publication.id}")
                },
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box {
                AsyncImage(
                    model = publication.urlContenido,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    contentScale = ContentScale.Fit
                )

                // Tres puntitos + DropdownMenu
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    // Icono “dots” hecho con 3 círculos
                    Column(
                        modifier = Modifier
                            .clickable { menuExpanded = true },
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        repeat(3) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(color = Color.White, shape = CircleShape)
                                    .border(BorderStroke(1.dp, DarkBlue), shape = CircleShape)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        // Si no es admin y no es dueño
                        if (userRole != "admin" && !isOwner) {
                            // Toggle guardar
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        if (publication.saved) "Guardado" else "Guardar",
                                        color = Color.White
                                    )
                                },
                                onClick = {
                                    onSave()
                                    menuExpanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (publication.saved)
                                            Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                            )
                            // Toggle like
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        if (publication.liked) "Quitar like" else "Me gusta",
                                        color = Color.White
                                    )
                                },
                                onClick = {
                                    onLike()
                                    menuExpanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (publication.liked)
                                            Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                            )
                        } else if(userRole != "admin" ){
                            // Toggle guardar
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        if (publication.saved) "Guardado" else "Guardar",
                                        color = Color.White
                                    )
                                },
                                onClick = {
                                    onSave()
                                    menuExpanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (publication.saved)
                                            Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                            )
                            // Toggle like
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        if (publication.liked) "Quitar like" else "Me gusta",
                                        color = Color.White
                                    )
                                },
                                onClick = {
                                    onLike()
                                    menuExpanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (publication.liked)
                                            Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Eliminar", color = Color.White) },
                                onClick = {
                                    showDeleteDialog = true
                                    menuExpanded = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
                                }
                            )
                        }
                        else {
                            DropdownMenuItem(
                                text = { Text("Eliminar", color = Color.White) },
                                onClick = {
                                    showDeleteDialog = true
                                    menuExpanded = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Mostrar el DeleteDialog cuando sea necesario
        if (showDeleteDialog) {
            DeleteDialog(
                text = "¿Estás seguro de que deseas eliminar esta publicación?",
                onConfirm = {
                    onDelete()
                    showDeleteDialog = false
                },
                onDismiss = { showDeleteDialog = false }
            )
        }
    }
}

@Composable
fun DeleteDialog(
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Confirmar eliminación", color = Color.White) },
        text = { Text(text) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Sí")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}

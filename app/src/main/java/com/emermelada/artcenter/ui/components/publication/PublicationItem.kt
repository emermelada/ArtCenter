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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.emermelada.artcenter.R
import com.emermelada.artcenter.data.model.publications.PublicationSimple
import com.emermelada.artcenter.ui.components.shared.DeleteDialog
import com.emermelada.artcenter.ui.navigation.Destinations
import com.emermelada.artcenter.ui.theme.DarkBlue
import com.emermelada.artcenter.ui.theme.LoraFontFamily
import com.emermelada.artcenter.ui.theme.MutedBlue
import java.util.Locale

/**
 * Composable que muestra un elemento de publicación con imagen, etiqueta, y opciones de interacción.
 *
 * @param publication Objeto [PublicationSimple] que contiene los datos básicos de la publicación.
 * @param userRole Rol del usuario actual, utilizado para determinar opciones disponibles en el menú.
 * @param isOwner Booleano que indica si el usuario actual es el dueño de la publicación.
 * @param onClickNav Lambda que recibe la ruta de navegación como [String] para navegar a otra pantalla.
 * @param onSave Lambda que se ejecuta cuando el usuario selecciona la opción de guardar o desguardar la publicación.
 * @param onLike Lambda que se ejecuta cuando el usuario selecciona la opción de dar o quitar "like" a la publicación.
 * @param onDelete Lambda que se ejecuta cuando el usuario confirma la eliminación de la publicación.
 */
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

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.clickable { menuExpanded = true },
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
                        onDismissRequest = { menuExpanded = false },
                        containerColor = DarkBlue
                    ) {
                        if (userRole != "admin" && !isOwner) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        if (publication.saved) stringResource(R.string.guardado) else stringResource(R.string.guardar),
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
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        if (publication.liked) stringResource(R.string.quitarLike) else stringResource(R.string.like),
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
                        } else if (userRole != "admin") {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        if (publication.saved) stringResource(R.string.guardado) else stringResource(R.string.guardar),
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
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        if (publication.liked) stringResource(R.string.quitarLike) else stringResource(R.string.like),
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
                                text = { Text(stringResource(R.string.eliminar), color = Color.White) },
                                onClick = {
                                    showDeleteDialog = true
                                    menuExpanded = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
                                }
                            )
                        } else {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.eliminar), color = Color.White) },
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

        if (showDeleteDialog) {
            DeleteDialog(
                text = stringResource(R.string.confirmarElimPublicacion),
                onConfirm = {
                    onDelete()
                    showDeleteDialog = false
                },
                onDismiss = { showDeleteDialog = false }
            )
        }
    }
}

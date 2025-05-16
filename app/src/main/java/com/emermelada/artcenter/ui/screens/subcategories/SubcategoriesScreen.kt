package com.emermelada.artcenter.ui.screens.subcategories

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.emermelada.artcenter.data.model.categories.Category
import com.emermelada.artcenter.data.model.categories.CategorySimple
import com.emermelada.artcenter.data.model.subcategories.Subcategory
import com.emermelada.artcenter.ui.UiState
import com.emermelada.artcenter.ui.components.categories.DeleteDialog
import com.emermelada.artcenter.ui.navigation.Destinations
import com.emermelada.artcenter.ui.screens.MainScaffoldViewModel
import com.emermelada.artcenter.ui.theme.LoraFontFamily

@Composable
fun SubcategoriesScreen(
    categoriaId: Int,
    navController: NavHostController,
    viewModel: MainScaffoldViewModel = hiltViewModel(),
    subcategoriesViewModel: SubcategoriesViewModel = hiltViewModel()
) {
    val userRole by viewModel.userRol.collectAsState()
    val subcategoriesState by subcategoriesViewModel.subcategoriesState.collectAsState()
    val categoriesState by subcategoriesViewModel.categoriesState.collectAsState()

    var subcategoryToDelete by remember { mutableStateOf<Subcategory?>(null) }

    LaunchedEffect(Unit) {
        subcategoriesViewModel.fetchSubcategories(categoriaId)
        subcategoriesViewModel.fetchCategoryById(categoriaId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 24.dp, end = 24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when(categoriesState) {
            is UiState.Loading -> { /* Opcional: mostrar loader */ }
            is UiState.Success<*> -> {
                val categoria = (categoriesState as UiState.Success<Category>).data

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = categoria.nombre,
                        fontSize = 20.sp,
                        color = Color.DarkGray
                    )

                    IconButton(
                        onClick = { navController.navigate(Destinations.CATEGORIES) },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.DarkGray
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .background(Color.Gray.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = categoria.descripcion,
                        fontSize = 16.sp,
                        color = Color.DarkGray
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
            is UiState.Error -> {
                Text(text = (categoriesState as UiState.Error).message, color = Color.Red)
            }
            else -> {}
        }

        when(subcategoriesState) {
            is UiState.Loading -> {
                CircularProgressIndicator()
            }
            is UiState.Error -> {
                Text(
                    text = (subcategoriesState as UiState.Error).message,
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }
            is UiState.Success<*> -> {
                val subcategorias = (subcategoriesState as UiState.Success<List<Subcategory>>).data

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(subcategorias) { index, subcategory ->
                        var menuExpanded by remember { mutableStateOf(false) }
                        val pastelColors = listOf(
                            Color(0xFFB3D9F7), Color(0xFFADD8E6), Color(0xFF7EC8E3),
                            Color(0xFF5AC2D3), Color(0xFF2F9CB6), Color(0xFF5AB6D3),
                            Color(0xFF9ACBDA), Color(0xFF7CA6B4), Color(0xFF74B1D4),
                            Color(0xFF61A4C1), Color(0xFF6BB9E4), Color(0xFF79D1D1),
                            Color(0xFF58A0C6), Color(0xFF89C8D8), Color(0xFF6FBCD9),
                            Color(0xFF5BB3D1)
                        )
                        val color = pastelColors[index % pastelColors.size]

                        Box(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (userRole == "admin") {
                                    Button(
                                        onClick = {
                                            navController.navigate("${Destinations.SUBCATEGORY}/${categoriaId}/${subcategory.id_subcategoria}")
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(60.dp),
                                        colors = ButtonDefaults.buttonColors(color),
                                        shape = RoundedCornerShape(16.dp),
                                        border = BorderStroke(2.dp, Color.DarkGray)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = subcategory.nombre.uppercase(),
                                                fontSize = 20.sp,
                                                color = Color.DarkGray,
                                                fontFamily = LoraFontFamily,
                                                fontWeight = FontWeight.Bold
                                            )
                                            IconButton(
                                                onClick = { menuExpanded = !menuExpanded },
                                                modifier = Modifier.padding(start = 8.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Edit,
                                                    contentDescription = "Opciones Subcategoría",
                                                    tint = Color.DarkGray
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            navController.navigate("${Destinations.SUBCATEGORY}/${categoriaId}/${subcategory.id_subcategoria}")
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(60.dp),
                                        colors = ButtonDefaults.buttonColors(color),
                                        shape = RoundedCornerShape(16.dp),
                                        border = BorderStroke(2.dp, Color.DarkGray)
                                    ) {
                                        Text(
                                            text = subcategory.nombre.uppercase(),
                                            fontSize = 20.sp,
                                            color = Color.DarkGray,
                                            fontFamily = LoraFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }

                            if (menuExpanded) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = (-100).dp, y = 65.dp)
                                ) {
                                    DropdownMenu(
                                        expanded = menuExpanded,
                                        onDismissRequest = { menuExpanded = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Editar", color = Color.DarkGray) },
                                            onClick = {
                                                menuExpanded = false
                                                navController.navigate("${Destinations.CREATE_SUBCATEGORIES}/${categoriaId}/${subcategory.id_subcategoria}")
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Eliminar", color = Color.DarkGray) },
                                            onClick = {
                                                menuExpanded = false
                                                subcategoryToDelete = subcategory
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else -> {}
        }

        subcategoryToDelete?.let { subcategory ->
            DeleteDialog(
                text = "¿Seguro que quieres eliminar la subcategoría ${subcategory.nombre}?",
                onConfirm = {
                    subcategoriesViewModel.deleteSubcategory(categoriaId, subcategory.id_subcategoria)
                    subcategoryToDelete = null
                },
                onDismiss = {
                    subcategoryToDelete = null
                }
            )
        }
    }
}


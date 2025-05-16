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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

    val pastelColors = listOf(
        Color(0xFFB3D9F7), // azul pastel claro
        Color(0xFFADD8E6), // azul celeste
        Color(0xFF7EC8E3), // azul suave
        Color(0xFF5AC2D3), // azul brillante suave
        Color(0xFF2F9CB6), // azul intenso pastel
        Color(0xFF5AB6D3), // azul brillante claro
        Color(0xFF9ACBDA), // azul muy suave
        Color(0xFF7CA6B4), // azul grisáceo suave
        Color(0xFF74B1D4), // azul pastel medio
        Color(0xFF61A4C1), // azul agua
        Color(0xFF6BB9E4), // azul cielo pastel
        Color(0xFF79D1D1), // azul suave verdoso
        Color(0xFF58A0C6), // azul más oscuro pastel
        Color(0xFF89C8D8), // azul medianamente fuerte
        Color(0xFF6FBCD9), // azul fuerte pastel
        Color(0xFF5BB3D1)  // azul oscuro pastel
    )


    // Esto lanza la carga solo una vez
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
        when(categoriesState){
            is UiState.Loading ->{
                // Puedes agregar un cargador aquí si lo deseas
            }

            is UiState.Success<*> -> {
                val categoria = categoriesState as UiState.Success<Category>

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = categoria.data.nombre,
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

                // Caja con fondo gris para la descripción
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp) // Ajusta el espaciado si lo necesitas
                        .background(Color.Gray.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp))
                        .padding(16.dp) // Espaciado interno de la caja
                ) {
                    Text(
                        text = categoria.data.descripcion,
                        fontSize = 16.sp,
                        color = Color.DarkGray
                    )
                }

                Spacer(modifier = Modifier.height(16.dp)) // Espaciado debajo de la caja de descripción
            }

            is UiState.Error ->{
                // Manejar error si es necesario
            }
            else -> {  }
        }


        when (subcategoriesState) {
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
                    contentPadding = PaddingValues(0.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp) // puedes ajustar este valor si quieres menos separación
                ) {
                    itemsIndexed(subcategorias) { index, subcategory ->
                        val color = pastelColors[index % pastelColors.size]
                        Button(
                            onClick = {
                                navController.navigate("${Destinations.SUBCATEGORY}/${categoriaId}/${subcategory.id_categoria}")},
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = color),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(2.dp, Color.DarkGray)
                        ) {
                            Text(
                                text = subcategory.nombre.uppercase(),
                                fontSize = 20.sp,
                                color = Color.DarkGray,
                                fontFamily = LoraFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

            }
            else -> {  }
        }
    }
}
package com.emermelada.artcenter.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.emermelada.artcenter.ui.screens.categories.CategoriesScreen
import com.emermelada.artcenter.ui.screens.createcategories.CreateCategoriesScreen
import com.emermelada.artcenter.ui.screens.createcategories.CreateSubcategoriesScreen
import com.emermelada.artcenter.ui.screens.feed.FeedScreen
import com.emermelada.artcenter.ui.screens.profile.ProfileScreen
import com.emermelada.artcenter.ui.screens.publication.PublicationScreen
import com.emermelada.artcenter.ui.screens.subcategories.SubcategoriesScreen
import com.emermelada.artcenter.ui.screens.subcategories.SubcategoryScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
    onClickNav: (String) -> Unit
) {

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
    ) {
        NavHost(navController = navController, startDestination = Destinations.FEED) {
            composable(Destinations.PUBLICATION) {
                PublicationScreen(onClickNav)
            }

            composable(Destinations.FEED) {
                FeedScreen(onClickNav)
            }

            composable(Destinations.CATEGORIES) {
                CategoriesScreen(onClickNav, navController)
            }

            composable("${Destinations.CREATE_CATEGORIES}/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")?.toInt() ?: 0
                CreateCategoriesScreen(id, onClickNav)
            }

            composable("${Destinations.CREATE_SUBCATEGORIES}/{idCategoria}/{idSubcategoria}"){ backStackEntry ->
                val categoriaId = backStackEntry.arguments?.getString("idCategoria")?.toIntOrNull() ?: 0
                val subcategoriaId = backStackEntry.arguments?.getString("idSubcategoria")?.toIntOrNull() ?: 0
                CreateSubcategoriesScreen(categoriaId, subcategoriaId, onClickNav)
            }

            composable("${Destinations.SUBCATEGORIES}/{id}"){ backStackEntry ->
                val categoriaId = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
                SubcategoriesScreen(categoriaId, navController)
            }

            composable("${Destinations.SUBCATEGORY}/{idCategoria}/{idSubcategoria}"){ backStackEntry ->
                val categoriaId = backStackEntry.arguments?.getString("idCategoria")?.toIntOrNull() ?: 0
                val subcategoriaId = backStackEntry.arguments?.getString("idSubcategoria")?.toIntOrNull() ?: 0
                SubcategoryScreen(categoriaId, subcategoriaId, onClickNav)
            }

            composable(Destinations.PROFILE) {
                ProfileScreen(onClickNav)
            }
        }
    }
}
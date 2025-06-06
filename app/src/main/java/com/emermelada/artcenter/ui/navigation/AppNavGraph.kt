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
import com.emermelada.artcenter.ui.screens.chat.ChatScreen
import com.emermelada.artcenter.ui.screens.createcategories.CreateCategoriesScreen
import com.emermelada.artcenter.ui.screens.createcategories.CreateSubcategoriesScreen
import com.emermelada.artcenter.ui.screens.details_publication.DetailsPublicationScreen
import com.emermelada.artcenter.ui.screens.feed.FeedScreen
import com.emermelada.artcenter.ui.screens.profile.ProfileScreen
import com.emermelada.artcenter.ui.screens.publication.PublicationScreen
import com.emermelada.artcenter.ui.screens.search.SearchPublicationsScreen
import com.emermelada.artcenter.ui.screens.subcategories.SubcategoriesScreen
import com.emermelada.artcenter.ui.screens.subcategories.SubcategoryScreen

/**
 * Define el grafo de navegación principal de la aplicación.
 *
 * Contiene todas las rutas y pantallas principales:
 * - Publicación detallada
 * - Feed de publicaciones
 * - Categorías y creación de categorías/subcategorías
 * - Subcategorías y detalle de subcategoría
 * - Perfil de usuario
 * - Búsqueda de publicaciones
 *
 * @param navController Controlador de navegación utilizado para manejar la pila de pantallas.
 * @param innerPadding Espaciado que debe respetarse alrededor del contenido, proveniente de Scaffold u otro contenedor.
 * @param onClickNav Lambda que recibe una cadena de ruta (`String`) y navega a dicha ruta cuando se invoca.
 */
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
        /**
         * Host de navegación que inicia en la ruta [Destinations.FEED].
         * Define las pantallas disponibles y sus argumentos.
         */
        NavHost(navController = navController, startDestination = Destinations.FEED) {
            /**
             * Pantalla de creación de nueva publicación.
             * Sin argumentos en la ruta.
             */
            composable(Destinations.PUBLICATION) {
                PublicationScreen(onClickNav)
            }

            /**
             * Pantalla principal de "Feed" de publicaciones.
             * Sin argumentos en la ruta.
             */
            composable(Destinations.FEED) {
                FeedScreen(onClickNav)
            }

            /**
             * Pantalla que muestra la lista de categorías.
             * Sin argumentos en la ruta.
             */
            composable(Destinations.CATEGORIES) {
                CategoriesScreen(onClickNav, navController)
            }

            /**
             * Pantalla para crear o editar una categoría.
             *
             * @arg id Identificador numérico de la categoría que se editará. Si es 0, se crea una nueva.
             */
            composable("${Destinations.CREATE_CATEGORIES}/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")?.toInt() ?: 0
                CreateCategoriesScreen(id, onClickNav)
            }

            /**
             * Pantalla para crear o editar una subcategoría.
             *
             * @arg idCategoria Identificador de la categoría padre.
             * @arg idSubcategoria Identificador numérico de la subcategoría que se editará. Si es 0, se crea una nueva.
             */
            composable("${Destinations.CREATE_SUBCATEGORIES}/{idCategoria}/{idSubcategoria}") { backStackEntry ->
                val categoriaId = backStackEntry.arguments?.getString("idCategoria")?.toIntOrNull() ?: 0
                val subcategoriaId = backStackEntry.arguments?.getString("idSubcategoria")?.toIntOrNull() ?: 0
                CreateSubcategoriesScreen(categoriaId, subcategoriaId, onClickNav)
            }

            /**
             * Pantalla que muestra la lista de subcategorías de una categoría específica.
             *
             * @arg id Identificador de la categoría padre.
             */
            composable("${Destinations.SUBCATEGORIES}/{id}") { backStackEntry ->
                val categoriaId = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
                SubcategoriesScreen(categoriaId, navController)
            }

            /**
             * Pantalla que muestra detalle de una subcategoría.
             *
             * @arg idCategoria Identificador de la categoría padre.
             * @arg idSubcategoria Identificador de la subcategoría a mostrar.
             */
            composable("${Destinations.SUBCATEGORY}/{idCategoria}/{idSubcategoria}") { backStackEntry ->
                val categoriaId = backStackEntry.arguments?.getString("idCategoria")?.toIntOrNull() ?: 0
                val subcategoriaId = backStackEntry.arguments?.getString("idSubcategoria")?.toIntOrNull() ?: 0
                SubcategoryScreen(categoriaId, subcategoriaId, onClickNav)
            }

            /**
             * Pantalla que muestra el perfil del usuario.
             * Sin argumentos en la ruta.
             */
            composable(Destinations.PROFILE) {
                ProfileScreen(onClickNav)
            }

            /**
             * Pantalla de detalle de una publicación específica.
             *
             * @arg idPublicacion Identificador numérico de la publicación a mostrar.
             */
            composable("${Destinations.DETAILS_PUBLICATION}/{idPublicacion}") { backStackEntry ->
                val idPublicacion = backStackEntry.arguments?.getString("idPublicacion")?.toIntOrNull() ?: 0
                DetailsPublicationScreen(idPublicacion, onClickNav)
            }

            /**
             * Pantalla de búsqueda de publicaciones.
             * Sin argumentos en la ruta.
             */
            composable(Destinations.SEARCH) {
                SearchPublicationsScreen(onClickNav)
            }

            /**
             * Pantalla de chat con la IA.
             * Sin argumentos en la ruta.
             */
            composable(Destinations.CHAT) {
                ChatScreen()
            }
        }
    }
}

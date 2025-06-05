package com.emermelada.artcenter.ui.navigation

/**
 * Objeto que define las rutas (destinos) utilizadas en la navegación de la aplicación.
 *
 * Cada constante representa una ruta única que se emplea para identificar pantallas
 * en los grafos de navegación.
 */
object Destinations {

    /**
     * Ruta para la pantalla de registro de usuario.
     */
    const val REGISTER = "register"

    /**
     * Ruta para la pantalla de inicio de sesión.
     */
    const val LOGIN = "login"

    /**
     * Ruta para la pantalla del feed de publicaciones.
     */
    const val FEED = "feed"

    /**
     * Ruta para la pantalla de listado de categorías.
     */
    const val CATEGORIES = "categories"

    /**
     * Ruta para la pantalla de listado de subcategorías de una categoría.
     */
    const val SUBCATEGORIES = "subcategories"

    /**
     * Ruta para la pantalla de detalle de una subcategoría específica.
     */
    const val SUBCATEGORY = "subcategory"

    /**
     * Ruta para la pantalla de búsqueda de publicaciones.
     */
    const val SEARCH = "search"

    /**
     * Ruta para la pantalla de chat (actualmente no utilizada en el grafo proporcionado).
     */
    const val CHAT = "chat"

    /**
     * Ruta para la pantalla de perfil de usuario.
     */
    const val PROFILE = "profile"

    /**
     * Ruta para la pantalla de creación o edición de categorías.
     */
    const val CREATE_CATEGORIES = "create_categories"

    /**
     * Ruta para la pantalla de creación o edición de subcategorías.
     */
    const val CREATE_SUBCATEGORIES = "create_subcategories"

    /**
     * Ruta para la pantalla de creación de una nueva publicación.
     */
    const val PUBLICATION = "publication"

    /**
     * Ruta para la pantalla de detalle de una publicación específica.
     */
    const val DETAILS_PUBLICATION = "details publication"
}

package com.emermelada.artcenter.ui.screens.createcategories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emermelada.artcenter.data.model.categories.CategorySimple
import com.emermelada.artcenter.data.model.subcategories.SubcategoryRequest
import com.emermelada.artcenter.data.repositories.CategoriesRepository
import com.emermelada.artcenter.data.repositories.SubcategoriesRepository
import com.emermelada.artcenter.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel que gestiona la creación, carga y actualización de subcategorías.
 *
 * - Carga la lista de categorías para mostrar en un selector.
 * - Crea nuevas subcategorías asociadas a una categoría existente.
 * - Carga la información de una subcategoría para edición.
 * - Actualiza subcategorías existentes.
 *
 * Expone varios [StateFlow] de [UiState] para reflejar el estado de cada operación
 * (carga de categorías, creación/actualización de subcategoría, carga de datos para edición).
 *
 * @property categoriesRepository Repositorio para obtener la lista de categorías.
 * @property subcategoriesRepository Repositorio para crear, cargar y actualizar subcategorías.
 */
@HiltViewModel
class CreateSubcategoriesViewModel @Inject constructor(
    private val categoriesRepository: CategoriesRepository,
    private val subcategoriesRepository: SubcategoriesRepository
) : ViewModel() {

    /**
     * Estado de la operación de creación o actualización de subcategoría.
     * - Idle: sin operación en curso
     * - Loading: operación en curso
     * - Success: operación completada con éxito (contenido en data, puede ser un mensaje)
     * - Error: operación fallida (mensaje de error).
     */
    private val _subcategoryState = MutableStateFlow<UiState>(UiState.Loading)
    val subcategoryState: StateFlow<UiState> = _subcategoryState.asStateFlow()

    /**
     * Estado de la operación de carga de la lista de categorías.
     * - Idle: sin operación en curso
     * - Loading: carga en curso
     * - Success: carga completada con éxito (data: List<CategorySimple>)
     * - Error: carga fallida (mensaje de error).
     */
    private val _categoriesState = MutableStateFlow<UiState>(UiState.Loading)
    val categoriesState: StateFlow<UiState> get() = _categoriesState

    /**
     * Estado de la operación de carga de una subcategoría concreta para edición.
     * - Idle: sin operación en curso
     * - Loading: carga en curso
     * - Success: carga completada con éxito (data: objeto subcategoría)
     * - Error: carga fallida (mensaje de error).
     */
    private val _subcategoryLoadedState = MutableStateFlow<UiState>(UiState.Idle)
    val subcategoryLoadedState: StateFlow<UiState> = _subcategoryLoadedState.asStateFlow()

    /**
     * Obtiene la lista de todas las categorías disponibles.
     *
     * - Actualiza [_categoriesState] a Loading antes de la llamada.
     * - Si la respuesta contiene datos, emite Success con la lista de [CategorySimple].
     * - En caso contrario, emite Error con el mensaje recibido o un mensaje por defecto.
     */
    fun fetchCategories() {
        viewModelScope.launch {
            _categoriesState.value = UiState.Loading
            val result = categoriesRepository.getAllCategories()
            if (result.data != null) {
                _categoriesState.value = UiState.Success(result.data)
            } else {
                _categoriesState.value = UiState.Error(result.msg ?: "Error al cargar categorías")
            }
        }
    }

    /**
     * Crea una nueva subcategoría asociada a la categoría cuyo nombre coincide con [categoriaSeleccionada].
     *
     * - Busca el ID de la categoría seleccionada en el estado [_categoriesState].
     * - Si no se encuentra la categoría, emite Error en [_subcategoryState].
     * - Construye un [SubcategoryRequest] con los datos proporcionados y llama a
     *   subcategoriesRepository.createSubcategory.
     * - Si la respuesta tiene código 200 o 201, emite Success con un mensaje de confirmación.
     * - En caso contrario, emite Error con el mensaje de error o uno por defecto.
     *
     * @param nombre Nombre de la nueva subcategoría.
     * @param descripcion Descripción o historia de la subcategoría.
     * @param caracteristicas Características de la subcategoría.
     * @param requerimientos Requerimientos asociados a la subcategoría.
     * @param tutoriales Texto o enlaces de tutoriales relacionados.
     * @param categoriaSeleccionada Nombre de la categoría padre seleccionada en la UI.
     */
    fun createSubcategory(
        nombre: String,
        descripcion: String,
        caracteristicas: String,
        requerimientos: String,
        tutoriales: String,
        categoriaSeleccionada: String
    ) {
        viewModelScope.launch {
            _subcategoryState.value = UiState.Loading
            try {
                // Obtener ID de la categoría seleccionada
                val categoriaId = (categoriesState.value as? UiState.Success<List<CategorySimple>>)
                    ?.data?.find { it.nombre == categoriaSeleccionada }?.id
                if (categoriaId == null) {
                    _subcategoryState.value = UiState.Error("Categoría no encontrada.")
                    return@launch
                }

                val subcategoryRequest = SubcategoryRequest(
                    id_categoria = categoriaId,
                    nombre = nombre,
                    historia = descripcion,
                    caracteristicas = caracteristicas,
                    requerimientos = requerimientos,
                    tutoriales = tutoriales
                )

                val result = subcategoriesRepository.createSubcategory(subcategoryRequest)
                if (result.code in listOf(200, 201)) {
                    _subcategoryState.value = UiState.Success("Subcategoría creada correctamente.")
                } else {
                    _subcategoryState.value = UiState.Error(result.msg
                        ?: "Error al crear la subcategoría.")
                }
            } catch (e: Exception) {
                _subcategoryState.value = UiState.Error("Error al crear la subcategoría")
            }
        }
    }

    /**
     * Carga la subcategoría identificada por [idCategoria] e [idSubcategoria] para su edición.
     *
     * - Actualiza [_subcategoryLoadedState] a Loading antes de la llamada.
     * - Si la respuesta contiene datos, emite Success con el objeto subcategoría.
     * - En caso contrario, emite Error con el mensaje recibido o uno por defecto.
     *
     * @param idCategoria Identificador de la categoría padre de la subcategoría.
     * @param idSubcategoria Identificador de la subcategoría a cargar.
     */
    fun loadSubcategory(idCategoria: Int, idSubcategoria: Int) {
        viewModelScope.launch {
            _subcategoryLoadedState.value = UiState.Loading
            val result = subcategoriesRepository.getSubcategoryById(idCategoria, idSubcategoria)
            if (result.data != null) {
                _subcategoryLoadedState.value = UiState.Success(result.data)
            } else {
                _subcategoryLoadedState.value =
                    UiState.Error(result.msg ?: "Error al cargar subcategoría")
            }
        }
    }

    /**
     * Actualiza una subcategoría existente.
     *
     * - Busca el ID de la categoría seleccionada en el estado [_categoriesState].
     * - Si no se encuentra la categoría, emite Error en [_subcategoryState].
     * - Crea un objeto [Subcategory] con los nuevos datos y llama a
     *   subcategoriesRepository.updateSubcategory.
     * - Si la respuesta tiene código 200 o 201, emite Success con mensaje de confirmación.
     * - En caso contrario, emite Error con el mensaje de error o uno por defecto.
     *
     * @param idCategoria Identificador de la categoría padre actual.
     * @param idSubcategoria Identificador de la subcategoría a actualizar.
     * @param nombre Nuevo nombre de la subcategoría.
     * @param descripcion Nueva descripción o historia de la subcategoría.
     * @param caracteristicas Nuevas características de la subcategoría.
     * @param requerimientos Nuevos requerimientos de la subcategoría.
     * @param tutoriales Nuevos tutoriales relacionados.
     * @param categoriaSeleccionada Nombre de la categoría padre seleccionada en la UI.
     */
    fun updateSubcategory(
        idCategoria: Int,
        idSubcategoria: Int,
        nombre: String,
        descripcion: String,
        caracteristicas: String,
        requerimientos: String,
        tutoriales: String,
        categoriaSeleccionada: String
    ) {
        viewModelScope.launch {
            _subcategoryState.value = UiState.Loading
            try {
                // Obtener ID de la categoría seleccionada
                val categoriaId = (categoriesState.value as? UiState.Success<List<CategorySimple>>)
                    ?.data?.find { it.nombre == categoriaSeleccionada }?.id
                if (categoriaId == null) {
                    _subcategoryState.value = UiState.Error("Categoría no encontrada.")
                    return@launch
                }

                val subcategory = com.emermelada.artcenter.data.model.subcategories.Subcategory(
                    id_categoria = categoriaId,
                    id_subcategoria = idSubcategoria,
                    nombre = nombre,
                    historia = descripcion,
                    caracteristicas = caracteristicas,
                    requerimientos = requerimientos,
                    tutoriales = tutoriales
                )

                val result = subcategoriesRepository.updateSubcategory(
                    idCategoria, idSubcategoria, subcategory
                )
                if (result.code in listOf(200, 201)) {
                    _subcategoryState.value = UiState.Success("Subcategoría actualizada correctamente.")
                } else {
                    _subcategoryState.value = UiState.Error(result.msg
                        ?: "Error al actualizar la subcategoría.")
                }
            } catch (e: Exception) {
                _subcategoryState.value = UiState.Error("Error al actualizar la subcategoría")
            }
        }
    }

    /**
     * Permite establecer un mensaje de error manualmente en el estado de la operación de subcategoría.
     *
     * @param message Mensaje de error a mostrar.
     */
    fun setError(message: String) {
        _subcategoryState.value = UiState.Error(message)
    }
}

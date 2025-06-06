package com.emermelada.artcenter.ui.screens.publication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emermelada.artcenter.data.model.publications.PublicationRequest
import com.emermelada.artcenter.data.model.tags.Tag
import com.emermelada.artcenter.data.repositories.PublicationRepository
import com.emermelada.artcenter.data.repositories.TagsRepository
import com.emermelada.artcenter.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

/**
 * ViewModel encargado de gestionar las operaciones relacionadas con la creación de publicaciones.
 *
 * @property publicationRepository Repositorio que expone métodos para crear publicaciones.
 * @property tagsRepository Repositorio que expone métodos para obtener las etiquetas disponibles.
 */
class PublicationViewModel(
    private val publicationRepository: PublicationRepository = PublicationRepository(),
    private val tagsRepository: TagsRepository = TagsRepository()
) : ViewModel() {

    /**
     * Estado que representa la carga y disponibilidad de las etiquetas.
     *
     * - [UiState.Loading] mientras se solicitan las etiquetas.
     * - [UiState.Success] cuando se obtienen correctamente, contiene la lista de [Tag].
     * - [UiState.Error] en caso de error, contiene el mensaje de error.
     */
    private val _tagsState = MutableStateFlow<UiState>(UiState.Loading)
    val tagsState: StateFlow<UiState> = _tagsState.asStateFlow()

    /**
     * Estado que representa la operación de subida de una publicación.
     *
     * - [UiState.Idle] cuando no hay ninguna subida en curso.
     * - [UiState.Loading] mientras se realiza la subida de la publicación.
     * - [UiState.Success] cuando la subida se completa con éxito, contiene un mensaje de confirmación.
     * - [UiState.Error] en caso de error, contiene el mensaje de error.
     */
    private val _uploadState = MutableStateFlow<UiState>(UiState.Idle)
    val uploadState: StateFlow<UiState> = _uploadState.asStateFlow()

    /**
     * Identificador de la etiqueta seleccionada por el usuario.
     * Puede ser nulo si el usuario no ha seleccionado ninguna etiqueta.
     */
    var selectedTagId: Int? = null

    /**
     * Solicita al repositorio la lista de todas las etiquetas disponibles.
     *
     * Actualiza [_tagsState] a [UiState.Loading] antes de la llamada. Si la respuesta contiene datos,
     * emite [UiState.Success] con la lista de [Tag]. En caso contrario, emite [UiState.Error] con
     * el mensaje recibido o uno por defecto.
     */
    fun fetchTags() {
        viewModelScope.launch {
            _tagsState.value = UiState.Loading
            val result = tagsRepository.getAllTags()
            if (result.data != null) {
                _tagsState.value = UiState.Success(result.data)
            } else {
                _tagsState.value = UiState.Error(result.msg ?: "Error cargando etiquetas")
            }
        }
    }

    /**
     * Sube una nueva publicación al servidor.
     *
     * - @param imageFile Archivo de imagen que se desea subir.
     * - @param descripcion Texto opcional que describe la publicación; se omite si es cadena vacía.
     *
     * Antes de iniciar la subida, actualiza [_uploadState] a [UiState.Loading]. Si la operación
     * devuelve un código HTTP 200 o 201, emite [UiState.Success] con un mensaje de confirmación.
     * En caso de código distinto, emite [UiState.Error] con el mensaje recibido o uno por defecto.
     * Si ocurre una excepción durante la subida, emite [UiState.Error] con una descripción del error.
     */
    fun uploadPublication(imageFile: File, descripcion: String) {
        viewModelScope.launch {
            _uploadState.value = UiState.Loading
            try {
                val publicationRequest = PublicationRequest(
                    descripcion = descripcion.takeIf { it.isNotBlank() },
                    id_etiqueta = selectedTagId
                )

                val result = publicationRepository.createPublication(imageFile, publicationRequest)

                if (result.code in listOf(200, 201)) {
                    _uploadState.value = UiState.Success("Publicación creada correctamente")
                } else {
                    _uploadState.value = UiState.Error(result.msg ?: "Error al crear publicación")
                }
            } catch (e: Exception) {
                _uploadState.value =
                    UiState.Error("Error al subir la publicación: ${e.localizedMessage}")
            }
        }
    }
}

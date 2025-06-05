package com.emermelada.artcenter.ui.components.shared

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.emermelada.artcenter.R

/**
 * Composable que muestra un cuadro de diálogo de confirmación para eliminación.
 *
 * @param text Texto de descripción dentro del diálogo que pregunta al usuario.
 * @param onConfirm Lambda que se ejecuta cuando el usuario confirma la acción.
 * @param onDismiss Lambda que se ejecuta cuando el usuario descarta el diálogo.
 */
@Composable
fun DeleteDialog(
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(stringResource(R.string.confirmarEliminación), color = Color.White) },
        text = { Text(text) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.si))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.no))
            }
        }
    )
}

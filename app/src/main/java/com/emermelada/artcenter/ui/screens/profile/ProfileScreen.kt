package com.emermelada.artcenter.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emermelada.artcenter.data.model.profile.User
import com.emermelada.artcenter.ui.UiState


@Composable
fun ProfileScreen(
    onClickNav: (String) -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val userInfoState by profileViewModel.userInfoState.collectAsState()

    LaunchedEffect(Unit) {
        profileViewModel.fetchUserInfo()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (userInfoState) {
            is UiState.Loading -> {
                CircularProgressIndicator()
            }

            is UiState.Error -> {
                Text(
                    text = (userInfoState as UiState.Error).message,
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }

            is UiState.Success<*> -> {
                val user = (userInfoState as UiState.Success<User>).data

                // Image profile (with default if null)
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color.LightGray, shape = MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PhotoCamera,
                        contentDescription = "Editar foto",
                        modifier = Modifier
                            .clickable { /* Add functionality to pick photo */ },
                        tint = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Username with editable icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = user.username,
                        fontSize = 22.sp,
                        fontFamily = FontFamily.SansSerif,
                        color = Color.Black
                    )
                    IconButton(
                        onClick = { /* Add functionality to change username */ }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Editar username"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Buttons for "Tus publicaciones" and "Guardados"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {}) {
                        Text(text = "Tus publicaciones")
                    }
                    Button(onClick = {}) {
                        Text(text = "Guardados")
                    }
                }
            }
        }
    }
}

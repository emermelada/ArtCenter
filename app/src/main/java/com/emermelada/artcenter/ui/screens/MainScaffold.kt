package com.emermelada.artcenter.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.emermelada.artcenter.R
import com.emermelada.artcenter.ui.navigation.AppNavGraph
import com.emermelada.artcenter.ui.navigation.Destinations
import androidx.compose.runtime.collectAsState



@Composable
fun MainScaffold(
    onClickSignOut: () -> Unit,
    viewModel: MainScaffoldViewModel = hiltViewModel()
){

    val navController = rememberNavController()
    val currentScreen = remember { mutableStateOf(Destinations.FEED) }
    val userRole by viewModel.userRol.collectAsState()


    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentScreen.value = destination.route ?: Destinations.FEED
        }
    }

    Scaffold(
        topBar = {
            TopBarView (onClickSignOut)
        },
        content = { innerPadding ->
            AppNavGraph(
                navController,
                innerPadding,
                onClickNav = { destination ->
                    navController.navigate(destination)
                }
            )
        },
        bottomBar = {
            if(userRole == "admin"){
                BottomBarAdminView(
                    currentRoute = currentScreen.value,
                    onClickNav = { destination ->
                        navController.navigate(destination)
                    }
                )
            }else{
                BottomBarUserView(
                    currentRoute = currentScreen.value,
                    onClickNav = { destination ->
                        navController.navigate(destination)
                    }
                )
            }
        }
    )

}

@Composable
fun BottomBarAdminView(
    currentRoute: String,
    onClickNav: (String) -> Unit)
{
    val icons = listOf(
        Icons.AutoMirrored.Filled.MenuBook to Destinations.CATEGORIES,
        Icons.Default.Home to Destinations.FEED,
        Icons.Default.Search to Destinations.SEARCH,
        Icons.Default.SmartToy to Destinations.CHAT
    )

    Column {
        Divider(
            thickness = 2.dp,
            color = Color.DarkGray
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            icons.forEach { (icon, destination) ->
                val isSelected = currentRoute == destination

                Box(
                    modifier = Modifier
                        .background(
                            if (isSelected) Color.DarkGray else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(6.dp)
                ) {
                    IconButton(onClick = { onClickNav(destination) }) {
                        Icon(
                            imageVector = icon,
                            contentDescription = destination,
                            tint = if (isSelected) Color.White else Color.Black
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun BottomBarUserView(
    currentRoute: String,
    onClickNav: (String) -> Unit)
{
    val icons = listOf(
        Icons.AutoMirrored.Filled.MenuBook to Destinations.CATEGORIES,
        Icons.Default.Search to Destinations.SEARCH,
        Icons.Default.Home to Destinations.FEED,
        Icons.Default.SmartToy to Destinations.CHAT,
        Icons.Default.Person to Destinations.PROFILE
    )

    Column {
        Divider(
            thickness = 2.dp,
            color = Color.DarkGray
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            icons.forEach { (icon, destination) ->
                val isSelected = currentRoute == destination

                Box(
                    modifier = Modifier
                        .background(
                            if (isSelected) Color.DarkGray else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(6.dp)
                ) {
                    IconButton(onClick = { onClickNav(destination) }) {
                        Icon(
                            imageVector = icon,
                            contentDescription = destination,
                            tint = if (isSelected) Color.White else Color.Black
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun TopBarView(onClickSignOut: () -> Unit) {
    var menuExpanded by remember { mutableStateOf(false) }

    val kuchekFont = FontFamily(Font(R.font.kuchek))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(Color.LightGray)
            .padding(horizontal = 8.dp, vertical = 4.dp), // MÁS PEQUEÑO
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            IconButton(
                onClick = { menuExpanded = !menuExpanded }
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Ajustes", tint = Color.Black)
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Cerrar sesión") },
                    onClick = {
                        menuExpanded = false
                        onClickSignOut()
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.TitleAuth),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontFamily = kuchekFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize, // MÁS PEQUEÑA
                    color = colorResource(R.color.black)
                )
            )
        }

        Spacer(modifier = Modifier.size(40.dp)) // equilibrio visual con el icono
    }
}



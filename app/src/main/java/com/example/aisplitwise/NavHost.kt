package com.example.aisplitwise

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.aisplitwise.dashboard.DashBoard
import com.example.aisplitwise.dashboard.DashboardViewModel
import kotlinx.serialization.Serializable


@Serializable
data class Profile(val name: String)
@Serializable
object FriendsList

@Composable
fun NavHostInitializer(navController:NavHostController) {
    NavHost(navController = navController, startDestination = FriendsList) {
        composable<Profile> {
            val args = it.toRoute<Profile>()
            val dashBoardViewModel = hiltViewModel<DashboardViewModel>()
            DashBoard(args,
                dashBoardViewModel)
        }
        composable<FriendsList> {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red)
            ) {
                Text(
                    text = "Hello",
                    Modifier
                        .align(Alignment.Center)
                        .clickable { navController.navigate(Profile(name = "Atharv")) })
            }
        }
        // Add more destinations similarly.
    }
}


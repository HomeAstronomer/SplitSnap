package com.example.aisplitwise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.aisplitwise.ui.theme.AISplitwiseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = FriendsList) {
                composable<Profile> {
                    val args = it.toRoute<Profile>()
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Cyan)
                    ) {
                        Text(text = args.name, Modifier.align(Alignment.Center))
                    }
                }
                composable<FriendsList> {
                    val dashBoardViewModel= hiltViewModel<DashboardViewModel>()
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
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AISplitwiseTheme {
        Greeting("Android")
    }
}
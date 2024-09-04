package com.example.aisplitwise.feature.feature_splash

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.aisplitwise.DashBoardRoute
import com.example.aisplitwise.LoginScreenRoute
import com.example.aisplitwise.R
import com.example.aisplitwise.SplashRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun SplashScreen(splashViewModel: SplashViewModel, navController: NavHostController, ){

    // Create a Lottie composition from the given file
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splash_json))
    // Create a Lottie animation state
    val progress by animateLottieCompositionAsState(composition, restartOnPlay = false)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            contentScale = ContentScale.FillBounds
        )
    }
   var hasNavigated by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = progress) {
        if (progress >= 0.25f && !hasNavigated) {
            hasNavigated = true // Set the flag to prevent further navigation
            withContext(Dispatchers.Main) {
                if (splashViewModel.isLoggedIn) {
                    navController.navigate(DashBoardRoute) {
                        popUpTo(SplashRoute) { inclusive = true }
                    }
                } else {
                    navController.navigate(LoginScreenRoute) {
                        popUpTo(SplashRoute) { inclusive = true }
                    }
                }
            }
        }
    }

}
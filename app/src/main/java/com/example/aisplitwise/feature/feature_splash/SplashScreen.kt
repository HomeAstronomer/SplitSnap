package com.example.aisplitwise.feature.feature_splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.aisplitwise.navigation.DashBoardRoute
import com.example.aisplitwise.navigation.LoginScreenRoute
import com.example.aisplitwise.R
import com.example.aisplitwise.navigation.SplashRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun SplashScreen(splashViewModel: SplashViewModel, navController: NavHostController, ){

    // Create a Lottie composition from the given file
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splash_json))
    // Create a Lottie animation state
    val progress by animateLottieCompositionAsState(composition, restartOnPlay = false)

    val splashScreenUiState by splashViewModel.uiState.collectAsStateWithLifecycle()

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
//            .clickable {
//
//                    if (splashViewModel.isLoggedIn) {
//                        navController.navigate(DashBoardRoute) {
//                            popUpTo(SplashRoute) { inclusive = true }
//                        }
//                    } else {
//                        navController.navigate(LoginScreenRoute) {
//                            popUpTo(SplashRoute) { inclusive = true }
//                        }
//                    }
//
//            }
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            contentScale = ContentScale.FillBounds
        )
    }
   var hasNavigated by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = progress, key2 =  splashScreenUiState.isLoading) {
        if (progress >= 0.25f && !hasNavigated && !splashScreenUiState.isLoading) {
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

    LaunchedEffect(key1 = Unit) {
        delay(10000)
        if ( !hasNavigated) {
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
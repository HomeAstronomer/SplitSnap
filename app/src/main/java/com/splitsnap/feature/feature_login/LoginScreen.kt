package com.splitsnap.feature.feature_login

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.splitsnap.R
import com.splitsnap.atoms.GradientBorderButton
import com.splitsnap.navigation.DashBoardRoute
import com.splitsnap.navigation.LoginScreenRoute
import com.splitsnap.navigation.SignUpScreenRoute
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun LoginScreen(loginViewModel: LoginViewModel, navController: NavHostController) {
    Scaffold(
        modifier = Modifier
            .imePadding()
            .background(MaterialTheme.colorScheme.background)
    ) { padding ->
        val uiState by loginViewModel.uiState.collectAsStateWithLifecycle()
        val context = LocalContext.current
        var email by remember {
            mutableStateOf("")
        }


        var password by remember {
            mutableStateOf("")
        }

        val oneTapClient = remember { Identity.getSignInClient(context) }
        val signInRequest by remember {
            mutableStateOf(
                BeginSignInRequest.builder().setGoogleIdTokenRequestOptions(
                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder().setSupported(true)
                        .setServerClientId("460580943651-q86lp28u749cohktgmbojp7mnb4b5492.apps.googleusercontent.com")
                        .setFilterByAuthorizedAccounts(false).build()
                ).setAutoSelectEnabled(true).build()
            )
        }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                    val idToken = credential.googleIdToken
                    when {
                        idToken != null -> {
                            Log.d("SignIn", "Got ID token: $idToken")
                            loginViewModel.firebaseAuthWithGoogle(idToken) {
                                navController.navigate(DashBoardRoute) {
                                    popUpTo(LoginScreenRoute) { inclusive = true }
                                }
                            }  // Authenticate with Firebase
                        }

                        else -> {
                            Log.d("SignIn", "No ID token!")
                        }
                    }
                } catch (e: ApiException) {
                    Log.e("SignIn", "One Tap sign-in failed: ${e.localizedMessage}")
                }
            }
        }
        val scrollState = rememberScrollState()
        Box(Modifier.fillMaxSize()) {

            Box(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        shape = BottomArcShape(arcHeight = 50.dp)
                    )
            )
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .align(Alignment.TopCenter),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_svg),
                    contentDescription = null,
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = padding.calculateTopPadding() + 24.dp, bottom = 24.dp)
                        .fillMaxHeight(0.15f)
                )
                Column(
                    modifier = Modifier

                        .padding(24.dp)
                        .background(MaterialTheme.colorScheme.onPrimary, RoundedCornerShape(24.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(24.dp))

                        .scrollable(
                            state = scrollState, orientation = Orientation.Vertical
                        )
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome to SplitSnap !!",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier,
                    )
                    Text(
                        text = "Choose a sign-in method to get started",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 24.dp),
                    )

                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    OutlinedButton(
                        onClick = {
                            loginViewModel.signIn(email, password) {
                                navController.navigate(DashBoardRoute)
                            }
                        }, modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)

                    ) {
                        Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Email, // Replace with your icon resource
                                contentDescription = "Create Group", // Replace with your string resource
                                modifier = Modifier.padding(end = 8.dp), tint = Color.Unspecified
                            )
                            Text(
                                text = "Sign In with Email",
                                fontStyle = MaterialTheme.typography.labelSmall.fontStyle
                            )
                        }
                    }

                    Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "New to SplitSnap? ",
                            color = Color.Gray,
                            fontStyle = MaterialTheme.typography.labelSmall.fontStyle,
                        )
                        Text(
                            text = "Create An Account",
                            color = MaterialTheme.colorScheme.primary,
                            fontStyle = MaterialTheme.typography.labelSmall.fontStyle,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { navController.navigate(SignUpScreenRoute) })
                    }

                    Box(contentAlignment = Alignment.Center) {

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 24.dp)
                        )
                        Text(
                            " or ",
                            modifier = Modifier.background(MaterialTheme.colorScheme.onPrimary)
                        )
                    }
                    val showLoader = remember { mutableStateOf(false) }

                    GradientBorderButton(showLoader = showLoader.value, onClick = {
                        signInWithGoogle(oneTapClient, signInRequest, launcher) {
                            showLoader.value = it
                        }
                    })
                }
                Spacer(Modifier.weight(1f))
            }

            LaunchedEffect(key1 = uiState.showToast) {
                if (uiState.showToast) {
                    Toast.makeText(
                        context,
                        uiState.toastMessage,
                        Toast.LENGTH_SHORT,
                    ).show()
                    delay(2.seconds)
                    loginViewModel.resetToast()
                }

            }
            if (uiState.showLoader) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))
                ) {
                    CircularProgressIndicator(
                        Modifier
                            .size(64.dp)
                            .align(Alignment.Center), strokeWidth = 8.dp
                    )
                }
            }

        }
    }
}

fun signInWithGoogle(
    oneTapClient: SignInClient,
    signInRequest: BeginSignInRequest,
    launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
    showLoader: (Boolean) -> Unit
) {
    showLoader.invoke(true)
    oneTapClient.beginSignIn(signInRequest).addOnSuccessListener { result ->
        showLoader.invoke(false)
        try {
            val intentSenderRequest =
                IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
            launcher.launch(intentSenderRequest)  // Start the One Tap sign-in UI
        } catch (e: Exception) {
            Log.e("SignIn", "Couldn't start One Tap UI: ${e.localizedMessage}")
        }
    }.addOnFailureListener { e ->
        showLoader.invoke(false)
        Log.e("SignIn", "One Tap sign-in failed: ${e.localizedMessage}")
    }
}

@Composable
fun RotatingSweepGradientBorderBox(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()

    // Animate the rotation angle
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Animated Sweep Gradient Brush
    val animatedBrush = Brush.sweepGradient(
        colors = listOf(Color.Red, Color.Green, Color.Blue, Color.Red)
    )

    Box(modifier = modifier
        .size(150.dp)
        .graphicsLayer {
            rotationZ = rotationAngle  // Rotates the entire border
        }
        .border(
            4.dp, animatedBrush, shape = RoundedCornerShape(24.dp)
        ), // Apply animated brush to border
        contentAlignment = Alignment.Center) {
        Text(text = "Rotating Border", color = Color.White, fontSize = 16.sp)
    }
}

@Preview
@Composable
fun PreviewRotatingSweepGradientBorderBox() {
    RotatingSweepGradientBorderBox()
}

class BottomArcShape(private val arcHeight: Dp) : Shape {

    override fun createOutline(
        size: androidx.compose.ui.geometry.Size, layoutDirection: LayoutDirection, density: Density
    ): Outline {
        val arcPx = with(density) { arcHeight.toPx() } // convert Dp -> Px
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(0f, size.height - arcPx)
            quadraticTo(
                size.width / 2f, size.height + arcPx, // control point
                size.width, size.height - arcPx
            )
            lineTo(size.width, 0f)
            close()
        }
        return Outline.Generic(path)
    }
}
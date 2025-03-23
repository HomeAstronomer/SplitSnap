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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.splitsnap.R
import com.splitsnap.navigation.DashBoardRoute
import com.splitsnap.navigation.LoginScreenRoute
import com.splitsnap.navigation.SignUpScreenRoute
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.splitsnap.theme.SplitSnapTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun LoginScreen(loginViewModel: LoginViewModel, navController: NavHostController) {
Scaffold(modifier= Modifier
    .imePadding()
    .background(MaterialTheme.colorScheme.surfaceBright)) {padding->
    val uiState by loginViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val oneTapClient = remember { Identity.getSignInClient(context) }
    val signInRequest by remember {
        mutableStateOf(
            BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId("460580943651-q86lp28u749cohktgmbojp7mnb4b5492.apps.googleusercontent.com")
                        .setFilterByAuthorizedAccounts(false)
                        .build()
                )
                .setAutoSelectEnabled(true)
                .build()
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
                        loginViewModel.firebaseAuthWithGoogle(idToken){
                            navController.navigate(DashBoardRoute){
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
    val scrollState= rememberScrollState()

    val showGoogleLoader=remember { mutableStateOf(false) }
    LoginScreenComposable(
       modifier= Modifier
           .background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.surfaceBright.copy(0.7f),
               MaterialTheme.colorScheme.surfaceBright)))
            .padding(padding)
           .padding(24.dp)
            .fillMaxSize(),
       uiState =  uiState,
       emailSignInClick =  { email, password ->
           loginViewModel.signIn(email, password) {
               navController.navigate(DashBoardRoute)
           }
                           },
        googleSignInClick={ signInWithGoogle(oneTapClient,signInRequest,launcher){
            showGoogleLoader.value=it
        }},
        navigate = { navController.navigate(it) },
        showGoogleLoader=showGoogleLoader.value,
        resetToast= loginViewModel::resetToast)



}
}

@Composable
fun LoginScreenComposable(
    modifier: Modifier,
    uiState: LoginScreenUiState,
    emailSignInClick: (String, String) -> Unit,
    googleSignInClick: () -> Unit,
    navigate: (Any)->Unit,
    showGoogleLoader:Boolean=false,
    resetToast: () -> Unit,
) {
    val context = LocalContext.current
    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }
    Box(modifier=modifier) {
        Column(Modifier.align(Alignment.Center)) {
            Column(
                modifier = Modifier.background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.secondaryContainer,
                            MaterialTheme.colorScheme.secondaryContainer.copy(0.5f)
                        )
                    ), RoundedCornerShape(16.dp)
                ).padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Sign In",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 24.dp),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = {
                        Text(
                            "Email",
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = {
                        Text(
                            "Password",
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                )

                OutlinedButton(
                    onClick = { emailSignInClick.invoke(email, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)

                ) {
                    Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Email, // Replace with your icon resource
                            contentDescription = "Create Group", // Replace with your string resource
                            modifier = Modifier
                                .padding(end = 8.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Sign In with Email",
                            fontStyle = MaterialTheme.typography.bodyMedium.fontStyle,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }


                OutlinedButton(
                    onClick = {
                        navigate(SignUpScreenRoute)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd, // Replace with your icon resource
                            contentDescription = "Create Group", // Replace with your string resource
                            modifier = Modifier
                                .padding(end = 8.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Sign Up with Email",
                            fontStyle = MaterialTheme.typography.bodyMedium.fontStyle,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }


            }
            val infiniteTransition = rememberInfiniteTransition(label = "background")


            val targetOffset = with(LocalDensity.current) {
                1000.dp.toPx()
            }
            val offset by infiniteTransition.animateFloat(
                initialValue = 0f, targetValue = targetOffset, animationSpec = infiniteRepeatable(
                    tween(5000, easing = LinearEasing), repeatMode = RepeatMode.Restart
                ), label = "offset"
            )
            val brushColors = listOf(
                Color(0xFF4285F4),  // Google Blue
                Color(0xFFEA4335),  // Google Red
                Color(0xFFFBBC05),  // Google Yellow
                Color(0xFF34A853)   // Google Green
            )
            OutlinedButton(
                onClick = googleSignInClick,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
                    .then(
                        if (showGoogleLoader) Modifier
                            .fillMaxWidth()
                            .border(
                                2.dp, brush = Brush.linearGradient(
                                    colors = brushColors,
                                    start = Offset(offset, offset),
                                    end = Offset(offset + 1000f, offset + 1000f),
                                    tileMode = TileMode.Repeated
                                ), shape = RoundedCornerShape(24.dp)
                            ) else Modifier.padding(2.dp)
                    ),
                enabled = true
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.android_neutral_rd_na), // Replace with your icon resource
                        contentDescription = "Create Group", // Replace with your string resource
                        modifier = Modifier
                            .size(36.dp)
                            .padding(end = 8.dp),
                        tint = Color.Unspecified
                    )
                    Text(
                        text = "Sign In with Google ",
                        fontStyle = MaterialTheme.typography.bodyMedium.fontStyle,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        LaunchedEffect(key1 = uiState.showToast) {
            if (uiState.showToast) {
                Toast.makeText(
                    context,
                    uiState.toastMessage,
                    Toast.LENGTH_SHORT,
                ).show()
                delay(2.seconds)
                resetToast()
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
                        .align(Alignment.Center),
                    strokeWidth = 8.dp
                )
            }
        }
    }
}

fun signInWithGoogle(
    oneTapClient: SignInClient,
    signInRequest: BeginSignInRequest,
    launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
    showLoader:(Boolean)->Unit
) {
    showLoader.invoke(true)
    oneTapClient.beginSignIn(signInRequest)
        .addOnSuccessListener { result ->
            showLoader.invoke(false)
            try {
                val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                launcher.launch(intentSenderRequest)  // Start the One Tap sign-in UI
            } catch (e: Exception) {
                Log.e("SignIn", "Couldn't start One Tap UI: ${e.localizedMessage}")
            }
        }
        .addOnFailureListener { e ->
            showLoader.invoke(false)
            Log.e("SignIn", "One Tap sign-in failed: ${e.localizedMessage}")
        }
}


@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES or android.content.res.Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun PreviewRotatingSweepGradientBorderBox() {
    SplitSnapTheme {
        Scaffold(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(0.2f)
                        )
                    )
                )
                .imePadding()
        ) { padding ->
            LoginScreenComposable(Modifier.background(
                MaterialTheme.colorScheme.surfaceContainer
            )
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp), LoginScreenUiState(), { _, _ -> }, {}, {}) { }
        }
    }
}

@Preview
@Composable
fun PreviewRotatingSweepGradientBorderBoxLight() {
    SplitSnapTheme {
        Scaffold(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.surfaceContainer
                )
                .imePadding()
        ) { padding ->
            LoginScreenComposable(Modifier.background(
                MaterialTheme.colorScheme.surfaceContainer
            )
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp), LoginScreenUiState(), { _, _ -> }, {}, {}) { }
        }
    }
}



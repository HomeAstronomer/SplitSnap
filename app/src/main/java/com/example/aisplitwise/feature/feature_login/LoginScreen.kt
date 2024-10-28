package com.example.aisplitwise.feature.feature_login

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.aisplitwise.R
import com.example.aisplitwise.navigation.DashBoardRoute
import com.example.aisplitwise.navigation.LoginScreenRoute
import com.example.aisplitwise.navigation.SignUpScreenRoute
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun LoginScreen(loginViewModel: LoginViewModel, navController: NavHostController) {
Scaffold(modifier=Modifier.imePadding().background(MaterialTheme.colorScheme.background)) {padding->
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
            BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId("435713973615-f5vued2okcs37ccdu76bmhqtrtslm6h0.apps.googleusercontent.com")
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
    Column(
        modifier = Modifier
            .padding(padding)
            .scrollable(
                state = scrollState,
                orientation = Orientation.Vertical
            )
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sign In",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom =24.dp),
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        FilledTonalButton(
            onClick = {
                loginViewModel.signIn(email, password) {
                    navController.navigate(DashBoardRoute)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)

        ) {
            Row( modifier = Modifier,verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector =  Icons.Default.Email, // Replace with your icon resource
                    contentDescription = "Create Group", // Replace with your string resource
                    modifier = Modifier
                        .padding(end = 8.dp),
                    tint = Color.Unspecified
                )
                Text(
                    text = "Sign In with Email",
                    fontStyle = MaterialTheme.typography.bodyMedium.fontStyle
                )
            }
        }


        OutlinedButton(
            onClick = {
                navController.navigate(SignUpScreenRoute)
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row( modifier = Modifier,verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector =  Icons.Default.PersonAdd, // Replace with your icon resource
                    contentDescription = "Create Group", // Replace with your string resource
                    modifier = Modifier
                        .padding(end = 8.dp),
                    tint = Color.Unspecified
                )
                Text(
                    text = "Sign Up with Email",
                    fontStyle = MaterialTheme.typography.bodyMedium.fontStyle
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 24.dp)
        )

        OutlinedButton(
            onClick = {
               signInWithGoogle(oneTapClient,signInRequest,launcher)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = true
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.android_neutral_rd_na), // Replace with your icon resource
                    contentDescription = "Create Group", // Replace with your string resource
                    modifier = Modifier.size(36.dp)
                        .padding(end = 8.dp),
                    tint = Color.Unspecified
                )
                Text(text = "Sign In with Google ",
                    fontStyle = MaterialTheme.typography.bodyMedium.fontStyle)
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
            loginViewModel.resetToast()
        }

    }
    if(uiState.showLoader) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))) {
            CircularProgressIndicator(
                Modifier
                    .size(64.dp)
                    .align(Alignment.Center),
                strokeWidth =8.dp )
        }
    }

}
}

fun signInWithGoogle(
    oneTapClient: SignInClient,
    signInRequest: BeginSignInRequest,
    launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
) {
    oneTapClient.beginSignIn(signInRequest)
        .addOnSuccessListener { result ->
            try {
                val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                launcher.launch(intentSenderRequest)  // Start the One Tap sign-in UI
            } catch (e: Exception) {
                Log.e("SignIn", "Couldn't start One Tap UI: ${e.localizedMessage}")
            }
        }
        .addOnFailureListener { e ->
            Log.e("SignIn", "One Tap sign-in failed: ${e.localizedMessage}")
        }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val navController= rememberNavController()
    LoginScreen(hiltViewModel(), navController)
}
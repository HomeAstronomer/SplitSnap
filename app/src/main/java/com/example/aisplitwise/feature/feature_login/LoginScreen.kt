package com.example.aisplitwise.feature.feature_login

import android.widget.Toast
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.aisplitwise.navigation.DashBoardRoute
import com.example.aisplitwise.navigation.SignUpScreenRoute
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun LoginScreen(loginViewModel: LoginViewModel, navController: NavHostController) {
Scaffold(modifier=Modifier.imePadding()) {padding->
    val uiState by loginViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }
    val scrollState= rememberScrollState()
    Column(
        modifier = Modifier
            .padding(padding)
            .scrollable(state=scrollState,
                orientation = Orientation.Vertical)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sign In",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp),
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
                .padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                loginViewModel.signIn(email, password) {
                    navController.navigate(DashBoardRoute)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text(text = "Sign In with Email")
        }


        Button(
            onClick = {
                navController.navigate(SignUpScreenRoute)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text(text = "Sign Up with Email")
        }

        Divider(
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Button(
            onClick = {
                //TODO
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = false
        ) {
            Text(text = "Sign In with Google Coming Soon...")
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

}
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val navController= rememberNavController()
    LoginScreen(hiltViewModel(), navController)
}
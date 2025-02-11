package com.splitsnap.feature.feature_signup

import android.widget.Toast
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.splitsnap.navigation.DashBoardRoute
import com.splitsnap.navigation.LoginScreenRoute
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun SignUpScreen(signUpViewModel: SignUpViewModel, navController: NavHostController){
    val uiState by signUpViewModel.uiState.collectAsStateWithLifecycle()
    val context= LocalContext.current
    Scaffold {padding->
        var name by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var phoneNumber by remember { mutableStateOf("") }

        val scrollState= rememberScrollState()
        Column(
            modifier = Modifier
                .imePadding()
                .fillMaxSize()
                .scrollable(state=scrollState,
                    orientation = Orientation.Vertical)
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Sign Up",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            TextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Phone
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            Spacer(Modifier.weight(1f))

            Button(
                onClick = { signUpViewModel.signup( email, password,name, phoneNumber,
                    onSuccess = {
                        navController.navigate(DashBoardRoute){
                            popUpTo<LoginScreenRoute>{
                                LoginScreenRoute
                                inclusive=true
                            }
                        }
                    }) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            ) {
                Text(text = "Sign Up")
            }
        }
    }
    LaunchedEffect(key1 = uiState.showToast) {
        Toast.makeText(
            context,
            uiState.toastMessage,
            Toast.LENGTH_SHORT,
        ).show()
        delay(2.seconds)
        signUpViewModel.resetToast()

    }

}
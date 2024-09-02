package com.example.aisplitwise.feature.feature_login

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.aisplitwise.DashBoardRoute

@Composable
fun LoginScreen(loginViewModel: LoginViewModel, navController: NavHostController) {

    val uiState by loginViewModel.uiState.collectAsStateWithLifecycle()
        val context= LocalContext.current
   var email by remember {
       mutableStateOf("")
   }

    var password by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sign In",
            style =  MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
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
            onClick = {loginViewModel.emailLogin(email,password,{fireBaseUser->
                navController.navigate(DashBoardRoute(displayName = fireBaseUser?.displayName,
                    email = fireBaseUser?.email,
                    phoneNumber = fireBaseUser?.phoneNumber,
                    photoUrl = fireBaseUser?.photoUrl?.path))
            },
                {error->
                    Toast.makeText(
                        context,
                        error?:"Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text(text = "Sign In with Email")
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
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val navController= rememberNavController()
    LoginScreen(hiltViewModel(), navController)
}
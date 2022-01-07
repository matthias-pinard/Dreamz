package com.matthias.dreamz.ui.screen.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun LoginScreen(loginViewModel: LoginViewModel, navController: NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Login") }) },
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(40.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.size(8.dp))
            TextField(
                value = loginViewModel.username,
                onValueChange = { loginViewModel.username = it },
                label = { Text("Usernames") })
            TextField(
                value = loginViewModel.password,
                onValueChange = { loginViewModel.password = it },
                label = { Text("Password") },
                visualTransformation = if (loginViewModel.passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (loginViewModel.passwordVisibility)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    IconButton(onClick = {
                        loginViewModel.togglePasswordVisibility()
                    }) {
                        Icon(imageVector  = image, "")
                    }
                }
            )
            Button(
                onClick = { loginViewModel.login() },
                enabled = loginViewModel.username.isNotBlank() && loginViewModel.password.isNotBlank()
            ) {
                Text("Login", style = MaterialTheme.typography.h6)
            }
            if (loginViewModel.loginError) {
                Text("Login Error")
            }
        }
    }
    if (loginViewModel.loginSuccessfull) {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
    }
}
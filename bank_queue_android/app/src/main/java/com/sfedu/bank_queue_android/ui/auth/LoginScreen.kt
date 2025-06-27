package com.sfedu.bank_queue_android.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sfedu.bank_queue_android.viewmodel.AuthUiState
import com.sfedu.bank_queue_android.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    nav: NavController,
    viewModel: AuthViewModel = hiltViewModel(),
    onSuccess: () -> Unit
) {
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val uiState = viewModel.uiState

    // Реагируем на смену состояния: при Success зовём onSuccess()
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onSuccess()
        }
    }

    Column(Modifier.padding(16.dp)) {
        Text("Авторизация", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = login,
            onValueChange = { login = it },
            label = { Text("login") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { viewModel.login(login, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState !is AuthUiState.Loading
        ) {
            if (uiState is AuthUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Войти")
            }
        }

        // Ошибка, если есть
        if (uiState is AuthUiState.Error) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = uiState.message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(Modifier.height(16.dp))

        TextButton(onClick = { nav.navigate("register") }) {
            Text("Нет аккаунта? Зарегистрируйтесь")
        }
    }
}

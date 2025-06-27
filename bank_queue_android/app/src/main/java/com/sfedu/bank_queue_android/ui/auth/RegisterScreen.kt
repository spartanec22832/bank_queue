package com.sfedu.bank_queue_android.ui.auth

import androidx.compose.foundation.layout.*
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
fun RegisterScreen(
    nav: NavController,
    viewModel: AuthViewModel = hiltViewModel(),
    onSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var login by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    val uiState = viewModel.uiState
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            nav.navigate("tickets") {
                popUpTo("register") { inclusive = true }
            }
        }
    }

    Column(Modifier.padding(16.dp)) {
        Text("Регистрация", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        // аналогично полям выше: TextField для name, login, email, password, phone
        OutlinedTextField(name, { name = it }, label = { Text("ФИО") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(login, { login = it }, label = { Text("Login") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            password, { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(phone, { phone = it }, label = { Text("Номер телефона") }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.register(name, login, email, password, phone)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState is AuthUiState.Loading) CircularProgressIndicator(Modifier.size(24.dp))
            else Text("Зарегистрироваться")
        }
        uiState.takeIf { it is AuthUiState.Error }?.let {
            Spacer(Modifier.height(8.dp))
            Text((it as AuthUiState.Error).message, color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(16.dp))

        TextButton(onClick = { nav.navigate("login") }) {
            Text("Уже есть аккаунт? Авторизуйтесь")
        }
    }
}

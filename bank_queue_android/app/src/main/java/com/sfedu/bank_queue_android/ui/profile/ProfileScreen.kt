package com.sfedu.bank_queue_android.ui.profile

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sfedu.bank_queue_android.model.User
import com.sfedu.bank_queue_android.viewmodel.UserViewModel

@Composable
fun ProfileScreen(
    nav: NavController,
    vm: UserViewModel = hiltViewModel()
) {
    val token     by vm::token
    val profile   by vm::profile
    val isLoading by vm::isLoadingProfile
    val error     by vm::errorMessage
    val navBackStack by nav.currentBackStackEntryAsState()
    val currentRoute = navBackStack?.destination?.route

    // флаг, чтобы пропустить первый null
    var tokenInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(token) {
        if (!tokenInitialized) {
            // помечаем, что мы уже “увидели” первый токен (даже если он null)
            tokenInitialized = true
        } else {
            // а теперь реагируем по-настоящему
            if (token.isNullOrBlank()) {
                nav.navigate("login") {
                    popUpTo("tickets") { inclusive = true }
                }
            } else {
                vm.loadProfile()
            }
        }
    }

    LaunchedEffect(currentRoute) {
        if (currentRoute == "profile") {
            vm.loadProfile()
        }
    }


    if (token.isNullOrBlank()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Профиль недоступен. Вы не авторизованы", textAlign = TextAlign.Center)
        }
        return
    }

    Box(Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
            profile != null -> {
                ProfileContent(
                    profile = profile!!,
                    onEdit = { nav.navigate("profile/edit") },
                    onChangePassword = { nav.navigate("profile/change-password") },
                    onLogout = {
                        vm.logout {
                            nav.navigate("login") {
                                popUpTo("tickets") { inclusive = true }
                            }
                        }
                    }
                )
            }
            error != null -> {
                // Только показываем ошибку и кнопку Retry
                Column(
                    Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Не удалось загрузить профиль:\n$error",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { vm.loadProfile() }) {
                        Text("Повторить")
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileContent(
    profile: User,
    onEdit: () -> Unit,
    onChangePassword: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("ФИО: ${profile.name}", style = MaterialTheme.typography.bodyLarge)
        Text("Логин: ${profile.login}", style = MaterialTheme.typography.bodyLarge)
        Text("Email: ${profile.email}", style = MaterialTheme.typography.bodyLarge)
        Text("Телефон: ${profile.phoneNumber}", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onEdit, Modifier.fillMaxWidth()) {
            Text("Редактировать профиль")
        }
        Button(onClick = onChangePassword, Modifier.fillMaxWidth()) {
            Text("Сменить пароль")
        }
        Button(
            onClick = onLogout,
            Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Выйти", color = MaterialTheme.colorScheme.onError)
        }
    }
}

@Composable
fun EditProfileScreen(
    vm: UserViewModel,
    onSaved: () -> Unit,
    onCancel: () -> Unit
) {
    LaunchedEffect(Unit) {
        if (vm.profile == null) {
            vm.loadProfile()
        }
    }

    // получаем текущий профиль
    val profile by vm::profile

    // инициализируем поля пустыми
    var name  by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }

    var isProcessing by remember { mutableStateOf(false) }
    var error        by remember { mutableStateOf<String?>(null) }

    // когда профиль придёт из VM, заполняем в локальные стейты
    LaunchedEffect(profile) {
        profile?.let {
            name  = it.name
            email = it.email
            phone = it.phoneNumber
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Редактировать профиль", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("ФИО") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Телефон") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Отмена")
            }

            Button(
                onClick = {
                    error = null
                    isProcessing = true
                    vm.updateProfile(name = name, email = email, phone = phone) { result ->
                        isProcessing = false
                        result
                            .onSuccess {
                                vm.loadProfile()
                                onSaved()
                            }
                            .onFailure { error = "Ошибка редактирования профиля" }
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = !isProcessing
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Сохранить")
                }
            }
        }

        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun ChangePasswordScreen(
    vm: UserViewModel,
    onSaved: () -> Unit,
    onCancel: () -> Unit
) {
    var oldPwd by rememberSaveable { mutableStateOf("") }
    var newPwd by rememberSaveable { mutableStateOf("") }
    var confPwd by rememberSaveable { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Сменить пароль", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = oldPwd, onValueChange = { oldPwd = it },
            label = { Text("Старый пароль") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        OutlinedTextField(
            value = newPwd, onValueChange = { newPwd = it },
            label = { Text("Новый пароль") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        OutlinedTextField(
            value = confPwd, onValueChange = { confPwd = it },
            label = { Text("Подтвердите пароль") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(24.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onCancel, modifier = Modifier.weight(1f)) {
                Text("Отмена")
            }
            Button(
                onClick = {
                    error = null
                    if (newPwd != confPwd) {
                        error = "Пароли не совпадают"
                        return@Button
                    }
                    isProcessing = true
                    vm.changePassword(oldPwd = oldPwd, newPwd = newPwd, confPwd = confPwd) { result ->
                        isProcessing = false
                        result
                            .onSuccess { onSaved() }
                            .onFailure {
                                error = "Ошибка смены пароля"
                                Log.d("ChangePwd", "change failed, exception = ${it::class.simpleName}, message=${it.message}")
                            }
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = !isProcessing
            ) {
                if (isProcessing) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                else Text("Сменить")
            }
        }

        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}


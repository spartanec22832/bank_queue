package com.sfedu.bank_queue_android.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sfedu.bank_queue_android.ui.auth.LoginScreen
import com.sfedu.bank_queue_android.ui.auth.RegisterScreen
import com.sfedu.bank_queue_android.ui.profile.ProfileScreen
import com.sfedu.bank_queue_android.ui.ticket.CreateTicketScreen
import com.sfedu.bank_queue_android.ui.ticket.TicketDetailScreen
import com.sfedu.bank_queue_android.ui.ticket.TicketListScreen
import kotlinx.coroutines.launch
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.sfedu.bank_queue_android.ui.profile.ChangePasswordScreen
import com.sfedu.bank_queue_android.ui.profile.EditProfileScreen
import com.sfedu.bank_queue_android.viewmodel.UserViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val vm: UserViewModel = hiltViewModel()
    val token = vm.token

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .fillMaxHeight()
                    .widthIn(max = 320.dp)
            ) {
                // общий модификатор для пунктов меню
                val itemModifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .border(
                        BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceColorAtElevation(50.dp)),
                        shape = MaterialTheme.shapes.small
                    )

                if (!token.isNullOrBlank()) {
                    // если уже авторизованы
                    Text(
                        "Вы уже авторизованы",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(16.dp))
                    NavigationDrawerItem(
                        label = {
                            Text(
                                "Выйти",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            // здесь ваш метод логаута в UserViewModel
                            vm.logout(){}
                            navController.navigate("login") {
                                popUpTo("tickets") { inclusive = true }
                            }
                        },
                        modifier = itemModifier
                    )
                } else {
                    // если не авторизованы
                    Text(
                        "Аутентификация",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(16.dp))
                    NavigationDrawerItem(
                        label = {
                            Text(
                                "Авторизация",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("login")
                        },
                        modifier = itemModifier
                    )
                    NavigationDrawerItem(
                        label = {
                            Text(
                                "Регистрация",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("register")
                        },
                        modifier = itemModifier
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        Surface(
                            tonalElevation = 0.dp,
                            shape = MaterialTheme.shapes.small,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceColorAtElevation(50.dp)),
                            modifier = Modifier.padding(start = 8.dp).requiredSize(35.dp)
                        ) {
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } }
                            ) {
                                Icon(
                                    Icons.Default.Menu,
                                    contentDescription = "Menu"
                                )
                            }
                        }
                    },
                    title = { Text("Банковская очередь") },
                    actions = {}
                )
            },
            bottomBar = {
                val navBackStack by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStack?.destination?.route?.substringBefore("/")
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Add, contentDescription = "Create") },
                        label = { Text("Создать тикет") },
                        selected = currentRoute == "create",
                        onClick = {
                            if (currentRoute != "create") {
                                navController.navigate("create")
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.List, contentDescription = "Tickets") },
                        label = { Text("Мои тикеты") },
                        selected = currentRoute == "tickets",
                        onClick = {
                            if (currentRoute != "tickets") {
                                navController.navigate("tickets")
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profile") },
                        label = { Text("Профиль") },
                        selected = currentRoute == "profile",
                        onClick = {
                            if (currentRoute != "profile") {
                                navController.navigate("profile")
                            }
                        }
                    )
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = if (token.isNullOrBlank()) "login" else "tickets",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("login") {
                    LoginScreen(
                        nav = navController,
                        onSuccess = {
                            navController.navigate("profile") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }
                composable("register") { RegisterScreen(nav = navController, onSuccess = { navController.navigateUp() }) }
                composable("create") { CreateTicketScreen(hiltViewModel(), onCreated = { navController.navigate("tickets") }) }
                composable("tickets") { TicketListScreen(
                    hiltViewModel(),
                    onClick = { id -> navController.navigate("ticket/$id") }) }
                composable("profile") {
                    ProfileScreen(navController, hiltViewModel())
                }
                composable("profile/edit") {
                    EditProfileScreen(
                        vm = hiltViewModel(),         // UserViewModel
                        onSaved = { navController.popBackStack()
                                    vm.loadProfile()
                                  },
                        onCancel = { navController.popBackStack() }
                    )
                }

                // экран смены пароля
                composable("profile/change-password") {
                    ChangePasswordScreen(
                        vm = hiltViewModel(),
                        onSaved = { navController.popBackStack() },
                        onCancel = { navController.popBackStack() }
                    )
                }
                composable(
                    route = "ticket/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.IntType })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getInt("id")!!
                    TicketDetailScreen(
                        id = id,
                        nav = navController,
                        vm = hiltViewModel()
                    )
                }
            }
        }
    }
}


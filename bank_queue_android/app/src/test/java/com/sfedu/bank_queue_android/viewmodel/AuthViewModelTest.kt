package com.sfedu.bank_queue_android.viewmodel

import com.sfedu.bank_queue_android.model.AuthRequest
import com.sfedu.bank_queue_android.model.AuthResponse
import com.sfedu.bank_queue_android.network.RemoteDataSource
import com.sfedu.bank_queue_android.repository.AuthRepository
import com.sfedu.bank_queue_android.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: AuthViewModel

    private val authRepo = mockk<AuthRepository>()
    private val userRepo = mockk<UserRepository>()
    private val remoteDataSource = mockk<RemoteDataSource>()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = AuthViewModel(authRepo, userRepo, remoteDataSource)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    /**
     * Сценарий: успешный логин
     * Должен установить uiState = Success
     */
    @Test
    fun login_success_sets_Success_state() = runTest {
        // GIVEN: remoteDataSource возвращает валидный токен и authRepo успешно сохраняет его
        val token = "tok123"
        coEvery { remoteDataSource.login(AuthRequest("u", "p")) } returns AuthResponse(token)
        coEvery { authRepo.login("u", "p") } returns Result.success(token)

        // WHEN: вызываем метод login
        viewModel.login("u", "p")
        advanceUntilIdle()

        // THEN: uiState переходит в Success
        assertTrue(viewModel.uiState is AuthUiState.Success)
    }

    /**
     * Сценарий: неуспешный логин (ошибка сети или неверные креды)
     * Должен установить uiState = Error с сообщением исключения
     */
    @Test
    fun login_failure_sets_Error_state_with_message() = runTest {
        // GIVEN: remoteDataSource бросает RuntimeException("bad creds")
        coEvery { remoteDataSource.login(any()) } throws RuntimeException("bad creds")

        // WHEN: пытаемся залогиниться
        viewModel.login("user", "pass")
        advanceUntilIdle()

        // THEN: uiState = Error, message == "bad creds"
        val state = viewModel.uiState
        assertTrue(state is AuthUiState.Error)
        assertEquals("Ошибка авторизации! Проверьте правильность заполнения формы", (state as AuthUiState.Error).message)
    }

    /**
     * Сценарий: успешная регистрация
     * Должен установить uiState = Success
     */
    @Test
    fun register_success_sets_Success_state() = runTest {
        // GIVEN: userRepo.register возвращает Result.success(Unit)
        coEvery { userRepo.register("Name", "login", "e@mail", "pwd", "123") } returns Result.success(Unit)

        // WHEN: вызываем метод register
        viewModel.register("Name", "login", "e@mail", "pwd", "123")
        advanceUntilIdle()

        // THEN: uiState = Success
        assertTrue(viewModel.uiState is AuthUiState.Success)
    }

    /**
     * Сценарий: неуспешная регистрация
     * Должен установить uiState = Error с сообщением исключения
     */
    @Test
    fun register_failure_sets_Error_state_with_message() = runTest {
        // GIVEN: userRepo.register возвращает Result.failure(Exception("no register"))
        coEvery { userRepo.register(any(), any(), any(), any(), any()) } returns Result.failure(Exception("no register"))

        // WHEN: пытаемся зарегистрироваться
        viewModel.register("N", "L", "E", "P", "M")
        advanceUntilIdle()

        // THEN: uiState = Error, message == "no register"
        val state = viewModel.uiState
        assertTrue(state is AuthUiState.Error)
        assertEquals("Ошибка регистрации! Проверьте правильность заполнения формы", (state as AuthUiState.Error).message)
    }

    /**
     * Сценарий: выход из аккаунта
     * Должен вызвать authRepo.logout() и сбросить uiState в Idle
     */
    @Test
    fun logout_resets_to_Idle_state() = runTest {
        // GIVEN: authRepo.logout возвращает Result.success и исходное состояние != Idle
        coEvery { authRepo.logout() } returns Result.success(Unit)
        viewModel.uiState = AuthUiState.Success

        // WHEN: вызываем метод logout
        viewModel.logout()
        advanceUntilIdle()

        // THEN: uiState = Idle и logout() вызвался ровно один раз
        assertTrue(viewModel.uiState is AuthUiState.Idle)
        coVerify(exactly = 1) { authRepo.logout() }
    }
}

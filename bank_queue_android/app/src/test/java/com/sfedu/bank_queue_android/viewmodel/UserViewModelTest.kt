package com.sfedu.bank_queue_android.viewmodel

import com.sfedu.bank_queue_android.model.User
import com.sfedu.bank_queue_android.repository.AuthRepository
import com.sfedu.bank_queue_android.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var vm: UserViewModel
    private val userRepo = mockk<UserRepository>()
    private val authRepo = mockk<AuthRepository>()

    @Before
    fun setup() {
        // Подменяем главный диспетчер и мокаем токен
        Dispatchers.setMain(dispatcher)
        every { authRepo.getToken() } returns flowOf("token123")
        vm = UserViewModel(userRepo, authRepo)
        runTest { advanceUntilIdle() }
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    /**
     * Сценарий: инициализация ViewModel
     * Должна собрать токен из authRepo.getToken()
     */
    @Test
    fun init_collects_token() {
        // THEN: сразу после создания vm.token == "token123"
        assertEquals("token123", vm.token)
    }

    /**
     * Сценарий: успешная загрузка профиля
     * Должна установить profile и сбросить isLoadingProfile
     */
    @Test
    fun load_profile_success_sets_profile_and_clears_loading() = runTest {
        // GIVEN: userRepo.getProfile() возвращает успешный Result с User
        val user = User(7L, "Ivan", "ivan2004", "ivan@gmail.com", "+79001233456")
        coEvery { userRepo.getProfile() } returns Result.success(user)

        // WHEN: вызываем loadProfile()
        vm.loadProfile()
        advanceUntilIdle()

        // THEN: isLoadingProfile == false, profile == user, errorMessage == null
        assertFalse(vm.isLoadingProfile)
        assertEquals(user, vm.profile)
        assertNull(vm.errorMessage)
    }

    /**
     * Сценарий: загрузка профиля с 401 Unauthorized
     * Должна вызвать authRepo.logout() и очистить profile
     */
    @Test
    fun load_profile_unauthorized_triggers_logout() = runTest {
        // GIVEN: userRepo.getProfile() бросает HttpException с кодом 401
        val httpEx = mockk<retrofit2.HttpException>()
        every { httpEx.code() } returns 401
        coEvery { userRepo.getProfile() } throws httpEx
        coEvery { authRepo.logout() } returns Result.success(Unit)

        // WHEN: вызываем loadProfile()
        vm.loadProfile()
        advanceUntilIdle()

        // THEN: isLoadingProfile == false, profile == null, authRepo.logout() вызван
        assertFalse(vm.isLoadingProfile)
        assertNull(vm.profile)
        coVerify(exactly = 1) { authRepo.logout() }
    }

    /**
     * Сценарий: загрузка профиля с другой ошибкой
     * Должна установить errorMessage
     */
    @Test
    fun load_profile_other_failure_sets_error_message() = runTest {
        // GIVEN: userRepo.getProfile() бросает RuntimeException("oh no")
        coEvery { userRepo.getProfile() } throws RuntimeException("oh no")

        // WHEN: вызываем loadProfile()
        vm.loadProfile()
        advanceUntilIdle()

        // THEN: isLoadingProfile == false, errorMessage == "oh no"
        assertFalse(vm.isLoadingProfile)
        assertEquals("oh no", vm.errorMessage)
    }

    /**
     * Сценарий: успешное обновление профиля
     * Должна сбросить isProcessing и вернуть результат через callback
     */
    @Test
    fun update_profile_success_invokes_callback_and_resets_processing() = runTest {
        // GIVEN: userRepo.updateProfile() возвращает Result.success(updatedUser)
        val updatedUser = User(8L, "Пётр", "pert", "pert@yandex.ru", "+79000000000")
        coEvery { userRepo.updateProfile("Пётр", "pt@example.com", "67890") } returns Result.success(updatedUser)

        // WHEN: вызываем updateProfile(...)
        var result: Result<User>? = null
        vm.updateProfile("Пётр", "pt@example.com", "67890") { result = it }
        advanceUntilIdle()

        // THEN: isProcessing == false, result.isSuccess и result.getOrNull() == updatedUser
        assertFalse(vm.isProcessing)
        assertTrue(result!!.isSuccess)
        assertEquals(updatedUser, result!!.getOrNull())
    }

    /**
     * Сценарий: ошибка при обновлении профиля
     * Должна сбросить isProcessing и вернуть ошибку через callback
     */
    @Test
    fun update_profile_failure_invokes_callback_with_error_and_resets_processing() = runTest {
        // GIVEN: userRepo.updateProfile() возвращает Result.failure(Exception("bad update"))
        coEvery { userRepo.updateProfile(any(), any(), any()) } returns Result.failure(Exception("bad update"))

        // WHEN: вызываем updateProfile(...)
        var result: Result<User>? = null
        vm.updateProfile("A", "b@e", "c") { result = it }
        advanceUntilIdle()

        // THEN: isProcessing == false, result.isFailure и message == "bad update"
        assertFalse(vm.isProcessing)
        assertTrue(result!!.isFailure)
        assertEquals("bad update", result!!.exceptionOrNull()?.message)
    }

    /**
     * Сценарий: успешная смена пароля
     * Должна сбросить isProcessing и вернуть успех через callback
     */
    @Test
    fun change_password_success_invokes_callback_and_resets_processing() = runTest {
        // GIVEN: userRepo.changePassword() возвращает Result.success(Unit)
        coEvery { userRepo.changePassword("old", "new", "new") } returns Result.success(Unit)

        // WHEN: вызываем changePassword(...)
        var result: Result<Unit>? = null
        vm.changePassword("old", "new", "new") { result = it }
        advanceUntilIdle()

        // THEN: isProcessing == false, result.isSuccess
        assertFalse(vm.isProcessing)
        assertTrue(result!!.isSuccess)
    }

    /**
     * Сценарий: ошибка при смене пароля
     * Должна сбросить isProcessing и вернуть ошибку через callback
     */
    @Test
    fun change_password_failure_invokes_callback_with_error_and_resets_processing() = runTest {
        // GIVEN: userRepo.changePassword() возвращает Result.failure(Exception("pw fail"))
        coEvery { userRepo.changePassword(any(), any(), any()) } returns Result.failure(Exception("pw fail"))

        // WHEN: вызываем changePassword(...)
        var result: Result<Unit>? = null
        vm.changePassword("x", "y", "z") { result = it }
        advanceUntilIdle()

        // THEN: isProcessing == false, result.isFailure и message == "pw fail"
        assertFalse(vm.isProcessing)
        assertTrue(result!!.isFailure)
        assertEquals("pw fail", result!!.exceptionOrNull()?.message)
    }

    /**
     * Сценарий: выход из аккаунта
     * Должна вызвать authRepo.logout(), очистить profile и вызвать onDone
     */
    @Test
    fun logout_invokes_auth_logout_and_clears_profile() = runTest {
        // GIVEN: authRepo.logout() возвращает Result.success, profile установлен
        coEvery { authRepo.logout() } returns Result.success(Unit)
        vm.profile = User(7L, "Ivan", "ivan2004", "ivan@gmail.com", "+79001233456")

        // WHEN: вызываем logout()
        var wasCalled = false
        vm.logout { wasCalled = true }
        advanceUntilIdle()

        // THEN: authRepo.logout() вызван, profile == null, callback onDone вызван
        coVerify(exactly = 1) { authRepo.logout() }
        assertNull(vm.profile)
        assertTrue(wasCalled)
    }
}

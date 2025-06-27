package com.sfedu.bank_queue_android.viewmodel

import com.sfedu.bank_queue_android.model.Ticket
import com.sfedu.bank_queue_android.repository.TicketRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.time.OffsetDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class TicketViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var vm: TicketViewModel
    private val repo = mockk<TicketRepository>()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        vm = TicketViewModel(repo)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    /**
     * Сценарий: успешная загрузка списка тикетов
     * Должен сбросить флаг загрузки, обновить список tickets и очистить errorMessage
     */
    @Test
    fun load_tickets_success_updates_tickets_and_resets_loading() = runTest {
        // GIVEN: репозиторий возвращает непустой список Ticket
        val now = OffsetDateTime.parse("2025-01-01T10:00:00Z")
        val items = listOf(
            Ticket(
                id = 1L,
                userId = 42L,
                address = "ул. Ленина, 1",
                ticketType = "standard",
                ticket = "A123",
                scheduledAt = now
            )
        )
        coEvery { repo.getAll() } returns items

        // WHEN: вызываем loadTickets()
        vm.loadTickets()
        advanceUntilIdle()

        // THEN: флаг isLoadingList сброшен, tickets == items, errorMessage == null
        assertFalse(vm.isLoadingList)
        assertEquals(items, vm.tickets)
        assertNull(vm.errorMessage)
    }

    /**
     * Сценарий: ошибка при загрузке списка тикетов
     * Должен сбросить флаг загрузки и установить errorMessage
     */
    @Test
    fun load_tickets_failure_sets_error_message() = runTest {
        // GIVEN: репозиторий бросает RuntimeException("network error")
        coEvery { repo.getAll() } throws RuntimeException("network error")

        // WHEN: вызываем loadTickets()
        vm.loadTickets()
        advanceUntilIdle()

        // THEN: флаг isLoadingList сброшен, errorMessage == "network error"
        assertFalse(vm.isLoadingList)
        assertEquals("network error", vm.errorMessage)
    }

    /**
     * Сценарий: успешная загрузка деталей тикета
     * Должен сбросить флаг загрузки деталей, обновить selected и очистить errorMessage
     */
    @Test
    fun load_detail_success_updates_selected_and_resets_loading_detail() = runTest {
        // GIVEN: репозиторий возвращает объект Ticket по id
        val now = OffsetDateTime.parse("2025-02-02T12:00:00Z")
        val ticket = Ticket(
            id = 2L,
            userId = 7L,
            address = "пр. Мира, 5",
            ticketType = "vip",
            ticket = "B456",
            scheduledAt = now
        )
        coEvery { repo.getById(2) } returns ticket

        // WHEN: вызываем loadDetail(2)
        vm.loadDetail(2)
        advanceUntilIdle()

        // THEN: флаг isLoadingDetail сброшен, selected == ticket, errorMessage == null
        assertFalse(vm.isLoadingDetail)
        assertEquals(ticket, vm.selected)
        assertNull(vm.errorMessage)
    }

    /**
     * Сценарий: ошибка при загрузке деталей тикета
     * Должен сбросить флаг загрузки деталей и установить errorMessage
     */
    @Test
    fun load_detail_failure_sets_error_message() = runTest {
        // GIVEN: репозиторий бросает RuntimeException("not found")
        coEvery { repo.getById(5) } throws RuntimeException("not found")

        // WHEN: вызываем loadDetail(5)
        vm.loadDetail(5)
        advanceUntilIdle()

        // THEN: флаг isLoadingDetail сброшен, errorMessage == "not found"
        assertFalse(vm.isLoadingDetail)
        assertEquals("not found", vm.errorMessage)
    }

    /**
     * Сценарий: успешное создание тикета
     * Должен сбросить флаг обработки и вернуть созданный тикет через callback
     */
    @Test
    fun create_success_invokes_callback_and_resets_processing() = runTest {
        // GIVEN: репозиторий create возвращает Result.success(createdTicket)
        val nowStr = "2025-03-03T15:00:00Z"
        val created = Ticket(
            id = 3L,
            userId = 8L,
            address = "ул. Пушкина, 10",
            ticketType = "standard",
            ticket = "C789",
            scheduledAt = OffsetDateTime.parse(nowStr)
        )
        coEvery { repo.create("ул. Пушкина, 10", "standard", nowStr) } returns Result.success(created)

        // WHEN: вызываем create(...)
        var callbackResult: Result<Ticket>? = null
        vm.create("ул. Пушкина, 10", "standard", nowStr) {
            callbackResult = it
        }
        advanceUntilIdle()

        // THEN: isProcessing сброшен, callbackResult.isSuccess и callbackResult.getOrNull() == created
        assertFalse(vm.isProcessing)
        assertTrue(callbackResult!!.isSuccess)
        assertEquals(created, callbackResult!!.getOrNull())
    }

    /**
     * Сценарий: ошибка при создании тикета
     * Должен сбросить флаг обработки и вернуть ошибку через callback
     */
    @Test
    fun create_failure_invokes_callback_with_failure_and_resets_processing() = runTest {
        // GIVEN: repo.create возвращает Result.failure(Exception("create fail"))
        val nowStr = "2025-04-04T16:00:00Z"
        coEvery { repo.create(any(), any(), any()) } returns Result.failure(Exception("create fail"))

        // WHEN: вызываем create(...)
        var callbackResult: Result<Ticket>? = null
        vm.create("x", "y", nowStr) {
            callbackResult = it
        }
        advanceUntilIdle()

        // THEN: isProcessing сброшен, callbackResult.isFailure и message == "create fail"
        assertFalse(vm.isProcessing)
        assertTrue(callbackResult!!.isFailure)
        assertEquals("create fail", callbackResult!!.exceptionOrNull()?.message)
    }

    /**
     * Сценарий: успешное обновление тикета
     * Должен сбросить флаг обработки и вернуть обновлённый тикет через callback
     */
    @Test
    fun update_success_invokes_callback_and_resets_processing() = runTest {
        // GIVEN: repo.update возвращает Result.success(updatedTicket)
        val nowStr = "2025-05-05T17:00:00Z"
        val updated = Ticket(
            id = 4L,
            userId = 9L,
            address = "ул. Лермонтова, 15",
            ticketType = "vip",
            ticket = "D012",
            scheduledAt = OffsetDateTime.parse(nowStr)
        )
        coEvery { repo.update(4, "ул. Лермонтова, 15", "vip", nowStr) } returns Result.success(updated)

        // WHEN: вызываем update(...)
        var callbackResult: Result<Ticket>? = null
        vm.update(4, "ул. Лермонтова, 15", "vip", nowStr) {
            callbackResult = it
        }
        advanceUntilIdle()

        // THEN: isProcessing сброшен, callbackResult.isSuccess и callbackResult.getOrNull() == updated
        assertFalse(vm.isProcessing)
        assertTrue(callbackResult!!.isSuccess)
        assertEquals(updated, callbackResult!!.getOrNull())
    }

    /**
     * Сценарий: ошибка при обновлении тикета
     * Должен сбросить флаг обработки и вернуть ошибку через callback
     */
    @Test
    fun update_failure_invokes_callback_with_failure_and_resets_processing() = runTest {
        // GIVEN: repo.update возвращает Result.failure(Exception("update fail"))
        coEvery { repo.update(any(), any(), any(), any()) } returns Result.failure(Exception("update fail"))

        // WHEN: вызываем update(...)
        var callbackResult: Result<Ticket>? = null
        vm.update(1, "a", "b", "c") {
            callbackResult = it
        }
        advanceUntilIdle()

        // THEN: isProcessing сброшен, callbackResult.isFailure и message == "update fail"
        assertFalse(vm.isProcessing)
        assertTrue(callbackResult!!.isFailure)
        assertEquals("update fail", callbackResult!!.exceptionOrNull()?.message)
    }

    /**
     * Сценарий: успешное удаление тикета
     * Должен сбросить флаг обработки и вернуть success через callback
     */
    @Test
    fun delete_success_invokes_callback_and_resets_processing() = runTest {
        // GIVEN: repo.delete возвращает Result.success(Unit)
        coEvery { repo.delete(10) } returns Result.success(Unit)

        // WHEN: вызываем delete(10)
        var callbackResult: Result<Unit>? = null
        vm.delete(10) {
            callbackResult = it
        }
        advanceUntilIdle()

        // THEN: isProcessing сброшен, callbackResult.isSuccess
        assertFalse(vm.isProcessing)
        assertTrue(callbackResult!!.isSuccess)
    }

    /**
     * Сценарий: ошибка при удалении тикета
     * Должен сбросить флаг обработки и вернуть ошибку через callback
     */
    @Test
    fun delete_failure_invokes_callback_with_failure_and_resets_processing() = runTest {
        // GIVEN: repo.delete возвращает Result.failure(Exception("delete fail"))
        coEvery { repo.delete(any()) } returns Result.failure(Exception("delete fail"))

        // WHEN: вызываем delete(20)
        var callbackResult: Result<Unit>? = null
        vm.delete(20) {
            callbackResult = it
        }
        advanceUntilIdle()

        // THEN: isProcessing сброшен, callbackResult.isFailure и message == "delete fail"
        assertFalse(vm.isProcessing)
        assertTrue(callbackResult!!.isFailure)
        assertEquals("delete fail", callbackResult!!.exceptionOrNull()?.message)
    }
}

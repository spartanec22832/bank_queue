package com.sfedu.bank_queue_android.mapper

import com.sfedu.bank_queue_android.model.Ticket
import com.sfedu.bank_queue_android.network.dto.TicketResponseDto
import org.junit.Test
import org.junit.Assert.*
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class TicketMappersTest {

    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    /**
     * Сценарий: преобразование TicketResponseDto → Ticket
     * Должно распарсить строку scheduledAt в OffsetDateTime и скопировать все поля
     */
    @Test
    fun to_domain_parses_scheduledAt_and_maps_all_fields() {
        // GIVEN: DTO с ISO-строкой даты и остальными полями
        val iso = "2025-06-26T12:34:56+03:00"
        val dto = TicketResponseDto(
            id = 7L,
            userId = 42L,
            address = "ул. Ленина, 1",
            ticketType = "standard",
            ticket = "A100",
            scheduledAt = iso
        )

        // WHEN: вызываем toDomain()
        val domain = dto.toDomain()

        // THEN: все поля совпадают и дата распознана правильно
        assertEquals(7L, domain.id)
        assertEquals(42L, domain.userId)
        assertEquals("ул. Ленина, 1", domain.address)
        assertEquals("standard", domain.ticketType)
        assertEquals("A100", domain.ticket)
        assertEquals(OffsetDateTime.parse(iso, formatter), domain.scheduledAt)
    }

    /**
     * Сценарий: преобразование Ticket → TicketCreateDto
     * Должно форматировать scheduledAt в ISO-строку и скопировать остальные поля
     */
    @Test
    fun to_create_dto_formats_scheduledAt_and_copies_fields() {
        // GIVEN: доменная модель с текущим OffsetDateTime
        val dt = OffsetDateTime.now()
        val domain = Ticket(
            id = null,
            userId = 0L,
            address = "пр. Мира, 5",
            ticketType = "vip",
            ticket = "B200",
            scheduledAt = dt
        )

        // WHEN: вызываем toCreateDto()
        val dto = domain.toCreateDto()

        // THEN: поля address и ticketType совпадают, а scheduledAt отформатирован в ISO
        assertEquals("пр. Мира, 5", dto.address)
        assertEquals("vip", dto.ticketType)
        assertEquals(dt.format(formatter), dto.scheduledAt)
    }

    /**
     * Сценарий: преобразование Ticket → TicketUpdateDto
     * Должно форматировать scheduledAt в ISO-строку и скопировать только address и ticketType
     */
    @Test
    fun to_update_dto_formats_scheduledAt_and_copies_fields() {
        // GIVEN: доменная модель с конкретной датой
        val dt = OffsetDateTime.parse("2025-01-01T00:00:00Z", formatter)
        val domain = Ticket(
            id = 9L,
            userId = 1L,
            address = "ул. Пушкина, 10",
            ticketType = "standard",
            ticket = "C300",
            scheduledAt = dt
        )

        // WHEN: вызываем toUpdateDto()
        val dto = domain.toUpdateDto()

        // THEN: поля address и ticketType совпадают, а scheduledAt отформатирован в ISO
        assertEquals("ул. Пушкина, 10", dto.address)
        assertEquals("standard", dto.ticketType)
        assertEquals("2025-01-01T00:00:00Z", dto.scheduledAt)
    }
}

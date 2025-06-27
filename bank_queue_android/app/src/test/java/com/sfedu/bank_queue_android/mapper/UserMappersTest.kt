package com.sfedu.bank_queue_android.mapper

import com.sfedu.bank_queue_android.model.User
import com.sfedu.bank_queue_android.network.dto.UserResponseDto
import org.junit.Test
import org.junit.Assert.*

class UserMappersTest {

    /**
     * Сценарий: маппинг UserResponseDto → User
     * Должен корректно скопировать все поля из DTO в доменный объект
     */
    @Test
    fun to_domain_maps_all_fields_correctly() {
        // GIVEN: DTO с заполненными полями
        val dto = UserResponseDto(
            id = 10,
            name = "Иван",
            login = "ivan10",
            email = "ivan@example.com",
            phoneNumber = "+70000000000"
        )

        // WHEN: вызываем toDomain()
        val domain = dto.toDomain()

        // THEN: все поля из DTO совпадают с полями в домене
        assertEquals(10, domain.id)
        assertEquals("Иван", domain.name)
        assertEquals("ivan10", domain.login)
        assertEquals("ivan@example.com", domain.email)
        assertEquals("+70000000000", domain.phoneNumber)
    }

    /**
     * Сценарий: маппинг User → UserCreateDto
     * Должен включать пароль и копировать остальные поля пользователя
     */
    @Test
    fun to_create_dto_includes_password_and_copies_fields() {
        // GIVEN: доменный объект User и строка пароля
        val user = User(
            id = 0,
            name = "Пётр",
            login = "petr5",
            email = "petr@example.com",
            phoneNumber = "+71111111111"
        )
        val pwd = "secret123"

        // WHEN: вызываем toCreateDto()
        val dto = user.toCreateDto(password = pwd)

        // THEN: поля name, login, email, password и phoneNumber скопированы в DTO
        assertEquals("Пётр", dto.name)
        assertEquals("petr5", dto.login)
        assertEquals("petr@example.com", dto.email)
        assertEquals("secret123", dto.password)
        assertEquals("+71111111111", dto.phoneNumber)
    }

    /**
     * Сценарий: маппинг User → UserUpdateDto
     * Должен копировать только имя, email и номер телефона
     */
    @Test
    fun to_update_dto_copies_only_name_email_and_phone() {
        // GIVEN: доменный объект User с заполненными полями
        val user = User(
            id = 5,
            name = "Ольга",
            login = "olga",
            email = "olga@example.com",
            phoneNumber = "+72222222222"
        )

        // WHEN: вызываем toUpdateDto()
        val dto = user.toUpdateDto()

        // THEN: в DTO присутствуют только поля name, email и phoneNumber
        assertEquals("Ольга", dto.name)
        assertEquals("olga@example.com", dto.email)
        assertEquals("+72222222222", dto.phoneNumber)
    }
}

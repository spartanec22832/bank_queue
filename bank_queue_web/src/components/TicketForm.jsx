// src/components/TicketForm.jsx
import React, { useState } from 'react';
import { createTicket } from '../services/tickets.js';
import ReactDatePicker, { registerLocale } from 'react-datepicker';
import ru from 'date-fns/locale/ru';
import 'react-datepicker/dist/react-datepicker.css';

// регистрируем русскую локаль под ключом 'ru'
registerLocale('ru', ru);

// Варианты типов талона — совпадают с префикс-картой на бэке
const TICKET_TYPE_OPTIONS = [
    "Вклад",
    "Кредит",
    "Карты",
    "Инвестиции",
    "Счета",
];

// Пример статического списка адресов; при необходимости замените на реальные
const ADDRESS_OPTIONS = [
    'пр. М.Нагибина, 32А',
    'пр. Соколова, 62',
    'пр. Буденновского, 97'
];

export default function TicketForm({ onCreated }) {
    const [form, setForm] = useState({
        address: '',
        ticketType: '',
        scheduledDate: null,
        scheduledTime: ''
    });
    const [error, setError]     = useState('');
    const [success, setSuccess] = useState('');

    // Генерируем слоты каждые 15 минут с 08:00 до 17:00
    const times = [];
    const start = 8 * 60;    // 08:00 в минутах
    const end   = 17 * 60;   // 17:00 в минутах
    for (let mins = start; mins <= end; mins += 15) {
        const hh = String(Math.floor(mins / 60)).padStart(2, '0');
        const mm = String(mins % 60).padStart(2, '0');
        times.push(`${hh}:${mm}`);
    }

    const handleChange = e => {
        const { name, value } = e.target;
        setForm(prev => ({ ...prev, [name]: value }));
    };

    const handleDateChange = date => {
        setForm(prev => ({ ...prev, scheduledDate: date }));
    };

    const handleSubmit = async e => {
        e.preventDefault();
        setError('');
        setSuccess('');

        const { address, ticketType, scheduledDate, scheduledTime } = form;
        if (!address || !ticketType || !scheduledDate || !scheduledTime) {
            setError('Пожалуйста, заполните все поля');
            return;
        }

        // Собираем локальную дату-время с учётом смещения +03:00
        const year  = scheduledDate.getFullYear();
        const month = String(scheduledDate.getMonth() + 1).padStart(2, '0');
        const day   = String(scheduledDate.getDate()).padStart(2, '0');
        const scheduledAt = `${year}-${month}-${day}T${scheduledTime}:00+03:00`;

        try {
            const dto = { address, ticketType, scheduledAt };
            const { data } = await createTicket(dto);
            onCreated(data);

            setForm({
                address: '',
                ticketType: '',
                scheduledDate: null,
                scheduledTime: ''
            });
            setSuccess('Тикет успешно создан!');
        } catch (err) {
            if (err.response?.status === 403) {
                setError('Ошибка создания тикета, выберите другое время!');
            } else {
                const msg = err.response?.data?.message || err.message || 'Ошибка создания тикета';
                setError(msg);
            }
        }
    };

    return (
        <form onSubmit={handleSubmit} className="ticket-form">
            <h2>Новый тикет</h2>

            <label>
                Адрес:
                <select
                    name="address"
                    value={form.address}
                    onChange={handleChange}
                    required
                >
                    <option value="" disabled>— выберите адрес —</option>
                    {ADDRESS_OPTIONS.map(a => (
                        <option key={a} value={a}>{a}</option>
                    ))}
                </select>
            </label>

            <label>
                Тип талона:
                <select
                    name="ticketType"
                    value={form.ticketType}
                    onChange={handleChange}
                    required
                >
                    <option value="" disabled>— выберите тип талона —</option>
                    {TICKET_TYPE_OPTIONS.map(t => (
                        <option key={t} value={t}>{t}</option>
                    ))}
                </select>
            </label>

            <div style={{ display: 'flex', gap: '1rem' }}>
                <label style={{ flex: 1 }}>
                    Дата:
                    <ReactDatePicker
                        selected={form.scheduledDate}
                        onChange={handleDateChange}
                        locale="ru"
                        dateFormat="dd.MM.yyyy"
                        className="form-input"
                        placeholderText="дд.мм.гггг"
                        required
                    />
                </label>
                <label style={{ flex: 1 }}>
                    Время:
                    <select
                        name="scheduledTime"
                        value={form.scheduledTime}
                        onChange={handleChange}
                        required
                    >
                        <option value="" disabled>— выбрать время —</option>
                        {times.map(t => (
                            <option key={t} value={t}>{t}</option>
                        ))}
                    </select>
                </label>
            </div>

            <button type="submit" className="cta-btn">
                Записаться
            </button>
            {error   && <p className="error-text">{error}</p>}
            {success && <p className="success-text">{success}</p>}
        </form>
    );
}

// src/pages/EditTicketPage.jsx
import React, { useState, useEffect, useContext } from 'react';
import { useParams, useNavigate, Navigate } from 'react-router-dom';
import {
    fetchTicketById,
    updateTicket,
    deleteTicket
} from '../services/tickets.js';
import { AuthContext } from '../contexts/AuthContext.jsx';
import ReactDatePicker, { registerLocale } from 'react-datepicker';
import ru from 'date-fns/locale/ru';
import 'react-datepicker/dist/react-datepicker.css';
import './EditTicketPage.css';

// Регистрация русской локали
registerLocale('ru', ru);

// Статические списки (можно потом заменить на fetch)
const ADDRESSES = [
    'пр. М.Нагибина, 32А',
    'пр. Соколова, 62',
    'пр. Буденновского, 97'
];
const TICKET_TYPES = [
    'Вклад',
    'Кредит',
    'Карты',
    'Инвестиции',
    'Счета'
];
// Генерация слотов времени каждые 15 минут от 08:00 до 17:00 включительно
const TIMES = [];
const start = 8 * 60;    // 08:00 в минутах
const end   = 17 * 60;   // 17:00 в минутах
for (let mins = start; mins <= end; mins += 15) {
    const hh = String(Math.floor(mins / 60)).padStart(2, '0');
    const mm = String(mins % 60).padStart(2, '0');
    TIMES.push(`${hh}:${mm}`);
}

export default function EditTicketPage() {
    const { user } = useContext(AuthContext);
    const { id }   = useParams();                // из URL: /tickets/:id/edit
    const navigate = useNavigate();

    const [ticketNum, setTicketNum] = useState('');
    const [form, setForm] = useState({
        address:    '',
        ticketType: '',
        date:       null,   // Date или null
        time:       ''
    });
    const [loading, setLoading] = useState(true);
    const [error, setError]     = useState('');

    // Загрузка тикета
    useEffect(() => {
        fetchTicketById(id)
            .then(res => {
                const t = res.data;
                setTicketNum(t.ticket);
                const dt = new Date(t.scheduledAt);
                setForm({
                    address:    t.address,
                    ticketType: t.ticketType,
                    date:       dt,
                    time:       dt.toTimeString().slice(0,5)
                });
            })
            .catch(() => setError('Не удалось загрузить тикет'))
            .finally(() => setLoading(false));
    }, [id]);

    if (!user) {
        return <Navigate to="/login" replace />;
    }
    if (loading) {
        return <p className="center-text">Загрузка…</p>;
    }

    const handleChange = e => {
        const { name, value } = e.target;
        setForm(f => ({ ...f, [name]: value }));
    };

    const handleDateChange = date => {
        setForm(f => ({ ...f, date }));
    };

    const handleSave = async e => {
        e.preventDefault();
        setError('');
        const { address, ticketType, date, time } = form;
        if (!address || !ticketType || !date || !time) {
            setError('Пожалуйста, заполните все поля');
            return;
        }

        // Собираем локальную дату-время с учётом +03:00
        const year  = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day   = String(date.getDate()).padStart(2, '0');
        const scheduledAt = `${year}-${month}-${day}T${time}:00+03:00`;

        try {
            await updateTicket(id, {
                address,
                ticketType,
                scheduledAt
            });
            navigate('/tickets');
        } catch (err) {
            setError(err.response?.data?.message || 'Ошибка при сохранении');
        }
    };

    const handleDelete = async () => {
        if (!window.confirm('Удалить этот тикет?')) return;
        try {
            await deleteTicket(id);
            navigate('/tickets');
        } catch {
            setError('Ошибка при удалении');
        }
    };

    return (
        <div className="page-container">
            <div className="card edit-card">
                <h1 className="card-title">Редактировать билет №{ticketNum}</h1>
                {error && <p className="error-text">{error}</p>}

                <form onSubmit={handleSave} className="edit-form">
                    <label className="form-label">
                        Адрес:
                        <select
                            name="address"
                            value={form.address}
                            onChange={handleChange}
                            className="form-select"
                            required
                        >
                            <option value="" disabled>— выберите адрес —</option>
                            {ADDRESSES.map(a => (
                                <option key={a} value={a}>{a}</option>
                            ))}
                        </select>
                    </label>

                    <label className="form-label">
                        Тип талона:
                        <select
                            name="ticketType"
                            value={form.ticketType}
                            onChange={handleChange}
                            className="form-select"
                            required
                        >
                            <option value="" disabled>— выберите тип талона —</option>
                            {TICKET_TYPES.map(t => (
                                <option key={t} value={t}>{t}</option>
                            ))}
                        </select>
                    </label>

                    <div className="datetime-group">
                        <label className="form-label">
                            Дата:
                            <ReactDatePicker
                                selected={form.date}
                                onChange={handleDateChange}
                                locale="ru"
                                dateFormat="dd.MM.yyyy"
                                className="form-input"
                                placeholderText="дд.мм.гггг"
                                required
                            />
                        </label>
                        <label className="form-label">
                            Время:
                            <select
                                name="time"
                                value={form.time}
                                onChange={handleChange}
                                className="form-select time-select"
                                required
                            >
                                <option value="" disabled>— выберите время —</option>
                                {TIMES.map(t => (
                                    <option key={t} value={t}>{t}</option>
                                ))}
                            </select>
                        </label>
                    </div>

                    <div className="btn-group">
                        <button type="submit" className="btn save-btn">
                            Сохранить
                        </button>
                        <button
                            type="button"
                            className="btn delete-btn"
                            onClick={handleDelete}
                        >
                            Удалить
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

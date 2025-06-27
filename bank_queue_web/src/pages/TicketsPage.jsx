// src/pages/TicketsPage.jsx
import React, { useEffect, useState, useContext } from 'react';
import { Link } from 'react-router-dom';
import { AuthContext }   from '../contexts/AuthContext.jsx';
import { fetchMyTickets } from '../services/tickets.js';
import './TicketsPage.css';

export default function TicketsPage() {
    const { user } = useContext(AuthContext);
    const [tickets, setTickets]   = useState([]);
    const [error, setError]       = useState('');
    const [loading, setLoading]   = useState(true);

    useEffect(() => {
        if (!user) {
            setLoading(false);
            return;
        }
        fetchMyTickets()
            .then(res => setTickets(res.data || []))
            .catch(err => {
                setError(err.response?.data?.message || err.message || 'Ошибка при загрузке');
            })
            .finally(() => {
                setLoading(false);
            });
    }, [user]);

    if (!user) {
        return <p className="tickets-message">Чтобы увидеть тикеты — пожалуйста, войдите.</p>;
    }

    if (loading) {
        return <p className="tickets-message">Загрузка тикетов…</p>;
    }

    return (
        <div className="page-container">
            <div className="tickets-card">
                <h1>Мои тикеты</h1>
                {error && <p className="error-text">{error}</p>}
                {tickets.length === 0 ? (
                    <p>У вас пока нет тикетов.</p>
                ) : (
                    <div className="tickets-list">
                        {tickets.map(t => (
                            <Link
                                to={`/tickets/${t.id}/edit`}
                                key={t.id}
                                className="ticket-card"
                            >
                                <div className="ticket-number">№ {t.ticket}</div>
                                <div className="ticket-info">
                                    <div><strong>Тип:</strong> {t.ticketType}</div>
                                    <div><strong>Адрес:</strong> {t.address}</div>
                                    <div>
                                        <strong>Дата:</strong>{' '}
                                        {new Date(t.scheduledAt).toLocaleDateString('ru-RU', {
                                            day:   '2-digit',
                                            month: '2-digit',
                                            year:  'numeric'
                                        })}
                                    </div>
                                    <div>
                                        <strong>Время:</strong>{' '}
                                        {new Date(t.scheduledAt).toLocaleTimeString('ru-RU', {
                                            hour:   '2-digit',
                                            minute: '2-digit'
                                        })}
                                    </div>
                                </div>
                            </Link>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}

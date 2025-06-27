// src/pages/HomePage.jsx
import React, { useContext } from 'react';
import { AuthContext } from '../contexts/AuthContext.jsx';
import TicketForm from '../components/TicketForm.jsx';
import './HomePage.css';

export default function HomePage() {
    const { user } = useContext(AuthContext);

    // Пока пользователь не вошёл — показываем гостевой баннер
    if (!user) {
        return (
            <section className="hero-section">
                <h1 className="hero-title">
                    <span className="no-wrap">Добро пожаловать в наш банк!</span>
                </h1>
                <p className="hero-subtitle">
                    Зарегистрируйтесь, чтобы получить цифровой талон и записаться на приём.
                </p>
            </section>
        );
    }

    // Пользователь вошёл — показываем форму без списка тикетов
    return (
        <div className="page-container">
            <div className="card">
                <h1 className="card-title">Добро пожаловать в наш банк!</h1>
                <p className="card-subtitle">
                    Заполните форму ниже, чтобы записаться на приём.
                </p>

                <TicketForm
                    onCreated={newTicket => {
                        // если нужно что-то сделать при создании,
                        // например, показать уведомление
                        console.log('Создан новый тикет', newTicket);
                    }}
                />

                {/* Блок "Мои тикеты" удалён */}
            </div>
        </div>
    );
}

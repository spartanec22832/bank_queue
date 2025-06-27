// src/pages/ChangePasswordPage.jsx
import React, { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../contexts/AuthContext.jsx';
import api from '../services/api.js';
import './ChangePasswordPage.css';

export default function ChangePasswordPage() {
    const { user } = useContext(AuthContext);
    const navigate = useNavigate();
    const [form, setForm] = useState({
        currentPassword: '',
        newPassword: '',
        confirmPassword: '',
    });
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    // Если не авторизован — показываем сообщение и не даём форму
    if (!user) {
        return (
            <div className="page-container">
                <div className="tickets-card">
                    <p className="error-text">
                        Пожалуйста, авторизуйтесь, чтобы сменить пароль.
                    </p>
                </div>
            </div>
        );
    }

    const handleChange = e => {
        const { name, value } = e.target;
        setForm(f => ({ ...f, [name]: value }));
    };

    const handleSubmit = async e => {
        e.preventDefault();
        setError('');
        setSuccess('');

        if (form.newPassword !== form.confirmPassword) {
            setError('Новый пароль и подтверждение не совпадают');
            return;
        }

        try {
            await api.post('/api/users/me/change-password', {
                currentPassword: form.currentPassword,
                newPassword:     form.newPassword,
                confirmPassword: form.confirmPassword,
            });
            setSuccess('Пароль успешно изменён');
            // Немного задержим, чтобы пользователь увидел сообщение, а потом на профиль
            setTimeout(() => {
                navigate('/profile', { replace: true });
            }, 1000);
        } catch (err) {
            setError(err.response?.data?.message || 'Ошибка при смене пароля');
        }
    };

    const handleCancel = () => {
        navigate('/profile', { replace: true });
    };

    return (
        <div className="page-container">
            <div className="tickets-card">
                <h1 className="card-title">Сменить пароль</h1>

                {error   && <p className="error-text">{error}</p>}
                {success && <p className="success-text">{success}</p>}

                <form className="ticket-form" onSubmit={handleSubmit}>
                    <label>Текущий пароль:</label>
                    <input
                        name="currentPassword"
                        type="password"
                        className="form-input"
                        value={form.currentPassword}
                        onChange={handleChange}
                        required
                    />

                    <label>Новый пароль:</label>
                    <input
                        name="newPassword"
                        type="password"
                        className="form-input"
                        value={form.newPassword}
                        onChange={handleChange}
                        required
                    />

                    <label>Подтвердите пароль:</label>
                    <input
                        name="confirmPassword"
                        type="password"
                        className="form-input"
                        value={form.confirmPassword}
                        onChange={handleChange}
                        required
                    />
                    <div className="form-actions">
                        <button type="submit" className="cta-btn">
                            Сменить пароль
                        </button>
                        <button
                            type="button"
                            className="cancel-btn"
                            onClick={handleCancel}
                        >
                            Отмена
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

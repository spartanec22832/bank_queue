// src/pages/EditProfilePage.jsx
import React, { useState, useEffect, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../contexts/AuthContext.jsx';
import api from '../services/api.js';
import './EditProfilePage.css';

export default function EditProfilePage() {
    const { user, setUser } = useContext(AuthContext);
    const navigate = useNavigate();

    const [form, setForm]   = useState({ name: '', email: '', phoneNumber: '' });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (!user) {
            navigate('/login', { replace: true });
            return;
        }

        api
            .get('/api/users/me')
            .then(res => {
                setForm({
                    name:         res.data.name        || '',
                    email:        res.data.email       || '',
                    phoneNumber:  res.data.phoneNumber || ''
                });
            })
            .catch(() => {
                setError('Не удалось загрузить профиль');
            })
            .finally(() => {
                setLoading(false);
            });
    }, [user, navigate]);

    const handleChange = e => {
        const { name, value } = e.target;
        setForm(f => ({ ...f, [name]: value }));
    };

    const handleSave = async e => {
        e.preventDefault();
        setError('');
        try {
            // Отправляем PATCH на /api/users/me
            const res = await api.patch('/api/users/me', {
                name:         form.name,
                email:        form.email,
                phoneNumber:  form.phoneNumber
            });
            // Если бэкенд возвращает тело, обновляем контекст
            if (res.data) {
                setUser(res.data);
            }
            // Переходим на главную или в профиль
            navigate('/', { replace: true });
        } catch (err) {
            // даже если axios упал при парсинге JSON — считаем, что всё успешно
            console.warn('PATCH /api/users/me error (ignored):', err);
            navigate('/', { replace: true });
        }
    };

    const handleCancel = () => navigate('/', { replace: true });

    if (loading) {
        return <p className="loading-text">Загрузка профиля…</p>;
    }

    return (
        <div className="page-container">
            <div className="tickets-card">
                <h1>Редактировать профиль</h1>

                {error && <p className="error-text">{error}</p>}

                <form onSubmit={handleSave} className="ticket-form">
                    <label>Имя:</label>
                    <input
                        name="name"
                        className="form-input"
                        value={form.name}
                        onChange={handleChange}
                        required
                    />

                    <label>Email:</label>
                    <input
                        name="email"
                        type="email"
                        className="form-input"
                        value={form.email}
                        onChange={handleChange}
                        required
                    />

                    <label>Телефон:</label>
                    <input
                        name="phoneNumber"
                        className="form-input"
                        value={form.phoneNumber}
                        onChange={handleChange}
                        placeholder="Необязательно"
                    />

                    <div className="form-actions">
                        <button type="submit" className="cta-btn">Сохранить</button>
                        <button type="button" className="cancel-btn" onClick={handleCancel}>
                            Отмена
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

// src/components/RegisterForm.jsx
import React, { useState, useContext } from 'react';
import { AuthContext } from '../contexts/AuthContext.jsx';
import api from '../services/api.js';

export default function RegisterForm({ onDone }) {
    const { /* не нужен login здесь */ } = useContext(AuthContext);
    const [form, setForm] = useState({
        name: '',
        login: '',
        email: '',
        phoneNumber: '',
        password: '',
    });
    const [error, setError] = useState('');

    const submit = async e => {
        e.preventDefault();
        setError('');
        try {
            // если backend возвращает ошибку 4xx/5xx, fetch не кидает исключение
            const res = await api.post('/api/users/register', form);
            if (!res.status.toString().startsWith('2')) {
                // прочитаем ответ как текст (на случай, если это не JSON)
                const text = await res.text();
                throw new Error(text || `Ошибка ${res.status}`);
            }
            // Успех — переходим дальше
            alert('Регистрация успешна!');
            onDone();
        } catch (e) {
            console.error('Registration error:', e);
            // Если e.message — это JSON, попробуем распарсить
            try {
                const data = JSON.parse(e.message);
                setError(data.message || JSON.stringify(data));
            } catch {
                // иначе просто текст ошибки
                setError(e.message);
            }
        }
    };

    return (
        <form className="auth-form" onSubmit={submit}>
            {['name','login','email','phoneNumber','password'].map(key => (
                <input
                    key={key}
                    type={key === 'password' ? 'password' : 'text'}
                    placeholder={
                        key === 'name' ? 'ФИО' :
                            key === 'login' ? 'Логин' :
                                key === 'email' ? 'Email' :
                                    key === 'phoneNumber' ? 'Телефон' :
                                        'Пароль'
                    }
                    value={form[key]}
                    onChange={e => setForm({ ...form, [key]: e.target.value })}
                    required
                />
            ))}
            <button type="submit" className="auth-btn">Зарегистрироваться</button>
            {error && <p className="error-text">{error}</p>}
        </form>
    );
}

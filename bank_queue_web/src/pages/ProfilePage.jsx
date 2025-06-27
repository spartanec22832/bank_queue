// src/pages/ProfilePage.jsx
import React, { useEffect, useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../contexts/AuthContext.jsx';
import { fetchMyProfile } from '../services/users.js';
import './ProfilePage.css';

export default function ProfilePage() {
    const { logout } = useContext(AuthContext);
    const [profile, setProfile] = useState(null);
    const [error, setError]     = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        fetchMyProfile()
            .then(res => {
                setProfile(res.data);
            })
            .catch(err => {
                if (err.response?.status === 403) {
                    setError('Пожалуйста, авторизуйтесь, чтобы увидеть профиль.');
                } else {
                    setError(err.response?.data?.message || err.message || 'Ошибка при загрузке профиля');
                }
            });
    }, []);

    if (!profile && !error) {
        return <p className="loading-text">Загрузка профиля…</p>;
    }

    if (error) {
        return <p className="error-text">{error}</p>;
    }

    return (
        <div className="page-container">
            <div className="profile-card">
                <h1 className="card-title">Профиль</h1>

                <div className="profile-info">
                    <p><strong>Имя:</strong> {profile.name}</p>
                    <p><strong>Логин:</strong> {profile.login}</p> {/* здесь */}
                    <p><strong>Email:</strong> {profile.email}</p>
                    <p><strong>Телефон:</strong> {profile.phoneNumber}</p>
                </div>

                <div className="profile-actions">
                    <button
                        className="cta-btn"
                        onClick={() => navigate('/profile/edit')}
                    >
                        Редактировать профиль
                    </button>
                    <button
                        className="cta-btn"
                        onClick={() => navigate('/profile/change-password')}
                    >
                        Сменить пароль
                    </button>
                    <button
                        className="delete-btn"
                        onClick={() => {
                            logout();
                            navigate('/', { replace: true });
                        }}
                    >
                        Выйти
                    </button>
                </div>
            </div>
        </div>
    );
}

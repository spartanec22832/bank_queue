// src/App.jsx
import React from 'react'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './contexts/AuthContext.jsx'

// Компоненты
import Layout             from './components/Layout.jsx'
import LoginPage          from './pages/LoginPage.jsx'
import HomePage           from './pages/HomePage.jsx'
import TicketsPage        from './pages/TicketsPage.jsx'
import EditTicketPage     from './pages/EditTicketPage.jsx'
import ProfilePage        from './pages/ProfilePage.jsx'
import EditProfilePage    from './pages/EditProfilePage.jsx'
import ChangePasswordPage from './pages/ChangePasswordPage.jsx'

export default function App() {
    return (
        <AuthProvider>
            <BrowserRouter>
                <Routes>
                    {/* 1) Страница входа — вне Layout */}
                    <Route path="/login" element={<LoginPage />} />

                    {/* 2) Всё остальное — под Layout */}
                    <Route element={<Layout />}>
                        {/* Главная */}
                        <Route path="/" element={<HomePage />} />

                        {/* Тикеты */}
                        <Route path="tickets" element={<TicketsPage />} />
                        <Route path="tickets/:id/edit" element={<EditTicketPage />} />

                        {/* Профиль — показывает информацию и кнопки */}
                        <Route path="profile" element={<ProfilePage />} />

                        {/* Редактирование профиля */}
                        <Route path="profile/edit" element={<EditProfilePage />} />

                        {/* Смена пароля */}
                        <Route path="profile/change-password" element={<ChangePasswordPage />} />
                    </Route>

                    {/* 3) Все остальные пути → на главную */}
                    <Route path="*" element={<Navigate to="/" replace />} />
                </Routes>
            </BrowserRouter>
        </AuthProvider>
    )
}

// src/components/Layout.jsx
import React, { useState, useContext } from 'react';
import { Link, Outlet } from 'react-router-dom';
import { AuthContext }  from '../contexts/AuthContext.jsx';
import LoginForm        from './LoginForm.jsx';
import RegisterForm     from './RegisterForm.jsx';
import '../App.css';

export default function Layout() {
    const [isSidebarOpen, setIsSidebarOpen] = useState(false);
    const [mode, setMode]                   = useState(null);
    const { user, logout }                  = useContext(AuthContext);

    const toggleSidebar = () => {
        setIsSidebarOpen(o => !o);
        setMode(null);
    };

    return (
        <div className="App animated-background">
            <header className="navbar">
                <Link to="/" className="logo">🏦</Link>
                <nav className="nav-menu">
                    <Link to="/"        className="nav-btn">Главная</Link>
                    <Link to="/profile" className="nav-btn">Профиль</Link>
                    <Link to="/tickets" className="nav-btn">Мои тикеты</Link>
                </nav>
                <div className="burger-menu" onClick={toggleSidebar}>☰</div>
            </header>

            <main className="main-content">
                <Outlet />
            </main>

            {isSidebarOpen && (
                <div className="sidebar">
                    <div className="sidebar-topbar">
                        <button className="close-btn" onClick={toggleSidebar}>×</button>
                    </div>
                    {!user && mode === null && (
                        <div className="auth-buttons">
                            <button className="auth-btn" onClick={() => setMode('login')}>Авторизация</button>
                            <button className="auth-btn" onClick={() => setMode('register')}>Регистрация</button>
                        </div>
                    )}
                    {!user && mode === 'login'    && <LoginForm    onDone={toggleSidebar} />}
                    {!user && mode === 'register' && <RegisterForm onDone={toggleSidebar} />}
                    {user && (
                        <div style={{ padding:'1rem', textAlign:'center' }}>
                            <p>Вы уже авторизованы как {user.username}</p>
                            <button className="auth-btn" onClick={() => { logout(); toggleSidebar(); }}>Выйти</button>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
}

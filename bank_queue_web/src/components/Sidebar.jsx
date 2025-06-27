import React from 'react';
export default function Sidebar({ onClose, onChoose }) {
    return (
        <div className="sidebar">
            <div className="sidebar-topbar">
                <button className="close-btn" onClick={onClose}>×</button>
            </div>
            <div className="auth-buttons">
                <button className="auth-btn" onClick={()=>onChoose('register')}>Регистрация</button>
                <button className="auth-btn" onClick={()=>onChoose('login')}>Авторизация</button>
            </div>
        </div>
    );
}

import React, { useState, useContext } from 'react';
import { AuthContext } from '../contexts/AuthContext.jsx';

export default function LoginForm({ onDone }) {
    const { login } = useContext(AuthContext);
    const [form, setForm] = useState({ username: '', password: '' });
    const [err, setErr] = useState('');

    const submit = async e => {
        e.preventDefault();
        try {
            await login(form);
            onDone();
        } catch {
            setErr('Неверный логин или пароль');
        }
    };

    return (
        <form onSubmit={submit}>
            <input value={form.username} onChange={e => setForm({...form,username: e.target.value})} placeholder="Логин" required />
            <input type="password" value={form.password} onChange={e => setForm({...form,password: e.target.value})} placeholder="Пароль" required />
            <button className="auth-btn">Войти</button>
            {err && <p style={{color:'red'}}>{err}</p>}
        </form>
    );
}

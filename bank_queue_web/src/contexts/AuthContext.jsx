// src/contexts/AuthContext.jsx
import React, { createContext, useState, useEffect } from 'react';
import { doLogin, doRegister, doLogout } from '../services/auth.js';

export const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);

    // При монтировании подтягиваем токен
    useEffect(() => {
        const token = localStorage.getItem('jwtToken');
        if (token) {
            try {
                const { sub } = JSON.parse(atob(token.split('.')[1]));
                setUser({ username: sub });
            } catch {
                localStorage.removeItem('jwtToken');
            }
        }
    }, []);

    const login = async creds => {
        const { token } = await doLogin(creds);
        localStorage.setItem('jwtToken', token);
        const { sub } = JSON.parse(atob(token.split('.')[1]));
        setUser({ username: sub });
    };

    const register = async userData => {
        await doRegister(userData);
    };

    const logout = () => {
        doLogout();
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, login, register, logout }}>
            {children}
        </AuthContext.Provider>
    );
}

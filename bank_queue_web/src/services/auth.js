import api from './api.js';

// POST /auth/login → { token }
export async function doLogin({ username, password }) {
    const { data } = await api.post('/auth/login', { username, password });
    return data;
}

// POST /api/users/register
export function doRegister(user) {
    return api.post('/api/users/register', user);
}

export function doLogout() {
    localStorage.removeItem('jwtToken');
}

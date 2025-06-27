// src/services/users.js
import api from './api.js'

export const fetchMyProfile = () => api.get('/api/users/me')
export const updateMyProfile = dto => api.patch('/api/users/me', dto)
export const changeMyPassword = dto => api.post('/api/users/me/change-password', dto)

// src/services/tickets.js
import api from './api.js';

// Получить текущего пользователя
export const fetchMyTickets = () => api.get('/api/tickets');

// Создать новый тикет
export const createTicket  = dto => api.post('/api/tickets', dto);

// Получить тикет по id
export const fetchTicketById = id => api.get(`/api/tickets/${id}`);

// Обновить тикет
export const updateTicket  = (id, dto) => api.patch(`/api/tickets/${id}`, dto);

// Удалить тикет
export const deleteTicket  = id => api.delete(`/api/tickets/${id}`);

// ***********************
// Ниже — новые экспорты
// ***********************

// Список всех доступных адресов (для селекта)
export const fetchAddresses = () => api.get('/api/addresses');

// Список всех типов талонов (для селекта)
export const fetchTicketTypes = () => api.get('/api/ticket-types');

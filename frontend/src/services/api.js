import axios from 'axios';

const API_BASE = import.meta.env.VITE_API_URL || '/api';

const api = axios.create({
    baseURL: API_BASE,
    headers: {
        'Content-Type': 'application/json',
    },
});

export const analyzeEmail = async (emailData) => {
    const response = await api.post('/scans/analyze', emailData);
    return response.data;
};

export const getAllScans = async () => {
    const response = await api.get('/scans');
    return response.data;
};

export const getScanById = async (id) => {
    const response = await api.get(`/scans/${id}`);
    return response.data;
};

export const deleteScan = async (id) => {
    await api.delete(`/scans/${id}`);
};

export const getDashboardStats = async () => {
    const response = await api.get('/dashboard/stats');
    return response.data;
};

export default api;

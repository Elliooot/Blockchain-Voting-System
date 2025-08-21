import axios from 'axios';
import type { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse, AxiosError } from 'axios';

// const baseURL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api/v1";
const baseURL = import.meta.env.VITE_API_BASE_URL || "/api/v1";
  
console.log("🔍 API Base URL:", import.meta.env.VITE_API_BASE_URL);
console.log("🔍 Environment variables:", import.meta.env);

const axiosInstance: AxiosInstance = axios.create({
    baseURL: baseURL,
    timeout: 60000,
    headers: {
        'Content-Type': 'application/json',
    }
});


// Request interceptor
axiosInstance.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        const token = localStorage.getItem('token');
        if(token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error: AxiosError) => {
        return Promise.reject(error);
    }
);

// Response interceptor
axiosInstance.interceptors.response.use(
    (response: AxiosResponse) => {
        return response;
    },
    (error: AxiosError) => {
        return Promise.reject(error);
    }
);

export default axiosInstance;
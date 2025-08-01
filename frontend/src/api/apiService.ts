import axios from 'axios';

const baseURL = "http://localhost:8080/api";

const apiClient = axios.create({
    baseURL: baseURL,
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json',
    }
});

apiClient.interceptors.request.use(
    (config) => {
        console.log("📤 API Request - URL:", config.url);
        console.log("📤 API Request - Method:", config.method);
        
        const token = localStorage.getItem('authToken');
        console.log("🔑 API Request - Token exists:", !!token);
        
        if(token) {
            console.log("🔑 API Request - Token (first 50 chars):", token.substring(0, 50) + "...");
            config.headers.Authorization = `Bearer ${token}`;
            console.log("✅ API Request - Authorization header set");
        } else {
            console.log("❌ API Request - NO TOKEN FOUND!");
        }
        
        console.log("📋 API Request - Final headers:", config.headers);
        return config;
    },
    (error) => {
        console.error("❌ API Request interceptor error:", error);
        return Promise.reject(error);
    }
);

apiClient.interceptors.response.use(
    (response) => {
        console.log("✅ API Response - Status:", response.status);
        console.log("📥 API Response - Data:", response.data);
        return response;
    },
    (error) => {
        console.error("❌ API Response error:", error.response?.status, error.response?.data);
        console.error("❌ API Error details:", error);
        return Promise.reject(error);
    }
);

export const createBallot = async (ballotData: object) => {
    try {
        const response = await apiClient.post('/ballots/create', ballotData);
        return response.data;
    } catch (error) {
        console.error("Error creating ballot: " + error);
        throw error;
    }
};

export const fetchBallots = async () => {
    try {
        const response = await apiClient.get('/ballots');
        return response.data;
    } catch (error) {
        console.error("Error fetching ballots: " + error);
        throw error;
    }
};
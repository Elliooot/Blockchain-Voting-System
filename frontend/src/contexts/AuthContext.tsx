import React, { createContext, useContext, useEffect, useMemo, useState, type ReactNode } from 'react';
import { jwtDecode } from 'jwt-decode';
import axiosInstance from '../api/axiosConfig';

interface User {
    sub: string;
    role: string;
    iat: number;
    exp: number;
    firstName: string;
    lastName: string;
    email: string;
    dateOfBirth: number;
    gender: string;
    userId: string;
    walletAddress: string;
}

interface AuthContextType {
    isAuthenticated: boolean;
    user: User | null;
    token: string | null;
    login: (token: string) => void;
    logout: () => void;
    deleteAccount: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType>({
    isAuthenticated: false,
    user: null,
    token: null,
    login: () => console.warn('login function not yet initialized'),
    logout: () => console.warn('logout function not yet initialized'),
    deleteAccount: () => Promise.resolve(),
});

export const useAuth = () => {
    return useContext(AuthContext);
}

interface AuthProviderProps {
    children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
    const [token, setToken] = useState<string | null>(null);
    const [user, setUser] = useState<User | null>(null);

    useEffect(() => {
        const initializeAuth = () => {
            const persistentToken = localStorage.getItem('token');
            if(persistentToken) {
                try {
                    const decodedUser = jwtDecode<User>(persistentToken);
                    if (decodedUser.exp * 1000 > Date.now()) {
                        setUser(decodedUser);
                        setToken(persistentToken);
                        axiosInstance.defaults.headers.common['Authorization'] = `Bearer ${persistentToken}`;
                        return;
                    } else {
                        localStorage.removeItem('token');
                    }
                } catch (error) {
                    console.error('Failed to decode persistent token', error);
                    localStorage.removeItem('token');
                }
            }

            setToken(null);
            setUser(null);
            delete axiosInstance.defaults.headers.common['Authorization'];
        };

        initializeAuth();
    }, []);

    const login = (token: string) => {
        try {
            const decodedUser = jwtDecode<User>(token);
            localStorage.setItem('token', token);
            setUser(decodedUser);
            setToken(token);

            // Set the global header of axios, all subsequent requests will automatically carry the token
            axiosInstance.defaults.headers.common['Authorization'] = `Bearer ${token}`;
            
            console.log("Token:", token);
            console.log("Decoded User:", decodedUser);
        } catch (error) {
            console.error("Failed to decode token on login", error);
        }
    };

    const logout = () => {
        localStorage.removeItem('token');
        sessionStorage.removeItem('token');
        
        setToken(null);
        setUser(null)

        delete axiosInstance.defaults.headers.common['Authorization'];
    };

    const deleteAccount = async () => {
        try {
            if(token) {
                await axiosInstance.delete('/user/delete', {
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }
                });
            }

            logout(); // logout to clear all status
            console.log('Account deleted successfully');
        } catch (error) {
            console.error('Failed to delete account', error);
            logout();
            throw error;
        }
    };

    const value = useMemo(() => ({
        isAuthenticated: !!user,
        user,
        token,
        login,
        logout,
        deleteAccount
    }), [user, token]);

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
}
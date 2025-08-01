import React, { createContext, useContext, useMemo, useState, type ReactNode } from 'react';
import { jwtDecode } from 'jwt-decode';
import axios from 'axios';

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
}

interface AuthContextType {
    isAuthenticated: boolean;
    user: User | null;
    login: (token: string) => void;
    logout: () => void;
    deleteAccount: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType>({
    isAuthenticated: false,
    user: null,
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
    const [user, setUser] = useState<User | null>(() => {
        const token = localStorage.getItem('authToken');
        if(token) {
            try {
                const decodedUser = jwtDecode<User>(token);
                if(decodedUser.exp * 1000 > Date.now()) {
                    return decodedUser;
                }
            } catch (error) {
                console.error("Failed to decode token on intial load", error);
                return null;
            }
        }
        return null;
    })

    const login = (token: string) => {
        try {
            const decodedUser = jwtDecode<User>(token);
            localStorage.setItem('authToken', token)
            setUser(decodedUser);
            console.log("Token:", token);
            console.log("Decoded User:", decodedUser);
        } catch (error) {
            console.error("Failed to decode token on login", error);
        }
    };

    const logout = () => {
        localStorage.removeItem('authToken')
        setUser(null)
    };

    const deleteAccount = async () => {
        try {
            const token = localStorage.getItem('authToken');
            if(token) {
                await axios.delete('http://localhost:8080/api/user/delete', {
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'appliation/json'
                    }
                });
            }

            localStorage.removeItem('authToken');
            setUser(null);
            console.log('Account deleted successfully');
        } catch (error) {
            console.error('Failed to delete account', error);
            localStorage.removeItem('authToken');
            setUser(null);
            throw error;
        }
    };

    const value = useMemo(() => ({
        isAuthenticated: !!user,
        user,
        login,
        logout,
        deleteAccount
    }), [user]);

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
}
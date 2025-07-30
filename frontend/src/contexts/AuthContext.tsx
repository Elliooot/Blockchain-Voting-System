import React, { createContext, useContext, useMemo, useState, type ReactNode } from 'react';
import { jwtDecode } from 'jwt-decode';

interface User {
    sub: string;
    role: string;
    iat: number;
    exp: number;
}

interface AuthContextType {
    isAuthenticated: boolean;
    user: User | null;
    login: (token: string) => void;
    logout: () => void;
}

const AuthContext = createContext<AuthContextType>({
    isAuthenticated: false,
    user: null,
    login: () => console.warn('login function not yet initialized'),
    logout: () => console.warn('logout function not yet initialized'),
});

export const useAuth = () => {
    // const context = useContext(AuthContext);
    // if (context === undefined) {
    //     throw new Error('useAuth must be used within an AuthProvider');
    // }
    // return context;
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
        } catch (error) {
            console.error("Failed to decode token on login", error);
        }
    };

    const logout = () => {
        localStorage.removeItem('authToken')
        setUser(null)
    };

    const value = useMemo(() => ({
        isAuthenticated: !!user,
        user,
        login,
        logout
    }), [user]);

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
}
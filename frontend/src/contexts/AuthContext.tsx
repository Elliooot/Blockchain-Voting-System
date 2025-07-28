import React, { createContext, useContext, useEffect, useMemo, useState, type ReactNode } from 'react';
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
    isLoading: boolean; // To fix navigating to homepage redirect to login
    login: (token: string) => void;
    logout: () => void;
}

const AuthContext = createContext<AuthContextType>({
    isAuthenticated: false,
    user: null,
    isLoading: true, // To present it's loading at the beginning
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
    const [user, setUser] = useState<User | null>(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const token = localStorage.getItem('authToken');
        if(token) {
            try {
                const decodedUser = jwtDecode<User>(token);
                if(decodedUser.exp * 1000 > Date.now()) {
                    setUser(decodedUser);
                }
            } catch (error) {
                console.error("Failed to decode token on intial load", error);
                setUser(null);
            }
        }
        setIsLoading(false);
    }, []); //The empty dependencies array ensures that the effect is only executed once, when the component is mounted

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
        isLoading,
        login,
        logout
    }), [user]);

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
}
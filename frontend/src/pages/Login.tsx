import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../api/axiosConfig';
import { useAuth } from '../contexts/AuthContext';

function Login() {
    const [formData, setFormData] = useState({
        email: '',
        password: ''
    });

    const [error, setError] = useState('');
    const navigate = useNavigate();

    const { login } = useAuth();

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleLogin = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setError('');

        try {
            const response = await axiosInstance.post('/auth/authenticate', formData);
            const token = response.data.token;

            if(token) { 
                login(token);
                navigate('/dashboard');
            } else {
                setError('Login successful, but no authentication token was received.');
            }
        } catch(err: any) {
            const message = 'Login failed. Please check your email and password.';
            setError(message); 
            console.error("Login failed:", err.response.data);
        }
    }

    return (
        <div className='flex justify-center items-center w-full min-h-screen bg-gray-300'>
            <div className='w-full max-w-md p-8 space-y-6 bg-white rounded-2xl shadow-lg'>
                <div className="text-center">
                    <h1 className='text-3xl font-bold text-gray-900'>Welcome Back</h1>
                    <p className="mt-2 text-gray-600">Please log in to continue</p>
                </div>
                <form onSubmit={handleLogin} className="space-y-6">
                    <div>
                        <label htmlFor='email' className="block mb-2 text-left text-sm font-medium text-gray-700">Email</label>
                        <input 
                            type='email' 
                            id='email' 
                            name='email' 
                            value={formData.email}
                            required 
                            onChange={handleChange}
                            className="w-full p-2.5 border border-gray-300 rounded-lg bg-gray-50 focus:ring-blue-500 focus:border-blue-500 transition-colors text-gray-900"
                        />
                    </div>
                    <div>
                        <label htmlFor='password'  className="block mb-2 text-left text-sm font-medium text-gray-700">Password</label>
                        <input 
                            type='password' 
                            id='password' 
                            name='password' 
                            value={formData.password}
                            required 
                            onChange={handleChange}
                            className="w-full p-2.5 border border-gray-300 rounded-lg bg-gray-50 focus:ring-blue-500 focus:border-blue-500 transition-colors text-gray-900"
                        />
                    </div>
                    <div className='flex items-center justify-end'>
                        <a href='/forgot-password' className="text-sm text-blue-600 hover:underline">Forgot Password?</a>
                    </div>

                    {error && <p className="text-red-500 text-sm text-center">{error}</p>}

                    <button 
                        type='submit'
                        className="w-full py-2.5 px-4 border-none rounded-lg bg-blue-600 text-white font-semibold cursor-pointer hover:bg-blue-700 transition-colors"
                    >
                        Log In
                    </button>
                    
                    <p className='text-center text-sm text-gray-600'>
                        Don't have an account? <a href='/register' className="font-medium text-blue-600 hover:underline">Register here</a>
                    </p>
                </form>
            </div>
        </div>
    )
}

export default Login;

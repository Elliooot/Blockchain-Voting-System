import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../api/axiosConfig';
import { useAuth } from '../contexts/AuthContext';

function Login() {
    const [formData, setFormData] = useState({
        email: '',
        password: ''
    });

    const [rememberMe, setRememberMe] = useState(false);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const { login } = useAuth();

    useEffect(() => {
        const savedEmail = localStorage.getItem('rememberedEmail');
        const savedRememberMe = localStorage.getItem('rememberMe');

        if(savedEmail) {
            setFormData(prev => ({
                ...prev,
                email: savedEmail
            }));
        }

        if(savedRememberMe === 'true') {
            setRememberMe(true);
        }
    }, []);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleRememberMeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setRememberMe(e.target.checked);
    };

    const handleLogin = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setError('');

        try {
            const response = await axiosInstance.post('/auth/authenticate', formData);
            console.log("Login response: " + response.data);

            const token = response.data.token;

            console.log("Token: " + token);

            if(token) { // response.status === 200
                // console.log("Token: " + token);
                if(rememberMe) {
                    localStorage.setItem('rememberedEmail', formData.email);
                    localStorage.setItem('rememberMe', 'true');
                } else {
                    localStorage.removeItem('rememberedEmail');
                    localStorage.removeItem('rememberMe');
                }

                login(token, rememberMe);
                console.log("✅ Token saved to localStorage");

                navigate('/dashboard');
            } else {
                setError('Login successful, but no authentication token was received.');
            }
        } catch(err: any) {
            if(err.response){
                setError(err.response.data.message || err.response.data);
            } else {
                setError('An error occurred during user registration');
            }
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
                    <div className='flex items-center justify-between'>
                        <div className="flex items-center">
                            <input 
                                id='remember-me' 
                                name='remember-me' 
                                type='checkbox' 
                                checked={rememberMe}
                                onChange={handleRememberMeChange}
                                className="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded focus:ring-blue-500" 
                            />
                            <label htmlFor='remember-me' className="ml-2 text-sm text-gray-600">Remember Me</label>
                        </div>
                        <a href='/forgot-password' className="text-sm text-blue-600 hover:underline">Forgot Password?</a>
                    </div>

                    {error && <p className="text-red-500 text-sm text-center">{error}</p>}

                    <button 
                        type='submit'
                        className="w-full py-2.5 px-4 border-none rounded-lg bg-blue-600 text-white font-semibold cursor-pointer hover:bg-blue-700 transition-colors"
                    >
                        Log In
                    </button>
                    
                    <div className="relative flex items-center py-2">
                        <div className="flex-grow border-t border-gray-300"></div>
                        <span className="flex-shrink mx-4 text-gray-500">Or</span>
                        <div className="flex-grow border-t border-gray-300"></div>
                    </div>

                    <div className="space-y-3">
                        <button type='button' className='w-full py-2.5 px-4 border-none rounded-lg text-white font-semibold cursor-pointer transition-colors bg-[#DB4437] hover:bg-[#C1351D]'>Log In with Google</button>
                        <button type='button' className='w-full py-2.5 px-4 border-none rounded-lg text-white font-semibold cursor-pointer transition-colors bg-[#3B5998] hover:bg-[#2D4373]'>Log In with Facebook</button>
                        <button type='button' className='w-full py-2.5 px-4 border-none rounded-lg text-white font-semibold cursor-pointer transition-colors bg-[#333] hover:bg-[#222]'>Log In with GitHub</button>
                    </div>

                    <p className='text-center text-sm text-gray-600'>
                        Don't have an account? <a href='/register' className="font-medium text-blue-600 hover:underline">Register here</a>
                    </p>
                </form>
            </div>
        </div>
    )
}

export default Login;

import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

function Registration() {
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        confirmPassword: '',
        role: ''
    });
    
    const [error, setError] = useState('');
    const navigate = useNavigate();
    
    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({
            ...formData, 
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        if(formData.password !== formData.confirmPassword) {
            setError('Passwords do not match');
            return;
        }
        setError('');

        try {
            const { confirmPassword, ...requestData } = formData;
            const response = await axios.post('http://localhost:8080/api/v1/auth/register', requestData);

            if(response.status === 201 || response.status === 200) {
                navigate('/login');
            } else {
                const errorText = await response.data;
                setError(errorText);
                console.error(errorText);
            }
        } catch(err) {
            if(axios.isAxiosError(err) && err.response){
                setError(err.response.data.message || err.response.data);
            } else {
                setError('An error occurred during user registration');
            }
        }
    };

    const inputStyle = "w-full p-2.5 border border-gray-300 rounded-lg bg-gray-50 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors text-gray-900";
    const socialButtonStyle = "w-full flex items-center justify-center py-2.5 px-4 rounded-lg text-white font-semibold transition-colors";

    return (
        <div className='flex justify-center items-center w-full min-h-screen bg-gray-300 p-4'>
            <div className='w-full max-w-lg p-8 space-y-6 bg-white rounded-2xl shadow-lg'>
                <div className="text-center">
                    <h1 className='text-3xl font-bold text-gray-900'>Sign Up Free</h1>
                    <p className="mt-2 text-gray-600">Please fill in the form to create an account</p>
                </div>

                <form onSubmit={handleSubmit} className="space-y-6">
                    {/* First Name & Last Name (Responsive layout) */}
                    <div className='flex flex-col sm:flex-row gap-4'>
                        <div className='w-full sm:w-1/2'>
                            <label htmlFor='firstName' className="block mb-2 text-left text-sm font-medium text-gray-700">First Name</label>
                            <input type='text' id='firstName' name='firstName' value={formData.firstName} onChange={handleChange} required className={inputStyle} />
                        </div>
                        <div className='w-full sm:w-1/2'>
                            <label htmlFor='lastName' className="block mb-2 text-left text-sm font-medium text-gray-700">Last Name</label>
                            <input type='text' id='lastName' name='lastName' value={formData.lastName} onChange={handleChange} required className={inputStyle} />
                        </div>
                    </div>
                    
                    <div>
                        <label htmlFor='email' className="block mb-2 text-left text-sm font-medium text-gray-700">Email</label>
                        <input type='email' id='email' name='email' value={formData.email} onChange={handleChange} required className={inputStyle} />
                    </div>

                    <div>
                        <label htmlFor='password' className="block mb-2 text-left text-sm font-medium text-gray-700">Password</label>
                        <input type='password' id='password' name='password' value={formData.password} onChange={handleChange} required className={inputStyle} />
                    </div>

                    <div>
                        <label htmlFor='confirmPassword' className="block mb-2 text-left text-sm font-medium text-gray-700">Confirm Password</label>
                        <input type='password' id='confirmPassword' name='confirmPassword' value={formData.confirmPassword} onChange={handleChange} required className={inputStyle} />
                    </div>

                    <div className='flex flex-col sm:flex-row gap-4'>
                        <label className="flex items-center space-x-3">
                            <input type="radio" id='roleVoter' name='role' value="Voter" onChange={handleChange} className="form-radio h-5 w-5 text-black focus:ring-black" required />
                            <span className="text-gray-900 font-medium">Voter</span>
                        </label>
                        <label className="flex items-center space-x-3">
                            <input type="radio" id='roleAdmin' name='role' value="ElectoralAdmin" onChange={handleChange} className="form-radio h-5 w-5 text-black focus:ring-black" required />
                            <span className="text-gray-900 font-medium">Electoral Admin</span>
                        </label>
                    </div>

                    {error && <p className="text-red-500 text-sm text-center">{error}</p>}
                    
                    <button type='submit' className="w-full py-3 px-4 bg-blue-600 hover:bg-blue-700 text-white font-bold rounded-lg transition-colors">Sign Up</button>
                    
                    <div className="my-4 flex items-center before:flex-1 before:border-t before:border-gray-300 after:flex-1 after:border-t after:border-gray-300">
                        <p className="text-center text-sm text-gray-500 mx-4">Or</p>
                    </div>

                    <div className="space-y-3">
                        <button type='button' className={`${socialButtonStyle} bg-[#db4437] hover:bg-[#c1351d]`}>Sign Up with Google</button>
                        <button type='button' className={`${socialButtonStyle} bg-[#3b5998] hover:bg-[#2d4373]`}>Sign Up with Facebook</button>
                        <button type='button' className={`${socialButtonStyle} bg-[#333] hover:bg-[#222]`}>Sign Up with GitHub</button>
                    </div>

                    <p className='text-center text-sm text-gray-600'>
                        Already have an account? <a href='/login' className="font-medium text-blue-600 hover:underline">Log in here</a>
                    </p>
                </form>
            </div>
        </div>
    );
}

export default Registration;

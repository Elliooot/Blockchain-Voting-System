import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../api/axiosConfig';

function ChangePassword() {
    const [formData, setFormData] = useState({
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
    });
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    
    const { token, logout } = useAuth();
    const navigate = useNavigate();

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        
        if (formData.newPassword !== formData.confirmPassword) {
            setError('New passwords do not match');
            return;
        }

        if (formData.newPassword.length < 6) {
            setError('New password must be at least 6 characters long');
            return;
        }

        if (formData.currentPassword === formData.newPassword) {
            setError('New password must be different from current password');
            return;
        }

        setIsLoading(true);
        setError('');
        setMessage('');

        try {
            await axiosInstance.post('/user/change-password', {
                currentPassword: formData.currentPassword,
                newPassword: formData.newPassword
            }, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            
            setMessage('Password changed successfully! Please log in again.');
            
            // Force the user to login again
            setTimeout(() => {
                logout();
                navigate('/login');
            }, 2000);
            
        } catch (error: any) {
            if (error.response) {
                setError(error.response.data.message || 'Failed to change password');
            } else {
                setError('Network error. Please try again.');
            }
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gray-100 py-12 px-4 sm:px-6 lg:px-8">
            <div className="max-w-md mx-auto bg-white rounded-lg shadow-md p-6">
                <h2 className="text-2xl font-bold text-gray-900 mb-6 text-center">Change Password</h2>
                
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label htmlFor="currentPassword" className="block text-sm font-medium text-gray-700 mb-1">
                            Current Password
                        </label>
                        <input
                            type="password"
                            id="currentPassword"
                            name="currentPassword"
                            value={formData.currentPassword}
                            onChange={handleChange}
                            required
                            className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                            placeholder="Enter current password"
                        />
                    </div>

                    <div>
                        <label htmlFor="newPassword" className="block text-sm font-medium text-gray-700 mb-1">
                            New Password
                        </label>
                        <input
                            type="password"
                            id="newPassword"
                            name="newPassword"
                            value={formData.newPassword}
                            onChange={handleChange}
                            required
                            className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                            placeholder="Enter new password"
                        />
                    </div>

                    <div>
                        <label htmlFor="confirmPassword" className="block text-sm font-medium text-gray-700 mb-1">
                            Confirm New Password
                        </label>
                        <input
                            type="password"
                            id="confirmPassword"
                            name="confirmPassword"
                            value={formData.confirmPassword}
                            onChange={handleChange}
                            required
                            className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                            placeholder="Confirm new password"
                        />
                    </div>

                    {message && (
                        <div className="p-3 text-green-700 bg-green-100 rounded-lg">
                            {message}
                        </div>
                    )}

                    {error && (
                        <div className="p-3 text-red-700 bg-red-100 rounded-lg">
                            {error}
                        </div>
                    )}

                    <button
                        type="submit"
                        disabled={isLoading}
                        className="w-full bg-blue-600 text-white py-3 px-4 rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 font-medium"
                    >
                        {isLoading ? 'Changing Password...' : 'Change Password'}
                    </button>
                </form>

                <div className="mt-4 text-center">
                    <button
                        onClick={() => navigate('/dashboard/setting')}
                        className="text-blue-600 hover:underline"
                    >
                        Back to Profile
                    </button>
                </div>
            </div>
        </div>
    );
}

export default ChangePassword;
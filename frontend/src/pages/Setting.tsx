import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';

function Setting() {
  const navigate = useNavigate();

  const handlePasswordChange = () => {
    navigate("/dashboard/change-password");
  };

  const { deleteAccount } = useAuth();

  const handleDeleteAccount = async () => {
    if (window.confirm('Are you sure you want to delete your account? This action cannot be undone.')) {
        try {
          deleteAccount();
          alert('Account deleted successfully!');
          navigate('/');
        } catch (error) {
          alert('Failed to delete account. Please try again.');                  
      }
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 p-4 sm:p-6 lg:p-8">
      <div className="max-w-4xl mx-auto">
        <h1 className="text-3xl font-bold text-gray-900 mb-2">Settings</h1>
        <p className="text-gray-600 mb-8">Manage your account and application preferences.</p>

        <div className="bg-white rounded-lg shadow-md overflow-hidden">

          <div className="p-6 flex justify-between items-center border-b border-gray-200">
            <div>
              <h3 className="text-lg font-semibold text-left text-gray-800">Account Security</h3>
              <p className="text-sm text-gray-500">Change your account password.</p>
            </div>
            <button
              onClick={handlePasswordChange}
              className="px-4 py-2 w-40 bg-blue-600 text-white font-semibold rounded-md shadow-sm hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-colors"
            >
              Change Password
            </button>
          </div>

          <div className="p-6 flex justify-between items-center border-t border-gray-200">
            <div>
              <h3 className="text-lg font-semibold text-left text-red-700">Delete Account</h3>
              <p className="text-sm text-gray-500">Permanently delete your account and all associated data.</p>
            </div>
            <button
              onClick={ handleDeleteAccount }
              className="px-4 py-2 w-40 bg-red-600 text-white font-semibold rounded-md shadow-sm hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 transition-colors"
            >
              Delete Account
            </button>
          </div>

        </div>
      </div>
    </div>
  );
}

export default Setting;
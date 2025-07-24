import React, { useState } from 'react';
import Switch from '@mui/material/Switch';

const label = { inputProps: { 'aria-label': 'Switch demo' } };

// const SettingSwitch = ({ checked, onChange }) => {
//   return (
//     <button
//       onClick={() => onChange(!checked)}
//       className={`relative inline-flex items-center h-6 rounded-full w-11 transition-colors duration-300 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 ${
//         checked ? 'bg-blue-600' : 'bg-gray-300'
//       }`}
//     >
//       <span
//         className={`inline-block w-4 h-4 transform bg-white rounded-full transition-transform duration-300 ${
//           checked ? 'translate-x-6' : 'translate-x-1'
//         }`}
//       />
//     </button>
//   );
// };

function Setting() {
  const [isDarkMode, setIsDarkMode] = useState(false);
  const [language, setLanguage] = useState('en');
  const [showVotingResults, setShowVotingResults] = useState(true);

  const handlePasswordChange = () => {
    alert('Change Password button clicked!');
  };

  return (
    <div className="min-h-screen bg-gray-100 p-4 sm:p-6 lg:p-8">
      <div className="max-w-4xl mx-auto">
        <h1 className="text-3xl font-bold text-gray-900 mb-2">Settings</h1>
        <p className="text-gray-600 mb-8">Manage your account and application preferences.</p>

        <div className="bg-white rounded-lg shadow-md overflow-hidden">
          
          {/* 1. Dark/Light Mode */}
          <div className="p-6 flex justify-between items-center border-b border-gray-200">
            <div>
              <h3 className="text-lg font-semibold text-gray-800">Dark/Light Mode</h3>
              <p className="text-sm text-gray-500">Enable dark mode for the interface.</p>
            </div>
            {/* <SettingSwitch checked={isDarkMode} onChange={setIsDarkMode} /> */}
            {/* <Switch {...label} defaultChecked={isDarkMode} onChange={() => setIsDarkMode(!isDarkMode)} /> */}
            <Switch defaultChecked={isDarkMode} onChange={() => setIsDarkMode(!isDarkMode)} />

          </div>

          {/* 2. Language */}
          <div className="p-6 flex justify-between items-center border-b border-gray-200">
            <div>
              <h3 className="text-lg font-semibold text-gray-800">Language</h3>
              <p className="text-sm text-gray-500">Choose your preferred language.</p>
            </div>
            <select
              value={language}
              onChange={(e) => setLanguage(e.target.value)}
              className="border border-gray-300 rounded-md shadow-sm py-2 px-3 bg-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="en">English</option>
              <option value="es">Español</option>
              <option value="zh">中文 (繁體)</option>
              <option value="ja">日本語</option>
            </select>
          </div>

          {/* 3. Change Password */}
          <div className="p-6 flex justify-between items-center border-b border-gray-200">
            <div>
              <h3 className="text-lg font-semibold text-gray-800">Account Security</h3>
              <p className="text-sm text-gray-500">Change your account password.</p>
            </div>
            <button
              onClick={handlePasswordChange}
              className="px-4 py-2 w-40 bg-blue-600 text-white font-semibold rounded-md shadow-sm hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-colors"
            >
              Change Password
            </button>
          </div>

          {/* 4. Voting Display Strategy */}
          <div className="p-6 flex justify-between items-center">
            <div>
              <h3 className="text-lg font-semibold text-gray-800">Voting Display Strategy</h3>
              <p className="text-sm text-gray-500">Show or hide voting results by default.</p>
            </div>
            {/* <SettingSwitch checked={showVotingResults} onChange={setShowVotingResults} /> */}
            <Switch {...label} defaultChecked={showVotingResults} onChange={() => setShowVotingResults(!showVotingResults)} />
          </div>

        </div>
      </div>
    </div>
  );
}

export default Setting;

// Dark/Light Mode, Language, Change Password, Voting display strategy(Switch: Show/Hide)
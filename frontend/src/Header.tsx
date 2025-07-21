import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './Header.css';

import CustomizeMenus from './components/dropdownButton';

function Header() {
    const navigate = useNavigate();

    return (
        <header className="app-header">
            <h1 className="text-2xl font-bold">Voting</h1>
            <nav className="mt-2">
                <ul className="flex space-x-4">
                    <li><a href="/" className="hover:underline">Home</a></li>
                    <li><a href="/dashboard" className='hover:underline'>Dashboard</a></li>
                    <li><a href="/dashboard2" className='hover:underline'>Dashboard2</a></li>
                    <li><a href="/dashboard3" className='hover:underline'>Dashboard3</a></li>
                    <CustomizeMenus />
                    <li><button onClick={() => navigate('/login')}>LogIn</button></li>    
                    <li><button onClick={() => navigate('/register')}>Register</button></li>
                </ul>
            </nav>
        </header>
    );
}

export default Header;
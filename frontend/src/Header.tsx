import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './Header.css';

function Header() {
    const [open, setOpen] = useState(false);
    const navigate = useNavigate();

    const toLoginPage = () => {
        navigate('/login');
    }

    return (
        <header className="app-header">
            <h1 className="text-2xl font-bold">Voting</h1>
            <nav className="mt-2">
                <ul className="flex space-x-4">
                    <li><a href="/" className="hover:underline">Home</a></li>
                    <button className='menu-btn' onClick={() => setOpen((prev) => !prev)}>Languages</button>
                    {open && (<ul className="language-menu">
                        <li><button>English</button></li>
                        <li><button>Spanish</button></li>
                        <li><button>French</button></li>
                    </ul>)}
                    <li><button onClick={toLoginPage}>Log In</button></li>    
                    <li><button>Register</button></li>
                </ul>
            </nav>
        </header>
    );
}

export default Header;
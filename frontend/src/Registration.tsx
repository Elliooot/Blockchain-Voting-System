import React, { useState } from 'react';
import './Registration.css';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

function Registration() {

    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        confirmPassword: ''
    })
    
    const [error, setError] = useState('')
    const navigate = useNavigate()
    
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
            const response = await axios.post('http://localhost:8080/register', formData);

            if(response.status === 201 || response.status === 200) {
                navigate('/login')
            } else {
                const errorText = await response.data;
                setError(errorText)
            }
        } catch(err) {
            if(axios.isAxiosError(err) && err.response){
                setError(err.response.data.message || err.response.data)
            } else {
                setError('An error occured during user registration')
            }
        }
    }

    return (
        <div className='register-container'>
            <div className='register-page'>
                <form onSubmit={handleSubmit}>
                    <h1>Sign Up Free</h1>
                    <p>Please fill in the form to create an account</p>
                    <div className='form-row'>
                        <div className='form-group'>
                            <label htmlFor='firstName'>First Name</label>
                            <input type='text' id='firstName' name='firstName'  
                                    value={formData.firstName} onChange={handleChange} required/>
                        </div>
                        <div className='form-group'>
                            <label htmlFor='lastName'>Last Name</label>
                            <input type='text' id='lastName' name='lastName'
                                    value={formData.lastName} onChange={handleChange} required/>
                        </div>
                    </div>
                    <div className='form-group'>
                        <label htmlFor='email'>Email</label>
                        <input type='email' id='email' name='email'
                                value={formData.email} onChange={handleChange} required/>
                    </div>
                    <div className='form-group'>
                        <label htmlFor='password'>Password</label>
                        <input type='password' id='password' name='password'
                                value={formData.password} onChange={handleChange} required/>
                    </div>
                    <div className='form-group'>
                        <label htmlFor='confirm-password'>Confirm Password</label>
                        <input type='password' id='confirm-password' name='confirm-password'
                                value={formData.confirmPassword} onChange={handleChange} required/>
                    </div>
                    <button type='submit'>Sign Up</button>
                    <p className='or'>Or</p>
                    <button type='button' className='google-login'>Sign Up with Google</button>
                    <button type='button' className='facebook-login'>Sign Up with Facebook</button>
                    <button type='button' className='github-login'>Sign Up with GitHub</button>
                    <p className='login-link'>
                        Already have an account? <a href='/login'>Log in here</a>
                    </p>
                </form>
            </div>
        </div>
    )
}

export default Registration;

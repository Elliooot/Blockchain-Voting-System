import './Register.css';

function Register() {
    return (
        <div className='register-container'>
            <div className='register-page'>
                <form>
                    <h1>Sign Up Free</h1>
                    <p>Please fill in the form to create an account</p>
                    <div className='form-row'>
                        <div className='form-group'>
                            <label htmlFor='firstName'>First Name</label>
                            <input type='text' id='firstName' name='firstName' required />
                        </div>
                        <div className='form-group'>
                            <label htmlFor='lastName'>Last Name</label>
                            <input type='text' id='lastName' name='lastName' required />
                        </div>
                    </div>
                    <div className='form-group'>
                        <label htmlFor='email'>Email</label>
                        <input type='email' id='email' name='email' required />
                    </div>
                    <div className='form-group'>
                        <label htmlFor='password'>Password</label>
                        <input type='password' id='password' name='password' required />
                    </div>
                    <div className='form-group'>
                        <label htmlFor='confirm-password'>Confirm Password</label>
                        <input type='password' id='confirm-password' name='confirm-password' required />
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

export default Register;

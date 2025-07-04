import './LoginPage.css';

function LoginPage() {
    return (
        <div className='login-page'>
            <form>
                <h1>Welcome Back</h1>
                <p>Please log in to continue</p>
                <div className='form-group'>
                    <label htmlFor='emailAddress'>Email Address</label>
                    <input type='email' id='emailAddress' name='emailAddress' required />
                </div>
                <div className='form-group'>
                    <label htmlFor='password'>Password</label>
                    <input type='password' id='password' name='password' required />
                </div>
                <div className='form-group'>
                    <label htmlFor='remember-me'>
                        <input type='checkbox' id='remember-me' name='remember-me' />
                        Remember Me
                    </label>
                </div>
                <div className='form-group'>
                    <a href='/forgot-password'>Forgot Password?</a>
                </div>
                <button type='submit'>Log In</button>
                <p className='or'>OR</p>
                <button type='button' className='google-login'>Log In with Google</button>
                <button type='button' className='facebook-login'>Log In with Facebook</button>
                <button type='button' className='github-login'>Log In with GitHub</button>
                <p className='register-link'>
                    Don't have an account? <a href='/register'>Register here</a>
                </p>
            </form>
        </div>
    )
}

export default LoginPage;

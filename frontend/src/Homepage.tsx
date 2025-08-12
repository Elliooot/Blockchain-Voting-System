import React from 'react';
import { useNavigate } from 'react-router-dom';
import GradientText from './blocks/TextAnimations/GradientText/GradientText';

function Homepage() {
    const navigate = useNavigate();
    
    const baseButtonStyles = "py-3 px-6 rounded-lg text-base font-semibold cursor-pointer transition-all duration-300 border-2";
    const primaryButtonStyles = `${baseButtonStyles} bg-blue-600 text-white border-transparent hover:bg-blue-700`;
    const secondaryButtonStyles = `${baseButtonStyles} bg-gray-50 text-blue-600 border-blue-600 hover:bg-gray-200`;

    return (
        <div className="min-h-screen bg-white">
            {/* Header Section */}
            <header className="bg-white sticky top-0 z-50 px-5 py-5 shadow-sm">
                <div className="flex items-center justify-between">
                    <h1 className="text-4xl font-bold font-['Courier_New',Courier,monospace] text-black">Voting</h1>
                    <nav>
                        <ul className="flex space-x-6 items-center">
                            <li>
                                <button onClick={() => navigate('/login')} className={primaryButtonStyles}>
                                    LogIn
                                </button>
                            </li>
                            <li>
                                <button 
                                    onClick={() => navigate('/register')}
                                    className={primaryButtonStyles}
                                >
                                    Register
                                </button>
                            </li>
                        </ul>
                    </nav>
                </div>
            </header>

            {/* Main Content */}
            <div className="px-5 pb-5 text-center text-gray-800">
                <main className="max-w-4xl mx-auto my-16">
                    <h1 className="text-5xl md:text-6xl font-bold mb-4 text-gray-900">SecureVote: The Future of Voting</h1>
                    <p className="text-lg md:text-xl mb-10 text-gray-600">Leveraging Blockchain for transparent, secure, and trustworthy elections.</p>
                    
                    <div className="flex justify-center gap-4 mb-16">
                        <button onClick={() => navigate('/register')} className={primaryButtonStyles}>Get Started</button>
                        <a
                            href="https://github.com/Elliooot/Blockchain-Voting-System"
                            target="_blank"
                            rel="noopener noreferrer"
                            >
                            <button className={secondaryButtonStyles}>
                                Learn More
                            </button>
                        </a>
                    </div>

                    <div className="mt-16 p-8 bg-gray-50 rounded-xl">
                        <h2 className='text-3xl font-bold mb-6 text-gray-800'>Why Blockchain?</h2>
                        <GradientText
                          colors={["#40ffaa", "#4079ff", "#40ffaa", "#4079ff", "#40ffaa"]}
                          animationSpeed={8}
                          showBorder={false}
                          className="text-lg leading-relaxed"
                        >
                          Electronic voting faces challenges related to security vulnerabilities and maintaining trustworthiness. Concerns about hacking, tampering, and unauthorized access undermine confidence in the electoral process. Blockchain technology can address these challenges by providing enhanced security measures, transparent record-keeping, and traceability.
                        </GradientText>
                    </div>
                </main>
            </div>
        </div>
    );
}

export default Homepage;
import React from 'react';
import { useNavigate } from 'react-router-dom';
import GradientText from '../blocks/TextAnimations/GradientText/GradientText';

function Homepage() {
    const navigate = useNavigate();
    
    const baseButtonStyles = "py-3 px-6 rounded-full text-base font-semibold cursor-pointer transition-all duration-300 border";
    const primaryButtonStyles = `${baseButtonStyles} bg-[#007aff] text-white border-transparent hover:bg-[#0063d1] shadow-[0_10px_24px_rgba(0,122,255,0.28)]`;
    const secondaryButtonStyles = `${baseButtonStyles} bg-white/80 text-[#007aff] border-white/60 hover:bg-white shadow-[0_8px_20px_rgba(0,0,0,0.08)]`;

    return (
        <div className="min-h-screen bg-transparent">
            {/* Header Section */}
            <header className="bg-white/70 backdrop-blur-xl sticky top-0 z-50 px-5 py-5 border-b border-white/60">
                <div className="flex items-center justify-between">
                    <h1 className="text-3xl font-semibold text-black tracking-tight">Hase</h1>
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
                    <h1 className="text-5xl md:text-6xl font-semibold mb-4 text-gray-900 tracking-tight">SecureVote: The Future of Voting</h1>
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

                    <div className="mt-16 p-8 bg-white/80 backdrop-blur-xl rounded-2xl border border-white/60 shadow-[0_16px_40px_rgba(0,0,0,0.08)]">
                        <h2 className='text-3xl font-semibold mb-6 text-gray-900 tracking-tight'>Why Blockchain?</h2>
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

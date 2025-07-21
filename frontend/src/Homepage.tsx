import './Homepage.css';
import Header from './Header';

// import ShinyText from './blocks/TextAnimations/ShinyText/ShinyText';
import GradientText from './blocks/TextAnimations/GradientText/GradientText';

function Homepage() {
    return (
        <div className="homepage">
            <Header />
            <main className="main-content">
                <h1 className="main-title">SecureVote: The Future of Voting</h1>
                <p className="subtitle">Leveraging Blockchain for transparent, secure, and trustworthy elections.</p>
                
                <div className="cta-buttons">
                    <button className="cta-primary">Get Started</button>
                    <button className="cta-secondary">Learn More</button>
                </div>

                <div className="feature-section">
                    <h2 className='section-title'>Why Blockchain?</h2>
                    <GradientText
                      colors={["#40ffaa", "#4079ff", "#40ffaa", "#4079ff", "#40ffaa"]}
                      animationSpeed={8}
                      showBorder={false}
                      className="gradient-text-large"
                    >
                      Electronic voting faces challenges related to security vulnerabilities and maintaining trustworthiness. Concerns about hacking, tampering, and unauthorized access undermine confidence in the electoral process. Blockchain technology can address these challenges by providing enhanced security measures, transparent record-keeping, and traceability.
                    </GradientText>
                    {/* <p>
                        Electronic voting faces challenges related to security vulnerabilities and maintaining trustworthiness. Concerns about hacking, tampering, and unauthorized access undermine confidence in the electoral process. Blockchain technology can address these challenges by providing enhanced security measures, transparent record-keeping, and traceability.
                    </p> */}
                </div>
            </main>
        </div>
    );
}

export default Homepage;
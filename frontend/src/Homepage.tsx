import './Homepage.css';
import Header from './Header';

function Homepage() {
    return (
        <div className="homepage">
            <Header />
            <h1>Welcome to the Voting App</h1>
            <p>Here you can create and participate in polls.</p>
            <p>Use the navigation bar to explore different sections.</p>
        </div>
    );
}

export default Homepage;
import { useState } from "react";
import Web3 from "web3";

// To resolve the problem "Property 'ethereum' does not exist on type 'Window & typeof globalThis'."
interface EthereumProvider {
    isMetaMask?: boolean;
    request: (args: { method: string; params?: Array<any> }) => Promise<any>;
}

declare global {
    interface Window {
        ethereum?: EthereumProvider;
    }
}

function Wallet() {
    const[accounts, setAccounts] = useState<string[] | null>(null);

    const connectWallet = async() => {
        if (window.ethereum && window.ethereum.isMetaMask) {
            // Initialize web3
            const web3Instance = new Web3(window.ethereum);
            const accounts = await web3Instance.eth.requestAccounts();
            setAccounts(accounts)
        }
    };
    
    return (
        <div className="min-h-screen bg-gray-100 p-4 sm:p-6 lg:p-8">
            <div className="max-w-4xl mx-auto">
                <h1 className="text-3xl font-bold text-gray-900 mb-2">Your Crypto Wallet</h1>
                <p className="text-gray-600 mb-8">Manage your wallet.</p>
                <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-4xl">
                    {accounts && accounts.length > 0 && (
                        <div className="card">
                            <h2>Account details</h2>
                            <h2>Address: {accounts[0]}</h2>
                        </div>
                    )}
                    {!accounts && (
                        <button className="w-72 bg-transparent text-red-500 hover:bg-red-500 hover:text-white font-bold py-2 px-4" onClick={connectWallet}>
                            Connect to MetaMask Wallet
                        </button>
                    )}
                </div>
            </div>
        </div>
    );
}

export default Wallet;
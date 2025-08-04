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
    const [accounts, setAccounts] = useState<string[] | null>(null);
    const [isConnecting, setIsConnecting] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const connectWallet = async () => {
        if (isConnecting) {
            console.log("Already connecting, please wait...");
            return;
        }

        if (!window.ethereum || !window.ethereum.isMetaMask) {
            setError("MetaMask is not installed. Please install MetaMask.");
            return;
        }

        setIsConnecting(true);
        setError(null);

        try {
            // 先檢查是否已經有連接的帳號
            const existingAccounts = await window.ethereum.request({
                method: 'eth_accounts'
            });

            if (existingAccounts.length > 0) {
                setAccounts(existingAccounts);
                console.log("Already connected accounts:", existingAccounts);
                return;
            }

            // 如果沒有連接的帳號，才請求連接
            const web3Instance = new Web3(window.ethereum);
            const newAccounts = await web3Instance.eth.requestAccounts();
            setAccounts(newAccounts);
            console.log("Connected accounts:", newAccounts);

        } catch (error: any) {
            console.error("Connection failed:", error);
            if (error.code === -32002) {
                setError("MetaMask is already processing a request. Please check MetaMask and try again.");
            } else if (error.code === 4001) {
                setError("Connection rejected by user.");
            } else {
                setError("Failed to connect wallet. Please try again.");
            }
        } finally {
            setIsConnecting(false);
        }
    };

    const disconnectWallet = () => {
        setAccounts(null);
        setError(null);
    };

    return (
        <div className="min-h-screen bg-gray-100 p-4 sm:p-6 lg:p-8">
            <div className="max-w-4xl mx-auto">
                <h1 className="text-3xl font-bold text-gray-900 mb-2">Your Crypto Wallet</h1>
                <p className="text-gray-600 mb-8">Manage your wallet.</p>
                <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-4xl">
                    
                    {error && (
                        <div className="mb-4 p-4 bg-red-100 border border-red-400 text-red-700 rounded">
                            {error}
                        </div>
                    )}

                    {accounts && accounts.length > 0 ? (
                        <div className="card">
                            <h2 className="text-xl font-semibold mb-4">Account Details</h2>
                            <p className="mb-4"><strong>Address:</strong> {accounts[0]}</p>
                            <button 
                                className="bg-gray-500 hover:bg-gray-600 text-white font-bold py-2 px-4 rounded"
                                onClick={disconnectWallet}
                            >
                                Disconnect Wallet
                            </button>
                        </div>
                    ) : (
                        <button 
                            className={`w-72 font-bold py-2 px-4 rounded ${
                                isConnecting 
                                    ? 'bg-gray-400 text-gray-600 cursor-not-allowed'
                                    : 'bg-transparent text-red-500 hover:bg-red-500 hover:text-white border border-red-500'
                            }`}
                            onClick={connectWallet}
                            disabled={isConnecting}
                        >
                            {isConnecting ? 'Connecting...' : 'Connect to MetaMask Wallet'}
                        </button>
                    )}
                </div>
            </div>
        </div>
    );
}

export default Wallet;
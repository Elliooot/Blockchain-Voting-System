import { useEffect, useState } from "react";
import Web3 from "web3";
import { updateWalletAddress, loadUserWallet } from "../api/apiService";

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
    const [isSaving, setIsSaving] = useState(false);
    const [isRemoving, setIsRemoving] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [savedWalletAddress, setSavedWalletAddress] = useState<string | null>(null);

    useEffect(() => {
        loadUserWallet();
    }, []);

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
            const newAccounts = await window.ethereum.request({ method: "eth_requestAccounts" });
            setAccounts(newAccounts);
            console.log("Connected to account: ", newAccounts[0]);

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

    const saveWalletAddress = async () => {
        if(!accounts || accounts.length === 0) {
            setError("Please connect your wallet first.");
            return;
        }

        setIsSaving(true);
        setError(null);
        
        try {
            const response = await updateWalletAddress(accounts[0]);
            setSavedWalletAddress(accounts[0]);
            console.log("Wallet address saved: " + response);
        } catch (error) {
            console.error("Failed to save wallet address: " + error);
            setError("Failed to save wallet address. Please try again.");
        } finally {
            setIsSaving(false);
        }
    };

    const removeWalletFromDB = async () => {
        setIsRemoving(true);
        setError(null);

        try {
            await updateWalletAddress("");
            setSavedWalletAddress(null);
            console.log("Wallet address removed from database.");
        } catch (error) {
            console.error("Failed to remove wallet address from database: " + error);
            setError("Failed to remove wallet address from database. Please try again.");
        } finally {
            setIsRemoving(false);
        }
    }

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

                    {/* Show saved wallet address */}
                    {savedWalletAddress && (
                        <div className="mb-6 p-4 bg-blue-50 border border-blue-200 rounded">
                            <h3 className="text-lg font-semibold text-blue-800 mb-2">Saved Wallet Address</h3>
                            <p className="text-blue-700 font-mono text-sm break-all">{savedWalletAddress}</p>
                            <button 
                                className={`mt-2 font-bold py-1 px-3 rounded text-sm ${
                                    isRemoving 
                                        ? 'bg-gray-400 text-gray-600 cursor-not-allowed'
                                        : 'bg-red-500 hover:bg-red-600 text-white'
                                }`}
                                onClick={removeWalletFromDB}
                                disabled={isRemoving}
                            >
                                {isRemoving ? 'Removing...' : 'Remove from Database'}
                            </button>
                        </div>
                    )}

                    {accounts && accounts.length > 0 ? (
                        <div className="card">
                            <h2 className="text-xl font-semibold mb-4">Account Details</h2>
                            <p className="mb-4"><strong>Address:</strong> {accounts[0]}</p>

                            <div className="space-x-2">
                                {accounts[0] !== savedWalletAddress && (
                                    <button 
                                        className={`font-bold py-2 px-4 rounded ${
                                            isSaving 
                                                ? 'bg-gray-400 text-gray-600 cursor-not-allowed'
                                                : 'bg-green-500 hover:bg-green-600 text-white'
                                        }`}
                                        onClick={saveWalletAddress}
                                        disabled={isSaving}
                                    >
                                        {isSaving ? 'Saving...' : 'Save Wallet Address'}
                                    </button>
                                )}
                                
                                {/* <button 
                                    className="bg-gray-500 hover:bg-gray-600 text-white font-bold py-2 px-4 rounded"
                                    onClick={disconnectWallet}
                                >
                                    Disconnect Wallet
                                </button> */}
                            </div>
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
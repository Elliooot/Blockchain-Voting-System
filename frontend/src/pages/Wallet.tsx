import { useEffect, useState } from "react";
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
    const [isConnecting, setIsConnecting] = useState(false);
    const [isRemoving, setIsRemoving] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [savedWalletAddress, setSavedWalletAddress] = useState<string | null>(null);

    useEffect(() => {
        const fetchAndSetWallet = async () => {
            try {
                const addressInDB = await loadUserWallet();

                if(addressInDB) {
                    console.log("Loaded wallet address from database: " + addressInDB);
                    setSavedWalletAddress(addressInDB);
                }
            } catch (error) {
                console.error("Failed to load wallet address from database: " + error);
            }
        };

        fetchAndSetWallet();
    }, []);

    const connectAndSaveWallet = async () => {
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
            const newAddress = newAccounts[0];
            console.log("Connected to account: ", newAccounts[0]);

            if(newAddress){
                await updateWalletAddress(newAddress);
                setSavedWalletAddress(newAddress);
                console.log("Wallet address saved: " + newAddress);
            }
        } catch (error: any) {
            console.error("Connection failed:", error);
            if (error.code === 4001) {
                setError("Connection rejected by user.");
            } else {
                setError("Failed to connect wallet. Please try again.");
            }
        } finally {
            setIsConnecting(false);
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
                    {savedWalletAddress ? (
                        <div className="mb-6 p-4 bg-blue-50 border border-blue-200 rounded">
                            <h3 className="text-lg font-semibold text-blue-800 mb-2">Connected Wallet Address</h3>
                            <p className="text-blue-700 font-mono text-sm break-all">{savedWalletAddress}</p>
                            <button 
                                className={`mt-4 font-bold py-2 px-4 rounded text-sm ${
                                    isRemoving 
                                        ? 'bg-gray-400 text-gray-600 cursor-not-allowed'
                                        : 'bg-red-500 hover:bg-red-600 text-white'
                                }`}
                                onClick={removeWalletFromDB}
                                disabled={isRemoving}
                            >
                                {isRemoving ? 'Disconnecting...' : 'Disconnect and Remove'}
                            </button>
                        </div>
                    ) : (
                        <button 
                            className={`w-full max-w-sm mx-auto font-bold py-3 px-4 rounded ${
                                isConnecting 
                                    ? 'bg-gray-400 text-gray-600 cursor-not-allowed'
                                    : 'bg-blue-600 hover:bg-blue-700 text-white'
                            }`}
                            onClick={connectAndSaveWallet}
                            disabled={isConnecting}
                        >
                            {isConnecting ? 'Connecting...' : 'Connect and Save MetaMask Wallet'}
                        </button>
                    )}
                </div>
            </div>
        </div>
    );
}

export default Wallet;
import axiosInstance from "./axiosConfig";

axiosInstance.interceptors.request.use(
    (config) => {
        console.log("📤 API Request - URL:", config.url);
        console.log("📤 API Request - Method:", config.method);
        
        const token = localStorage.getItem('token') || sessionStorage.getItem('token');
        
        if(token) {
            config.headers.Authorization = `Bearer ${token}`;
        } else {
            console.log("❌ API Request - NO TOKEN FOUND!");
        }
        
        console.log("📋 API Request - Final headers:", config.headers);
        return config;
    },
    (error) => {
        console.error("❌ API Request interceptor error:", error);
        return Promise.reject(error);
    }
);

axiosInstance.interceptors.response.use(
    (response) => {
        console.log("✅ API Response - Status:", response.status);
        console.log("📥 API Response - Data:", response.data);
        return response;
    },
    (error) => {
        return Promise.reject(error);
    }
);

export const createBallot = async (ballotData: object) => {
    try {
        const response = await axiosInstance.post('/ballots/create', ballotData);
        return response.data;
    } catch (error) {
        console.error("Failed to create ballot in apiService:", error);
        throw error;
    }
};

export const editBallot = async (ballotId: number, ballotData: object) => {
    try {
        const response = await axiosInstance.patch(`/ballots/update/${ballotId}`, ballotData);
        return response.data;
    } catch (error) {
        console.error("Failed to edit ballot in apiService:", error);
        throw error;
    }
};

export const fetchBallots = async () => {
    try {
        const response = await axiosInstance.get('/ballots');
        console.log("Fetched ballots:", response.data);
        return response.data;
    } catch (error) {
        console.error("Error fetching ballots: " + error);
        throw error;
    }
};

export const fetchBallotById = async (ballotId: number) => {
    try {
        const response = await axiosInstance.get(`/ballots/${ballotId}`);
        console.log("Fetched ballot: " + ballotId);
        return response.data;
    } catch (error) {
        console.error("Error fetching ballot: " + error);
        throw error;
    }
};

export const fetchBallotResult = async () => {
    try {
        const reponse = await axiosInstance.get('/ballots/result');
        console.log("Fetched ballot result: " + reponse.data);
        return reponse.data;
    } catch (error) {
        console.error("Error fetching ballot result: " + error);
        throw error;
    }
};

export const deleteBallot = async (ballotId: number) => {
    try {
        const response = await axiosInstance.delete('ballots/delete/' + ballotId);
        console.log("Deleted ballot:", response.data);
        return response.data;
    } catch (error) {
        console.error("Error deleting ballot: " + error);
        throw error;
    }
};

export const searchVoterByEmail = async (email: string) => {
    try {
        const response = await axiosInstance.get('/user/search?email=' + email);
        console.log("Fetched voters:", response.data);
        return response.data;
    } catch (error) {
        console.log("Failed to get user: ", error);
        throw error;
    }
}

export const castVote = async (voteData: object) => {
    try {
        const response = await axiosInstance.post('/voting/vote', voteData);
        console.log("Cast vote response:", response.data);
        return response.data;
    } catch (error) {
        console.error("Failed to cast vote in apiService:", error);
        throw error;
    }
};

export const getVoteRecords = async () => {
    try {
        const response = await axiosInstance.get('/voting/records');
        console.log("Fetched vote records:", response.data);
        return response.data;
    } catch (error) {
        console.error("Error fetching vote records: " + error);
        throw error;
    }
};

export const loadUserWallet = async () => {
    try {
        const response = await axiosInstance.get('/user/get_wallet');
        console.log("Loaded wallet address:", response.data);
        return response.data.walletAddress;
    } catch (error) {
        console.error("Error loading wallet address: " + error);
        return null;
    }
};

export const updateWalletAddress = async (walletAddress: string) => {
    try {
        console.log("📤 API: Updating wallet with address:", walletAddress);
        const response = await axiosInstance.put('/user/update_wallet', { walletAddress });
        console.log("📥 API: Update wallet response:", response.data);
        return response.data;
    } catch (error) {
        console.error("❌ API: Error updating wallet address:", error);
        throw error;
    }
}
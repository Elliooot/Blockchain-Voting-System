import axiosInstance from "./axiosConfig";

axiosInstance.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token') || sessionStorage.getItem('token');
        
        if(token) {
            config.headers.Authorization = `Bearer ${token}`;
        } else {
            console.log("❌ API Request - NO TOKEN FOUND!");
        }
        
        return config;
    },
    (error) => {
        console.error("❌ API Request interceptor error:", error);
        return Promise.reject(error);
    }
);

axiosInstance.interceptors.response.use(
    (response) => {
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
        throw error;
    }
};

export const editBallot = async (ballotId: number, ballotData: object) => {
    try {
        const response = await axiosInstance.patch(`/ballots/update/${ballotId}`, ballotData);
        return response.data;
    } catch (error) {
        throw error;
    }
};

export const fetchBallots = async () => {
    try {
        const response = await axiosInstance.get('/ballots');
        return response.data;
    } catch (error) {
        throw error;
    }
};

export const fetchBallotById = async (ballotId: number) => {
    try {
        const response = await axiosInstance.get(`/ballots/${ballotId}`);
        return response.data;
    } catch (error) {
        throw error;
    }
};

export const fetchBallotResult = async () => {
    try {
        const reponse = await axiosInstance.get('/ballots/result');
        return reponse.data;
    } catch (error) {
        throw error;
    }
};

export const deleteBallot = async (ballotId: number) => {
    try {
        const response = await axiosInstance.delete('ballots/delete/' + ballotId);
        return response.data;
    } catch (error) {
        throw error;
    }
};

export const searchVoterByEmail = async (email: string) => {
    try {
        const response = await axiosInstance.get('/user/search?email=' + email);
        return response.data;
    } catch (error) {
        throw error;
    }
}

export const castVote = async (voteData: object) => {
    try {
        const response = await axiosInstance.post('/voting/vote', voteData);
        return response.data;
    } catch (error) {
        throw error;
    }
};

export const getVoteRecords = async () => {
    try {
        const response = await axiosInstance.get('/voting/records');
        return response.data;
    } catch (error) {
        throw error;
    }
};

export const loadUserWallet = async () => {
    try {
        const response = await axiosInstance.get('/user/get_wallet');
        return response.data.walletAddress;
    } catch (error) {
        return null;
    }
};

export const updateWalletAddress = async (walletAddress: string) => {
    try {
        const response = await axiosInstance.put('/user/update_wallet', { walletAddress });
        return response.data;
    } catch (error) {
        throw error;
    }
}
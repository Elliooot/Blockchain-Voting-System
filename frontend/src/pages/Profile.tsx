import { useEffect, useState } from "react";
import { getVoteRecords } from "../api/apiService";
import { useAuth } from "../contexts/AuthContext";

interface apiVoteRecord {
    voteId: number;
    ballotId: number;
    ballotTitle: string;
    optionName: string;
}

function Profile() {
    const { user } = useAuth();
    const [voteRecords, setVoteRecords] = useState<apiVoteRecord[]>([]);

    const dob = new Date(user?.dateOfBirth || 0);
    const formattedDob = dob.toISOString().slice(0, 10);

    const loadVoteRecords = async () => {
        try {
            const recordData: apiVoteRecord[] = await getVoteRecords();
            
            const recordForm = recordData.map(vote => ({
                voteId: vote.voteId,
                ballotId: vote.ballotId,
                ballotTitle: vote.ballotTitle,
                optionName: vote.optionName
            }));
            
            setVoteRecords(recordForm);
        } catch (error) {
            console.error("Failed to load vote records in component: ", error);
            throw error;
        }
    };

    useEffect(() => {
        loadVoteRecords();
    }, []);

    return (
        <div className="min-h-screen bg-gray-100 p-4 sm:p-6 lg:p-8">
            <div className="max-w-4xl mx-auto">
                <h1 className="text-3xl font-bold text-gray-900 mb-2">Profile</h1>
                <p className="text-gray-600 mb-8">Manage your profile and voting history.</p>
            
                {/* Profile Card：Set width, shadow and padding */}
                <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-4xl">

                    <div className="flex items-start gap-8">

                        <div className="flex-grow">

                            <div className="w-1/3">
                                <label htmlFor="userId" className="block text-left text-gray-700 text-sm font-bold mb-2">
                                    User ID: {user?.userId || ""}
                                </label>
                            </div>
                            
                            <div className="flex gap-4 mb-4">
                                <div className="w-1/2">
                                    <label htmlFor="first-name" className="block text-gray-700 text-sm text-left font-bold mb-2">
                                        First Name
                                    </label>
                                    <input 
                                    type="text" 
                                    id="first-name" 
                                    value={user?.firstName || ""}
                                    readOnly
                                    className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline bg-gray-100" 
                                    />
                                </div>
                                <div className="w-1/2">
                                    <label htmlFor="last-name" className="block text-gray-700 text-sm text-left font-bold mb-2">
                                        Last Name
                                    </label>
                                    <input 
                                    type="text" 
                                    id="last-name" 
                                    value={user?.lastName || ""}
                                    readOnly
                                    className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline bg-gray-100"
                                    />
                                </div>
                            </div>

                            <div className="flex gap-4 mb-4">
                                <div className="w-2/3">
                                    <label htmlFor="email" className="block text-gray-700 text-sm text-left font-bold mb-2">
                                        Email
                                    </label>
                                    <input 
                                        type="email" 
                                        id="email" 
                                        value={user?.email || ""}
                                        readOnly
                                        className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline bg-gray-100"
                                    />
                                </div>
                                <div className="w-1/3">
                                    <label htmlFor="age" className="block text-gray-700 text-sm text-left font-bold mb-2">
                                        Date of Birth
                                    </label>
                                    <input
                                        type="date"
                                        id="dateOfBirth"
                                        value={formattedDob}
                                        readOnly
                                        className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline bg-gray-100" 
                                    />
                                </div>
                            </div>

                            <div className="w-1/3">
                                <label htmlFor="gender" className="block text-gray-700 text-sm text-left font-bold mb-2">
                                    Gender
                                </label>
                                <input 
                                    type="text"
                                    id="gender"
                                    value={user?.gender || ""}
                                    readOnly
                                    className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline bg-gray-100" 
                                />
                            </div>

                            <div>
                                <label htmlFor=""></label>
                            </div>
                        </div>

                        {/* Avatar */}
                        <div className="flex-shrink-0">
                            {user?.lastName ? (
                                <div className="rounded-full w-36 h-36 flex items-center justify-center bg-gray-400 text-white text-6xl font-bold border-4 border-white shadow-sm">
                                {user.firstName.charAt(0).toUpperCase()}
                                </div>
                            ) : (
                                <div className="rounded-full w-36 h-36 flex items-center justify-center bg-gray-400 text-white text-6xl font-bold border-4 border-white shadow-sm">
                                ?
                                </div>
                            )}
                        </div>

                    </div>

                    <div className="border-t my-8"></div>

                    <div className="voting-record">
                        <div className="flex items-center justify-between mb-6">
                            <h3 className="text-xl font-bold text-gray-800">Voting History</h3>
                            <button 
                                onClick={loadVoteRecords}
                                className="text-blue-600 hover:text-blue-800 text-sm font-medium flex items-center gap-1"
                            >
                                🔄 Refresh
                            </button>
                        </div>
                        <div className="hidden md:block overflow-hidden shadow ring-1 ring-black ring-opacity-5 rounded-lg">
                            <table className="min-w-full divide-y divide-gray-300">
                                <thead className="bg-gray-50">
                                    <tr>
                                        <th className="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                                            Ballot ID
                                        </th>
                                        <th className="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                                            Election Title
                                        </th>
                                        <th className="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                                            Your Choice
                                        </th>
                                        <th className="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                                            Status
                                        </th>
                                    </tr>
                                </thead>
                                <tbody className="bg-white divide-y divide-gray-200">
                                    {voteRecords.map((record: any, index: number) => (
                                        <tr key={record.voteId} className={index % 2 === 0 ? 'bg-white' : 'bg-gray-50'}>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                                                #{record.ballotId}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                                                {record.ballotTitle}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                                                    {record.optionName}
                                                </span>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                                                    ✅ Voted
                                                </span>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>

                        <div className="mt-6 text-center">
                            <p className="text-sm text-gray-500">
                                Total: {voteRecords.length} voting record{voteRecords.length !== 1 ? 's' : ''}
                            </p>
                        </div>
                    </div>
                </div>
            </div>       
        </div>
    );
}

export default Profile;

// Gender
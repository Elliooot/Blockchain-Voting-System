import { useAuth } from "../contexts/AuthContext";

function Profile() {
    const { user } = useAuth();
    console.log("Profile user:", user);
    const dob = new Date(user?.dateOfBirth || 0);
    console.log("DOB:", dob);
    const formattedDob = dob.toISOString().slice(0, 10);

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
                                    <label htmlFor="first-name" className="block text-gray-700 text-sm font-bold mb-2">
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
                                    <label htmlFor="last-name" className="block text-gray-700 text-sm font-bold mb-2">
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
                                    <label htmlFor="email" className="block text-gray-700 text-sm font-bold mb-2">
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
                                    <label htmlFor="age" className="block text-gray-700 text-sm font-bold mb-2">
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
                                <label htmlFor="gender" className="block text-gray-700 text-sm font-bold mb-2">
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
                            <img className="rounded-full w-36 h-36 object-cover border-4 border-white shadow-sm" src="" alt="User avatar" />
                        </div>

                    </div>

                    <div className="border-t my-8"></div>

                    <div className="voting-record">
                        <h3 className="text-xl font-bold text-gray-800">Voting Record</h3>
                        {/* Voting Record */}
                    </div>
                </div>
            </div>       
        </div>
    );
}

export default Profile;

// Gender
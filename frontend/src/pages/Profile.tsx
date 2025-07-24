function Profile() {
    return (
        <div className="min-h-screen bg-gray-100 p-4 sm:p-6 lg:p-8">
            <div className="max-w-4xl mx-auto">
                <h1 className="text-3xl font-bold text-gray-900 mb-2">Profile</h1>
                <p className="text-gray-600 mb-8">Manage your profile and voting history.</p>
            
                {/* Profile Card：Set width, shadow and padding */}
                <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-4xl">

                    <div className="flex items-start gap-8">

                        <div className="flex-grow">
                            
                            <div className="flex gap-4 mb-4">
                                <div className="w-1/2">
                                    <label htmlFor="first-name" className="block text-gray-700 text-sm font-bold mb-2">
                                        First Name
                                    </label>
                                    <input 
                                    type="text" 
                                    id="first-name" 
                                    defaultValue="John" 
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
                                    defaultValue="Doe"
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
                                        defaultValue="john.doe@example.com"
                                        readOnly
                                        className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline bg-gray-100"
                                    />
                                </div>
                                <div className="w-1/3">
                                    <label htmlFor="age" className="block text-gray-700 text-sm font-bold mb-2">
                                        Age
                                    </label>
                                    <input
                                        type="number"
                                        id="age"
                                        defaultValue="30"
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
                                    defaultValue="Male"
                                    readOnly
                                    className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline bg-gray-100" 
                                />
                            </div>
                            <div>
                                <label htmlFor=""></label>
                            </div>
                        </div>

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
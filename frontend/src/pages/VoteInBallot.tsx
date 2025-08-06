import { useNavigate, useParams } from "react-router-dom";
import { castVote, fetchBallotById } from "../api/apiService";
import { useCallback, useEffect, useState } from "react";

interface VoteFormData {
    blockchainBallotId: number;
    optionId: number;
}

interface ApiBallot {
    id: number;
    blockchainBallotId: number;
    title: string;
    description: string;
    options: Array<{
        id: number;
        name: string;
        description: string;
        voteCount: number;
        displayOrder: number;
    }>;
    startTime: string;
    duration: string;
}

function VoteInBallot() {
    const [ballot, setBallot] = useState<ApiBallot | null>(null);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const navigate = useNavigate();

    const { ballotId } = useParams<{ ballotId: string }>();

    const [formData, setFormData] = useState<VoteFormData>({
        blockchainBallotId: 0,
        optionId: 0,
    });

    const loadBallot = useCallback(async () => {
        if(!ballotId){
            console.log("No ballot ID provided");
            return;
        }
        try {
            const ballotData: ApiBallot = await fetchBallotById(parseInt(ballotId));
            
            setBallot(ballotData);
            setFormData(prev => ({...prev, ballotId: ballotData.id }));
        } catch (error) {
            console.log("Failded to fetch current ballot: ", error);
        }
    }, [ballotId]);

    useEffect(() => {
        loadBallot();
    }, [loadBallot]);

    const handleLockOption = (optionId: number) => {
        setFormData(prev => ({...prev, optionId}));
    }

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if(formData.optionId === 0) {
            alert("Please select an option before voting.")
            return;
        }

        if(!window.confirm("Are you sure?")) return;
        
        setIsSubmitting(true);


        try {
            await castVote(formData);
            alert("Vote Submitted!");
            navigate('/dashboard/ballots');
        } catch (error) {
            console.error('Failed to cast vote:', error);
            alert('Failed to cast vote. Please try again.')
        }
    }

    const TitleStyle = "text-left text-3xl font-bold text-gray-900 mb-2"
    const ContentStyle = "text-left text-base font-medium text-gray-500"

    return (
        <div className="min-h-screen bg-gray-100 p-4 sm:p-6 lg:p-4">
            <div className="max-w-4xl mx-10">
                <div className="mb-4">
                    <h3 className={TitleStyle} title="StartTime">Start Time</h3>
                    <p className={ContentStyle}>{ballot?.startTime}</p>
                </div>
                <div className="mb-4">
                    <h3 className={TitleStyle} title="Duration">Duration</h3>
                    <p className={ContentStyle}>{ballot?.duration}</p>
                </div>
                <div className="mb-4">
                    <h1 className={TitleStyle} title="Title">Title</h1>
                    <p className={ContentStyle}>{ballot?.title}</p>
                </div>
                <div className="mb-4">
                    <h1 className={TitleStyle} title="Description">Description</h1>
                    <p className={ContentStyle}>{ballot?.description}</p>
                </div>
                
                <div className="bg-white rounded-lg shadow-md p-6">
                    <form onSubmit={handleSubmit}>
                        <h3 className="text-lg font-semibold text-gray-800 mb-4">
                            Select your choice:
                        </h3>
                        
                        <div className="space-y-3 mb-6">
                            {ballot?.options.map((option) => (
                                <label 
                                    key={option.id}
                                    className={`block p-4 border rounded-lg cursor-pointer transition-colors ${
                                        formData.optionId === option.id
                                            ? 'border-blue-500 bg-blue-50'
                                            : 'border-gray-300 hover:border-gray-400'
                                    }`}
                                >
                                    <input
                                        type="radio"
                                        name="option"
                                        value={option.id}
                                        checked={formData.optionId === option.id}
                                        onChange={() => handleLockOption(option.id)}
                                        className="sr-only"
                                    />
                                    <div className="flex items-center">
                                        <div className={`w-4 h-4 rounded-full border-2 mr-3 ${
                                            formData.optionId === option.id
                                                ? 'border-blue-500 bg-blue-500'
                                                : 'border-gray-300'
                                        }`}>
                                            {formData.optionId === option.id && (
                                                <div className="w-2 h-2 bg-white rounded-full mx-auto mt-0.5"></div>
                                            )}
                                        </div>
                                        <div>
                                            <div className="font-medium text-gray-900">
                                                {option.name}
                                            </div>
                                            {option.description && (
                                                <div className="text-sm text-gray-600">
                                                    {option.description}
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                </label>
                            ))}
                        </div>

                        <div className="flex justify-end gap-4 pt-6 border-t border-gray-200">
                            <button
                                type="button"
                                onClick={() => navigate('/dashboard/ballots')}
                                className="px-6 py-2 border bg-gray-300 text-gray-700 rounded-md hover:bg-gray-50 transition-colors"
                            >
                                Cancel
                            </button>
                            <button
                                type="submit"
                                disabled={isSubmitting}
                                className="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-75 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                            >
                                {isSubmitting ? 'Voting...' : 'Vote'}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    )
}

export default VoteInBallot;
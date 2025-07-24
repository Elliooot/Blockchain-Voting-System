import React, { useState } from 'react';

const electionResults = [
  {
    id: 1,
    title: 'Community Park Renovation Project',
    description: 'Vote on the proposed budget and design for the renovation of the central community park.',
    endTime: '2025-07-30T18:00:00Z',
    totalVoters: 1258,
    result: {
      approve: 890,
      reject: 368,
    },
  },
  {
    id: 2,
    title: 'Annual School Board Election',
    description: 'Electing two new members to the district school board for the upcoming term.',
    endTime: '2025-07-28T20:00:00Z',
    totalVoters: 3450,
    result: {
      'Candidate A': 1560,
      'Candidate B': 1020,
      'Candidate C': 870,
    },
  },
  {
    id: 3,
    title: 'City Council Motion #2025-08',
    description: 'A motion to approve the new zoning regulations for the downtown commercial area.',
    endTime: '2025-08-05T17:00:00Z',
    totalVoters: 872,
    result: null,
  },
];

// Define a type for the component's props
interface ResultCardProps {
  title: string;
  description: string;
  endTime: string;
  totalVoters: number;
  result: Record<string, number> | null;
}

// Use the new type for the props
const ResultCard = ({ title, description, endTime, totalVoters, result }: ResultCardProps) => {
  const [showResult, setShowResult] = useState(false);

  const totalVotes = result ? Object.values(result).reduce((sum, votes) => sum + votes, 0) : 0;

  return (
    <div className="bg-white rounded-lg shadow-md overflow-hidden transition-all duration-300 hover:shadow-xl">
      <div className="p-6">
        <h3 className="text-xl font-bold text-gray-800">{title}</h3>
        
        <p className="text-gray-600 mt-2">{description}</p>

        <div className="mt-4 pt-4 border-t border-gray-200 flex flex-col sm:flex-row justify-between text-sm text-gray-500">
          <span>End Time: {new Date(endTime).toLocaleString()}</span>
          <span>Total Voters: {totalVoters.toLocaleString()}</span>
        </div>
      </div>

      <div className="bg-gray-50 px-6 py-4">
        <div className="flex items-center justify-between">
          <span className="font-semibold text-gray-700">Result</span>
          <button
            onClick={() => setShowResult(!showResult)}
            disabled={!result}
            className={`relative inline-flex items-center h-6 rounded-full w-11 transition-colors duration-300 focus:outline-none ${
              showResult ? 'bg-blue-600' : 'bg-gray-300'
            } ${!result ? 'cursor-not-allowed opacity-50' : 'cursor-pointer'}`}
          >
            <span
              className={`inline-block w-4 h-4 transform bg-white rounded-full transition-transform duration-300 ${
                showResult ? 'translate-x-6' : 'translate-x-1'
              }`}
            />
          </button>
        </div>

        {showResult && result && (
          <div className="mt-4 space-y-3">
            {Object.entries(result).map(([option, votes]) => {
              // With the correct type, 'votes' is now known to be a number.
              // The 'as number' assertion is no longer needed.
              const percentage = totalVotes > 0 ? ((votes / totalVotes) * 100).toFixed(1) : "0";
              return (
                <div key={option}>
                  <div className="flex justify-between text-sm font-medium text-gray-700 mb-1">
                    <span>{option}</span>
                    {/* 'votes' is correctly inferred as a number here too */}
                    <span>{votes.toLocaleString()} votes ({percentage}%)</span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-2.5">
                    <div className="bg-blue-500 h-2.5 rounded-full" style={{ width: `${percentage}%` }}></div>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
};


function Result() {
  return (
    <div className="min-h-screen bg-gray-100 p-4 sm:p-6 lg:p-8">
      <div className="max-w-4xl mx-auto">
        <h1 className="text-3xl font-bold text-gray-900 mb-6">Election Results</h1>
        
        <div className="space-y-4">
          {electionResults.map((item) => (
            <ResultCard key={item.id} {...item} />
          ))}
        </div>
      </div>
    </div>
  );
}

export default Result;

// Title, Description, End Time, Total number of voters, Result(Switch: Show/Hide)
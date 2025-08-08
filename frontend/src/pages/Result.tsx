import React, { useCallback, useEffect, useState } from 'react';
import { fetchBallotResult } from '../api/apiService';

interface ApiResult {
  id: number;
  title: string;
  description: string;
  startTime: string;
  endTime: string;
  optionNames: Array<string>;
  voteCounts: Array<number>;
  totalVotes: number;
  resultOptionNames: Array<string>;
}

interface BallotResult {
  id: number;
  title: string;
  description: string;
  startTime: string;
  endTime: string;
  options: Array<string>;
  voteCounts: Array<number>;
  totalVotes: number;
  result: Array<string>;
}

// Use the new type for the props
const ResultCard = ({ id, title, description, startTime, endTime, options, voteCounts, totalVotes, result }: BallotResult) => {
  const [showResult, setShowResult] = useState(false);

  useEffect(() => {
    console.log('ResultCard props', { id, options, voteCounts, totalVotes, result });
  }, [id, options, voteCounts, totalVotes, result]);

  return (
    <div className="bg-white rounded-lg shadow-md overflow-hidden transition-all duration-300 hover:shadow-xl">
      <div className="p-6">
        <h3 className="text-xl font-bold text-gray-800">{title}</h3>
        
        <p className="text-gray-600 mt-2">{description}</p>

        <div className="mt-4 pt-4 border-t border-gray-200 flex flex-col sm:flex-row justify-between text-sm text-gray-500">
          <span>Start Time: {new Date(startTime).toLocaleString()}</span>
          <span>End Time: {new Date(endTime).toLocaleString()}</span>
          <span>Total Votes: {totalVotes.toLocaleString()}</span>
        </div>
      </div>

      <div className="bg-gray-50 px-6 py-4">
        <div className="flex items-center justify-between">
          <span className="font-semibold text-gray-700">Result</span>
          <button
            onClick={() => setShowResult(!showResult)}
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

        {showResult && options && (
          <div className="mt-4 space-y-3">
            {options.map((option, index) => {
              const isWinner = result.includes(option);
              const percentage = totalVotes > 0 ? ((voteCounts[index] / totalVotes) * 100).toFixed(1) : "0";
              return (
                <div key={option}>
                  <div className="flex justify-between text-sm font-medium text-gray-700 mb-1">
                    <span className={isWinner ? 'font-bold text-blue-600' : ''}>{option}</span>
                    <span>{voteCounts[index].toLocaleString()} votes ({percentage}%)</span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-2.5">
                    <div className={`${isWinner ? 'bg-blue-500' : 'bg-gray-400'} h-2.5 rounded-full`} style={{ width: `${percentage}%` }}></div>
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
  const [result, setResult] = useState<BallotResult[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  
  const loadResult = useCallback(async () => {
    try {
      setIsLoading(true);
      const resultData: ApiResult[] = await fetchBallotResult();

      // This tasks will be set to result, and result will later map the fetched data into ResultCard
      const formattedTasks = resultData.map(ballotDto => ({
        id: ballotDto.id,
        title: ballotDto.title,
        description: ballotDto.description,
        startTime: ballotDto.startTime,
        endTime: ballotDto.endTime,
        options: ballotDto.optionNames,
        voteCounts: ballotDto.voteCounts,
        totalVotes: ballotDto.totalVotes,
        result: ballotDto.resultOptionNames
      }));

      console.log("Options check: ", formattedTasks[0].options);
      
      setResult(formattedTasks);
    } catch (error) {
      console.error("Failed to load result: ", error);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    loadResult();
  }, [loadResult]);

  return (
    <div className="min-h-screen bg-gray-100 p-4 sm:p-6 lg:p-8">
      <div className="max-w-4xl mx-auto">
        <h1 className="text-3xl font-bold text-gray-900 mb-6">Ballot Results</h1>

        {isLoading && <div className='p-5 text-center text-gray-500'>Loading results...</div>}
        
        {!isLoading && (
          <div className="space-y-4">
            {result.map((item) => (
              <ResultCard
                key={item.id}
                id={item.id}
                title={item.title}
                description={item.description}
                startTime={item.startTime}
                endTime={item.endTime}
                options={item.options}
                voteCounts={item.voteCounts}
                totalVotes={item.totalVotes}
                result={item.result}
              />
            ))
            }
          </div>
        )}
      </div>
    </div>
  );
}

export default Result;
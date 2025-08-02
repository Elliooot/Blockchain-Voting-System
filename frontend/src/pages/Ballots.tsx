import React, { useState, useEffect, useCallback } from 'react';
import { Edit as EditIcon, Add as AddIcon } from '@mui/icons-material';
import { fetchBallots, createBallot } from '../api/apiService';
import { useNavigate } from 'react-router-dom';

interface ApiBallot {
  id: number;
  title: string;
  description: string;
  startTime: string; 
  duration: string; 
  options: Array<{
    id: number;
    name: string;
    description: string;
    voteCount: number;
    displayOrder: number;
  }>;
  status: 'Pending' | 'Active' | 'Ended';
  message?: string;
}

interface BallotTask {
  id: number;
  title: string;
  description: string;
  startTime: string; 
  duration: string; 
  options: Array<{
    id: number;
    name: string;
    description: string;
    voteCount: number;
    displayOrder: number;
  }>;
  status: 'Pending' | 'Active' | 'Ended';
  message?: string;
}

const statusDisplayMap = {
  'Pending': 'Upcoming',
  'Active': 'In Progress',
  'Ended': 'Completed'
} as const;

const TaskCard = ({ title, description, options, status }: Omit<BallotTask, 'id' | 'startTime' | 'duration' | 'message'>) => {
  const [isExpanded, setIsExpanded] = useState(false);

  const handleToggleExpand = () => {
    setIsExpanded(!isExpanded);
  };

  return (
    <div className="bg-white mb-4 shadow-md p-4 rounded-lg">
      <h2 className="text-gray-900 mb-2 font-semibold truncate" title={title}>
        {title}
      </h2>
      
      <div 
        className={`text-sm font-medium mb-4 text-gray-700 cursor-pointer ${isExpanded ? 'line-clamp-none' : 'line-clamp-3'}`}
        onClick={handleToggleExpand}
      >
        {description}
      </div>

      <div className="text-xs text-gray-500 mb-2">
        {options.length} option(s) available
      </div>
      
      <div className="h-px bg-gray-200 mb-2" />

      <div className="mb-2">
        <span className={`px-2 py-1 rounded-full text-xs font-medium ${
          status === 'Pending' ? 'bg-blue-100 text-blue-800' :
          status === 'Active' ? 'bg-green-100 text-green-800' :
          'bg-gray-100 text-gray-800'
        }`}>
          {statusDisplayMap[status]}
        </span>
      </div>
      
      <div className="flex gap-2">
        <button className="bg-transparent border-none cursor-pointer p-1 rounded text-lg text-gray-500 hover:bg-gray-100">+</button>
        <button className="bg-transparent border-none cursor-pointer p-1 rounded text-lg text-gray-500 hover:bg-gray-100">📅</button>
        <EditIcon className="bg-transparent border-none cursor-pointer p-1 rounded text-lg text-gray-500 hover:bg-gray-100" fontSize='large'></EditIcon>
        <button className="bg-transparent border-none cursor-pointer p-1 rounded text-lg text-gray-500 hover:bg-gray-100">📋</button>
      </div>
    </div>
  );
};

function Ballots() {
  const [tasks, setTasks] = useState<BallotTask[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  const loadBallots = useCallback(async () => {
    try {
        setIsLoading(true);
        setError(null);
        const apiData: ApiBallot[] = await fetchBallots();

        const formattedTasks = apiData.map(ballot => ({
            id: ballot.id,
            title: ballot.title,
            description: ballot.description,
            startTime: ballot.startTime,
            duration: ballot.duration,
            options: ballot.options,
            status: ballot.status,
            message: ballot.message
        }));

        setTasks(formattedTasks);
    } catch (error) {
        console.error("Failed to load ballots in component: ", error);
        setError("Could not fetch election. Please try again later.");
    } finally {
        setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    loadBallots();
  }, [loadBallots]);

  const handleCreateBallot = async () => {
    navigate('/dashboard/ballots/create');
  };

  const columns: BallotTask['status'][] = ['Pending', 'Active', 'Ended'];

  return (
    <div className="p-5 min-h-screen">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Manage Ballots</h1>
        <button
          onClick={handleCreateBallot}
          className="flex items-center gap-2 bg-blue-600 text-white font-semibold py-2 px-4 rounded-lg shadow-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-75 transition-colors"
        >
          <AddIcon />
          Create Ballot
        </button>
      </div>

      {isLoading && <div className="p-5 text-center text-gray-500">Loading elections...</div>}
      
      {error && !isLoading && <div className="p-5 text-center text-red-500">{error}</div>}
      
      {!isLoading && !error && (    
        <div className="flex flex-col md:flex-row gap-6">
            {columns.map(columnStatus => (
            <div key={columnStatus} className="bg-gray-200 p-5 rounded-lg w-full md:w-1/2 lg:w-1/3">
                <div className="flex items-start justify-between mb-4">
                <h3 className="text-lg font-medium text-gray-800 whitespace-nowrap">{columnStatus}</h3>
                <button className="bg-transparent border-none cursor-pointer p-1 rounded hover:bg-gray-300">
                    <span className="text-lg text-gray-600">⋯</span>
                </button>
                </div>
                
                {tasks
                .filter(task => task.status === columnStatus)
                .map(task => (
                    <TaskCard 
                    key={task.id} 
                    title={task.title} 
                    description={task.description} 
                    options={task.options}
                    status={task.status}
                    />
                ))
                }
            </div>
            ))}
        </div>
        )}
    </div>
  );
}

export default Ballots;
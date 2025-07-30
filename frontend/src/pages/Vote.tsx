import React, { useState, useEffect } from 'react';
import { Edit as EditIcon } from '@mui/icons-material';
import { fetchBallots } from '../api/apiService';

interface BallotTask {
  id: number;
  category: string;
  description: string;
  status: 'Upcoming' | 'In Progress' | 'Completed';
}

interface ApiBallot {
  id: number;
  title: string;
  description: string;
  startTime: string; 
  duration: string; 
  status: 'Upcoming' | 'In Progress' | 'Completed';
}

const TaskCard = ({ category, description }: Omit<BallotTask, 'id' | 'status'>) => {
  const [isExpanded, setIsExpanded] = useState(false);

  const handleToggleExpand = () => {
    setIsExpanded(!isExpanded);
  };

  return (
    <div className="bg-white mb-4 shadow-md p-4 rounded-lg">
      <h2 className="text-gray-900 mb-2 font-semibold truncate" title={category}>
        {category}
      </h2>
      
      <div 
        className={`text-sm font-medium mb-4 text-gray-700 cursor-pointer ${isExpanded ? 'line-clamp-none' : 'line-clamp-3'}`}
        onClick={handleToggleExpand}
      >
        {description}
      </div>
      
      {/* {showImage && (
        <div className="h-32 bg-gray-100 rounded mb-4 flex items-center justify-center border-2 border-dashed border-gray-300">
          <div className="text-4xl text-gray-400 transform rotate-45">
            ⊞
          </div>
        </div>
      )} */}
      
      <div className="h-px bg-gray-200 mb-2" />
      
      <div className="flex gap-2">
        <button className="bg-transparent border-none cursor-pointer p-1 rounded text-lg text-gray-500 hover:bg-gray-100">+</button>
        <button className="bg-transparent border-none cursor-pointer p-1 rounded text-lg text-gray-500 hover:bg-gray-100">📅</button>
        <EditIcon className="bg-transparent border-none cursor-pointer p-1 rounded text-lg text-gray-500 hover:bg-gray-100" fontSize='large'></EditIcon>
        <button className="bg-transparent border-none cursor-pointer p-1 rounded text-lg text-gray-500 hover:bg-gray-100">📋</button>
      </div>
    </div>
  );
};

function Vote() {
  const [tasks, setTasks] = useState<BallotTask[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadBallots = async () => {
      try {
        setIsLoading(true);
        setError(null);
        const apiData: ApiBallot[] = await fetchBallots();

        const formattedTasks = apiData.map(ballot => ({
          id: ballot.id,
          category: ballot.title,
          description: ballot.description,
          status: ballot.status,
        }));

        setTasks(formattedTasks);
      } catch (err) {
        console.error("Failed to load ballots in component:", err);
        setError("Could not fetch election data. Please try again later.");
      } finally {
        setIsLoading(false);
      }
    };

    loadBallots();
  }, []);

  const columns: BallotTask['status'][] = ['Upcoming', 'In Progress', 'Completed'];

  if (isLoading) {
    return <div className="p-5 text-center text-gray-500">Loading elections...</div>;
  }

  if (error) {
    return <div className="p-5 text-center text-red-500">{error}</div>;
  }

  return (
    <div className="p-5 min-h-screen">      
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
                  category={task.category} 
                  description={task.description} 
                />
              ))
            }
          </div>
        ))}
      </div>
    </div>
  );
}

export default Vote;
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowBack as ArrowBackIcon, Add as AddIcon, Delete as DeleteIcon } from '@mui/icons-material';
import { createBallot } from '../api/apiService';

interface BallotOption {
  name: string;
  description: string;
}

interface BallotFormData {
  title: string;
  description: string;
  startTime: string;
  duration: string;
  options: BallotOption[];
}

function CreateBallot() {
  const navigate = useNavigate();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [formData, setFormData] = useState<BallotFormData>({
    title: '',
    description: '',
    startTime: '',
    duration: '',
    options: [{ name: '', description: '' }]
  });

  const handleInputChange = (field: keyof BallotFormData, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  const handleOptionChange = (index: number, field: keyof BallotOption, value: string) => {
    setFormData(prev => ({
      ...prev,
      options: prev.options.map((option, i) => 
        i === index ? { ...option, [field]: value } : option
      )
    }));
  };

  const addOption = () => {
    setFormData(prev => ({
      ...prev,
      options: [...prev.options, { name: '', description: '' }]
    }));
  };

  const removeOption = (index: number) => {
    if (formData.options.length > 1) {
      setFormData(prev => ({
        ...prev,
        options: prev.options.filter((_, i) => i !== index)
      }));
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);

    const payload = {
      ...formData,
      duration: `PT${formData.duration}H`
    };

    try {
      await createBallot(payload);
      alert('Ballot created successfully!');
      navigate('/dashboard/ballots');
    } catch (error) {
      console.error('Failed to create ballot:', error);
      alert('Failed to create ballot. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const labelStyle = "block text-gray-700 text-sm text-left font-bold mb-2";
  const inputStyle = "shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline bg-gray-100";

  return (
    <div className="min-h-screen bg-gray-100 p-4 sm:p-6 lg:p-4">
      <div className="flex items-center justify-between mb-6">
        <div className="flex items-center gap-4">
          <button
            onClick={() => navigate('/dashboard/ballots')}
            className="flex items-center gap-2 text-gray-600 hover:text-gray-800 transition-colors"
          >
            <ArrowBackIcon />
            Back to Ballots
          </button>
          
        </div>
      </div>

      <div className="max-w-4xl mx-auto">
        <h1 className="text-3xl font-bold text-gray-900 mb-2">Create New Ballot</h1>
        <p className="text-gray-600 mb-8">Fill out the form below to create a new ballot.</p>

        <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-4xl">
            <form onSubmit={handleSubmit} className="space-y-6">

            <div>
                <label className={labelStyle}>
                    Ballot Title *
                </label>
                <input
                    type="text"
                    required
                    value={formData.title}
                    onChange={(e) => handleInputChange('title', e.target.value)}
                    className={inputStyle}
                    placeholder="Enter ballot title"
                />
            </div>
            

            <div>
                <label className={labelStyle}>
                Description *
                </label>
                <textarea
                required
                rows={4}
                value={formData.description}
                onChange={(e) => handleInputChange('description', e.target.value)}
                className={inputStyle}
                placeholder="Describe what this ballot is about..."
                />
            </div>

            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                    <label className={labelStyle}>
                    Start Time *
                    </label>
                    <input
                    type="datetime-local"
                    required
                    value={formData.startTime}
                    onChange={(e) => handleInputChange('startTime', e.target.value)}
                    className={inputStyle}
                    />
                </div>

                <div>
                <label className={labelStyle}>
                    Duration (in hours) *
                </label>
                <input
                    type="number"
                    required
                    min="1"
                    value={formData.duration}
                    onChange={(e) => handleInputChange('duration', e.target.value)}
                    className={inputStyle}
                    placeholder="24"
                />
                </div>
            </div>

            <div>
                <div className="flex items-center justify-between mb-4">
                <label className={labelStyle}>
                    Ballot Options * (At least 1 required)
                </label>
                <button
                    type="button"
                    onClick={addOption}
                    className="flex items-center gap-2 px-3 py-1 bg-blue-600 text-white text-sm rounded-md hover:bg-blue-700 transition-colors"
                >
                    <AddIcon fontSize="small" />
                    Add Option
                </button>
                </div>

                <div className="space-y-4">
                {formData.options.map((option, index) => (
                    <div key={index} className="border border-gray-200 rounded-lg p-4 bg-gray-50">
                    <div className="flex items-center justify-between mb-3">
                        <h4 className="text-sm font-medium text-gray-700">Option {index + 1}</h4>
                        {formData.options.length > 1 && (
                        <button
                            type="button"
                            onClick={() => removeOption(index)}
                            className="text-red-600 hover:text-red-800 transition-colors"
                        >
                            <DeleteIcon fontSize="small" />
                        </button>
                        )}
                    </div>
                    
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div>
                        <label className={labelStyle}>
                            Option Name *
                        </label>
                        <input
                            type="text"
                            required
                            value={option.name}
                            onChange={(e) => handleOptionChange(index, 'name', e.target.value)}
                            className={inputStyle}
                            placeholder="e.g., Candidate A"
                        />
                        </div>
                        
                        <div>
                        <label className={labelStyle}>
                            Option Description
                        </label>
                        <input
                            type="text"
                            value={option.description}
                            onChange={(e) => handleOptionChange(index, 'description', e.target.value)}
                            className={inputStyle}
                            placeholder="Brief description (optional)"
                        />
                        </div>
                    </div>
                    </div>
                ))}
                </div>
            </div>

            <div className="flex justify-end gap-4 pt-6 border-t border-gray-200">
                <button
                    type="button"
                    onClick={() => navigate('/dashboard/ballots')}
                    className="px-6 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 transition-colors"
                >
                Cancel
                </button>
                <button
                    type="submit"
                    disabled={isSubmitting}
                    className="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-75 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                >
                {isSubmitting ? 'Creating...' : 'Create Ballot'}
                </button>
            </div>

            </form>
        </div>
      </div>
    </div>
  );
}

export default CreateBallot;
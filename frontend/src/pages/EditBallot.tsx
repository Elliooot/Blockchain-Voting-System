import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { ArrowBack as ArrowBackIcon, Delete as DeleteIcon } from '@mui/icons-material';
import { editBallot, fetchBallotById, searchVoterByEmail } from '../api/apiService';

interface BallotOption {
  name: string;
  description: string;
}

interface Voter {
  id: number;
  email: string;
}

interface BallotFormData {
  id: number;
  title: string;
  description: string;
  startTime: string;
  duration: string;
  options: BallotOption[];
  qualifiedVotersId: Array<number>;
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
    qualifiedVotersEmail: Array<string>;
    qualifiedVotersId: Array<number>;
}

function EditBallot() {
  const navigate = useNavigate();
  const [ballot, setBallot] = useState<ApiBallot | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [voterEmail, setVoterEmail] = useState(''); 
  const [isAddingVoter, setIsAddingVoter] = useState(false);
  const [qualifiedVotersList, setQualifiedVotersList] = useState<Voter[]>([])

  const { ballotId } = useParams<{ ballotId: string }>();

  const [formData, setFormData] = useState<BallotFormData>({
    id: 0,
    title: '',
    description: '',
    startTime: '',
    duration: '',
    options: [{ name: '', description: '' }],
    qualifiedVotersId: []
  });

  const loadBallot = useCallback(async () => {
    if(!ballotId){
        return;
    }
    try {
        const ballotData: ApiBallot = await fetchBallotById(parseInt(ballotId));

        setBallot(ballotData);

        // Parse the backend ISO8601 Duration -> hour numeric string
        const hoursFromIso = (iso: string) => {
          const m = iso?.match(/PT(\d+)H/i);
          return m ? m[1] : '';
        };

        const toLocalDatetime = (iso: string) => {
          const d = new Date(iso);
          if (isNaN(d.getTime())) return '';
          const yy = d.getFullYear();
          const mm = String(d.getMonth() + 1).padStart(2, '0');
          const dd = String(d.getDate()).padStart(2, '0');
          const hh = String(d.getHours()).padStart(2, '0');
          const mi = String(d.getMinutes()).padStart(2, '0');
          return `${yy}-${mm}-${dd}T${hh}:${mi}`;
        };

        setFormData(prev => ({
          ...prev,
          id: ballotData.id,
          title: ballotData.title,
          description: ballotData.description,
          startTime: toLocalDatetime(ballotData.startTime),
          duration: hoursFromIso(ballotData.duration),
          options: (ballotData.options ?? []).map(o => ({ name: o.name, description: o.description })),
          qualifiedVotersId: ballotData.qualifiedVotersId
        }));
    } catch (error) {
        console.log("Failded to fetch current ballot: ", error);
    }
  }, [ballotId]);
  
  useEffect(() => {
      loadBallot();
  }, [loadBallot]);

  const currentOriginalEmails = useMemo(() => {
    if (!ballot?.qualifiedVotersEmail || !ballot?.qualifiedVotersId) return [];
    return ballot?.qualifiedVotersEmail.filter((_, idx) =>
        formData.qualifiedVotersId?.includes(ballot.qualifiedVotersId[idx])
    );
  }, [ballot, formData.qualifiedVotersId]);

  const handleInputChange = (field: keyof Omit<BallotFormData, 'options' | 'qualifiedVoters'>, value: string) => {
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

  const handleAddVoter = async () => {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!voterEmail || !emailRegex.test(voterEmail)) {
        alert('Please enter a valid email.');
        return;
      }
  
      if (qualifiedVotersList.some(v => v.email === voterEmail.toLowerCase())) {
        alert('This voter has already been added.');
        return;
      }
  
      setIsAddingVoter(true);
      try {
        const foundVoter: Voter = await searchVoterByEmail(voterEmail);
  
        // Verify if api returns a valid voter object
        if (foundVoter && foundVoter.id) {
          setQualifiedVotersList(prev => [...prev, foundVoter]);
          setVoterEmail('');
        } else {
          alert(`Error: No user found with the email "${voterEmail}".`);
        }
      } catch (error) {
        console.error("An error occurred while searching for the voter:", error);
        alert('An error occurred while searching for the voter.');
      } finally {
        setIsAddingVoter(false);
      }
    };
  
    const handleRemoveVoter = (emailToRemove: string) => {
      setQualifiedVotersList(prev => prev.filter(voter => voter.email !== emailToRemove));
      
      // Remove from original voter list
      if (ballot?.qualifiedVotersEmail && ballot?.qualifiedVotersId) {
        const indexToRemove = ballot.qualifiedVotersEmail.indexOf(emailToRemove);

        if(indexToRemove >= 0) {
            const idToRemove = ballot.qualifiedVotersId[indexToRemove];
            setFormData(prev => ({
              ...prev,
              qualifiedVotersId: prev.qualifiedVotersId.filter(id => id !== idToRemove)
            }));
        }
      }
    };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);

    const allQualifiedVoterIds = [
      ...(formData.qualifiedVotersId || []), // original
      ...qualifiedVotersList.map(voter => voter.id) // new
    ];

    const uniqueVoterIds = [...new Set(allQualifiedVoterIds)];

    const payload = {
      ...(formData.title && { title: formData.title }),
      ...(formData.description && { description: formData.description }),
      ...(formData.options && { options: formData.options }),
      ...(formData.startTime && { startTime: new Date(formData.startTime).toISOString() }),
      ...(formData.duration && { duration: `PT${Number(formData.duration)}H` }),
      ...(uniqueVoterIds.length > 0 && { qualifiedVoterIds: uniqueVoterIds }),
    };

    try {
      const idNum = ballot?.id ?? (ballotId ? parseInt(ballotId, 10) : NaN);
      if (!Number.isFinite(idNum)) {
        throw new Error('Invalid ballot id');
      }
      await editBallot(idNum as number, payload);
      alert('Ballot edited successfully!');
      navigate('/dashboard/ballots');
    } catch (error) {
      console.error('Failed to edit ballot:', error);
      alert('Failed to edit ballot. Please try again.');
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
            <h1 className="text-3xl font-bold text-gray-900 mb-2">Edit Ballot</h1>
            <p className="text-gray-600 mb-8">Fill out the form below to edit your ballot.</p>
    
            <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-4xl">
                <form onSubmit={handleSubmit} className="space-y-6">
    
                <div>
                    <label className={labelStyle}>
                        Ballot Title
                    </label>
                    <input
                        type="text"
                        value={formData.title}
                        onChange={(e) => handleInputChange('title', e.target.value)}
                        className={inputStyle}
                        placeholder={ballot?.title || 'Enter ballot title'}
                    />
                </div>
                
    
                <div>
                    <label className={labelStyle}>
                    Description
                    </label>
                    <textarea
                      rows={4}
                      value={formData.description}
                      onChange={(e) => handleInputChange('description', e.target.value)}
                      className={inputStyle}
                      placeholder={ballot?.description || 'Describe what this ballot is about...'}
                    />
                </div>
    
                
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div>
                        <label className={labelStyle}>
                        Start Time
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
                        Duration (in hours)
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
                            Ballot Options
                        </label>
                    </div>
    
                    <div className="space-y-4">
                    {formData.options.map((option, index) => (
                        <div key={index} className="border border-gray-200 rounded-lg p-4 bg-gray-50">
                        <div className="flex items-center justify-between mb-3">
                            <h4 className="text-sm font-medium text-gray-700">Option {index + 1}</h4>
                        </div>
                        
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                            <label className={labelStyle}>
                                Option Name
                            </label>
                            <input
                                type="text"
                                readOnly
                                value={option.name}
                                onChange={(e) => handleOptionChange(index, 'name', e.target.value)}
                                className={inputStyle}
                                placeholder={ballot?.options?.[index]?.name || 'e.g., Candidate A'}
                            />
                            </div>
                            
                            <div>
                            <label className={labelStyle}>
                                Option Description
                            </label>
                            <input
                                readOnly
                                value={option.description}
                                onChange={(e) => handleOptionChange(index, 'description', e.target.value)}
                                className={inputStyle}
                                placeholder={ballot?.options?.[index]?.description || 'Brief description (optional)'}
                            />
                            </div>
                        </div>
                        </div>
                    ))}
                    </div>
                </div>
    
                <div>
                  <label className={labelStyle}>
                    Qualified Voters
                  </label>
                  <p className="text-xs text-gray-500 mb-2">Enter voter emails. Only these users will be able to vote.</p>
                  <div className="flex gap-2 mb-3">
                    <input
                      type="email"
                      value={voterEmail}
                      onChange={(e) => setVoterEmail(e.target.value)}
                      className={inputStyle}
                      placeholder="voter@example.com"
                      disabled={isAddingVoter}
                      onKeyDown={(e) => { if (e.key === 'Enter') { e.preventDefault(); handleAddVoter(); } }}
                    />
                    <button
                      type="button"
                      onClick={handleAddVoter}
                      disabled={isAddingVoter}
                      className="flex-shrink-0 px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
                    >
                      {isAddingVoter ? 'Adding...' : 'Add'}
                    </button>
                  </div>
                  <div className="flex flex-wrap gap-2 p-2 bg-gray-100 rounded-md min-h-[40px]">
                    {currentOriginalEmails.map((voterEmail) => (
                      <div key={voterEmail} className="flex items-center gap-2 bg-blue-100 text-blue-800 text-sm font-medium px-3 py-1 rounded-full">
                        <span>{voterEmail}</span>
                        <button
                          type="button"
                          onClick={() => handleRemoveVoter(voterEmail)}
                          className="text-blue-600 hover:text-blue-800"
                        >
                          <DeleteIcon style={{ fontSize: '16px' }} />
                        </button>
                      </div>
                    ))}
                    {qualifiedVotersList.map((voter) => (
                      <div key={voter.id} className="flex items-center gap-2 bg-blue-100 text-blue-800 text-sm font-medium px-3 py-1 rounded-full">
                        <span>{voter.email}</span>
                        <button
                          type="button"
                          onClick={() => handleRemoveVoter(voter.email)}
                          className="text-blue-600 hover:text-blue-800"
                        >
                          <DeleteIcon style={{ fontSize: '16px' }} />
                        </button>
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
                    {isSubmitting ? 'Editing...' : 'Edit Ballot'}
                    </button>
                </div>
    
                </form>
            </div>
          </div>
        </div>
  );
}

export default EditBallot;
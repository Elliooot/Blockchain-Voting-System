import React from 'react';
import { renderWithRouter, screen, waitFor } from '../test/test-utils';
import { Route, Routes } from 'react-router-dom';
import { describe, it, expect, vi } from 'vitest';
import { act } from '@testing-library/react';

import Homepage from '../pages/Homepage';
import Login from '../pages/Login';
import Register from '../pages/Registration';
import Dashboard from '../pages/Dashboard';
import Overview from '../pages/Overview';
import Result from '../pages/Result';
import Wallet from '../pages/Wallet';
import Profile from '../pages/Profile';
import Setting from '../pages/Setting';
import ChangePassword from '../pages/ChangePassword';
import Ballots from '../pages/Ballots';
import CreateBallot from '../pages/CreateBallot';
import VoteInBallot from '../pages/VoteInBallot';
import EditBallot from '../pages/EditBallot';
import CheckBallot from '../pages/CheckBallot';
import FAQ from '../pages/FAQ';

// Provide a default AuthContext mock so components depending on it can render
vi.mock('../contexts/AuthContext', () => ({
  useAuth: () => ({
    isAuthenticated: true,
    user: { userId: '1', role: 'Admin' },
    token: 'fake-token',
    login: vi.fn(),
    logout: vi.fn(),
    deleteAccount: vi.fn(),
  }),
}));

// Stub API layer to avoid network calls
vi.mock('../api/apiService', () => ({
  fetchBallots: vi.fn().mockResolvedValue([]),
  deleteBallot: vi.fn(),
  createBallot: vi.fn(),
  editBallot: vi.fn(),
  fetchBallotById: vi.fn().mockResolvedValue(null),
  fetchBallotResult: vi.fn().mockResolvedValue([]),
  searchVoterByEmail: vi.fn().mockResolvedValue([]),
  castVote: vi.fn(),
  getVoteRecords: vi.fn().mockResolvedValue([]),
  loadUserWallet: vi.fn().mockResolvedValue(null),
  updateWalletAddress: vi.fn(),
}));

describe('Pages integration tests', () => {
  it('renders Homepage', async () => {
    await act(async () => {
      renderWithRouter(<Homepage />);
    });
    expect(document.body).toBeTruthy();
  });

  it('renders Login', async () => {
    await act(async () => {
      renderWithRouter(<Login />);
    });
    expect(document.body).toBeTruthy();
  });

  it('renders Register', async () => {
    await act(async () => {
      renderWithRouter(<Register />);
    });
    expect(document.body).toBeTruthy();
  });

  it('renders Dashboard with nested Overview', async () => {
    await act(async () => {
      renderWithRouter(
        <Routes>
          <Route path="/dashboard" element={<Dashboard />}> 
            <Route index element={<Overview />} />
          </Route>
        </Routes>,
        { initialEntries: ['/dashboard'] }
      );
    });
    
    await waitFor(() => {
      expect(document.body).toBeTruthy();
    });
  });

  it('renders Ballots', async () => {
    let component: any;
    
    await act(async () => {
      component = renderWithRouter(<Ballots />);
    });
    
  // Wait for the component to fully render and update its state
    await waitFor(() => {
      expect(screen.getByRole('heading', { name: /Manage Ballots/i })).toBeInTheDocument();
    }, { timeout: 3000 });
  });

  it('renders CreateBallot', async () => {
    await act(async () => {
      renderWithRouter(<CreateBallot />);
    });
    
    await waitFor(() => {
      expect(document.body).toBeTruthy();
    });
  });

  it('renders VoteInBallot with param', async () => {
    await act(async () => {
      renderWithRouter(
        <Routes>
          <Route path="/dashboard/ballots/vote/:ballotId" element={<VoteInBallot />} />
        </Routes>,
        { initialEntries: ['/dashboard/ballots/vote/123'] }
      );
    });
    
    await waitFor(() => {
      expect(document.body).toBeTruthy();
    });
  });

  it('renders EditBallot with param', async () => {
    await act(async () => {
      renderWithRouter(
        <Routes>
          <Route path="/dashboard/ballots/edit/:ballotId" element={<EditBallot />} />
        </Routes>,
        { initialEntries: ['/dashboard/ballots/edit/123'] }
      );
    });
    
    await waitFor(() => {
      expect(document.body).toBeTruthy();
    });
  });

  it('renders CheckBallot with param', async () => {
    await act(async () => {
      renderWithRouter(
        <Routes>
          <Route path="/dashboard/ballots/check/:ballotId" element={<CheckBallot />} />
        </Routes>,
        { initialEntries: ['/dashboard/ballots/check/123'] }
      );
    });
    
    await waitFor(() => {
      expect(document.body).toBeTruthy();
    });
  });

  it('renders other pages', async () => {
    await act(async () => {
      renderWithRouter(<Result />);
    });
    
    await act(async () => {
      renderWithRouter(<Wallet />);
    });
    
    await act(async () => {
      renderWithRouter(<Profile />);
    });
    
    await act(async () => {
      renderWithRouter(<Setting />);
    });
    
    await act(async () => {
      renderWithRouter(<ChangePassword />);
    });
    
    await act(async () => {
      renderWithRouter(<FAQ />);
    });
    
    expect(document.body).toBeTruthy();
  });
});
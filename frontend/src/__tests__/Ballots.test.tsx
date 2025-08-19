import React from 'react';
import { renderWithRouter, screen, waitFor } from '../test/test-utils';
import Ballots from '../pages/Ballots';
import { describe, it, expect, vi } from 'vitest';

vi.mock('../contexts/AuthContext', () => ({
  useAuth: () => ({
    isAuthenticated: true,
    user: { userId: '1', role: 'Admin' },
    token: 'fake-token',
  }),
}));

vi.mock('../api/apiService', () => ({
  fetchBallots: vi.fn().mockResolvedValue([
    {
      id: 101,
      title: 'Election 2025',
      description: 'General election',
      startTime: '2026-01-01T00:00:00Z',
      duration: '48h',
      options: [
        { id: 1, name: 'A', description: 'A', voteCount: 0 },
        { id: 2, name: 'B', description: 'B', voteCount: 0 },
      ],
      qualifiedVoterIds: [1,2,3],
      status: 'Pending',
      hasVoted: false,
    },
  ]),
  deleteBallot: vi.fn(),
}));

describe('Ballots page', () => {
  it('renders columns and fetched ballot', async () => {
    renderWithRouter(<Ballots />);

    // shows loading first
    expect(screen.getByText(/Loading elections/i)).toBeInTheDocument();

    // after data load, columns and item appear
    await waitFor(() => {
      expect(screen.getByText('Pending')).toBeInTheDocument();
      expect(screen.getByText('Active')).toBeInTheDocument();
      expect(screen.getByText('Ended')).toBeInTheDocument();
      expect(screen.getByText('Election 2025')).toBeInTheDocument();
    });
  });

  it('shows Create Ballot button for Admin', async () => {
    renderWithRouter(<Ballots />);
    await waitFor(() => {
      const buttons = screen.getAllByRole('button', { name: /Create Ballot/i });
      expect(buttons.length).toBeGreaterThan(0);
    });
  });
});

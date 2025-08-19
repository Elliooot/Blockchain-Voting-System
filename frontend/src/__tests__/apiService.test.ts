import { describe, it, expect, vi, beforeEach } from 'vitest';

vi.mock('../api/axiosConfig', () => {
  return {
    default: {
      get: vi.fn(),
      post: vi.fn(),
      put: vi.fn(),
      patch: vi.fn(),
      delete: vi.fn(),
      defaults: { headers: { common: {} as Record<string, string> } },
      interceptors: { request: { use: vi.fn() }, response: { use: vi.fn() } },
    },
  };
});

import axiosInstance from '../api/axiosConfig';
import { fetchBallots, createBallot, editBallot, deleteBallot, fetchBallotById } from '../api/apiService';

const mockedAxios = axiosInstance as unknown as {
  get: ReturnType<typeof vi.fn>;
  post: ReturnType<typeof vi.fn>;
  put: ReturnType<typeof vi.fn>;
  patch: ReturnType<typeof vi.fn>;
  delete: ReturnType<typeof vi.fn>;
  defaults: { headers: { common: Record<string, string> } };
};

beforeEach(() => {
  vi.resetAllMocks();
});

describe('apiService', () => {
  it('fetchBallots calls GET /ballots', async () => {
    mockedAxios.get.mockResolvedValueOnce({ data: [{ id: 1 }] });
    const data = await fetchBallots();
    expect(mockedAxios.get).toHaveBeenCalledWith('/ballots');
    expect(data).toEqual([{ id: 1 }]);
  });

  it('createBallot calls POST /ballots/create', async () => {
    mockedAxios.post.mockResolvedValueOnce({ data: { ok: true } });
    const res = await createBallot({ title: 't' });
    expect(mockedAxios.post).toHaveBeenCalledWith('/ballots/create', { title: 't' });
    expect(res).toEqual({ ok: true });
  });

  it('editBallot calls PATCH /ballots/update/:id', async () => {
    mockedAxios.patch.mockResolvedValueOnce({ data: { id: 5 } });
    const res = await editBallot(5, { title: 'x' });
    expect(mockedAxios.patch).toHaveBeenCalledWith('/ballots/update/5', { title: 'x' });
    expect(res).toEqual({ id: 5 });
  });

  it('deleteBallot calls DELETE ballots/delete/:id', async () => {
    mockedAxios.delete.mockResolvedValueOnce({ data: { deleted: true } });
    const res = await deleteBallot(7);
    expect(mockedAxios.delete).toHaveBeenCalledWith('ballots/delete/7');
    expect(res).toEqual({ deleted: true });
  });

  it('fetchBallotById calls GET /ballots/:id', async () => {
    mockedAxios.get.mockResolvedValueOnce({ data: { id: 9 } });
    const res = await fetchBallotById(9);
    expect(mockedAxios.get).toHaveBeenCalledWith('/ballots/9');
    expect(res).toEqual({ id: 9 });
  });
});

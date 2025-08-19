import React from 'react';
import type { PropsWithChildren } from 'react';
import { render } from '@testing-library/react';
import type { RenderOptions } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';

// Simple wrapper to provide routing context
const AllProviders: React.FC<PropsWithChildren<{ initialEntries?: string[] }>> = ({ children, initialEntries }) => {
  return <MemoryRouter initialEntries={initialEntries}>{children}</MemoryRouter>;
};

export function renderWithRouter(ui: React.ReactElement, options?: RenderOptions & { initialEntries?: string[] }) {
  const { initialEntries, ...rest } = (options || {}) as RenderOptions & { initialEntries?: string[] };
  return render(ui, { wrapper: (p: PropsWithChildren) => <AllProviders initialEntries={initialEntries} {...p} />, ...rest });
}

export * from '@testing-library/react';

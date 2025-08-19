import React from 'react';

export const MockIcon = React.forwardRef<HTMLDivElement, any>((props, ref) => {
  return React.createElement('div', {
    ref,
    'data-testid': 'mui-icon',
    'aria-label': 'mock icon',
    role: 'img',
    ...props
  });
});

MockIcon.displayName = 'MockIcon';

export const Dashboard = MockIcon;
export const HowToVote = MockIcon;
export const Analytics = MockIcon;
export const Wallet = MockIcon;
export const AccountBox = MockIcon;
export const Settings = MockIcon;
export const ListAlt = MockIcon;
export const Logout = MockIcon;
export const Menu = MockIcon;
export const Add = MockIcon;

export const Edit = MockIcon;
export const Delete = MockIcon;
export const Search = MockIcon;
export const Close = MockIcon;
export const ArrowBack = MockIcon;
export const ArrowForward = MockIcon;
export const MoreVert = MockIcon;
export const Visibility = MockIcon;
export const VisibilityOff = MockIcon;
export const PushPin = MockIcon;
export const Note = MockIcon;
export const ZoomIn = MockIcon;
export const ExpandMore = MockIcon;
export const ExpandLess = MockIcon;
export const Poll = MockIcon;
export const CheckCircle = MockIcon;
export const Schedule = MockIcon;

export default MockIcon;
import { Typography } from '@mui/material';

function Overview() {
  return (
    <div className="min-h-screen bg-gray-100 dark:bg-gray-900 dark:text-gray-100 p-4 sm:p-6 lg:p-8">
      <Typography variant="h4" gutterBottom>
        Dashboard Overview
      </Typography>
      <Typography>
        Welcome to the SecureVote dashboard. Here you can find a summary of all voting activities.
      </Typography>
    </div>
  );
}

export default Overview;
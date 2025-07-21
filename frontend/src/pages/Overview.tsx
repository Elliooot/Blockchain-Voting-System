import { Typography } from '@mui/material';

function Overview() {
  return (
    <div>
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
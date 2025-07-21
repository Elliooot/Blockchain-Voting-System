import { Typography } from '@mui/material';

function Vote() {
  return (
    <div>
      <Typography variant="h4" gutterBottom>
        Cast Your Vote
      </Typography>
      <Typography>
        Demo
      </Typography>
      {/* 這裡將會是您的投票表單 */}
    </div>
  );
}

export default Vote;
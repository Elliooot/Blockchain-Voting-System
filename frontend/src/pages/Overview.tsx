import { Typography, Card, CardContent, Box, Stack } from '@mui/material';
import { HowToVote, Poll, CheckCircle, Schedule } from '@mui/icons-material';
import { Link } from 'react-router-dom';

function Overview() {
  // Can use API to fetch data later
  const stats = [
    { title: 'Active Ballots', value: '3', icon: <Poll className="text-blue-300" />, color: 'bg-blue-50' },
    { title: 'Total Votes Cast', value: '127', icon: <HowToVote className="text-green-300" />, color: 'bg-green-50' },
    { title: 'Completed Ballots', value: '8', icon: <CheckCircle className="text-purple-300" />, color: 'bg-purple-50' },
    { title: 'Upcoming Ballots', value: '2', icon: <Schedule className="text-orange-300" />, color: 'bg-orange-50' },
  ];

  return (
    <div className="min-h-screen bg-gray-100 dark:bg-gray-900 dark:text-gray-100 p-4 sm:p-6 lg:p-8">
      <Typography variant="h4" gutterBottom>
        Dashboard Overview
      </Typography>
      <Typography className="mb-6">
        Welcome to the SecureVote dashboard. Here you can find a summary of all voting activities.
      </Typography>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 mt-8 mb-8">
        {stats.map((stat, index) => (
          <Card key={index} className="h-full hover:shadow-lg transition-shadow duration-300">
            <CardContent>
              <Box className="flex items-center justify-between">
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    {stat.title}
                  </Typography>
                  <Typography variant="h4" component="div" className="font-bold">
                    {stat.value}
                  </Typography>
                </Box>
                <Box className={`p-3 rounded-full ${stat.color}`}>
                  {stat.icon}
                </Box>
              </Box>
            </CardContent>
          </Card>
        ))}
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <Card className="h-full">
          <CardContent>
            <Typography variant="h6" gutterBottom>
              Quick Actions
            </Typography>
            <Stack spacing={1}>
              <Typography variant="body2" className="text-gray-600 cursor-pointer hover:underline" component={Link} to="/dashboard/ballots">
                → View Active Ballots
              </Typography>
              <Typography variant="body2" className="text-gray-600 cursor-pointer hover:underline" component={Link} to="/dashboard/result">
                → Check Voting Results
              </Typography>
              <Typography variant="body2" className="text-gray-600 cursor-pointer hover:underline" component={Link} to="/dashboard/wallet">
                → Manage Wallet Connection
              </Typography>
            </Stack>
          </CardContent>
        </Card>

        <Card className="h-full">
          <CardContent>
            <Typography variant="h6" gutterBottom>
              Recent Activity
            </Typography>
            <Box className="space-y-2">
              <Typography variant="body2" className="text-gray-600">
                • New ballot "Community Fund Allocation" started
              </Typography>
              <Typography variant="body2" className="text-gray-600">
                • You voted on "Infrastructure Upgrade"
              </Typography>
              <Typography variant="body2" className="text-gray-600">
                • "Budget Proposal 2024" voting ended
              </Typography>
            </Box>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}

export default Overview;
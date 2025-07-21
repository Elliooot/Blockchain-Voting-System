import * as React from 'react';
import Box from '@mui/material/Box';
import Tab from '@mui/material/Tab';
import TabContext from '@mui/lab/TabContext';
import TabList from '@mui/lab/TabList';
import TabPanel from '@mui/lab/TabPanel';

export default function LabTabs() {
  const [value, setValue] = React.useState('1');

  const handleChange = (event: React.SyntheticEvent, newValue: string) => {
    setValue(newValue);
  };

  return (
    <Box sx={{ width: '100%', typography: 'body1' }}>
      <TabContext value={value}>
        <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
          <TabList onChange={handleChange} aria-label="lab API tabs example">
            <Tab label="Overview" value="1" />
            <Tab label="Vote" value="2" />
            <Tab label="Result" value="3" />
            <Tab label="Profile" value="4" />
            <Tab label="Wallet" value="5" />
          </TabList>
        </Box>
        <TabPanel value="1">Overview</TabPanel>
        <TabPanel value="2">Vote</TabPanel>
        <TabPanel value="3">Result</TabPanel>
        <TabPanel value="4">Profile</TabPanel>
        <TabPanel value="5">Wallet</TabPanel>
      </TabContext>
    </Box>
  );
}

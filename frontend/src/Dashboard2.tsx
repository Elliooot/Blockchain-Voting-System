import { useState } from 'react';
// import MenuIcon from '@mui/icons-material/Menu';
import { 
    Dashboard as DashboardIcon, 
    HowToVote as HowToVoteIcon,
    ListAlt as ListAltIcon,
    Analytics as AnalyticsIcon,
    Wallet as WalletIcon,
    AccountBox as AccountBoxIcon,
    Settings as SettingsIcon
} from '@mui/icons-material';
import { AppProvider, DashboardHeader, DashboardLayout } from '@toolpad/core';
import LabTabs from './components/labtabs';
import { Box } from '@mui/material';
import { Outlet } from 'react-router-dom';
// import { createTheme } from '@mui/material';

// const theme = createTheme({
    
// })

function Dashboard() {
    return (
        <AppProvider
            navigation={[
                {
                segment: 'dashboard2/overview',
                title: 'Overview',
                icon: <DashboardIcon />,
                },
                {
                segment: 'dashboard2/ballots',
                title: 'Ballots',
                icon: <HowToVoteIcon />,
                },
                {
                segment: 'dashboard2/voters',
                title: 'Voters',
                icon: <ListAltIcon />,
                },
                {
                segment: 'dashboard2/wallet',
                title: 'Wallet',
                icon: <WalletIcon />,
                },
                {
                segment: 'dashboard2/profile',
                title: 'Profile',
                icon: <AccountBoxIcon />,
                },
                {
                segment: 'dashboard2/setting',
                title: 'Setting',
                icon: <SettingsIcon />,
                },
            ]}
            branding={{
                title: 'Dashboard',
                homeUrl: '/dashboard2',
                // logo: <FavIcon />
            }}
            // router={router}
            // theme={theme}
            // window={demoWindow}
        >
            <DashboardLayout
                sidebarExpandedWidth={280}>
                {/* <DemoPageContent pathname={router.pathname} /> */}
                
                <main>
                    <Outlet />
                </main>
            </DashboardLayout>        
        </AppProvider>
    )
}

export default Dashboard;
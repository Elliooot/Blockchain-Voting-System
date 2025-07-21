import { useState } from 'react';
import { 
    Dashboard as DashboardIcon, 
    HowToVote as HowToVoteIcon,
    Analytics as AnalyticsIcon,
    Wallet as WalletIcon,
    AccountBox as AccountBoxIcon,
    Settings as SettingsIcon
} from '@mui/icons-material';
import { AppProvider, DashboardHeader, DashboardLayout } from '@toolpad/core';
import { createTheme } from '@mui/material';

import { Outlet, Route, Routes } from 'react-router-dom';

const theme = createTheme({
    palette: {
    // text: {
    //   primary: '#ff0000', 
    // },
    background: {
      paper: '#f0f0f0',
    },
  },
  components: {
    MuiAppBar: {
        styleOverrides: {
            root: {
                color: '#ff0000'
            }
        }
    }
  }
});

function Dashboard() {
    return (
        <AppProvider
            navigation={[
                {
                segment: 'dashboard/overview',
                title: 'Overview',
                icon: <DashboardIcon />,
                },
                {
                segment: 'dashboard/vote',
                title: 'Vote',
                icon: <HowToVoteIcon />,
                },
                {
                segment: 'dashboard/result',
                title: 'Result',
                icon: <AnalyticsIcon />,
                },
                {
                segment: 'dashboard/wallet',
                title: 'Wallet',
                icon: <WalletIcon />,
                },
                {
                segment: 'dashboard/profile',
                title: 'Profile',
                icon: <AccountBoxIcon />,
                },
                {
                segment: 'dashboard/setting',
                title: 'Setting',
                icon: <SettingsIcon />,
                },
            ]}
            branding={{
                title: 'Voting',
                homeUrl: '/dashboard',
                // logo: <FavIcon />
            }}
            // router={router}
            theme={theme}
            // window={demoWindow}
        >
            <DashboardLayout
                sidebarExpandedWidth={280}>
                
                {/* <DemoPageContent pathname={router.pathname} /> */}
                
                <main style={{ padding: '24px'}}>
                    <Outlet />
                </main>
            </DashboardLayout>        
        </AppProvider>
    )
}

export default Dashboard;
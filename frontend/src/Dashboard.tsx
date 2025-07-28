import { useState } from 'react';
import { 
    Dashboard as DashboardIcon, 
    HowToVote as HowToVoteIcon,
    Analytics as AnalyticsIcon,
    Wallet as WalletIcon,
    AccountBox as AccountBoxIcon,
    Settings as SettingsIcon,
    ListAlt as VotersIcon,
    Logout as LogoutIcon
} from '@mui/icons-material';
import MenuIcon from '@mui/icons-material/Menu';
import MuiDrawer from '@mui/material/Drawer';
import { Box, CssBaseline, Divider, IconButton, List, ListItem, ListItemButton, ListItemIcon, ListItemText, styled, ThemeProvider, type Theme } from '@mui/material';
import { createTheme } from '@mui/material';

import { Outlet, Link, useNavigate } from 'react-router-dom';
import type { CSSObject } from '@emotion/react';
import { useAuth } from './contexts/AuthContext';

const drawerWidth = 240;

const openedMixin = (theme: Theme): CSSObject => ({
    width: drawerWidth,
    transition: theme.transitions.create('width', {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.enteringScreen,
    }),
    overflowX: 'hidden',
});

const closedMixin = (theme: Theme): CSSObject => ({
    transition: theme.transitions.create('width', {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.enteringScreen,
    }),
    overflowX: 'hidden',
    width: `calc(${theme.spacing(7)} + 1px)`,
    [theme.breakpoints.up('sm')]: {
        width: `calc(${theme.spacing(8)} + 1px)`,
    },
});

const DrawerHeader = styled('div')(({ theme }) => ({
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'flex-end',
    padding: theme.spacing(0, 1),
    // necessary for content to be below app bar
    ...theme.mixins.toolbar,
}))

const Drawer = styled(MuiDrawer, { shouldForwardProp: (prop) => prop !== 'open'})(
    ({ theme, open }) => ({
        width: drawerWidth,
        flexShrink: 0,
        whiteSpace: 'nowrap',
        boxSizing: 'border-box',
        ...(open && {
            ...openedMixin(theme),
            '& .MuiDrawer-paper': openedMixin(theme),
        }),
        ...(!open && {
            ...closedMixin(theme),
            '& .MuiDrawer-paper': closedMixin(theme),
        }),
    }),
);

const navigationItemsVoter = [
    { text: 'Overview', icon: <DashboardIcon />, path: '/dashboard/overview' },
    { text: 'Vote', icon: <HowToVoteIcon />, path: '/dashboard/vote' },
    { text: 'Result', icon: <AnalyticsIcon />, path: '/dashboard/result' },
    { text: 'Wallet', icon: <WalletIcon />, path: '/dashboard/wallet' },
    { text: 'Profile', icon: <AccountBoxIcon />, path: '/dashboard/profile' },
    { text: 'Setting', icon: <SettingsIcon />, path: '/dashboard/setting'},
]

const navigationItemsAdmin = [
    { text: 'Overview', icon: <DashboardIcon />, path: '/dashboard/overview' },
    { text: 'Ballots', icon: <HowToVoteIcon />, path: '/dashboard/ballots' },
    { text: 'Voters', icon: <VotersIcon />, path: '/dashboard/voters' },
    { text: 'Wallet', icon: <WalletIcon />, path: '/dashboard/wallet' },
    { text: 'Profile', icon: <AccountBoxIcon />, path: '/dashboard/profile' },
    { text: 'Setting', icon: <SettingsIcon />, path: '/dashboard/setting'},
]

const theme = createTheme({
    palette: {
        background: {
            paper: '#d3d3d3',
        },
    },
});

function Dashboard() {
    const [open, setOpen] = useState(true);
    const navigate = useNavigate();
    const { user, logout } = useAuth();

    const handleToggleDrawer = () => {
        setOpen(!open);
    }

    const handleLogout = () => {
        logout();
        navigate('/');
    }

    const currentNavItems = user?.role.includes('admin') ? navigationItemsAdmin : navigationItemsVoter;

    return (
        <ThemeProvider theme={theme}>
            <Box sx={{ display: 'flex' }}>
                <CssBaseline />
                <Drawer variant="permanent" open={open}>
                    <DrawerHeader sx={{ justifyContent: open ? 'flex-end' : 'center' }}>
                        <IconButton 
                            className="w-10" 
                            onClick={handleToggleDrawer} 
                            sx={{
                                position: 'fixed',
                                top:10,
                                left:10,
                                zIndex:1201,
                            }}>
                            <MenuIcon />
                        </IconButton>
                    </DrawerHeader>
                    <Divider />
                    <List sx={{
                        display: 'flex',
                        flexDirection: 'column',
                        height: '100%'
                    }}>
                        {currentNavItems.map((item) => (
                            <ListItem key={item.text} disablePadding sx={{ display: 'block' }}>
                                <ListItemButton
                                    component={Link}
                                    to={item.path}
                                    sx={{
                                        minHeight: 48,
                                        justifyContent: open ? 'initial' : 'center',
                                        px: 2.5,
                                    }}
                                >
                                    <ListItemIcon
                                        sx={{
                                        minWidth: 0,
                                        mr: open ? 3 : 'auto',
                                        justifyContent: 'center',
                                        }}
                                    >
                                        {item.icon}
                                    </ListItemIcon>
                                    <ListItemText primary={item.text} sx={{ opacity: open ? 1 : 0 }} />
                                </ListItemButton>
                            </ListItem>
                        ))}
                        <ListItem key="Logout" disablePadding sx={{ display: 'block' }} className='mt-auto'>
                            <ListItemButton
                                onClick={handleLogout}
                                sx={{
                                    minHeight: 48,
                                    justifyContent: open ? 'initial' : 'center',
                                    px: 2.5,
                                }}
                            >
                                <ListItemIcon
                                    sx={{
                                        minWidth: 0,
                                        mr: open ? 3 : 'auto',
                                        justifyContent: 'center',
                                    }}
                                >
                                    <LogoutIcon />
                                </ListItemIcon>
                                <ListItemText primary="Logout" sx={{ opacity: open ? 1 : 0 }} />
                            </ListItemButton>
                        </ListItem>
                    </List>
                </Drawer>
                <Box component="main" sx={{ flexGrow: 1, p: 3, backgroundColor: '#f0f0f0', minHeight: '100vh' }}>
                    <DrawerHeader />
                    <Outlet />
                </Box>
            </Box>
        </ThemeProvider>
    )
}

export default Dashboard;
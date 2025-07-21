import { Routes, Route, useNavigate, useLocation } from 'react-router-dom';
import { 
    Dashboard as DashboardIcon, 
    HowToVote as HowToVoteIcon,
    Wallet as WalletIcon,
} from '@mui/icons-material';
import { Box, Drawer, List, ListItemButton, ListItemIcon, ListItemText } from '@mui/material';
import { useState } from 'react';

// 建立簡單的頁面元件作為佔位符
const OverviewPage = () => <h2>Overview Page</h2>;
const VotePage = () => <h2>Vote Page</h2>;
const WalletPage = () => <h2>Wallet Page</h2>;

const drawerWidth = 240; // 定義側邊欄寬度

function Dashboard3() {
  const navigate = useNavigate();
  const location = useLocation();
  
  const sidebarItems = [
    { path: '/dashboard3/overview', icon: <DashboardIcon />, label: 'Overview' },
    { path: '/dashboard3/vote', icon: <HowToVoteIcon />, label: 'Vote' },
    { path: '/dashboard3/wallet', icon: <WalletIcon />, label: 'Wallet' },
  ];

  return (
    <Box sx={{ display: 'flex' }}>
      {/* 修正 1: 為 Drawer 加上 variant 和寬度 */}
      <Drawer
        variant="permanent"
        sx={{
          width: drawerWidth,
          flexShrink: 0,
          [`& .MuiDrawer-paper`]: { width: drawerWidth, boxSizing: 'border-box' },
        }}
      >
        <List>
          {sidebarItems.map((item) => (
            <ListItemButton 
              key={item.path}
              selected={location.pathname === item.path}
              onClick={() => navigate(item.path)}
            >
              <ListItemIcon>{item.icon}</ListItemIcon>
              <ListItemText primary={item.label} />
            </ListItemButton>
          ))}
        </List>
      </Drawer>
      
      {/* 修正 2: 為 main 區域加上樣式，使其佔用剩餘空間 */}
      <Box component="main" sx={{ flexGrow: 1, p: 3 }}>
        {/* 修正 3: 為 Route 加上 element 屬性 */}
        <Routes>
          <Route path="/overview" element={<OverviewPage />} />
          <Route path="/vote" element={<VotePage />} />
          <Route path="/wallet" element={<WalletPage />} />
        </Routes>
      </Box>
    </Box>
  );
};

export default Dashboard3;
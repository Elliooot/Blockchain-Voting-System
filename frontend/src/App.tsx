import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import './App.css'
import Homepage from './Homepage'
import Login from './Login'
import Register from './Registration'
import Dashboard from './Dashboard'
import Overview from './pages/Overview'
import Vote from './pages/Vote'
import Result from './pages/Result'
import Wallet from './pages/Wallet'
import Profile from './pages/Profile'
import Setting from './pages/Setting'
import Ballots from './pages/Ballots'
import Voters from './pages/Voters'
import ProtectRoute from './utils/ProtectedRoute'
import { AuthProvider } from './contexts/AuthContext'
import CreateBallot from './pages/CreateBallot'

function App() {
  return (
    <Router>
      <div className='App'>
        <AuthProvider>
          <Routes>
            <Route path='/' element={<Homepage />} />
            <Route path='/login' element={<Login />} />
            <Route path='/register' element={<Register />} />

            <Route element={<ProtectRoute />}>
              <Route path='/dashboard' element={<Dashboard />}>
                <Route index element={<Overview />} />
                <Route path='/dashboard/overview' element={<Overview />} />
                <Route path='/dashboard/vote' element={<Vote />} />
                <Route path='/dashboard/result' element={<Result />} />
                <Route path='/dashboard/wallet' element={<Wallet />} />
                <Route path='/dashboard/profile' element={<Profile />} />
                <Route path='/dashboard/setting' element={<Setting />} />
                <Route path='/dashboard/ballots' element={<Ballots />} />
                <Route path='/dashboard/ballots/create' element={<CreateBallot />} />
                <Route path='/dashboard/voters' element={<Voters />} />
              </Route>
            </Route>
          </Routes>
        </AuthProvider>
      </div>
    </Router>
  )
}

export default App

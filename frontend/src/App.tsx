import { Routes, Route } from 'react-router-dom'
import './App.css'
import Homepage from './pages/Homepage'
import Login from './pages/Login'
import Register from './pages/Registration'
import Dashboard from './pages/Dashboard'
import Overview from './pages/Overview'
import Result from './pages/Result'
import Wallet from './pages/Wallet'
import Profile from './pages/Profile'
import Setting from './pages/Setting'
import Ballots from './pages/Ballots'
import ProtectRoute from './utils/ProtectedRoute'
import CreateBallot from './pages/CreateBallot'
import VoteInBallot from './pages/VoteInBallot'
import EditBallot from './pages/EditBallot'
import ChangePassword from './pages/ChangePassword'
import CheckBallot from './pages/CheckBallot'
import FAQ from './pages/FAQ'

function App() {
  return (
    <div className='App'>
      <Routes>
        <Route path='/' element={<Homepage />} />
        <Route path='/login' element={<Login />} />
        <Route path='/register' element={<Register />} />

        <Route element={<ProtectRoute />}>
          <Route path='/dashboard' element={<Dashboard />}>
            <Route index element={<Overview />} />
            <Route path='/dashboard/overview' element={<Overview />} />
            <Route path='/dashboard/result' element={<Result />} />
            <Route path='/dashboard/wallet' element={<Wallet />} />
            <Route path='/dashboard/profile' element={<Profile />} />
            <Route path='/dashboard/setting' element={<Setting />} />
            <Route path='/dashboard/change-password' element={<ChangePassword />} />
            <Route path='/dashboard/ballots' element={<Ballots />} />
            <Route path='/dashboard/ballots/create' element={<CreateBallot />} />
            <Route path="/dashboard/ballots/vote/:ballotId" element={<VoteInBallot />} />
            <Route path="/dashboard/ballots/edit/:ballotId" element={<EditBallot />} />
            <Route path="/dashboard/ballots/check/:ballotId" element={<CheckBallot />} />
            <Route path='/dashboard/FAQs' element={<FAQ />} />
          </Route>
        </Route>
      </Routes>
    </div>
  )
}

export default App

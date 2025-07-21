import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import './App.css'
import Homepage from './Homepage'
import Login from './Login'
import Register from './Registration'
import Dashboard from './Dashboard'
import Dashboard2 from './Dashboard2'
import Dashboard3 from './Dashboard3'
import Overview from './pages/Overview'
import Vote from './pages/Vote'

function App() {
  // const [count, setCount] = useState(0)

  return (
    <Router>
      <div className='App'>
        <Routes>
          <Route path='/' element={<Homepage />} />
          <Route path='/login' element={<Login />} />
          <Route path='/register' element={<Register />} />
          <Route path='/dashboard' element={<Dashboard />}>
            <Route index element={<Overview />} />
            <Route path='/dashboard/overview' element={<Overview />} />
            <Route path='/dashboard/vote' element={<Vote />} />
          </Route>
          <Route path='/dashboard2' element={<Dashboard2 />} />
          <Route path='/dashboard3' element={<Dashboard3 />} />
          
        </Routes>
      </div>
    </Router>
  )
}

export default App

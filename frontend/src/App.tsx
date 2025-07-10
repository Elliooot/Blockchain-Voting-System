import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import './App.css'
import Homepage from './Homepage'
import Login from './Login'
import Register from './Register'
// import { customTheme } from './flowbite-theme'
// import { Flowbite } from 'flowbite-react'

function App() {
  // const [count, setCount] = useState(0)

  return (
    <Router>
      <div className='App'>
        {/* <Flowbite theme={{ theme: customTheme }}> */}
          <Routes>
            <Route path='/' element={<Homepage />} />
            <Route path='/login' element={<Login />} />
            <Route path='/register' element={<Register />} />
          </Routes>
        {/* </Flowbite> */}
      </div>
    </Router>
  )
}

export default App

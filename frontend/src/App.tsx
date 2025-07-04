import { useState } from 'react'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import './App.css'
import Homepage from './Homepage'
import Login from './LoginPage'
// import Register from './RegisterPage'

import ShinyText from './blocks/TextAnimations/ShinyText/ShinyText'

function App() {
  // const [count, setCount] = useState(0)

  return (
    <Router>
      <div className='App'>

        <Routes>
          <Route path='/' element={<Homepage />} />
          <Route path='/login' element={<Login />} />
          {/* <Route path='/register' element={<Register />} /> */}
        </Routes>
      </div>
    </Router>
  )
}

export default App

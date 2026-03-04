import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import Sidebar from './components/Sidebar'
import Dashboard from './pages/Dashboard'
import Scanner from './pages/Scanner'
import ScanResult from './pages/ScanResult'
import History from './pages/History'

function App() {
    return (
        <Router>
            <div className="app-layout">
                <Sidebar />
                <main className="main-content">
                    <Routes>
                        <Route path="/" element={<Dashboard />} />
                        <Route path="/scan" element={<Scanner />} />
                        <Route path="/scan/:id" element={<ScanResult />} />
                        <Route path="/history" element={<History />} />
                    </Routes>
                </main>
            </div>
        </Router>
    )
}

export default App

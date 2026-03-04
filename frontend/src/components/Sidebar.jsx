import { NavLink } from 'react-router-dom'
import { LuShieldCheck, LuLayoutDashboard, LuScanSearch, LuHistory, LuGlobe, LuChartBar } from 'react-icons/lu'

function Sidebar() {
    return (
        <aside className="sidebar">
            <div className="sidebar-brand">
                <div className="shield-icon"><LuShieldCheck /></div>
                <div>
                    <h1>PhishGuard</h1>
                    <span>Cyber Defense Platform</span>
                </div>
            </div>

            <nav className="sidebar-nav">
                <div className="nav-section-title">Main</div>

                <NavLink
                    to="/"
                    end
                    className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
                >
                    <span className="nav-icon"><LuLayoutDashboard /></span>
                    <span>Dashboard</span>
                </NavLink>

                <NavLink
                    to="/scan"
                    className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
                >
                    <span className="nav-icon"><LuScanSearch /></span>
                    <span>Scan Email</span>
                </NavLink>

                <NavLink
                    to="/history"
                    className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
                >
                    <span className="nav-icon"><LuHistory /></span>
                    <span>Scan History</span>
                </NavLink>

                <div className="nav-section-title">Intelligence</div>

                <div className="nav-item" style={{ opacity: 0.5, cursor: 'default' }}>
                    <span className="nav-icon"><LuGlobe /></span>
                    <span>Threat Feed</span>
                </div>

                <div className="nav-item" style={{ opacity: 0.5, cursor: 'default' }}>
                    <span className="nav-icon"><LuChartBar /></span>
                    <span>Analytics</span>
                </div>
            </nav>

            <div className="sidebar-footer">
                <div className="status-badge">
                    <div className="status-dot"></div>
                    <span>Engine Active</span>
                </div>
            </div>
        </aside>
    )
}

export default Sidebar

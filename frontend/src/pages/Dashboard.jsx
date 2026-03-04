import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { getDashboardStats } from '../services/api'
import { LuShieldCheck, LuMail, LuTriangleAlert, LuShieldOff, LuActivity, LuTrendingUp, LuTarget, LuTag, LuClock, LuScanSearch } from 'react-icons/lu'
import {
    BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip,
    ResponsiveContainer, PieChart, Pie, Cell, AreaChart, Area
} from 'recharts'

const THREAT_COLORS = {
    SAFE: '#00ff88',
    LOW: '#00d4ff',
    MEDIUM: '#ffd93d',
    HIGH: '#ff9f43',
    CRITICAL: '#ff4757'
}

function Dashboard() {
    const [stats, setStats] = useState(null)
    const [loading, setLoading] = useState(true)
    const navigate = useNavigate()

    useEffect(() => {
        loadStats()
    }, [])

    const loadStats = async () => {
        try {
            const data = await getDashboardStats()
            setStats(data)
        } catch (err) {
            console.error('Failed to load dashboard stats:', err)
        } finally {
            setLoading(false)
        }
    }

    if (loading) {
        return (
            <div className="loading-spinner">
                <div className="spinner"></div>
            </div>
        )
    }

    const pieData = stats?.threatsByLevel
        ? Object.entries(stats.threatsByLevel)
            .filter(([_, value]) => value > 0)
            .map(([key, value]) => ({ name: key, value }))
        : []

    const categoryData = stats?.threatsByCategory
        ? Object.entries(stats.threatsByCategory).map(([key, value]) => ({ name: key, count: value }))
        : []

    return (
        <div className="fade-in">
            <div className="page-header">
                <h2><LuShieldCheck style={{ verticalAlign: 'middle', marginRight: '8px', color: 'var(--accent-green)' }} /> Security Dashboard</h2>
                <p>Real-time overview of email threat analysis</p>
            </div>

            {/* Stats Cards */}
            <div className="stats-grid">
                <div className="stat-card">
                    <div className="stat-icon green"><LuMail /></div>
                    <div className="stat-value">{stats?.totalScans || 0}</div>
                    <div className="stat-label">Total Scans</div>
                </div>
                <div className="stat-card">
                    <div className="stat-icon red"><LuTriangleAlert /></div>
                    <div className="stat-value">{stats?.threatsDetected || 0}</div>
                    <div className="stat-label">Threats Detected</div>
                </div>
                <div className="stat-card">
                    <div className="stat-icon cyan"><LuShieldCheck /></div>
                    <div className="stat-value">{stats?.safeEmails || 0}</div>
                    <div className="stat-label">Safe Emails</div>
                </div>
                <div className="stat-card">
                    <div className="stat-icon orange"><LuShieldOff /></div>
                    <div className="stat-value">{stats?.criticalThreats || 0}</div>
                    <div className="stat-label">Critical Threats</div>
                </div>
                <div className="stat-card">
                    <div className="stat-icon purple"><LuActivity /></div>
                    <div className="stat-value">{stats?.averageThreatScore || 0}</div>
                    <div className="stat-label">Avg Threat Score</div>
                </div>
            </div>

            {/* Charts Row */}
            <div className="grid-2">
                {/* Daily Activity Chart */}
                <div className="card">
                    <div className="card-header">
                        <h3><LuTrendingUp style={{ color: 'var(--accent-green)' }} /> Scan Activity (7 Days)</h3>
                    </div>
                    <div className="chart-container">
                        {stats?.dailyStats && stats.dailyStats.length > 0 ? (
                            <ResponsiveContainer width="100%" height="100%">
                                <AreaChart data={stats.dailyStats}>
                                    <defs>
                                        <linearGradient id="scanGradient" x1="0" y1="0" x2="0" y2="1">
                                            <stop offset="5%" stopColor="#00ff88" stopOpacity={0.3} />
                                            <stop offset="95%" stopColor="#00ff88" stopOpacity={0} />
                                        </linearGradient>
                                        <linearGradient id="threatGradient" x1="0" y1="0" x2="0" y2="1">
                                            <stop offset="5%" stopColor="#ff4757" stopOpacity={0.3} />
                                            <stop offset="95%" stopColor="#ff4757" stopOpacity={0} />
                                        </linearGradient>
                                    </defs>
                                    <CartesianGrid strokeDasharray="3 3" stroke="#1e2536" />
                                    <XAxis dataKey="date" stroke="#5a6178" fontSize={12} />
                                    <YAxis stroke="#5a6178" fontSize={12} />
                                    <Tooltip
                                        contentStyle={{
                                            background: '#1a1f2e',
                                            border: '1px solid #1e2536',
                                            borderRadius: '8px',
                                            color: '#e8eaf0'
                                        }}
                                    />
                                    <Area type="monotone" dataKey="scans" stroke="#00ff88" fill="url(#scanGradient)" name="Scans" />
                                    <Area type="monotone" dataKey="threats" stroke="#ff4757" fill="url(#threatGradient)" name="Threats" />
                                </AreaChart>
                            </ResponsiveContainer>
                        ) : (
                            <div className="empty-state">
                                <p>No scan data yet. Start scanning emails!</p>
                            </div>
                        )}
                    </div>
                </div>

                {/* Threat Distribution Pie */}
                <div className="card">
                    <div className="card-header">
                        <h3><LuTarget style={{ color: 'var(--accent-cyan)' }} /> Threat Distribution</h3>
                    </div>
                    <div className="chart-container">
                        {pieData.length > 0 ? (
                            <ResponsiveContainer width="100%" height="100%">
                                <PieChart>
                                    <Pie
                                        data={pieData}
                                        cx="50%"
                                        cy="50%"
                                        innerRadius={60}
                                        outerRadius={100}
                                        paddingAngle={3}
                                        dataKey="value"
                                    >
                                        {pieData.map((entry, index) => (
                                            <Cell key={index} fill={THREAT_COLORS[entry.name] || '#5a6178'} />
                                        ))}
                                    </Pie>
                                    <Tooltip
                                        contentStyle={{
                                            background: '#1a1f2e',
                                            border: '1px solid #1e2536',
                                            borderRadius: '8px',
                                            color: '#e8eaf0'
                                        }}
                                    />
                                </PieChart>
                            </ResponsiveContainer>
                        ) : (
                            <div className="empty-state">
                                <p>No threat data to display yet.</p>
                            </div>
                        )}
                    </div>
                </div>
            </div>

            {/* Threat Categories Bar Chart */}
            {categoryData.length > 0 && (
                <div className="card">
                    <div className="card-header">
                        <h3><LuTag style={{ color: 'var(--accent-purple)' }} /> Threats by Category</h3>
                    </div>
                    <div className="chart-container">
                        <ResponsiveContainer width="100%" height="100%">
                            <BarChart data={categoryData}>
                                <CartesianGrid strokeDasharray="3 3" stroke="#1e2536" />
                                <XAxis dataKey="name" stroke="#5a6178" fontSize={12} />
                                <YAxis stroke="#5a6178" fontSize={12} />
                                <Tooltip
                                    contentStyle={{
                                        background: '#1a1f2e',
                                        border: '1px solid #1e2536',
                                        borderRadius: '8px',
                                        color: '#e8eaf0'
                                    }}
                                />
                                <Bar dataKey="count" fill="#a855f7" radius={[4, 4, 0, 0]} />
                            </BarChart>
                        </ResponsiveContainer>
                    </div>
                </div>
            )}

            {/* Recent Scans */}
            <div className="card">
                <div className="card-header">
                    <h3><LuClock style={{ color: 'var(--text-secondary)' }} /> Recent Scans</h3>
                    <button className="btn btn-secondary btn-sm" onClick={() => navigate('/history')}>
                        View All
                    </button>
                </div>
                {stats?.recentScans && stats.recentScans.length > 0 ? (
                    <table className="scan-table">
                        <thead>
                            <tr>
                                <th>Sender</th>
                                <th>Subject</th>
                                <th>Score</th>
                                <th>Level</th>
                                <th>Date</th>
                            </tr>
                        </thead>
                        <tbody>
                            {stats.recentScans.map((scan) => (
                                <tr
                                    key={scan.id}
                                    onClick={() => navigate(`/scan/${scan.id}`)}
                                    style={{ cursor: 'pointer' }}
                                >
                                    <td>{scan.senderEmail || '—'}</td>
                                    <td>{scan.subject || '—'}</td>
                                    <td className="score-cell">{scan.threatScore}</td>
                                    <td>
                                        <span className={`threat-level-badge ${scan.threatLevel?.toLowerCase()}`}>
                                            {scan.threatLevel}
                                        </span>
                                    </td>
                                    <td style={{ color: '#8b92a5', fontSize: '12px' }}>{scan.scanDate}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                ) : (
                    <div className="empty-state">
                        <div className="empty-icon"><LuScanSearch size={48} /></div>
                        <h3>No scans yet</h3>
                        <p>Start by scanning your first email to see threat analysis here.</p>
                        <button
                            className="btn btn-primary"
                            onClick={() => navigate('/scan')}
                            style={{ marginTop: '16px' }}
                        >
                            <LuScanSearch /> Scan First Email
                        </button>
                    </div>
                )}
            </div>
        </div>
    )
}

export default Dashboard

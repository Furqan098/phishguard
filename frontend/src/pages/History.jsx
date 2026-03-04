import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { getAllScans, deleteScan } from '../services/api'
import { LuHistory, LuScanSearch, LuTrash2 } from 'react-icons/lu'

function History() {
    const [scans, setScans] = useState([])
    const [loading, setLoading] = useState(true)
    const [filter, setFilter] = useState('ALL')
    const navigate = useNavigate()

    useEffect(() => {
        loadScans()
    }, [])

    const loadScans = async () => {
        try {
            const data = await getAllScans()
            setScans(data)
        } catch (err) {
            console.error('Failed to load scans:', err)
        } finally {
            setLoading(false)
        }
    }

    const handleDelete = async (id, e) => {
        e.stopPropagation()
        if (window.confirm('Delete this scan?')) {
            try {
                await deleteScan(id)
                setScans(scans.filter((s) => s.id !== id))
            } catch (err) {
                console.error('Failed to delete scan:', err)
            }
        }
    }

    const filteredScans = filter === 'ALL'
        ? scans
        : scans.filter((s) => s.threatLevel === filter)

    const threatCounts = {
        ALL: scans.length,
        SAFE: scans.filter((s) => s.threatLevel === 'SAFE').length,
        LOW: scans.filter((s) => s.threatLevel === 'LOW').length,
        MEDIUM: scans.filter((s) => s.threatLevel === 'MEDIUM').length,
        HIGH: scans.filter((s) => s.threatLevel === 'HIGH').length,
        CRITICAL: scans.filter((s) => s.threatLevel === 'CRITICAL').length
    }

    if (loading) {
        return (
            <div className="loading-spinner">
                <div className="spinner"></div>
            </div>
        )
    }

    return (
        <div className="fade-in">
            <div className="page-header">
                <h2><LuHistory style={{ verticalAlign: 'middle', marginRight: '8px', color: 'var(--accent-cyan)' }} /> Scan History</h2>
                <p>View all previously scanned emails and their threat assessments</p>
            </div>

            {/* Filter Tabs */}
            <div className="actions-bar" style={{ flexWrap: 'wrap' }}>
                {['ALL', 'SAFE', 'LOW', 'MEDIUM', 'HIGH', 'CRITICAL'].map((level) => (
                    <button
                        key={level}
                        className={`btn btn-sm ${filter === level ? 'btn-primary' : 'btn-secondary'}`}
                        onClick={() => setFilter(level)}
                    >
                        {level} ({threatCounts[level]})
                    </button>
                ))}
            </div>

            <div className="card">
                {filteredScans.length > 0 ? (
                    <table className="scan-table">
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>Sender</th>
                                <th>Subject</th>
                                <th>Score</th>
                                <th>Level</th>
                                <th>Indicators</th>
                                <th>Date</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {filteredScans.map((scan, idx) => (
                                <tr
                                    key={scan.id}
                                    onClick={() => navigate(`/scan/${scan.id}`)}
                                    style={{ cursor: 'pointer' }}
                                >
                                    <td style={{ color: 'var(--text-muted)', fontFamily: 'var(--font-mono)' }}>
                                        {idx + 1}
                                    </td>
                                    <td style={{ fontFamily: 'var(--font-mono)', fontSize: '12px' }}>
                                        {scan.senderEmail || '—'}
                                    </td>
                                    <td>{scan.subject || '—'}</td>
                                    <td className="score-cell" style={{
                                        color: scan.threatScore >= 80 ? '#ff4757'
                                            : scan.threatScore >= 60 ? '#ff9f43'
                                                : scan.threatScore >= 35 ? '#ffd93d'
                                                    : '#00ff88'
                                    }}>
                                        {scan.threatScore}
                                    </td>
                                    <td>
                                        <span className={`threat-level-badge ${scan.threatLevel?.toLowerCase()}`}>
                                            {scan.threatLevel}
                                        </span>
                                    </td>
                                    <td style={{ fontFamily: 'var(--font-mono)' }}>
                                        {scan.indicators?.length || 0}
                                    </td>
                                    <td style={{ color: 'var(--text-muted)', fontSize: '12px' }}>
                                        {scan.scanDate ? new Date(scan.scanDate).toLocaleDateString() : '—'}
                                    </td>
                                    <td>
                                        <button
                                            className="btn btn-danger btn-sm"
                                            onClick={(e) => handleDelete(scan.id, e)}
                                        >
                                            <LuTrash2 />
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                ) : (
                    <div className="empty-state">
                        <div className="empty-icon"><LuScanSearch size={48} /></div>
                        <h3>{filter === 'ALL' ? 'No scans yet' : `No ${filter.toLowerCase()} scans`}</h3>
                        <p>
                            {filter === 'ALL'
                                ? 'Start scanning emails to build your threat analysis history.'
                                : 'No scans match this filter.'}
                        </p>
                        {filter === 'ALL' && (
                            <button
                                className="btn btn-primary"
                                onClick={() => navigate('/scan')}
                                style={{ marginTop: '16px' }}
                            >
                                <LuScanSearch /> Scan First Email
                            </button>
                        )}
                    </div>
                )}
            </div>
        </div>
    )
}

export default History

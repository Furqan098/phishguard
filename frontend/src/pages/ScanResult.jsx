import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { getScanById } from '../services/api'
import { LuShieldCheck, LuShieldAlert, LuArrowLeft, LuHistory, LuSearch, LuInfo, LuTriangleAlert, LuCircleAlert, LuOctagonAlert } from 'react-icons/lu'

const THREAT_COLORS = {
    SAFE: '#00ff88',
    LOW: '#00d4ff',
    MEDIUM: '#ffd93d',
    HIGH: '#ff9f43',
    CRITICAL: '#ff4757'
}

const LEVEL_ICONS = {
    SAFE: <LuShieldCheck />,
    LOW: <LuInfo />,
    MEDIUM: <LuTriangleAlert />,
    HIGH: <LuCircleAlert />,
    CRITICAL: <LuOctagonAlert />
}

function ThreatScoreCircle({ score, level }) {
    const radius = 70
    const circumference = 2 * Math.PI * radius
    const progress = (score / 100) * circumference
    const dashOffset = circumference - progress
    const color = THREAT_COLORS[level] || '#5a6178'

    return (
        <div className="threat-score-circle">
            <svg viewBox="0 0 180 180">
                <circle cx="90" cy="90" r={radius} className="bg-circle" />
                <circle
                    cx="90" cy="90" r={radius}
                    className="score-circle"
                    stroke={color}
                    strokeDasharray={circumference}
                    strokeDashoffset={dashOffset}
                />
            </svg>
            <div className="threat-score-value" style={{ color }}>
                {score}
            </div>
        </div>
    )
}

function ScanResult() {
    const { id } = useParams()
    const navigate = useNavigate()
    const [scan, setScan] = useState(null)
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        loadScan()
    }, [id])

    const loadScan = async () => {
        try {
            const data = await getScanById(id)
            setScan(data)
        } catch (err) {
            console.error('Failed to load scan:', err)
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

    if (!scan) {
        return (
            <div className="empty-state">
                <div className="empty-icon"><LuCircleAlert size={48} /></div>
                <h3>Scan not found</h3>
                <p>This scan result doesn't exist or has been deleted.</p>
                <button className="btn btn-primary" onClick={() => navigate('/scan')} style={{ marginTop: '16px' }}>
                    <LuSearch /> New Scan
                </button>
            </div>
        )
    }

    const level = scan.threatLevel?.toLowerCase() || 'safe'

    return (
        <div className="fade-in">
            <div className="page-header">
                <h2><LuShieldAlert style={{ verticalAlign: 'middle', marginRight: '8px', color: 'var(--accent-cyan)' }} /> Scan Result</h2>
                <p>Analysis details for: {scan.subject || 'Untitled Email'}</p>
            </div>

            <div className="actions-bar">
                <button className="btn btn-secondary btn-sm" onClick={() => navigate('/scan')}>
                    <LuArrowLeft /> New Scan
                </button>
                <button className="btn btn-secondary btn-sm" onClick={() => navigate('/history')}>
                    <LuHistory /> History
                </button>
            </div>

            {/* Summary */}
            <div className={`summary-box ${level}`}>
                {scan.summary}
            </div>

            <div className="result-grid">
                {/* Left: Score */}
                <div className="card">
                    <div className="threat-meter">
                        <ThreatScoreCircle score={scan.threatScore} level={scan.threatLevel} />
                        <div className="threat-score-label">Threat Score</div>
                        <div className={`threat-level-badge ${level}`}>
                            {LEVEL_ICONS[scan.threatLevel] || <LuShieldCheck />}
                            {scan.threatLevel}
                        </div>
                    </div>

                    {/* Email Info */}
                    <div style={{ borderTop: '1px solid var(--border-color)', paddingTop: '16px', marginTop: '16px' }}>
                        <div style={{ marginBottom: '10px' }}>
                            <div style={{ fontSize: '11px', color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '1px' }}>
                                Sender
                            </div>
                            <div style={{ fontSize: '13px', fontFamily: 'var(--font-mono)', marginTop: '4px' }}>
                                {scan.senderEmail || '—'}
                            </div>
                        </div>
                        <div style={{ marginBottom: '10px' }}>
                            <div style={{ fontSize: '11px', color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '1px' }}>
                                Subject
                            </div>
                            <div style={{ fontSize: '13px', marginTop: '4px' }}>
                                {scan.subject || '—'}
                            </div>
                        </div>
                        <div>
                            <div style={{ fontSize: '11px', color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '1px' }}>
                                Scanned
                            </div>
                            <div style={{ fontSize: '13px', fontFamily: 'var(--font-mono)', marginTop: '4px' }}>
                                {scan.scanDate ? new Date(scan.scanDate).toLocaleString() : '—'}
                            </div>
                        </div>
                    </div>
                </div>

                {/* Right: Indicators */}
                <div className="card">
                    <div className="card-header">
                        <h3><LuSearch style={{ color: 'var(--accent-cyan)' }} /> Threat Indicators</h3>
                        <span className="card-badge">{scan.indicators?.length || 0} found</span>
                    </div>

                    {scan.indicators && scan.indicators.length > 0 ? (
                        <div className="indicators-list">
                            {scan.indicators.map((indicator, idx) => (
                                <div key={idx} className="indicator-item">
                                    <div className={`indicator-severity ${indicator.severity?.toLowerCase()}`}></div>
                                    <div className="indicator-content">
                                        <h4>{indicator.description}</h4>
                                        <p>{indicator.details}</p>
                                        <div className="indicator-meta">
                                            <span className="indicator-tag category">{indicator.category}</span>
                                            <span className="indicator-tag impact">Impact: +{indicator.scoreImpact}</span>
                                            <span className={`threat-level-badge ${indicator.severity?.toLowerCase()}`} style={{ padding: '2px 8px', fontSize: '10px' }}>
                                                {indicator.severity}
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    ) : (
                        <div className="empty-state">
                            <div className="empty-icon"><LuShieldCheck size={48} /></div>
                            <h3>All Clear!</h3>
                            <p>No threat indicators were found in this email.</p>
                        </div>
                    )}
                </div>
            </div>
        </div>
    )
}

export default ScanResult

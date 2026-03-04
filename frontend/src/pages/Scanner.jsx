import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { analyzeEmail } from '../services/api'
import { LuScanSearch, LuMail, LuTriangleAlert, LuShieldCheck, LuShieldAlert, LuLoader } from 'react-icons/lu'

function Scanner() {
    const navigate = useNavigate()
    const [loading, setLoading] = useState(false)
    const [formData, setFormData] = useState({
        senderEmail: '',
        senderName: '',
        subject: '',
        body: '',
        rawHeaders: ''
    })

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value })
    }

    const handleSubmit = async (e) => {
        e.preventDefault()
        if (!formData.body.trim()) return

        setLoading(true)
        try {
            const result = await analyzeEmail(formData)
            navigate(`/scan/${result.id}`)
        } catch (err) {
            console.error('Analysis failed:', err)
            alert('Failed to analyze email. Make sure the backend is running.')
        } finally {
            setLoading(false)
        }
    }

    const fillSamplePhishing = () => {
        setFormData({
            senderEmail: 'security-alert@paypai-verify.xyz',
            senderName: 'PayPal Security Team',
            subject: 'URGENT: Your account has been compromised - Immediate action required!',
            body: `Dear valued customer,

We have detected unauthorized access to your PayPal account. Your account will be suspended within 24 hours if you do not verify your identity immediately.

CLICK HERE to verify your account: http://192.168.1.1/paypal-login/verify.php?user=confirm

You must provide the following to restore access:
- Your credit card number and CVV
- Social Security Number (SSN)
- Bank account and routing number
- Date of birth and mother's maiden name

Failure to act now will result in permanent account closure and legal action.

This is your final warning. Respond immediately to avoid account termination.

Best regards,
PayPal Security Department
helpdesk@paypal-secure-alert.tk`,
            rawHeaders: `From: security-alert@paypai-verify.xyz
Reply-To: recovery@mail-fraud.gq
Received: from unknown (192.168.1.100)
X-Mailer: PHPMailer
Authentication-Results: spf=fail; dkim=fail; dmarc=fail`
        })
    }

    const fillSampleSafe = () => {
        setFormData({
            senderEmail: 'noreply@github.com',
            senderName: 'GitHub',
            subject: 'Your repository has a new star',
            body: `Hey there!

Someone just starred your repository phishguard on GitHub.

You now have 42 stars! Keep up the great work.

You can view your repository at https://github.com/yourusername/phishguard

Happy coding!
The GitHub Team`,
            rawHeaders: `From: noreply@github.com
Reply-To: noreply@github.com
Authentication-Results: spf=pass; dkim=pass; dmarc=pass`
        })
    }

    return (
        <div className="fade-in">
            <div className="page-header">
                <h2><LuScanSearch style={{ verticalAlign: 'middle', marginRight: '8px', color: 'var(--accent-cyan)' }} /> Email Scanner</h2>
                <p>Paste an email's details below to analyze it for phishing threats</p>
            </div>

            {/* Sample Buttons */}
            <div className="actions-bar">
                <button className="btn btn-secondary btn-sm" onClick={fillSamplePhishing}>
                    <LuTriangleAlert /> Load Phishing Sample
                </button>
                <button className="btn btn-secondary btn-sm" onClick={fillSampleSafe}>
                    <LuShieldCheck /> Load Safe Sample
                </button>
            </div>

            <form className="scanner-form" onSubmit={handleSubmit}>
                <div className="card">
                    <div className="card-header">
                        <h3><LuMail style={{ color: 'var(--accent-green)' }} /> Email Details</h3>
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label>Sender Email</label>
                            <input
                                type="text"
                                name="senderEmail"
                                value={formData.senderEmail}
                                onChange={handleChange}
                                placeholder="e.g. support@company.com"
                            />
                        </div>
                        <div className="form-group">
                            <label>Sender Name</label>
                            <input
                                type="text"
                                name="senderName"
                                value={formData.senderName}
                                onChange={handleChange}
                                placeholder="e.g. Customer Support"
                            />
                        </div>
                    </div>

                    <div className="form-group">
                        <label>Subject Line</label>
                        <input
                            type="text"
                            name="subject"
                            value={formData.subject}
                            onChange={handleChange}
                            placeholder="e.g. Your account needs verification"
                        />
                    </div>

                    <div className="form-group">
                        <label>Email Body *</label>
                        <textarea
                            name="body"
                            value={formData.body}
                            onChange={handleChange}
                            placeholder="Paste the full email body content here..."
                            rows={10}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Raw Email Headers (Optional)</label>
                        <textarea
                            name="rawHeaders"
                            value={formData.rawHeaders}
                            onChange={handleChange}
                            placeholder="Paste email headers here for deeper analysis (From, Reply-To, SPF, DKIM, etc.)"
                            rows={5}
                        />
                    </div>
                </div>

                <button
                    type="submit"
                    className="btn btn-primary"
                    disabled={loading || !formData.body.trim()}
                    style={{ alignSelf: 'flex-start', padding: '12px 32px', fontSize: '15px' }}
                >
                    {loading ? (
                        <>
                            <LuLoader className="icon-spin" style={{ width: '18px', height: '18px' }} />
                            Analyzing...
                        </>
                    ) : (
                        <><LuShieldAlert /> Analyze Email</>
                    )}
                </button>
            </form>
        </div>
    )
}

export default Scanner

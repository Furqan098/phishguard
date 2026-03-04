# 🛡️ PhishGuard — Phishing Email Detector & Cybersecurity Dashboard

> A full-stack Java application that analyzes emails for phishing threats using a multi-layered detection engine with a stunning cybersecurity-themed dashboard.

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green?style=flat-square)
![React](https://img.shields.io/badge/React-18-blue?style=flat-square)
![Vite](https://img.shields.io/badge/Vite-5-purple?style=flat-square)

---

## ✨ Features

### 🔍 Phishing Detection Engine
- **URL Analysis** — Detects IP addresses, suspicious TLDs, URL shorteners, brand impersonation, and obfuscation
- **Content Analysis** — Identifies urgency manipulation, credential requests, financial scams, threat language, and hidden HTML
- **Header Analysis** — Checks SPF/DKIM/DMARC, sender spoofing, Reply-To mismatches, and domain reputation
- **Threat Scoring** — 0-100 score with SAFE/LOW/MEDIUM/HIGH/CRITICAL levels

### 📊 Cybersecurity Dashboard
- Real-time threat statistics and metrics
- Interactive charts (Area, Pie, Bar) with Recharts
- Scan history with filtering and search
- Detailed threat breakdown per scan

### 🎨 Modern UI
- Dark cybersecurity theme with neon green/cyan accents
- Animated circular threat score meter
- Responsive design for all screen sizes
- Pre-loaded sample emails for demo

---

## 🚀 Quick Start

### Prerequisites
- **Java 17+** — [Download](https://adoptium.net/)
- **Node.js 18+** — [Download](https://nodejs.org/)
- **Maven** — [Download](https://maven.apache.org/) (or use the included wrapper)

### 1. Start the Backend
```bash
cd backend
mvn spring-boot:run
```
Backend runs at: `http://localhost:8080`

### 2. Start the Frontend
```bash
cd frontend
npm install
npm run dev
```
Frontend runs at: `http://localhost:5173`

### 3. Open the App
Navigate to `http://localhost:5173` in your browser.

---

## 📁 Project Structure

```
PhishGuard/
├── backend/                          # Spring Boot Backend
│   ├── src/main/java/com/phishguard/
│   │   ├── PhishGuardApplication.java
│   │   ├── config/                   # CORS configuration
│   │   ├── model/                    # JPA entities
│   │   ├── dto/                      # Request/Response objects
│   │   ├── engine/                   # Detection analyzers
│   │   │   ├── UrlAnalyzer.java
│   │   │   ├── ContentAnalyzer.java
│   │   │   └── HeaderAnalyzer.java
│   │   ├── service/                  # Business logic
│   │   ├── controller/               # REST endpoints
│   │   └── repository/               # Data access
│   └── src/main/resources/
│       └── application.properties
│
├── frontend/                         # React Frontend
│   ├── src/
│   │   ├── components/               # Sidebar navigation
│   │   ├── pages/                    # Dashboard, Scanner, Results, History
│   │   ├── services/                 # API client (Axios)
│   │   └── index.css                 # Cybersecurity theme
│   ├── package.json
│   └── vite.config.js
│
└── README.md
```

---

## 🔌 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/scans/analyze` | Analyze an email for phishing |
| `GET` | `/api/scans` | Get all scan history |
| `GET` | `/api/scans/{id}` | Get specific scan result |
| `DELETE` | `/api/scans/{id}` | Delete a scan |
| `GET` | `/api/dashboard/stats` | Get dashboard statistics |

---

## 🌐 Free Deployment

### Frontend → Vercel
```bash
cd frontend
npm run build
# Deploy the `dist` folder to Vercel
```

### Backend → Render
1. Push code to GitHub
2. Create a new **Web Service** on [render.com](https://render.com)
3. Set build command: `cd backend && mvn clean package -DskipTests`
4. Set start command: `java -jar backend/target/phishguard-backend-1.0.0.jar`

### Database → Neon PostgreSQL
1. Create a free database at [neon.tech](https://neon.tech)
2. Update `application.properties` with PostgreSQL URL

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 17, Spring Boot 3.2, Spring Data JPA |
| Frontend | React 18, Vite 5, React Router, Recharts |
| Database | H2 (dev) / PostgreSQL (prod) |
| HTTP Client | Axios |
| Animations | Framer Motion |
| Styling | Vanilla CSS (Dark Cybersecurity Theme) |

---

## 📜 License

MIT License — feel free to use this project in your portfolio!

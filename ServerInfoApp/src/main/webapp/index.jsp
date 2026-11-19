<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Date, java.text.SimpleDateFormat, java.util.concurrent.atomic.AtomicInteger" %>
<%
    // Handle optional counter reset
    String resetParam = request.getParameter("reset");
    AtomicInteger totalHits = (AtomicInteger) application.getAttribute("totalHitCounter");

    if (totalHits == null) {
        synchronized (application) {
            totalHits = (AtomicInteger) application.getAttribute("totalHitCounter");
            if (totalHits == null) {
                totalHits = new AtomicInteger(0);
                application.setAttribute("totalHitCounter", totalHits);
            }
        }
    }

    if ("true".equalsIgnoreCase(resetParam)) {
        totalHits.set(1); // Reset to 1 for this current visit
        session.setAttribute("sessionHitCounter", 1);
    } else {
        totalHits.incrementAndGet();
        Integer sessionHits = (Integer) session.getAttribute("sessionHitCounter");
        session.setAttribute("sessionHitCounter", (sessionHits == null ? 1 : sessionHits + 1));
    }

    int currentTotalHit = totalHits.get();
    int currentSessionHit = (Integer) session.getAttribute("sessionHitCounter");

    // Client IP Address resolution
    String clientIp = request.getHeader("X-Forwarded-For");
    if (clientIp == null || clientIp.trim().isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
        clientIp = request.getHeader("Proxy-Client-IP");
    }
    if (clientIp == null || clientIp.trim().isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
        clientIp = request.getHeader("WL-Proxy-Client-IP");
    }
    if (clientIp == null || clientIp.trim().isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
        clientIp = request.getRemoteAddr();
    } else {
        // If X-Forwarded-For contains multiple IPs, the first one is the client
        if (clientIp.contains(",")) {
            clientIp = clientIp.split(",")[0].trim();
        }
    }

    String remoteAddr = request.getRemoteAddr();
    String remoteHost = request.getRemoteHost();
    int remotePort = request.getRemotePort();

    // URL & Request information
    String requestURL = request.getRequestURL().toString();
    String requestURI = request.getRequestURI();
    String queryString = request.getQueryString();
    String protocol = request.getProtocol();
    String scheme = request.getScheme();
    String httpMethod = request.getMethod();

    // Context & Server information
    String contextPath = request.getContextPath();
    String servletContextName = application.getServletContextName();
    if (servletContextName == null || servletContextName.isEmpty()) {
        servletContextName = contextPath.replace("/", "");
    }
    String serverName = request.getServerName();
    int serverPort = request.getServerPort();
    String serverInfo = application.getServerInfo();
    String servletVersion = application.getMajorVersion() + "." + application.getMinorVersion();
    String realPath = application.getRealPath("/");

    // Host & Environment Details
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
    String serverTime = sdf.format(new Date());
    String osName = System.getProperty("os.name") + " " + System.getProperty("os.arch") + " (v" + System.getProperty("os.version") + ")";
    String javaVersion = System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ")";
    String sessionId = session.getId();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Server-Side Information Dashboard | JSP</title>
    <!-- Modern Google Font -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&family=JetBrains+Mono:wght@400;500;600&display=swap" rel="stylesheet">
    <style>
        :root {
            --bg-primary: #0f172a;
            --bg-secondary: #1e293b;
            --card-bg: rgba(30, 41, 59, 0.7);
            --card-border: rgba(148, 163, 184, 0.15);
            --text-primary: #f8fafc;
            --text-secondary: #94a3b8;
            --text-muted: #64748b;
            --accent-blue: #38bdf8;
            --accent-indigo: #6366f1;
            --accent-purple: #a855f7;
            --accent-emerald: #10b981;
            --accent-amber: #f59e0b;
            --code-bg: #090d16;
        }

        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        body {
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
            background: radial-gradient(circle at 10% 20%, #1e1b4b 0%, #0f172a 90%);
            color: var(--text-primary);
            min-height: 100vh;
            padding: 2.5rem 1.5rem;
            line-height: 1.6;
        }

        .container {
            max-width: 1100px;
            margin: 0 auto;
        }

        /* Header section */
        header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 1.5rem;
            margin-bottom: 2rem;
            padding-bottom: 1.5rem;
            border-bottom: 1px solid var(--card-border);
        }

        .header-title h1 {
            font-size: 1.85rem;
            font-weight: 700;
            background: linear-gradient(135deg, #38bdf8 0%, #818cf8 50%, #c084fc 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            letter-spacing: -0.02em;
        }

        .header-title p {
            color: var(--text-secondary);
            font-size: 0.95rem;
            margin-top: 0.25rem;
        }

        .actions {
            display: flex;
            gap: 0.75rem;
        }

        .btn {
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
            padding: 0.55rem 1.1rem;
            border-radius: 8px;
            font-size: 0.875rem;
            font-weight: 500;
            text-decoration: none;
            cursor: pointer;
            transition: all 0.2s ease;
            border: 1px solid transparent;
        }

        .btn-primary {
            background: linear-gradient(135deg, #3b82f6, #6366f1);
            color: #ffffff;
            box-shadow: 0 4px 12px rgba(59, 130, 246, 0.25);
        }

        .btn-primary:hover {
            opacity: 0.92;
            transform: translateY(-1px);
        }

        .btn-secondary {
            background: rgba(51, 65, 85, 0.6);
            color: #e2e8f0;
            border-color: var(--card-border);
        }

        .btn-secondary:hover {
            background: rgba(71, 85, 105, 0.8);
            transform: translateY(-1px);
        }

        /* Hit Counter Banner */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
            gap: 1.25rem;
            margin-bottom: 2rem;
        }

        .stat-card {
            background: var(--card-bg);
            backdrop-filter: blur(12px);
            border: 1px solid var(--card-border);
            border-radius: 14px;
            padding: 1.25rem 1.5rem;
            position: relative;
            overflow: hidden;
            box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.3);
        }

        .stat-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 3px;
        }

        .stat-card.hits-total::before {
            background: linear-gradient(90deg, #38bdf8, #6366f1);
        }

        .stat-card.hits-session::before {
            background: linear-gradient(90deg, #34d399, #10b981);
        }

        .stat-card.server-status::before {
            background: linear-gradient(90deg, #fbbf24, #f59e0b);
        }

        .stat-label {
            font-size: 0.825rem;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.05em;
            color: var(--text-secondary);
            margin-bottom: 0.5rem;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }

        .stat-value {
            font-size: 2.25rem;
            font-weight: 700;
            letter-spacing: -0.03em;
            color: #ffffff;
            font-family: 'JetBrains Mono', monospace;
        }

        .stat-desc {
            font-size: 0.8rem;
            color: var(--text-muted);
            margin-top: 0.35rem;
        }

        /* Detail Cards Grid */
        .dashboard-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
            gap: 1.5rem;
            margin-bottom: 2rem;
        }

        @media (max-width: 600px) {
            .dashboard-grid {
                grid-template-columns: 1fr;
            }
        }

        .panel {
            background: var(--card-bg);
            backdrop-filter: blur(12px);
            border: 1px solid var(--card-border);
            border-radius: 14px;
            overflow: hidden;
            box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.25);
        }

        .panel-header {
            padding: 1.1rem 1.5rem;
            border-bottom: 1px solid var(--card-border);
            display: flex;
            align-items: center;
            gap: 0.65rem;
            background: rgba(15, 23, 42, 0.4);
        }

        .panel-header h2 {
            font-size: 1.05rem;
            font-weight: 600;
            color: #f1f5f9;
        }

        .badge-icon {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            width: 28px;
            height: 28px;
            border-radius: 6px;
            background: rgba(56, 189, 248, 0.15);
            color: var(--accent-blue);
        }

        .panel-body {
            padding: 1.25rem 1.5rem;
        }

        .info-table {
            width: 100%;
            border-collapse: collapse;
        }

        .info-table tr {
            border-bottom: 1px solid rgba(148, 163, 184, 0.08);
        }

        .info-table tr:last-child {
            border-bottom: none;
        }

        .info-table td {
            padding: 0.75rem 0.25rem;
            font-size: 0.9rem;
            vertical-align: middle;
        }

        .info-table td.property-name {
            width: 38%;
            color: var(--text-secondary);
            font-weight: 500;
        }

        .info-table td.property-value {
            color: var(--text-primary);
        }

        .code-pill {
            display: inline-block;
            font-family: 'JetBrains Mono', monospace;
            background: var(--code-bg);
            border: 1px solid rgba(148, 163, 184, 0.15);
            border-radius: 6px;
            padding: 0.2rem 0.55rem;
            font-size: 0.85rem;
            color: #38bdf8;
            word-break: break-all;
        }

        .tag-pill {
            display: inline-block;
            padding: 0.15rem 0.5rem;
            border-radius: 4px;
            font-size: 0.75rem;
            font-weight: 600;
            text-transform: uppercase;
        }

        .tag-get {
            background: rgba(16, 185, 129, 0.15);
            color: #34d399;
            border: 1px solid rgba(16, 185, 129, 0.3);
        }

        .tag-ip {
            background: rgba(99, 102, 241, 0.15);
            color: #818cf8;
            border: 1px solid rgba(99, 102, 241, 0.3);
        }

        /* Note box */
        .info-banner {
            margin-top: 1.5rem;
            padding: 1rem 1.25rem;
            border-radius: 10px;
            background: rgba(56, 189, 248, 0.08);
            border: 1px solid rgba(56, 189, 248, 0.2);
            font-size: 0.85rem;
            color: #94a3b8;
            display: flex;
            align-items: flex-start;
            gap: 0.75rem;
        }

        .info-banner strong {
            color: var(--accent-blue);
        }

        /* Footer */
        footer {
            margin-top: 3rem;
            text-align: center;
            font-size: 0.825rem;
            color: var(--text-muted);
            border-top: 1px solid var(--card-border);
            padding-top: 1.5rem;
        }
    </style>
</head>
<body>

<div class="container">
    <!-- Header -->
    <header>
        <div class="header-title">
            <h1>Server Information Dashboard</h1>
            <p>Real-time server-side environment, request analysis, and hit tracking using Java Server Pages (JSP)</p>
        </div>
        <div class="actions">
            <a href="index.jsp" class="btn btn-primary" title="Reload page to increment hit counter">
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21.5 2v6h-6M21.34 15.57a10 10 0 1 1-.57-8.38l5.67-5.67"/></svg>
                Refresh (Hit +1)
            </a>
            <a href="index.jsp?reset=true" class="btn btn-secondary" onclick="return confirm('Are you sure you want to reset the hit counter?');" title="Reset hit counter to 1">
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"/><path d="M3 3v5h5"/></svg>
                Reset Counter
            </a>
        </div>
    </header>

    <!-- Top Hit Counter Stats -->
    <section class="stats-grid">
        <div class="stat-card hits-total">
            <div class="stat-label">
                <span>Total Server Hit Count</span>
                <svg width="18" height="18" fill="none" stroke="#38bdf8" stroke-width="2" viewBox="0 0 24 24"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
            </div>
            <div class="stat-value"><%= currentTotalHit %></div>
            <div class="stat-desc">Application scope &bull; Synchronized atomic counter</div>
        </div>

        <div class="stat-card hits-session">
            <div class="stat-label">
                <span>Your Session Hit Count</span>
                <svg width="18" height="18" fill="none" stroke="#34d399" stroke-width="2" viewBox="0 0 24 24"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
            </div>
            <div class="stat-value"><%= currentSessionHit %></div>
            <div class="stat-desc">Session scope &bull; Tracks current browser instance</div>
        </div>

        <div class="stat-card server-status">
            <div class="stat-label">
                <span>Server Protocol & Port</span>
                <svg width="18" height="18" fill="none" stroke="#fbbf24" stroke-width="2" viewBox="0 0 24 24"><rect x="2" y="2" width="20" height="8" rx="2" ry="2"/><rect x="2" y="14" width="20" height="8" rx="2" ry="2"/><line x1="6" y1="6" x2="6.01" y2="6"/><line x1="6" y1="18" x2="6.01" y2="18"/></svg>
            </div>
            <div class="stat-value" style="font-size: 1.7rem; padding-top: 0.35rem;"><%= scheme.toUpperCase() %> : <%= serverPort %></div>
            <div class="stat-desc"><%= protocol %> &bull; Active &amp; Ready</div>
        </div>
    </section>

    <!-- Main Grid Content -->
    <div class="dashboard-grid">

        <!-- 1. Client Information Panel -->
        <div class="panel">
            <div class="panel-header">
                <div class="badge-icon">
                    <svg width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><circle cx="12" cy="12" r="10"/><path d="M12 2a14.5 14.5 0 0 0 0 20 14.5 14.5 0 0 0 0-20"/><path d="M2 12h20"/></svg>
                </div>
                <h2>Client Information</h2>
            </div>
            <div class="panel-body">
                <table class="info-table">
                    <tr>
                        <td class="property-name">Client IP Address:</td>
                        <td class="property-value">
                            <span class="code-pill"><%= clientIp %></span>
                            <span class="tag-pill tag-ip">Active IP</span>
                        </td>
                    </tr>
                    <tr>
                        <td class="property-name">Direct Remote Addr:</td>
                        <td class="property-value">
                            <span class="code-pill"><%= remoteAddr %></span>
                        </td>
                    </tr>
                    <tr>
                        <td class="property-name">Client Hostname:</td>
                        <td class="property-value">
                            <span class="code-pill"><%= remoteHost %></span>
                        </td>
                    </tr>
                    <tr>
                        <td class="property-name">Client Remote Port:</td>
                        <td class="property-value">
                            <span class="code-pill"><%= remotePort %></span>
                        </td>
                    </tr>
                    <tr>
                        <td class="property-name">User-Agent Header:</td>
                        <td class="property-value" style="font-size: 0.8rem; color: var(--text-secondary); word-break: break-all;">
                            <%= request.getHeader("User-Agent") != null ? request.getHeader("User-Agent") : "N/A" %>
                        </td>
                    </tr>
                </table>
            </div>
        </div>

        <!-- 2. Request & URL Panel -->
        <div class="panel">
            <div class="panel-header">
                <div class="badge-icon">
                    <svg width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"/><path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"/></svg>
                </div>
                <h2>Request &amp; URL Details</h2>
            </div>
            <div class="panel-body">
                <table class="info-table">
                    <tr>
                        <td class="property-name">Full Request URL:</td>
                        <td class="property-value">
                            <span class="code-pill"><%= requestURL %></span>
                        </td>
                    </tr>
                    <tr>
                        <td class="property-name">Request URI:</td>
                        <td class="property-value">
                            <span class="code-pill"><%= requestURI %></span>
                        </td>
                    </tr>
                    <tr>
                        <td class="property-name">HTTP Method:</td>
                        <td class="property-value">
                            <span class="tag-pill tag-get"><%= httpMethod %></span>
                        </td>
                    </tr>
                    <tr>
                        <td class="property-name">Protocol &amp; Scheme:</td>
                        <td class="property-value">
                            <span class="code-pill"><%= protocol %> (<%= scheme %>)</span>
                        </td>
                    </tr>
                    <tr>
                        <td class="property-name">Query String:</td>
                        <td class="property-value">
                            <span class="code-pill"><%= queryString != null ? queryString : "None" %></span>
                        </td>
                    </tr>
                </table>
            </div>
        </div>

        <!-- 3. Server & Context Information Panel -->
        <div class="panel">
            <div class="panel-header">
                <div class="badge-icon">
                    <svg width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><rect x="4" y="4" width="16" height="16" rx="2" ry="2"/><rect x="9" y="9" width="6" height="6"/><line x1="9" y1="1" x2="9" y2="4"/><line x1="15" y1="1" x2="15" y2="4"/><line x1="9" y1="20" x2="9" y2="23"/><line x1="15" y1="20" x2="15" y2="23"/><line x1="20" y1="9" x2="23" y2="9"/><line x1="20" y1="14" x2="23" y2="14"/><line x1="1" y1="9" x2="4" y2="9"/><line x1="1" y1="14" x2="4" y2="14"/></svg>
                </div>
                <h2>Context &amp; Server Info</h2>
            </div>
            <div class="panel-body">
                <table class="info-table">
                    <tr>
                        <td class="property-name">Context Path:</td>
                        <td class="property-value">
                            <span class="code-pill"><%= contextPath.isEmpty() ? "/" : contextPath %></span>
                        </td>
                    </tr>
                    <tr>
                        <td class="property-name">Context Name:</td>
                        <td class="property-value">
                            <span class="code-pill"><%= servletContextName %></span>
                        </td>
                    </tr>
                    <tr>
                        <td class="property-name">Server Container:</td>
                        <td class="property-value">
                            <span class="code-pill"><%= serverInfo %></span>
                        </td>
                    </tr>
                    <tr>
                        <td class="property-name">Server Name &amp; Port:</td>
                        <td class="property-value">
                            <span class="code-pill"><%= serverName %> : <%= serverPort %></span>
                        </td>
                    </tr>
                    <tr>
                        <td class="property-name">Servlet Specification:</td>
                        <td class="property-value">
                            <span class="code-pill">Version <%= servletVersion %></span>
                        </td>
                    </tr>
                    <tr>
                        <td class="property-name">Context Real Path:</td>
                        <td class="property-value" style="font-size: 0.8rem; word-break: break-all; color: var(--text-secondary);">
                            <%= realPath != null ? realPath : "Not available in archive deployment" %>
                        </td>
                    </tr>
                </table>
            </div>
        </div>

        <!-- 4. Environment & Session Details Panel -->
        <div class="panel">
            <div class="panel-header">
                <div class="badge-icon">
                    <svg width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path d="M12 2v4M12 18v4M4.93 4.93l2.83 2.83M16.24 16.24l2.83 2.83M2 12h4M18 12h4M4.93 19.07l2.83-2.83M16.24 7.76l2.83-2.83"/></svg>
                </div>
                <h2>Environment &amp; Session</h2>
            </div>
            <div class="panel-body">
                <table class="info-table">
                    <tr>
                        <td class="property-name">Server Local Time:</td>
                        <td class="property-value">
                            <span class="code-pill"><%= serverTime %></span>
                        </td>
                    </tr>
                    <tr>
                        <td class="property-name">Java Runtime:</td>
                        <td class="property-value">
                            <span class="code-pill"><%= javaVersion %></span>
                        </td>
                    </tr>
                    <tr>
                        <td class="property-name">Operating System:</td>
                        <td class="property-value">
                            <span class="code-pill"><%= osName %></span>
                        </td>
                    </tr>
                    <tr>
                        <td class="property-name">Session Identifier:</td>
                        <td class="property-value" style="font-size: 0.8rem; word-break: break-all;">
                            <span class="code-pill"><%= sessionId %></span>
                        </td>
                    </tr>
                    <tr>
                        <td class="property-name">Session Created:</td>
                        <td class="property-value" style="font-size: 0.85rem; color: var(--text-secondary);">
                            <%= sdf.format(new Date(session.getCreationTime())) %>
                        </td>
                    </tr>
                </table>
            </div>
        </div>

    </div>

    <!-- Helpful Notes for Eclipse & Tomcat -->
    <div class="info-banner">
        <svg width="20" height="20" fill="none" stroke="#38bdf8" stroke-width="2" viewBox="0 0 24 24" style="flex-shrink: 0; margin-top: 2px;"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>
        <div>
            <strong>Quick Note on Localhost IP:</strong> If Client IP displays <code style="color:#38bdf8;">0:0:0:0:0:0:0:1</code> or <code style="color:#38bdf8;">::1</code>, this is the standard IPv6 loopback representation for <code style="color:#38bdf8;">localhost</code>. To see IPv4 (<code style="color:#38bdf8;">127.0.0.1</code>), access via <a href="http://127.0.0.1:8080<%= contextPath %>/" style="color:#38bdf8; text-decoration: underline;">http://127.0.0.1:8080<%= contextPath %>/</a>.
        </div>
    </div>

    <!-- Footer -->
    <footer>
        <p>JSP Server Information Application &bull; Ready to run on Eclipse IDE with Apache Tomcat</p>
    </footer>
</div>

</body>
</html>

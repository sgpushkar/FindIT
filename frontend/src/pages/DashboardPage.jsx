import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';
import MatchCard from '../components/MatchCard';
import { PlusCircle, Inbox, ArrowRight } from 'lucide-react';

const TABS = ['ALL', 'LOST', 'FOUND', 'ACTIVE', 'RESOLVED'];

export default function DashboardPage() {
  const { user, isAuthenticated, loading: authLoading } = useAuth();
  const navigate = useNavigate();

  const [activeTab, setActiveTab] = useState('ALL');
  const [items, setItems] = useState([]);
  const [matches, setMatches] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!authLoading && !isAuthenticated) {
      navigate('/login?next=/dashboard');
    }
  }, [authLoading, isAuthenticated, navigate]);

  useEffect(() => {
    async function loadDashboard() {
      if (!isAuthenticated) return;
      setLoading(true);
      try {
        const res = await api.dashboard.getMyReports(activeTab);
        if (res.success && res.data) {
          setItems(res.data.items || []);
          setMatches(res.data.matches || []);
        }
      } catch (err) {
        console.error('Failed to load dashboard:', err);
      } finally {
        setLoading(false);
      }
    }
    loadDashboard();
  }, [isAuthenticated, activeTab]);

  if (authLoading || (!isAuthenticated && loading)) {
    return (
      <section className="container section fade-in">
        <div className="loading-state">
          <div className="spinner"></div>
          <p>Loading your dashboard...</p>
        </div>
      </section>
    );
  }

  const formatTabName = (tab) => {
    if (tab === 'ALL') return 'All';
    return tab.charAt(0) + tab.slice(1).toLowerCase();
  };

  return (
    <section className="container section fade-in">
      <div className="section-head">
        <div>
          <p className="eyebrow">Personal Dashboard</p>
          <h1 className="page-title">
            Welcome, {user?.firstName || user?.name || 'Student'}
          </h1>
        </div>
        <Link to="/report" className="btn btn-accent">
          <PlusCircle size={16} />
          Report an Item
        </Link>
      </div>

      {/* Possible matches for user's reports */}
      {matches.length > 0 && (
        <div style={{ marginTop: 32, marginBottom: 48 }}>
          <h2 className="sub-head" style={{ marginTop: 0 }}>
            Possible matches for your reports
          </h2>
          <div className="matches">
            {matches.map((m, idx) => (
              <MatchCard key={idx} match={m} />
            ))}
          </div>
        </div>
      )}

      {/* My Reports */}
      <div className="section-head mt">
        <h2>My reports</h2>
      </div>

      <nav className="tabs" aria-label="Filter reports">
        {TABS.map((tab) => (
          <button
            key={tab}
            type="button"
            className={`tab ${activeTab === tab ? 'active' : ''}`}
            onClick={() => setActiveTab(tab)}
          >
            {formatTabName(tab)}
          </button>
        ))}
      </nav>

      {loading ? (
        <div className="loading-state">
          <div className="spinner"></div>
          <p>Loading reports...</p>
        </div>
      ) : items.length === 0 ? (
        <div className="empty">
          <Inbox size={40} className="muted" style={{ marginBottom: 16 }} />
          <h3>
            {activeTab === 'ALL'
              ? "You haven't reported anything yet."
              : 'No reports in this view.'}
          </h3>
          <p>
            {activeTab === 'ALL'
              ? "Lost something, or found something that isn't yours?"
              : 'Try selecting another tab or post a new report.'}
          </p>
          <Link to="/report" className="btn btn-dark" style={{ marginTop: 16 }}>
            Report an Item
          </Link>
        </div>
      ) : (
        <ul className="report-list">
          {items.map((item) => {
            const isLost = item.type?.toUpperCase() === 'LOST';
            const isResolved = item.status?.toUpperCase() === 'RESOLVED';
            const displayDate = item.displayDate || item.itemDate || '-';

            return (
              <li key={item.id}>
                <Link to={`/item/${item.id}`} className="report-row">
                  <span className={`tag ${isLost ? 'tag-lost' : 'tag-found'}`}>
                    {item.type}
                  </span>
                  <span className="report-name">{item.itemName}</span>
                  <span className="report-loc">{item.location}</span>
                  <span className="report-date">{displayDate}</span>
                  <span className={`status ${isResolved ? 'status-resolved' : 'status-active'}`}>
                    {item.status}
                  </span>
                  <ArrowRight size={15} className="muted row-arrow" />
                </Link>
              </li>
            );
          })}
        </ul>
      )}
    </section>
  );
}


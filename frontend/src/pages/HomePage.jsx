import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { api } from '../services/api';
import ItemCard from '../components/ItemCard';
import { Search, PlusCircle, ArrowRight, CheckCircle2, ShieldCheck, HeartHandshake } from 'lucide-react';

export default function HomePage() {
  const navigate = useNavigate();
  const [query, setQuery] = useState('');
  const [recentItems, setRecentItems] = useState([]);
  const [stats, setStats] = useState({
    activeLost: 0,
    activeFound: 0,
    resolvedCount: 0,
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadData() {
      try {
        const [recentRes, statsRes] = await Promise.all([
          api.items.getRecent(6),
          api.items.getStats(),
        ]);
        if (recentRes.success && recentRes.data) {
          setRecentItems(recentRes.data);
        }
        if (statsRes.success && statsRes.data) {
          setStats(statsRes.data);
        }
      } catch (err) {
        console.error('Failed to load homepage data:', err);
      } finally {
        setLoading(false);
      }
    }
    loadData();
  }, []);

  const handleSearch = (e) => {
    e.preventDefault();
    if (query.trim()) {
      navigate(`/search?q=${encodeURIComponent(query.trim())}`);
    } else {
      navigate('/search');
    }
  };

  return (
    <div className="home-page fade-in">
      {/* Hero Section */}
      <section className="hero container">
        <p className="eyebrow accent">Lost something?</p>
        <h1 className="hero-title">
          Someone might<br />have found it.
        </h1>
        <p className="hero-lead">
          Find what was lost. Return what was found.<br />
          The campus lost &amp; found, in one place.
        </p>

        <div className="hero-actions">
          <Link to="/report?type=LOST" className="btn btn-accent">
            <PlusCircle size={17} />
            Report Lost Item
          </Link>
          <Link to="/report?type=FOUND" className="btn btn-outline">
            <CheckCircle2 size={17} />
            Report Found Item
          </Link>
        </div>

        <form className="quick-search" onSubmit={handleSearch} role="search">
          <Search size={18} className="search-icon" />
          <input
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Search by item, category or location..."
            aria-label="Search"
            maxLength={100}
          />
          <button type="submit" className="btn btn-dark">
            Search
          </button>
        </form>

        <dl className="stats">
          <div>
            <dt>Lost reports</dt>
            <dd>{stats.activeLost ?? 0}</dd>
          </div>
          <div>
            <dt>Found reports</dt>
            <dd>{stats.activeFound ?? 0}</dd>
          </div>
          <div>
            <dt>Returned</dt>
            <dd>{stats.resolvedCount ?? 0}</dd>
          </div>
        </dl>
      </section>

      {/* Recent Reports Section */}
      <section className="container section">
        <div className="section-head">
          <div>
            <p className="eyebrow">Campus feed</p>
            <h2>Recent reports</h2>
          </div>
          <Link to="/search" className="link flex-link">
            <span>View all</span>
            <ArrowRight size={15} />
          </Link>
        </div>

        {loading ? (
          <div className="loading-state">
            <div className="spinner"></div>
            <p>Loading recent reports...</p>
          </div>
        ) : recentItems.length === 0 ? (
          <div className="empty">
            <h3>Nothing reported yet.</h3>
            <p>Be the first to report a lost or found item.</p>
            <Link to="/report" className="btn btn-dark">
              Report an Item
            </Link>
          </div>
        ) : (
          <div className="grid">
            {recentItems.map((item) => (
              <ItemCard key={item.id} item={item} />
            ))}
          </div>
        )}
      </section>

      {/* How it Works Section */}
      <section className="container section how">
        <p className="eyebrow">Simplicity first</p>
        <h2>How it works</h2>
        <ol className="steps">
          <li>
            <span>01</span>
            <h3>Report</h3>
            <p>Tell us what you lost or found, where and when.</p>
          </li>
          <li>
            <span>02</span>
            <h3>Match</h3>
            <p>FindIT compares reports and suggests possible matches automatically.</p>
          </li>
          <li>
            <span>03</span>
            <h3>Return</h3>
            <p>Meet up, hand it back safely, and mark the report resolved.</p>
          </li>
        </ol>
      </section>
    </div>
  );
}


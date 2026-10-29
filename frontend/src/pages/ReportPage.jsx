import React, { useState, useEffect } from 'react';
import { useSearchParams, useNavigate, Link } from 'react-router-dom';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import MatchCard from '../components/MatchCard';
import { CheckCircle, AlertCircle, ArrowLeft, Send } from 'lucide-react';

export default function ReportPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuth();
  const { showToast } = useToast();

  const today = new Date().toISOString().split('T')[0];

  const initialType = searchParams.get('type')?.toUpperCase() === 'FOUND' ? 'FOUND' : 'LOST';

  const [form, setForm] = useState({
    type: initialType,
    itemName: '',
    category: '',
    itemDate: today,
    location: '',
    description: '',
  });

  const [categories, setCategories] = useState([]);
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [submittedData, setSubmittedData] = useState(null);

  useEffect(() => {
    async function loadCategories() {
      try {
        const res = await api.categories.getAll();
        if (res.success && res.data) {
          setCategories(res.data);
        }
      } catch (err) {
        console.error('Failed to load categories:', err);
      }
    }
    loadCategories();
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
    if (error) setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!isAuthenticated) {
      navigate('/login?next=/report');
      return;
    }

    if (!form.itemName.trim() || !form.category || !form.location.trim() || !form.itemDate) {
      setError('Item name, category, date and location are required.');
      return;
    }

    setSubmitting(true);
    setError('');

    try {
      const res = await api.items.create(form);
      if (res.success && res.data) {
        setSubmittedData(res.data);
        showToast('Report submitted successfully!', 'success');
      } else {
        setError(res.message || 'Failed to submit report.');
      }
    } catch (err) {
      setError(err.message || 'Failed to submit report. Please try again.');
    } finally {
      setSubmitting(false);
    }
  };

  if (!isAuthenticated) {
    return (
      <section className="container narrow fade-in" style={{ paddingTop: 80 }}>
        <div className="form-card wide">
          <p className="eyebrow accent">Authentication Required</p>
          <h1>Report an item</h1>
          <p className="muted">
            Please log in or create an account to post a lost or found report on campus.
          </p>
          <div className="actions" style={{ marginTop: 32 }}>
            <Link to="/login?next=/report" className="btn btn-dark">
              Log in to continue
            </Link>
            <Link to="/register" className="btn btn-outline">
              Sign up
            </Link>
          </div>
        </div>
      </section>
    );
  }

  // Success view with immediate matching recommendations
  if (submittedData) {
    const { item, matches } = submittedData;
    const hasMatches = matches && matches.length > 0;

    return (
      <section className="container narrow fade-in" style={{ paddingTop: 60 }}>
        <div className="form-card wide">
          <p className="eyebrow accent">Report submitted</p>
          <h1>Thanks &mdash; it's on the board.</h1>
          <p className="muted">
            Your <strong>{item?.type?.toLowerCase()}</strong> report for{' '}
            <strong>{item?.itemName}</strong> is now visible to everyone on campus.
          </p>

          {hasMatches ? (
            <div style={{ marginTop: 32 }}>
              <h2 className="sub-head" style={{ marginTop: 0 }}>
                We found {matches.length} possible match{matches.length > 1 ? 'es' : ''}
              </h2>
              <div className="matches">
                {matches.map((m, idx) => (
                  <MatchCard key={idx} match={m} />
                ))}
              </div>
            </div>
          ) : (
            <p className="note" style={{ marginTop: 24 }}>
              No matching reports yet. We'll show possible matches on your dashboard as soon as one appears.
            </p>
          )}

          <div className="actions" style={{ marginTop: 36, display: 'flex', gap: 12, flexWrap: 'wrap' }}>
            <Link to={`/item/${item?.id}`} className="btn btn-dark">
              View report
            </Link>
            <Link to="/dashboard" className="btn btn-outline">
              Go to dashboard
            </Link>
            <button
              onClick={() => {
                setSubmittedData(null);
                setForm({
                  type: 'LOST',
                  itemName: '',
                  category: '',
                  itemDate: today,
                  location: '',
                  description: '',
                });
              }}
              className="btn btn-ghost"
            >
              Report another item
            </button>
          </div>
        </div>
      </section>
    );
  }

  return (
    <section className="container narrow fade-in" style={{ paddingTop: 60 }}>
      <div className="form-card wide">
        <p className="eyebrow">New report</p>
        <h1>Report an item</h1>
        <p className="muted">Clear details help the right person find it faster.</p>

        {error && (
          <div className="alert">
            <AlertCircle size={16} />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="form" noValidate>
          <fieldset className="type-choice">
            <legend>What happened?</legend>
            <label className={`choice ${form.type === 'LOST' ? 'selected' : ''}`}>
              <input
                type="radio"
                name="type"
                value="LOST"
                checked={form.type === 'LOST'}
                onChange={handleChange}
              />
              <span>
                <strong>I lost something</strong>
                <small>Ask the campus for help finding it</small>
              </span>
            </label>
            <label className={`choice ${form.type === 'FOUND' ? 'selected' : ''}`}>
              <input
                type="radio"
                name="type"
                value="FOUND"
                checked={form.type === 'FOUND'}
                onChange={handleChange}
              />
              <span>
                <strong>I found something</strong>
                <small>Help it get safely back to its owner</small>
              </span>
            </label>
          </fieldset>

          <label>
            Item name
            <input
              type="text"
              name="itemName"
              value={form.itemName}
              onChange={handleChange}
              placeholder="e.g. Black Leather Wallet, Sony WH-1000XM4..."
              required
              maxLength={100}
            />
          </label>

          <div className="form-row">
            <label>
              Category
              <select name="category" value={form.category} onChange={handleChange} required>
                <option value="">Choose a category</option>
                {categories.map((cat) => (
                  <option key={cat} value={cat}>
                    {cat}
                  </option>
                ))}
              </select>
            </label>
            <label>
              Date
              <input
                type="date"
                name="itemDate"
                value={form.itemDate}
                onChange={handleChange}
                max={today}
                required
              />
            </label>
          </div>

          <label>
            Location
            <input
              type="text"
              name="location"
              value={form.location}
              onChange={handleChange}
              placeholder="e.g. Central Library, 2nd Floor Canteen, Main Gate..."
              required
              maxLength={100}
            />
          </label>

          <label>
            Description
            <textarea
              name="description"
              rows={4}
              value={form.description}
              onChange={handleChange}
              maxLength={1000}
              placeholder="Colour, brand, stickers, keyrings, distinguishing marks..."
            />
          </label>

          <button type="submit" disabled={submitting} className="btn btn-accent btn-block">
            <Send size={16} />
            {submitting ? 'Submitting report...' : 'Submit report'}
          </button>
        </form>
      </div>
    </section>
  );
}


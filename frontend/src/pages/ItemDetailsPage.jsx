import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import MatchCard from '../components/MatchCard';
import { ArrowLeft, CheckCircle, Tag, MapPin, Calendar, User, Check, AlertTriangle } from 'lucide-react';

export default function ItemDetailsPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuth();
  const { showToast } = useToast();

  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [resolving, setResolving] = useState(false);
  const [showConfirmModal, setShowConfirmModal] = useState(false);

  useEffect(() => {
    async function fetchItem() {
      setLoading(true);
      try {
        const res = await api.items.getById(id);
        if (res.success && res.data) {
          setData(res.data);
        } else {
          showToast(res.message || 'Report not found', 'error');
        }
      } catch (err) {
        showToast(err.message || 'Failed to load report', 'error');
      } finally {
        setLoading(false);
      }
    }
    fetchItem();
  }, [id, showToast]);

  const handleResolve = async () => {
    setResolving(true);
    try {
      const res = await api.items.resolve(id);
      if (res.success) {
        showToast('Report marked as resolved!', 'success');
        setShowConfirmModal(false);
        // Refresh item data
        const updated = await api.items.getById(id);
        if (updated.success && updated.data) {
          setData(updated.data);
        }
      } else {
        showToast(res.message || 'Failed to resolve report', 'error');
      }
    } catch (err) {
      showToast(err.message || 'Failed to update report', 'error');
    } finally {
      setResolving(false);
    }
  };

  if (loading) {
    return (
      <section className="container narrow fade-in" style={{ paddingTop: 80 }}>
        <div className="loading-state">
          <div className="spinner"></div>
          <p>Loading report details...</p>
        </div>
      </section>
    );
  }

  if (!data || !data.item) {
    return (
      <section className="container narrow fade-in" style={{ paddingTop: 80 }}>
        <div className="empty">
          <h3>Report not found</h3>
          <p>This report might have been removed or the link is invalid.</p>
          <Link to="/search" className="btn btn-dark">
            Back to Search
          </Link>
        </div>
      </section>
    );
  }

  const { item, canManage, matches } = data;
  const isLost = item.type?.toUpperCase() === 'LOST';
  const isResolved = item.status?.toUpperCase() === 'RESOLVED';
  const displayDate = item.longDate || item.displayDate || item.itemDate;

  return (
    <section className="container narrow fade-in" style={{ paddingTop: 40, paddingBottom: 80 }}>
      <Link to="/search" className="back">
        <ArrowLeft size={16} />
        <span>Back to search</span>
      </Link>

      <article className="detail">
        <div className="card-top">
          <span className={`tag ${isLost ? 'tag-lost' : 'tag-found'}`}>
            {item.type}
          </span>
          <span className={`status ${isResolved ? 'status-resolved' : 'status-active'}`}>
            {item.status}
          </span>
        </div>

        <h1 className="detail-title">{item.itemName}</h1>

        <dl className="detail-grid">
          <div>
            <dt><Tag size={14} style={{ display: 'inline', marginRight: 4 }} />Category</dt>
            <dd>{item.category}</dd>
          </div>
          <div>
            <dt><MapPin size={14} style={{ display: 'inline', marginRight: 4 }} />Location</dt>
            <dd>{item.location}</dd>
          </div>
          <div>
            <dt><Calendar size={14} style={{ display: 'inline', marginRight: 4 }} />Date</dt>
            <dd>{displayDate}</dd>
          </div>
          <div>
            <dt><User size={14} style={{ display: 'inline', marginRight: 4 }} />Reported by</dt>
            <dd>{item.reporterName || 'Campus Student'}</dd>
          </div>
        </dl>

        <div className="detail-desc">
          <h2>Description</h2>
          <p>{item.description || 'No description provided.'}</p>
        </div>

        {canManage && !isResolved && (
          <div className="detail-action">
            <button
              onClick={() => setShowConfirmModal(true)}
              disabled={resolving}
              className="btn btn-accent"
            >
              <Check size={16} />
              Mark as Resolved
            </button>
            <span className="muted small">
              Use this once the item is returned to its rightful owner.
            </span>
          </div>
        )}

        {isResolved && (
          <p className="note">
            ✓ This item has been returned and marked resolved. The report is kept for reference.
          </p>
        )}

        {!isAuthenticated && !isResolved && (
          <p className="note">
            Is this yours, or did you find it?{' '}
            <Link to={`/login?next=/item/${id}`} className="link">
              Log in
            </Link>{' '}
            to report or manage items.
          </p>
        )}
      </article>

      {/* Confirmation Modal */}
      {showConfirmModal && (
        <div className="modal-backdrop">
          <div className="modal-content">
            <div className="modal-icon">
              <CheckCircle size={32} color="var(--accent)" />
            </div>
            <h3>Mark as Resolved?</h3>
            <p className="muted">
              Are you sure you want to mark "<strong>{item.itemName}</strong>" as resolved?
              This indicates the item has been returned to its owner.
            </p>
            <div className="modal-actions">
              <button
                onClick={handleResolve}
                disabled={resolving}
                className="btn btn-accent"
              >
                {resolving ? 'Resolving...' : 'Yes, mark resolved'}
              </button>
              <button
                onClick={() => setShowConfirmModal(false)}
                className="btn btn-outline"
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Possible Matches */}
      {matches && matches.length > 0 && (
        <div style={{ marginTop: 48 }}>
          <h2 className="sub-head">Possible matches for this report</h2>
          <div className="matches">
            {matches.map((m, idx) => (
              <MatchCard key={idx} match={m} />
            ))}
          </div>
        </div>
      )}
    </section>
  );
}


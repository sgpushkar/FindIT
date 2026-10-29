import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import { Shield, Trash2, CheckCircle2, AlertTriangle, Users, FileText } from 'lucide-react';

export default function AdminPage() {
  const { user, isAdmin, loading: authLoading } = useAuth();
  const navigate = useNavigate();
  const { showToast } = useToast();

  const [stats, setStats] = useState(null);
  const [items, setItems] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  const [deleteTarget, setDeleteTarget] = useState(null);
  const [deleting, setDeleting] = useState(false);

  useEffect(() => {
    if (!authLoading && (!user || !isAdmin)) {
      navigate('/login?next=/admin');
    }
  }, [authLoading, user, isAdmin, navigate]);

  const loadAdminData = async () => {
    setLoading(true);
    try {
      const res = await api.admin.getOverview();
      if (res.success && res.data) {
        setStats(res.data.stats);
        setItems(res.data.items || []);
        setUsers(res.data.users || []);
      } else {
        showToast(res.message || 'Failed to load admin data', 'error');
      }
    } catch (err) {
      showToast(err.message || 'Failed to load admin overview', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (isAdmin) {
      loadAdminData();
    }
  }, [isAdmin]);

  const handleResolve = async (id, itemName) => {
    try {
      const res = await api.items.resolve(id);
      if (res.success) {
        showToast(`"${itemName}" marked as resolved!`, 'success');
        loadAdminData();
      } else {
        showToast(res.message || 'Failed to resolve report', 'error');
      }
    } catch (err) {
      showToast(err.message || 'Error resolving report', 'error');
    }
  };

  const confirmDelete = async () => {
    if (!deleteTarget) return;
    setDeleting(true);
    try {
      const res = await api.admin.deleteItem(deleteTarget.id);
      if (res.success) {
        showToast('Report deleted permanently.', 'info');
        setDeleteTarget(null);
        loadAdminData();
      } else {
        showToast(res.message || 'Failed to delete report', 'error');
      }
    } catch (err) {
      showToast(err.message || 'Error deleting report', 'error');
    } finally {
      setDeleting(false);
    }
  };

  if (authLoading || loading) {
    return (
      <section className="container section fade-in">
        <div className="loading-state">
          <div className="spinner"></div>
          <p>Loading administration portal...</p>
        </div>
      </section>
    );
  }

  return (
    <section className="container section fade-in" style={{ paddingBottom: 100 }}>
      <div className="admin-header">
        <p className="eyebrow accent" style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
          <Shield size={14} />
          <span>Campus Moderation</span>
        </p>
        <h1 className="page-title">Admin Overview</h1>
      </div>

      {/* Stat Cards */}
      {stats && (
        <dl className="stat-cards">
          <div>
            <dt>Total users</dt>
            <dd>{stats.totalUsers}</dd>
          </div>
          <div>
            <dt>Lost reports</dt>
            <dd>{stats.activeLost}</dd>
          </div>
          <div>
            <dt>Found reports</dt>
            <dd>{stats.activeFound}</dd>
          </div>
          <div>
            <dt>Active</dt>
            <dd>{stats.totalReports - stats.resolvedCount}</dd>
          </div>
          <div>
            <dt>Resolved</dt>
            <dd>{stats.resolvedCount}</dd>
          </div>
        </dl>
      )}

      {/* All Reports Table */}
      <h2 className="sub-head">All campus reports ({items.length})</h2>
      {items.length === 0 ? (
        <div className="empty">
          <h3>No reports yet.</h3>
        </div>
      ) : (
        <div className="table-wrap">
          <table className="table">
            <thead>
              <tr>
                <th>Type</th>
                <th>Item</th>
                <th>Location</th>
                <th>Reported by</th>
                <th>Date</th>
                <th>Status</th>
                <th style={{ textAlign: 'right' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {items.map((item) => {
                const isLost = item.type?.toUpperCase() === 'LOST';
                const isResolved = item.status?.toUpperCase() === 'RESOLVED';
                const displayDate = item.displayDate || item.itemDate || '-';

                return (
                  <tr key={item.id}>
                    <td>
                      <span className={`tag ${isLost ? 'tag-lost' : 'tag-found'}`}>
                        {item.type}
                      </span>
                    </td>
                    <td>
                      <Link to={`/item/${item.id}`} className="link" style={{ fontWeight: 600 }}>
                        {item.itemName}
                      </Link>
                    </td>
                    <td>{item.location}</td>
                    <td>{item.reporterName || 'Student'}</td>
                    <td className="nowrap">{displayDate}</td>
                    <td>
                      <span className={`status ${isResolved ? 'status-resolved' : 'status-active'}`}>
                        {item.status}
                      </span>
                    </td>
                    <td className="row-actions" style={{ textAlign: 'right' }}>
                      {!isResolved && (
                        <button
                          onClick={() => handleResolve(item.id, item.itemName)}
                          className="btn btn-ghost btn-sm"
                          title="Mark resolved"
                        >
                          Resolve
                        </button>
                      )}
                      <button
                        onClick={() => setDeleteTarget(item)}
                        className="btn btn-danger btn-sm"
                        title="Delete permanently"
                      >
                        <Trash2 size={13} />
                        Delete
                      </button>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}

      {/* Registered Users Table */}
      <h2 className="sub-head" style={{ marginTop: 64 }}>
        Registered users ({users.length})
      </h2>
      <div className="table-wrap">
        <table className="table">
          <thead>
            <tr>
              <th>Name</th>
              <th>Email</th>
              <th>Role</th>
              <th>Joined Date</th>
            </tr>
          </thead>
          <tbody>
            {users.map((u) => {
              const isAdminUser = u.role?.toUpperCase() === 'ADMIN';
              const joined = u.createdAt ? u.createdAt.substring(0, 10) : '-';

              return (
                <tr key={u.id}>
                  <td style={{ fontWeight: 550 }}>{u.name}</td>
                  <td>{u.email}</td>
                  <td>
                    <span className={`status ${isAdminUser ? 'status-active' : 'status-resolved'}`}>
                      {u.role}
                    </span>
                  </td>
                  <td className="nowrap">{joined}</td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>

      {/* Delete Confirmation Modal */}
      {deleteTarget && (
        <div className="modal-backdrop">
          <div className="modal-content">
            <div className="modal-icon" style={{ background: 'var(--accent-soft)', color: 'var(--accent)' }}>
              <AlertTriangle size={32} />
            </div>
            <h3>Delete this report?</h3>
            <p className="muted">
              Are you sure you want to permanently delete "<strong>{deleteTarget.itemName}</strong>"?
              This action cannot be undone.
            </p>
            <div className="modal-actions">
              <button
                onClick={confirmDelete}
                disabled={deleting}
                className="btn btn-danger"
              >
                {deleting ? 'Deleting...' : 'Yes, delete permanently'}
              </button>
              <button
                onClick={() => setDeleteTarget(null)}
                className="btn btn-outline"
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </section>
  );
}


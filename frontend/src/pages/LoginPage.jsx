import React, { useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import { LogIn, KeyRound, AlertCircle, Zap } from 'lucide-react';

export default function LoginPage() {
  const [searchParams] = useSearchParams();
  const next = searchParams.get('next') || '';
  const navigate = useNavigate();
  const { login } = useAuth();
  const { showToast } = useToast();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!email.trim() || !password) {
      setError('Please enter your email and password.');
      return;
    }

    setSubmitting(true);
    setError('');

    try {
      const user = await login(email.trim(), password);
      showToast(`Welcome back, ${user.firstName || user.name}!`, 'success');
      if (next && next.startsWith('/')) {
        navigate(next);
      } else {
        navigate(user.role?.toUpperCase() === 'ADMIN' ? '/admin' : '/dashboard');
      }
    } catch (err) {
      setError(err.message || 'Incorrect email or password.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleQuickLogin = (demoEmail, demoPassword) => {
    setEmail(demoEmail);
    setPassword(demoPassword);
    setError('');
  };

  return (
    <section className="container narrow fade-in" style={{ paddingTop: 60, paddingBottom: 80 }}>
      <div className="form-card">
        <p className="eyebrow">Welcome back</p>
        <h1>Log in</h1>
        <p className="muted">Manage your reported items and check matches.</p>

        {error && (
          <div className="alert">
            <AlertCircle size={16} />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="form" noValidate>
          <label>
            Email address
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="student@findit.local"
              required
              autoFocus
            />
          </label>

          <label>
            Password
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              required
            />
          </label>

          <button type="submit" disabled={submitting} className="btn btn-accent btn-block">
            <LogIn size={16} />
            {submitting ? 'Logging in...' : 'Log in'}
          </button>
        </form>

        {/* Demo Accounts Helper */}
        <div className="demo-accounts-box">
          <p className="small muted demo-title">
            <Zap size={14} style={{ display: 'inline', marginRight: 4, color: 'var(--accent)' }} />
            One-click demo credentials:
          </p>
          <div className="demo-buttons">
            <button
              type="button"
              className="btn btn-ghost btn-sm"
              onClick={() => handleQuickLogin('student@findit.local', 'student123')}
            >
              Student (student@findit.local)
            </button>
            <button
              type="button"
              className="btn btn-ghost btn-sm"
              onClick={() => handleQuickLogin('admin@findit.local', 'admin123')}
            >
              Admin (admin@findit.local)
            </button>
          </div>
        </div>

        <p className="switch-auth">
          Don't have an account yet?{' '}
          <Link to="/register" className="link">
            Sign up here
          </Link>
        </p>
      </div>
    </section>
  );
}


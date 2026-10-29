import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import { UserPlus, AlertCircle } from 'lucide-react';

export default function RegisterPage() {
  const navigate = useNavigate();
  const { register } = useAuth();
  const { showToast } = useToast();

  const [form, setForm] = useState({
    name: '',
    email: '',
    password: '',
    confirmPassword: '',
  });

  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
    if (error) setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const { name, email, password, confirmPassword } = form;

    if (!name.trim() || !email.trim() || !password || !confirmPassword) {
      setError('All fields are required.');
      return;
    }
    if (name.length > 100) {
      setError('Name must be under 100 characters.');
      return;
    }
    if (password.length < 6) {
      setError('Password must be at least 6 characters.');
      return;
    }
    if (password !== confirmPassword) {
      setError('Passwords do not match.');
      return;
    }

    setSubmitting(true);
    setError('');

    try {
      await register(form);
      showToast('Account created successfully! You can log in now.', 'success');
      navigate('/login');
    } catch (err) {
      setError(err.message || 'Registration failed. Please try again.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <section className="container narrow fade-in" style={{ paddingTop: 60, paddingBottom: 80 }}>
      <div className="form-card">
        <p className="eyebrow">Join campus</p>
        <h1>Create an account</h1>
        <p className="muted">Report and track items lost or found across campus.</p>

        {error && (
          <div className="alert">
            <AlertCircle size={16} />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="form" noValidate>
          <label>
            Full name
            <input
              type="text"
              name="name"
              value={form.name}
              onChange={handleChange}
              placeholder="e.g. Maya Sharma"
              required
              autoFocus
              maxLength={100}
            />
          </label>

          <label>
            Campus email address
            <input
              type="email"
              name="email"
              value={form.email}
              onChange={handleChange}
              placeholder="e.g. maya@findit.local"
              required
            />
          </label>

          <div className="form-row">
            <label>
              Password
              <input
                type="password"
                name="password"
                value={form.password}
                onChange={handleChange}
                placeholder="At least 6 characters"
                required
              />
            </label>

            <label>
              Confirm password
              <input
                type="password"
                name="confirmPassword"
                value={form.confirmPassword}
                onChange={handleChange}
                placeholder="Re-type password"
                required
              />
            </label>
          </div>

          <button type="submit" disabled={submitting} className="btn btn-dark btn-block">
            <UserPlus size={16} />
            {submitting ? 'Creating account...' : 'Create account'}
          </button>
        </form>

        <p className="switch-auth">
          Already have an account?{' '}
          <Link to="/login" className="link">
            Log in here
          </Link>
        </p>
      </div>
    </section>
  );
}


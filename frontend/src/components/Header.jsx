import React, { useState } from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import { Shield, User, LogOut, Menu, X, PlusCircle } from 'lucide-react';

export default function Header() {
  const { user, isAdmin, logout } = useAuth();
  const { showToast } = useToast();
  const navigate = useNavigate();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const handleLogout = async () => {
    await logout();
    showToast('You have been logged out.');
    navigate('/');
    setMobileMenuOpen(false);
  };

  const closeMenu = () => setMobileMenuOpen(false);

  return (
    <header className="site-header">
      <div className="container nav">
        <Link to="/" className="brand" onClick={closeMenu}>
          <span className="brand-name">FindIT</span>
          <span className="brand-sub">Campus Lost &amp; Found</span>
        </Link>

        <button
          className="nav-burger-btn"
          onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
          aria-label={mobileMenuOpen ? 'Close menu' : 'Open menu'}
        >
          {mobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
        </button>

        <nav className={`nav-links ${mobileMenuOpen ? 'open' : ''}`}>
          <NavLink to="/" end className={({ isActive }) => (isActive ? 'active' : '')} onClick={closeMenu}>
            Home
          </NavLink>
          <NavLink to="/search" className={({ isActive }) => (isActive ? 'active' : '')} onClick={closeMenu}>
            Search
          </NavLink>
          <NavLink
            to="/report?type=LOST"
            className={({ isActive }) => (isActive ? 'active' : '')}
            onClick={closeMenu}
          >
            Report Lost
          </NavLink>
          <NavLink
            to="/report?type=FOUND"
            className={({ isActive }) => (isActive ? 'active' : '')}
            onClick={closeMenu}
          >
            Report Found
          </NavLink>

          <span className="nav-spacer"></span>

          {user ? (
            <div className="user-nav-group">
              {isAdmin && (
                <NavLink
                  to="/admin"
                  className={({ isActive }) => `admin-link ${isActive ? 'active' : ''}`}
                  onClick={closeMenu}
                >
                  <Shield size={15} />
                  <span>Admin</span>
                </NavLink>
              )}
              <NavLink
                to="/dashboard"
                className={({ isActive }) => `dashboard-link ${isActive ? 'active' : ''}`}
                onClick={closeMenu}
              >
                <User size={15} />
                <span>Dashboard</span>
              </NavLink>
              <button onClick={handleLogout} className="btn-logout" title="Log out">
                <LogOut size={15} />
                <span>Log out</span>
              </button>
            </div>
          ) : (
            <div className="guest-nav-group">
              <NavLink to="/login" className={({ isActive }) => (isActive ? 'active' : '')} onClick={closeMenu}>
                Log in
              </NavLink>
              <Link to="/register" className="btn btn-dark btn-sm" onClick={closeMenu}>
                Sign up
              </Link>
            </div>
          )}
        </nav>
      </div>
    </header>
  );
}


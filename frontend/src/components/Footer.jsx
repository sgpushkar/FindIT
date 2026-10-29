import React from 'react';
import { Link } from 'react-router-dom';

export default function Footer() {
  return (
    <footer className="site-footer">
      <div className="container footer-content">
        <div className="footer-brand">
          <p className="footer-title">FindIT &middot; Campus Lost &amp; Found</p>
          <p className="footer-lead">Find what was lost. Return what was found.</p>
        </div>
        <div className="footer-links">
          <Link to="/">Home</Link>
          <Link to="/search">Search Reports</Link>
          <Link to="/report?type=LOST">Report Lost</Link>
          <Link to="/report?type=FOUND">Report Found</Link>
        </div>
        <div className="footer-bottom">
          <p className="small muted">
            Built with Spring Boot &amp; React &middot; Running locally with SQLite database.
          </p>
        </div>
      </div>
    </footer>
  );
}


import React from 'react';
import { Link } from 'react-router-dom';
import { Compass } from 'lucide-react';

export default function NotFoundPage() {
  return (
    <section className="container narrow fade-in" style={{ paddingTop: 80, paddingBottom: 80 }}>
      <div className="empty">
        <Compass size={48} className="muted" style={{ marginBottom: 16 }} />
        <p className="eyebrow accent">404 &middot; Not Found</p>
        <h1 className="page-title" style={{ marginBottom: 12 }}>Page not found</h1>
        <p className="muted">
          The page or report you are looking for doesn't exist or may have been relocated.
        </p>
        <div style={{ marginTop: 24, display: 'flex', gap: 12, justifyContent: 'center' }}>
          <Link to="/" className="btn btn-dark">
            Go to Home
          </Link>
          <Link to="/search" className="btn btn-outline">
            Browse Reports
          </Link>
        </div>
      </div>
    </section>
  );
}


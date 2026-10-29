import React from 'react';
import { Link } from 'react-router-dom';
import { MapPin, Calendar, Tag, ArrowRight } from 'lucide-react';

export default function ItemCard({ item }) {
  const isLost = item.type?.toUpperCase() === 'LOST';
  const isResolved = item.status?.toUpperCase() === 'RESOLVED';

  const desc = item.shortDescription || item.description || 'No description provided.';
  const displayDate = item.displayDate || item.itemDate || '-';

  return (
    <Link to={`/item/${item.id}`} className="card">
      <div className="card-top">
        <span className={`tag ${isLost ? 'tag-lost' : 'tag-found'}`}>
          {item.type}
        </span>
        <span className={`status ${isResolved ? 'status-resolved' : 'status-active'}`}>
          {item.status}
        </span>
      </div>

      <h3 className="card-title">{item.itemName}</h3>
      <p className="card-desc">{desc}</p>

      <dl className="card-meta">
        <div>
          <dt><Tag size={13} style={{ display: 'inline', marginRight: 4 }} />Category</dt>
          <dd>{item.category}</dd>
        </div>
        <div>
          <dt><MapPin size={13} style={{ display: 'inline', marginRight: 4 }} />Location</dt>
          <dd>{item.location}</dd>
        </div>
        <div>
          <dt><Calendar size={13} style={{ display: 'inline', marginRight: 4 }} />Date</dt>
          <dd>{displayDate}</dd>
        </div>
      </dl>

      <span className="card-link">
        View details <ArrowRight size={14} className="arrow-icon" />
      </span>
    </Link>
  );
}


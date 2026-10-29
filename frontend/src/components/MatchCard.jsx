import React from 'react';
import { Link } from 'react-router-dom';
import { Sparkles, ArrowRight } from 'lucide-react';

export default function MatchCard({ match }) {
  const { source, candidate, score } = match;

  return (
    <div className="match">
      <div className="match-head">
        <span className="eyebrow accent match-badge">
          <Sparkles size={14} />
          <span>Possible match found</span>
        </span>
        <span className="match-score">{score}% match</span>
      </div>

      <div className="match-body">
        <div>
          <span className="label">
            Your {source?.type?.toLowerCase() || 'reported'} item
          </span>
          <strong>{source?.itemName}</strong>
        </div>
        <div>
          <span className="label">
            Possible {candidate?.type?.toLowerCase() || 'matching'} item
          </span>
          <strong>{candidate?.itemName}</strong>
        </div>
        <div>
          <span className="label">Location</span>
          <strong>{candidate?.location}</strong>
        </div>
      </div>

      <div className="meter" aria-hidden="true">
        <span style={{ width: `${score}%` }}></span>
      </div>

      <Link to={`/item/${candidate?.id}`} className="link match-action-link">
        View match details <ArrowRight size={14} />
      </Link>
    </div>
  );
}


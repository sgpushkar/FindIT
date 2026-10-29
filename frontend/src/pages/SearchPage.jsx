import React, { useState, useEffect, useCallback } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import { api } from '../services/api';
import ItemCard from '../components/ItemCard';
import { Search, Filter, RotateCcw } from 'lucide-react';

export default function SearchPage() {
  const [searchParams, setSearchParams] = useSearchParams();

  const [q, setQ] = useState(searchParams.get('q') || '');
  const [type, setType] = useState(searchParams.get('type') || '');
  const [category, setCategory] = useState(searchParams.get('category') || '');
  const [location, setLocation] = useState(searchParams.get('location') || '');
  const [status, setStatus] = useState(searchParams.get('status') || '');

  const [categories, setCategories] = useState([]);
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(true);

  // Load available categories
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

  // Sync state with URL params
  useEffect(() => {
    setQ(searchParams.get('q') || '');
    setType(searchParams.get('type') || '');
    setCategory(searchParams.get('category') || '');
    setLocation(searchParams.get('location') || '');
    setStatus(searchParams.get('status') || '');
  }, [searchParams]);

  // Execute search
  const performSearch = useCallback(async () => {
    setLoading(true);
    try {
      const params = {
        q: searchParams.get('q') || '',
        type: searchParams.get('type') || '',
        category: searchParams.get('category') || '',
        location: searchParams.get('location') || '',
        status: searchParams.get('status') || '',
      };
      const res = await api.items.search(params);
      if (res.success && res.data) {
        setResults(res.data);
      }
    } catch (err) {
      console.error('Search error:', err);
    } finally {
      setLoading(false);
    }
  }, [searchParams]);

  useEffect(() => {
    performSearch();
  }, [performSearch]);

  const handleSubmit = (e) => {
    e.preventDefault();
    const nextParams = {};
    if (q.trim()) nextParams.q = q.trim();
    if (type) nextParams.type = type;
    if (category) nextParams.category = category;
    if (location.trim()) nextParams.location = location.trim();
    if (status) nextParams.status = status;
    setSearchParams(nextParams);
  };

  const handleClear = () => {
    setQ('');
    setType('');
    setCategory('');
    setLocation('');
    setStatus('');
    setSearchParams({});
  };

  return (
    <section className="container section fade-in">
      <p className="eyebrow">Search &amp; Filter</p>
      <h1 className="page-title">Browse reports</h1>

      <form className="filters" onSubmit={handleSubmit} role="search">
        <label className="span-2">
          Keyword
          <input
            type="text"
            value={q}
            onChange={(e) => setQ(e.target.value)}
            placeholder="e.g. AirPods, keys, backpack..."
            maxLength={100}
          />
        </label>

        <label>
          Type
          <select value={type} onChange={(e) => setType(e.target.value)}>
            <option value="">Any</option>
            <option value="LOST">Lost</option>
            <option value="FOUND">Found</option>
          </select>
        </label>

        <label>
          Category
          <select value={category} onChange={(e) => setCategory(e.target.value)}>
            <option value="">Any</option>
            {categories.map((cat) => (
              <option key={cat} value={cat}>
                {cat}
              </option>
            ))}
          </select>
        </label>

        <label>
          Location
          <input
            type="text"
            value={location}
            onChange={(e) => setLocation(e.target.value)}
            placeholder="e.g. Library, Canteen"
            maxLength={100}
          />
        </label>

        <label>
          Status
          <select value={status} onChange={(e) => setStatus(e.target.value)}>
            <option value="">Any</option>
            <option value="ACTIVE">Active</option>
            <option value="RESOLVED">Resolved</option>
          </select>
        </label>

        <div className="filter-actions">
          <button type="submit" className="btn btn-dark">
            <Search size={15} />
            Search
          </button>
          <button type="button" onClick={handleClear} className="btn btn-ghost">
            <RotateCcw size={14} />
            Clear
          </button>
        </div>
      </form>

      <p className="result-count">
        {loading ? 'Searching reports...' : `${results.length} result${results.length === 1 ? '' : 's'}`}
      </p>

      {loading ? (
        <div className="loading-state">
          <div className="spinner"></div>
          <p>Filtering campus reports...</p>
        </div>
      ) : results.length === 0 ? (
        <div className="empty">
          <h3>No items found.</h3>
          <p>Nothing matching your search criteria exists yet.</p>
          <Link to="/report" className="btn btn-dark">
            Report an Item
          </Link>
        </div>
      ) : (
        <div className="grid">
          {results.map((item) => (
            <ItemCard key={item.id} item={item} />
          ))}
        </div>
      )}
    </section>
  );
}


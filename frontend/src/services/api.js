const API_BASE = '/api';

async function request(endpoint, options = {}) {
  const token = localStorage.getItem('findit_token');
  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
    ...options.headers,
  };

  const config = {
    ...options,
    headers,
    credentials: 'include',
  };

  try {
    const res = await fetch(`${API_BASE}${endpoint}`, config);
    const data = await res.json().catch(() => ({}));

    if (!res.ok) {
      const error = new Error(data.message || `Request failed with status ${res.status}`);
      error.status = res.status;
      error.data = data;
      throw error;
    }

    return data;
  } catch (err) {
    if (err.status) throw err;
    throw new Error(err.message || 'Network error occurred. Please check your connection.');
  }
}

export const api = {
  // Auth
  auth: {
    login: (credentials) => request('/auth/login', {
      method: 'POST',
      body: JSON.stringify(credentials),
    }),
    register: (userData) => request('/auth/register', {
      method: 'POST',
      body: JSON.stringify(userData),
    }),
    logout: () => request('/auth/logout', { method: 'POST' }),
    me: () => request('/auth/me'),
  },

  // Items
  items: {
    search: (params = {}) => {
      const searchParams = new URLSearchParams();
      if (params.q) searchParams.append('q', params.q);
      if (params.type) searchParams.append('type', params.type);
      if (params.category) searchParams.append('category', params.category);
      if (params.location) searchParams.append('location', params.location);
      if (params.status) searchParams.append('status', params.status);
      const query = searchParams.toString();
      return request(`/items${query ? `?${query}` : ''}`);
    },
    getRecent: (limit = 6) => request(`/items/recent?limit=${limit}`),
    getStats: () => request('/items/stats'),
    getById: (id) => request(`/items/${id}`),
    create: (itemData) => request('/items', {
      method: 'POST',
      body: JSON.stringify(itemData),
    }),
    resolve: (id) => request(`/items/${id}/resolve`, { method: 'POST' }),
    getMatches: (id) => request(`/items/${id}/matches`),
  },

  // Dashboard
  dashboard: {
    getMyReports: (filter = 'ALL') => request(`/dashboard/my-reports?filter=${filter}`),
  },

  // Admin
  admin: {
    getOverview: () => request('/admin/overview'),
    deleteItem: (id) => request(`/admin/items/${id}`, { method: 'DELETE' }),
  },

  // Categories
  categories: {
    getAll: () => request('/categories'),
  },
};


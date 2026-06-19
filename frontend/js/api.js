// ==================== CONFIG ====================
const API_BASE = '/api';

// ==================== TOKEN STORAGE ====================
const Auth = {
  getAccess()  { return localStorage.getItem('access_token');  },
  getRefresh() { return localStorage.getItem('refresh_token'); },
  setTokens(access, refresh) {
    localStorage.setItem('access_token',  access);
    localStorage.setItem('refresh_token', refresh);
  },
  clear() {
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('user_info');
  },
  isLoggedIn() { return !!this.getAccess(); },
  saveUser(user) { localStorage.setItem('user_info', JSON.stringify(user)); },
  getUser()    {
    try { return JSON.parse(localStorage.getItem('user_info')); } catch { return null; }
  },
  isAdmin() {
    const user = this.getUser();
    return user && user.roles && (
      Array.isArray(user.roles)
        ? user.roles.includes('ADMIN')
        : String(user.roles).includes('ADMIN')
    );
  },
};

// ==================== HTTP CLIENT ====================
const Http = {
  async _request(method, path, body = null, skipAuth = false, isFormData = false) {
    const headers = {};
    if (!isFormData) {
      headers['Content-Type'] = 'application/json';
    }
    if (!skipAuth && Auth.getAccess()) {
      headers['Authorization'] = `Bearer ${Auth.getAccess()}`;
    }
    const opts = { method, headers };
    if (body) {
      opts.body = isFormData ? body : JSON.stringify(body);
    }

    let res = await fetch(API_BASE + path, opts);

    // Token expired -> try refresh
    if (res.status === 401 && Auth.getRefresh()) {
      const refreshed = await Http._refreshTokens();
      if (refreshed) {
        headers['Authorization'] = `Bearer ${Auth.getAccess()}`;
        opts.headers = headers;
        res = await fetch(API_BASE + path, opts);
      } else {
        Auth.clear();
        window.location.href = '/pages/auth.html';
        return;
      }
    }

    // Forbidden - not admin
    if (res.status === 403) {
      showToast('Доступ запрещен', 'error');
      throw new Error('Доступ запрещен');
    }

    if (!res.ok) {
      let errMsg = `Ошибка ${res.status}`;
      try {
        const err = await res.json();
        errMsg = err.message || errMsg;
      } catch {}
      throw new Error(errMsg);
    }

    const text = await res.text();
    if (!text) return null;
    try { return JSON.parse(text); } catch { return text; }
  },

  async _refreshTokens() {
    try {
      const res = await fetch(API_BASE + '/auth/refresh', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken: Auth.getRefresh() }),
      });
      if (!res.ok) return false;
      const data = await res.json();
      Auth.setTokens(data.accessToken, data.refreshToken);
      // Update user info if returned
      if (data.userId) {
        Auth.saveUser({
          email: data.email,
          firstName: data.firstName,
          lastName: data.lastName,
          roles: data.roles,
        });
      }
      return true;
    } catch { return false; }
  },

  get(path)           { return this._request('GET',    path);        },
  post(path, body)    { return this._request('POST',   path, body);  },
  put(path, body)     { return this._request('PUT',    path, body);  },
  delete(path)        { return this._request('DELETE', path);        },
  postPublic(path, b) { return this._request('POST',   path, b, true); },
  upload(path, formData) { return this._request('POST', path, formData, false, true); },
  uploadPut(path, formData) { return this._request('PUT', path, formData, false, true); },
};

// ==================== API METHODS ====================
const API = {
  // Auth
  login(email, password)                    { return Http.postPublic('/auth/login',    { email, password }); },
  register(email, password, firstName, lastName) {
    return Http.postPublic('/auth/register', { email, password, firstName, lastName });
  },

  // Buildings
  getBuildings()                            { return Http.get('/buildings'); },
  getBuilding(id)                           { return Http.get(`/buildings/${id}`); },
  getFloorsByBuilding(buildingId)           { return Http.get(`/buildings/${buildingId}/floors`); },
  getFloorByNumber(buildingId, number)      { return Http.get(`/buildings/${buildingId}/floors/${number}`); },

  // Floors / Rooms
  getRoomsByFloor(floorId)                  { return Http.get(`/floors/${floorId}/rooms`); },
  getRoom(roomId)                           { return Http.get(`/rooms/${roomId}`); },
  getWorkplacesByRoom(roomId)               { return Http.get(`/rooms/${roomId}/workplaces`); },
  getWorkplace(workplaceId)                 { return Http.get(`/workplaces/${workplaceId}`); },

  // Map
  getFloorMap(floorId, from, to) {
    let url = `/map/floors/${floorId}`;
    const params = [];
    if (from) params.push(`from=${encodeURIComponent(from)}`);
    if (to)   params.push(`to=${encodeURIComponent(to)}`);
    if (params.length) url += '?' + params.join('&');
    return Http.get(url);
  },

  // Bookings
  createBooking(targetId, targetType, startTime, endTime) {
    return Http.post('/booking/', { targetId, targetType, startTime, endTime });
  },
  getBooking(id)                            { return Http.get(`/booking/${id}`); },
  cancelBooking(id)                         { return Http.delete(`/booking/${id}`); },
  getMyBookings()                           { return Http.get('/booking/my'); },

  // Availability
  checkRoomAvailability(roomId, startTime, endTime) {
    return Http.get(`/booking/rooms/${roomId}/availability?startTime=${encodeURIComponent(startTime)}&endTime=${encodeURIComponent(endTime)}`);
  },
  checkWorkplaceAvailability(workplaceId, startTime, endTime) {
    return Http.get(`/booking/workplaces/${workplaceId}/availability?startTime=${encodeURIComponent(startTime)}&endTime=${encodeURIComponent(endTime)}`);
  },

  // ==================== ADMIN API ====================

  // Admin - Users
  adminGetUsers()       { return Http.get('/admin/users'); },
  adminBlockUser(id)    { return Http._request('PATCH', `/admin/users/${id}/block`); },
  adminUnblockUser(id)  { return Http._request('PATCH', `/admin/users/${id}/unblock`); },

  // Admin - Bookings
  adminGetBookings()           { return Http.get('/admin/bookings'); },
  adminUpdateBookingStatus(id, status) {
    return Http._request('PATCH', `/admin/bookings/${id}/status?status=${encodeURIComponent(status)}`);
  },

  // Admin - Buildings
  adminGetBuildings()          { return Http.get('/admin/buildings'); },
  adminCreateBuilding(data)    { return Http.post('/admin/buildings', data); },
  adminUpdateBuilding(id, data) { return Http.put(`/admin/buildings/${id}`, data); },
  adminDeleteBuilding(id)      { return Http.delete(`/admin/buildings/${id}`); },

  // Admin - Floors
  adminGetFloors()             { return Http.get('/admin/floors'); },
  adminCreateFloor(formData)   { return Http.upload('/admin/floors', formData); },
  adminUpdateFloor(id, formData) { return Http.uploadPut(`/admin/floors/${id}`, formData); },
  adminDeleteFloor(id)         { return Http.delete(`/admin/floors/${id}`); },

  // Admin - Rooms
  adminGetRooms()              { return Http.get('/admin/rooms'); },
  adminCreateRoom(data)        { return Http.post('/admin/rooms', data); },
  adminUpdateRoom(id, data)    { return Http.put(`/admin/rooms/${id}`, data); },
  adminDeleteRoom(id)          { return Http.delete(`/admin/rooms/${id}`); },

  // Admin - Workplaces
  adminGetWorkplaces()         { return Http.get('/admin/workplaces'); },
  adminCreateWorkplace(data)   { return Http.post('/admin/workplaces', data); },
  adminUpdateWorkplace(id, data) { return Http.put(`/admin/workplaces/${id}`, data); },
  adminDeleteWorkplace(id)     { return Http.delete(`/admin/workplaces/${id}`); },

  // Admin: Equipment
  adminGetEquipment()          { return Http.get('/admin/equipment'); },
  adminCreateEquipment(data)   { return Http.post('/admin/equipment', data); },
  adminUpdateEquipment(id, data) { return Http.put(`/admin/equipment/${id}`, data); },
  adminDeleteEquipment(id)     { return Http.delete(`/admin/equipment/${id}`); },
};

// ==================== UI HELPERS ====================
function showToast(msg, type = '') {
  const container = document.getElementById('toast-container') || (() => {
    const el = document.createElement('div');
    el.id = 'toast-container';
    document.body.appendChild(el);
    return el;
  })();
  const t = document.createElement('div');
  t.className = `toast ${type}`;
  t.textContent = msg;
  container.appendChild(t);
  setTimeout(() => t.remove(), 3200);
}

function setLoading(el, isLoading, text = 'Загрузка…') {
  if (isLoading) {
    el._origContent = el.innerHTML;
    el.disabled = true;
    el.innerHTML = `<span class="spinner" style="width:15px;height:15px;border-width:2px;border-top-color:#fff"></span> ${text}`;
  } else {
    el.disabled = false;
    el.innerHTML = el._origContent || el.innerHTML;
  }
}

function showAlert(elId, msg, type = 'error') {
  const el = document.getElementById(elId);
  if (!el) return;
  el.className = `alert alert-${type} show`;
  el.textContent = msg;
}

function hideAlert(elId) {
  const el = document.getElementById(elId);
  if (el) el.className = 'alert';
}

function initials(firstName = '', lastName = '') {
  return ((firstName[0] || '') + (lastName[0] || '')).toUpperCase() || '?';
}

function formatDateTime(dt) {
  if (!dt) return '—';
  const d = new Date(dt);
  return d.toLocaleString('ru-RU', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' });
}

function formatDate(dt) {
  if (!dt) return '—';
  return new Date(dt).toLocaleDateString('ru-RU', { day: '2-digit', month: 'long', year: 'numeric' });
}

// ==================== NAVBAR INIT ====================
function initNav(activePage) {
  const user = Auth.getUser();
  if (!Auth.isLoggedIn()) { window.location.href = '/pages/auth.html'; return; }

  const navLinks = [
    { href: '/pages/home.html',      label: 'Главная',   key: 'home'      },
    { href: '/pages/buildings.html', label: 'Корпуса',   key: 'buildings' },
    { href: '/pages/profile.html',   label: 'Кабинет',   key: 'profile'   },
  ];

  // Add admin link for ADMIN users
  if (Auth.isAdmin()) {
    navLinks.push({ href: '/pages/admin.html', label: 'Админка', key: 'admin' });
  }

  const nav = document.getElementById('app-nav');
  if (!nav) return;

  nav.innerHTML = `
    <div class="brand">
      <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2">
        <rect x="2" y="3" width="20" height="14" rx="2"/><path d="M8 21h8M12 17v4"/>
      </svg>
      WorkSpace
    </div>
    <nav class="nav-links">
      ${navLinks.map(l => `<a href="${l.href}" class="${l.key === activePage ? 'active' : ''}">${l.label}</a>`).join('')}
    </nav>
    <div class="nav-user">
      <div class="avatar" title="${user?.firstName || ''} ${user?.lastName || ''}" id="nav-avatar">
        ${initials(user?.firstName, user?.lastName)}
      </div>
    </div>
  `;

  document.getElementById('nav-avatar')?.addEventListener('click', () => {
    window.location.href = '/pages/profile.html';
  });
}

// ==================== BOOKING MODE LABELS ====================
const BookingModeLabel = { ROOM: 'Комната', WORKPLACE: 'Рабочее место' };
const BookingStatusLabel = {
  CONFIRMED: 'Подтверждено',
  CANCELLED:  'Отменено',
  COMPLETED:  'Завершено',
};
const BookingStatusClass = {
  CONFIRMED: 'badge-confirmed',
  CANCELLED:  'badge-cancelled',
  COMPLETED:  'badge-completed',
};
const RoomTypeLabel = {
  OPEN_SPACE:    'Опенспейс',
  MEETING_ROOM:  'Переговорная',
  COWORKING:     'Коворкинг',
  LECTURE_ROOM:  'Лекционный зал',
};
const WorkplaceStatusLabel = {
  AVAILABLE:    'Доступно',
  OUT_OF_SERVICE: 'Недоступно',
  MAINTENANCE:  'Обслуживание',
};

import http from './http';

const TrackingService = {
  // Axios convierte automáticamente el objeto { page: 0, sort: '...' } en ?page=0&sort=...
  getAll: (params) => http.get('/tracking', { params }),

  getById: (id) => http.get(`/tracking/${id}`),

  create: (data) => http.post('/tracking', data),

  update: (id, data) => http.put(`/tracking/${id}`, data),

  remove: (id) => http.delete(`/tracking/${id}`),

  // ACTUALIZADO: Para soportar la lógica de Vidragem/Escolha
  // phaseIds: Array [1, 3]
  // shapeId: String "001" (Opcional)
  getCandidates: (phaseIds, referenceId = null, filterType = 'SHAPE') => {
    const params = {};

    if (phaseIds && Array.isArray(phaseIds)) {
      params.phaseIds = phaseIds.join(',');
    } else {
      params.phaseIds = phaseIds;
    }

    if (referenceId) {
      params.referenceId = referenceId; // Axios lo enviará como string. Perfecto.
      params.filterType = filterType;
    }
    return http.get('/tracking/candidates', { params });
  }
};

export default TrackingService;
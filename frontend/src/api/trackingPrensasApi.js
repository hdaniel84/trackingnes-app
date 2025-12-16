import http from './http';

const TrackingPrensasService = {
  getAll: (params) => http.get('/tracking-prensas', { params }),
  getById: (id) => http.get(`/tracking-prensas/${id}`),
  create: (data) => http.post('/tracking-prensas', data),
  update: (id, data) => http.put(`/tracking-prensas/${id}`, data),
  remove: (id) => http.delete(`/tracking-prensas/${id}`),
  searchByCodigoProduto: (codigoProduto, page = 0, size = 10, sort = 'startTime,desc') =>
    http.get('/tracking-prensas/search', { params: { codigoProduto, page, size, sort } })
};

export default TrackingPrensasService;

import http from './http'; 

const TrackingPrensasService = {
  getAll: () => http.get('/tracking-prensas'),
  getById: (id) => http.get(`/tracking-prensas/${id}`),
  create: (data) => http.post('/tracking-prensas', data),
  update: (id, data) => http.put(`/tracking-prensas/${id}`, data),
  remove: (id) => http.delete(`/tracking-prensas/${id}`)
};

export default TrackingPrensasService;

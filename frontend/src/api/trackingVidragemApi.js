import http from './http'; 

const TrackingVidragemService = {
  getAll: (params) => http.get('/tracking-vidragem', { params }),
  getById: (id) => http.get(`/tracking-vidragem/${id}`),
  create: (data) => http.post('/tracking-vidragem', data),
  update: (id, data) => http.put(`/tracking-vidragem/${id}`, data),
  remove: (id) => http.delete(`/tracking-vidragem/${id}`)
};

export default TrackingVidragemService;

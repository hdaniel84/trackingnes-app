import http from './http'; 

const TrackingVidragemService = {
  getAll: (params) => http.get('/tracking-forno-entrada', { params }),
  getById: (id) => http.get(`/tracking-forno-entrada/${id}`),
  create: (data) => http.post('/tracking-forno-entrada', data),
  update: (id, data) => http.put(`/tracking-forno-entrada/${id}`, data),
  remove: (id) => http.delete(`/tracking-forno-entrada/${id}`)
};

export default TrackingVidragemService;

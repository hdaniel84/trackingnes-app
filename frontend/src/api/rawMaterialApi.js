import http from './http'; 

const RawMaterialsService = {
  getAll: () => http.get('/raw-materials'),
  getById: (id) => http.get(`/raw-materials/${id}`)
};

export default RawMaterialsService;

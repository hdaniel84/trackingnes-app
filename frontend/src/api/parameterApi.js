import http from './http'; 

const ParameterService = {
  getAll: () => http.get('/parameters'),
  getById: (id) => http.get(`/parameters/${id}`)
};

export default ParameterService;

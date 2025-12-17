import http from './http'; 

const EquipmentService = {
  getAll: () => http.get('/equipments'),
};

export default EquipmentService;
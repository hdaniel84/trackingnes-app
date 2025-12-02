import http from './http'; 

export const getEquipments = () => http.get('/equipments');

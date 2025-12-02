import http from './http'; 

export const getShifts = () => http.get('/shifts');

import http from './http'; 

const ProductApi = {
    getAll: () => http.get('/products'),

    // ACTUALIZADO: Filtrado por prefijos (Strategy Pattern)
    // prefixes: Array ['FF', 'FS']
    getByPrefixes: (prefixes) => {
        const params = {};
        if (prefixes && Array.isArray(prefixes) && prefixes.length > 0) {
            params.prefixes = prefixes.join(',');
        }
        return http.get('/products/filter', { params });
    }
};

export default ProductApi;
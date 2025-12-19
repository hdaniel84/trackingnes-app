package com.mesacer.trackingnes.trackingnes_app.service;

import java.util.List;

import com.mesacer.trackingnes.trackingnes_app.dto.ProductDTO;
import com.mesacer.trackingnes.trackingnes_app.dto.ProductSelectDTO;

public interface ProductService {

    public List<ProductDTO> findAll();
    public List<ProductSelectDTO> findByPrefixes(List<String> prefixes);

}

package com.mesacer.trackingnes.trackingnes_app.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.mesacer.trackingnes.trackingnes_app.dto.ProductDTO;
import com.mesacer.trackingnes.trackingnes_app.mapper.ProductMapper;
import com.mesacer.trackingnes.trackingnes_app.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public List<ProductDTO> findAll() {
        return productRepository.findAll().stream().map(productMapper::toDTO).toList();
    }
}

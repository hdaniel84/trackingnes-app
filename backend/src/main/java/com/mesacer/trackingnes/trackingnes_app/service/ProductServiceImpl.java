package com.mesacer.trackingnes.trackingnes_app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mesacer.trackingnes.trackingnes_app.dto.ProductDTO;
import com.mesacer.trackingnes.trackingnes_app.dto.ProductSelectDTO;
import com.mesacer.trackingnes.trackingnes_app.mapper.ProductMapper;
import com.mesacer.trackingnes.trackingnes_app.model.Product;
import com.mesacer.trackingnes.trackingnes_app.repository.ProductRepository;
import com.mesacer.trackingnes.trackingnes_app.repository.specs.ProductSpecifications;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public List<ProductDTO> findAll() {
        return productRepository.findAll(Sort.by(Sort.Direction.ASC, "description")).stream().map(productMapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductSelectDTO> findByPrefixes(List<String> prefixes) {
        // 1. Crear la especificación
        Specification<Product> spec = ProductSpecifications.hasCodePrefixes(prefixes);

        // 2. Buscar (Ahora sí funciona findAll(spec))
        List<Product> products = productRepository.findAll(spec);

        return products.stream()
                .map(productMapper::toSelectDTO)
                .collect(Collectors.toList());
    }
}

package com.mesacer.trackingnes.trackingnes_app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.mesacer.trackingnes.trackingnes_app.dto.ProductDTO;
import com.mesacer.trackingnes.trackingnes_app.dto.ProductSelectDTO;
import com.mesacer.trackingnes.trackingnes_app.service.ProductService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // GET /api/products/filter?prefixes=FF,FS,W
    @GetMapping("/filter")
    public ResponseEntity<List<ProductSelectDTO>> getFilteredProducts(
            @RequestParam(required = false) List<String> prefixes) {

        return ResponseEntity.ok(service.findByPrefixes(prefixes));
    }

}

package com.mesacer.trackingnes.trackingnes_app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.mesacer.trackingnes.trackingnes_app.dto.ProductDTO;
import com.mesacer.trackingnes.trackingnes_app.model.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);
    
    ProductDTO toDTO(Product product);

    Product toEntity(ProductDTO dto);
}

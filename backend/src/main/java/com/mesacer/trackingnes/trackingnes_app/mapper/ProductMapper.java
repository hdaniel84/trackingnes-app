package com.mesacer.trackingnes.trackingnes_app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.mesacer.trackingnes.trackingnes_app.dto.ProductDTO;
import com.mesacer.trackingnes.trackingnes_app.model.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);
    

    @Mapping(source = "shape.id", target = "shapeId")
    @Mapping(source = "glass.id", target = "glassId")
    @Mapping(source = "decoration.id", target = "decorationId")
    ProductDTO toDTO(Product product);


    @Mapping(source = "shapeId", target = "shape.id")
    @Mapping(source = "glassId", target = "glass.id")
    @Mapping(source = "decorationId", target = "decoration.id")
    Product toEntity(ProductDTO dto);
}

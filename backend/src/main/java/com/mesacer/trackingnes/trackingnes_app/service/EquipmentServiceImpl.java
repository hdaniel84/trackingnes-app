package com.mesacer.trackingnes.trackingnes_app.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.mesacer.trackingnes.trackingnes_app.dto.EquipmentDTO;
import com.mesacer.trackingnes.trackingnes_app.mapper.EquipmentMapper;
import com.mesacer.trackingnes.trackingnes_app.repository.EquipmentRepository;

@Service
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentMapper equipmentMapper;

    public EquipmentServiceImpl(EquipmentRepository equipmentRepository, EquipmentMapper equipmentMapper) {
        this.equipmentRepository = equipmentRepository;
        this.equipmentMapper = equipmentMapper;
    }

    public List<EquipmentDTO> findAll() {
        return equipmentRepository.findAll(Sort.by(Sort.Direction.ASC, "description")).stream()
                .map(equipmentMapper::toDTO).toList();
    }
}

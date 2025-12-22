package com.mesacer.trackingnes.trackingnes_app.mapper;

import org.mapstruct.Mapper;
import com.mesacer.trackingnes.trackingnes_app.model.*;

@Mapper(componentModel = "spring")
public class EntityReferenceMapper {

    public Team mapTeam(Long id) {
        if (id == null)
            return null;
        Team t = new Team();
        t.setId(id);
        return t;
    }

    public Shift mapShift(Long id) {
        if (id == null)
            return null;
        Shift s = new Shift();
        s.setId(id);
        return s;
    }

    public Product mapProduct(Long id) {
        if (id == null)
            return null;
        Product p = new Product();
        p.setId(id);
        return p;
    }

    public Equipment mapEquipment(Long id) {
        if (id == null)
            return null;
        Equipment e = new Equipment();
        e.setId(id);
        return e;
    }

    public Phase mapPhase(Long id) {
        if (id == null)
            return null;
        Phase p = new Phase();
        p.setId(id);
        return p;
    }


    // OPCIÓN B (Avanzada/JPA): Si alguna vez necesitas la entidad real de la DB
    // @Autowired
    // private EntityManager entityManager;
    // public Product resolveProduct(Long id) {
    // return id != null ? entityManager.getReference(Product.class, id) : null;
    // }
}
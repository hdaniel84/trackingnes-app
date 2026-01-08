package com.mesacer.trackingnes.trackingnes_app.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrackingCompositionId implements Serializable {
    private Long childId; 
    private Long parentId; 
}
// src/domain/phaseRules.js

export const PHASE_RULES = {
    // 1: PRENSAS
    1: {
        productFilter: ['W'],
        allowedSourcePhases: [],
        matchReference: false,
        hideRawMaterials: false
    },
    // 3: VIDRAGEM
    3: {
        productFilter: ['FF'],  //Filtro del select product en la fase (lo que el usuario puede seleccionar)
        allowedSourcePhases: [1, 2, 3], //Permite elegir trackingSource (Fases de origem) de esas fases
        matchReference: true,
        filterType: 'PRODUCT_CODE', //Debe buscar por Shape (W) de la fase anterior
        // Opcional: ¿Ocultar materias primas?
        // Vidragem usa materias primas, así que lo dejamos en false (o undefined)
        hideRawMaterials: false
    },
    /*
    //Kerajet
    4: {
        productFilter: ['FF'],  //Filtro del select product en la fase (lo que el usuario puede seleccionar)
        allowedSourcePhases: [3],
        matchReference: true,
        hideRawMaterials: true
    },
    //Forno entrada
    5: {
        productFilter: ['FF'],  //Filtro del select product en la fase (lo que el usuario puede seleccionar)
        allowedSourcePhases: [3, 4],
        matchReference: true,
        hideRawMaterials: true
    },*/
    //Forno
    6: {
        productFilter: ['FF'],  //Filtro del select product en la fase (lo que el usuario puede seleccionar)
        allowedSourcePhases: [3],
        matchReference: true,
        hideRawMaterials: true
    },
    //Escolha
    7: {
        productFilter: ['FF'],  //['FF','FS']Filtro del select product en la fase (lo que el usuario puede seleccionar)
        allowedSourcePhases: [6],
        matchReference: true,
        hideRawMaterials: true,
        showSecondQuantiy: true
    },
    //Embalagem
    9: {
        productFilter: ['FF'],  //Filtro del select product en la fase (lo que el usuario puede seleccionar)
        allowedSourcePhases: [7],
        matchReference: true
    }
};
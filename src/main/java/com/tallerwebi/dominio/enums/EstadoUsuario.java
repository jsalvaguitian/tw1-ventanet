package com.tallerwebi.dominio.enums;
//Anotaciones para recordar los estados posibles de un usuario

public enum EstadoUsuario {
    ACTIVO, //Cliente activa su cuenta por email o el admin la aprueba el proveedor
    NO_ACTIVO, //Cliente no activó su cuenta por email
    PENDIENTE, //Proveedor esperando aprobación del admin
    RECHAZADO, //Proveedor rechazado por el admin
}

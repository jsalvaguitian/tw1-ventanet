/*INSERT INTO
    Usuario(id, email, password, rol, activo)
VALUES
    (1, 'admin@unlam.edu.ar', 'admin', 'ADMINISTRADOR', true),
    (2, 'cliente@unlam.edu.ar', 'cliente', 'CLIENTE', true),
    (3, 'proveedor@unlam.edu.ar', 'proveedor', 'PROVEEDOR', true),
    (4, 'fabricante@unlam.edu.ar', 'fabricante', 'FABRICANTE', true);*/
    -- =====================================================
-- INSERTAR ADMIN pwd: Totoro1!
-- =====================================================
INSERT INTO Usuario (
    id, nombre, apellido, username, email, password, telefono, direccion, fechaCreacion, rol, activo
) VALUES (
    1, 'Admin', 'Principal', 'admin', 'admin@email.com', '$2a$10$9rO6fX2qRUcVOYB7R7/B/uFvI6NoRp1L7wUcFHunCqTD0s9gJ5wKq', '1234567890', 'Av. Principal 1', CURRENT_DATE, 'ADMIN', true
);

INSERT INTO Admin (id) VALUES (1);


-- =====================================================
-- INSERTAR CLIENTE  PASSWORD: Totoro1!
-- =====================================================
INSERT INTO Usuario (
    id, nombre, apellido, username, email, password, telefono, direccion, fechaCreacion, rol, activo
) VALUES (
    2, 'Juan', 'Pérez', 'cliente1', 'juan.perez@email.com', '$2a$10$9rO6fX2qRUcVOYB7R7/B/uFvI6NoRp1L7wUcFHunCqTD0s9gJ5wKq', '111222333', 'Calle Falsa 123', CURRENT_DATE, 'CLIENTE', true
);

INSERT INTO Cliente (id) VALUES (2);


-- =====================================================
-- INSERTAR PROVEEDOR  PASSWORD: Totoro1!
-- =====================================================
INSERT INTO Usuario (
    id, nombre, apellido, username, email, password, telefono, direccion, fechaCreacion, rol, activo
) VALUES (
    3, 'Pedro', 'Gómez', 'proveedor1', 'pedro.gomez@email.com', '$2a$10$9rO6fX2qRUcVOYB7R7/B/uFvI6NoRp1L7wUcFHunCqTD0s9gJ5wKq', '444555666', 'Calle Empresa 45', CURRENT_DATE, 'PROVEEDOR', true
);

INSERT INTO Proveedor (
    id, razonsocial, cuit, rubro, sitioWeb, estado, documento
) VALUES (
    3, 'Gómez Servicios S.A.', '30123456789', null , 'https://www.gomezservicios.com', 'PENDIENTE', null
);

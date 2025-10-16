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



INSERT INTO
    Marca(id, nombre)
VALUES
    (1, 'Marca A'),
    (2, 'Marca B'),
    (3, 'Marca C');

INSERT INTO
    Presentacion(id, descripcion, unidadMedida, cantidad)
VALUES
    (1, 'Kilogramo', 'kg', 1.0),
    (2, 'Litro', 'litros', 0.5),
    (3, 'Unidad', 'unidades', 10.0);

INSERT INTO
    TipoProducto(id, nombre)
VALUES
    (1, 'Puerta'),
    (2, 'Ventana'),
    (3, 'Techo');

INSERT INTO Usuario (id, nombre, apellido, username, email, password, telefono, direccion, fechaCreacion, rol, activo) VALUES
(4, 'Homero', 'Simpson', 'proveedor2', 'homero.simpson@email.com', '$2a$10$9rO6fX2qRUcVOYB7R7/B/uFvI6NoRp1L7wUcFHunCqTD0s9gJ5wKq', '444555666', 'Calle Siempre Viva 145', CURRENT_DATE, 'PROVEEDOR', true),
(5, 'Pedro', 'Simpson', 'proveedor3', 'pedro.simpson@email.com', '$2a$10$9rO6fX2qRUcVOYB7R7/B/uFvI6NoRp1L7wUcFHunCqTD0s9gJ5wKq', '444555666', 'Calle Cordoba 245', CURRENT_DATE, 'PROVEEDOR', true),
(6, 'Juan', 'Fulano', 'proveedor4', 'juan.fulano@email.com', '$2a$10$9rO6fX2qRUcVOYB7R7/B/uFvI6NoRp1L7wUcFHunCqTD0s9gJ5wKq', '444555666', 'Calle Santa fe 2245', CURRENT_DATE, 'PROVEEDOR', true);    

INSERT INTO Proveedor (id, razonSocial, cuit, rubro, sitioWeb, estado, documento, ubicacion, latitud, longitud) VALUES
(4, 'Obra Uno SRL', '20123456781', 'MATERIALES_OBRA', 'http://p1.com', 'ACTIVO', '/docs/p1.pdf', 'Buenos Aires', -34.60, -58.38),
(5, 'Illumination Dos S.A.', '20876543219', 'ELECTRICIDAD_ILUMINACION', 'http://p2.com', 'ACTIVO', '/docs/p2.pdf', 'Córdoba', -31.41, -64.18),
(6, 'Proveedor Tres SRL', '20112233445', 'PINTURAS_ACABADOS', 'http://p3.com', 'ACTIVO', '/docs/p3.pdf', 'Rosario', -32.95, -60.66);


INSERT INTO Producto (id, nombre, precio, descripcion, imagenUrl, stock, cuitProveedor, tipo_producto_id, marca_id, presentacion_id, modelo, aceptaEnvio)
VALUES
(1, 'Puerta de Madera', 15000.00, 'Puerta maciza de madera', '/uploads/imagenes/puerta-madera.jpg', 20, '20123456781', 1, 1, 1, 'PMA-01', true),
(2, 'Ventana de Aluminio', 10000.00, 'Ventana corrediza de aluminio', '/uploads/imagenes/ventana-aluminio.jpg', 30, '20123456781', 2, 2, 2, 'VAL-01', true),
(3, 'Techo de chapa', 25000.00, 'Techo acanalado galvanizado', '/uploads/imagenes/techo-chapa.jpeg', 15, '20123456781', 3, 3, 3, 'TCH-01', false),
(4, 'Puerta de Madera', 14500.00, 'Puerta igual pero de otro proveedor', '/uploads/imagenes/puerta-madera.jpg', 10, '20876543219', 1, 1, 1, 'PMA-01', true),
(5, 'Ventana de Aluminio', 12000.00, 'Ventana igual de otro proveedor', '/uploads/imagenes/ventana-aluminio-2.jpg', 25, '20112233445', 2, 2, 2, 'VAL-01', true),
(6, 'Pintura blanca', 5000.00, 'Pintura para interiores', '/uploads/imagenes/pintura-blanca.jpg', 40, '20112233445', 3, 2, 2, 'PIN-BL', true),
(7, 'Puerta de PVC', 17000.00, 'Puerta plástica económica', '/uploads/imagenes/puerta-pvc.jpeg', 20, '20876543219', 1, 3, 1, 'PVP-02', true),
(8, 'Techo aislante', 30000.00, 'Techo con aislante térmico', '/uploads/imagenes/techo-aislante.jpg', 10, '20123456781', 3, 2, 3, 'TCH-A2', false),
(9, 'Ventana doble vidrio', 20000.00, 'Ventana doble vidrio templado', '/uploads/imagenes/ventana-doble-vidrio.jpg', 5, '20112233445', 2, 1, 2, 'VDV-01', true),
(10, 'Puerta blindada', 45000.00, 'Puerta de seguridad', '/uploads/imagenes/puerta-blindada.jpg', 8, '20876543219', 1, 3, 1, 'PB-02', false);

INSERT INTO Proveedor_Producto (proveedor_id, productos_id) VALUES
(4, 1), (4, 2), (4, 3), (4, 8),
(5, 4), (5, 7), (5, 10),
(6, 5), (6, 6), (6, 9);


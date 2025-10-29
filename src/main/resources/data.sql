-- ===============================================
-- TABLAS BASE (sin dependencias)
-- ===============================================

-- Marcas
INSERT INTO Marca (id, nombre) VALUES
    (1, 'Marca A'),
    (2, 'Marca B'),
    (3, 'Marca C');

-- Presentaciones
INSERT INTO Presentacion (id, descripcion, unidadMedida, cantidad) VALUES
    (1, 'Kilogramo', 'kg', 1.0),
    (2, 'Litro', 'litros', 0.5),
    (3, 'Unidad', 'unidades', 10.0);

-- Tipos de producto
INSERT INTO TipoProducto (id, nombre) VALUES
    (1, 'Puerta'),
    (2, 'Ventana'),
    (3, 'Techo');

-- Tipo de ventana
INSERT INTO TipoVentana (id, nombre) VALUES
    (1, 'Corrediza'),
    (2, 'Oscilobatiente'),
    (3, 'Paño fijo'),
    (4, 'Abatible'),
    (5, 'Plegable');

-- Ancho
INSERT INTO Ancho (id, nombre) VALUES
    (1, '0.5 m'),
    (2, '0.6 m'),
    (3, '0.7 m'),
    (4, '0.8 m'),
    (5, '0.9 m'),
    (6, '1.0 m'),
    (7, '1.2 m'),
    (8, '1.3 m'),
    (9, '1.4 m'),
    (10, '1.5 m'),
    (11, '1.6 m'),
    (12, '1.7 m'),
    (13, '1.8 m'),
    (14, '1.9 m'),
    (15, '2.0 m'),
    (16, '2.2 m'),
    (17, '2.3 m'),
    (18, '2.4 m'),
    (19, '2.5 m'),
    (20, '2.6 m'),
    (21, '2.7 m'),
    (22, '2.8 m'),
    (23, '2.9 m');   
    
-- Alto
INSERT INTO Alto (id, nombre) VALUES
    (1, '0.5 m'),
    (2, '0.6 m'),
    (3, '0.7 m'),
    (4, '0.8 m'),
    (5, '0.9 m'),
    (6, '1.0 m'),
    (7, '1.2 m'),
    (8, '1.3 m'),
    (9, '1.4 m'),
    (10, '1.5 m'),
    (11, '1.6 m'),
    (12, '1.7 m'),
    (13, '1.8 m'),
    (14, '1.9 m'),
    (15, '2.0 m'),
    (16, '2.2 m'),
    (17, '2.3 m'),
    (18, '2.4 m'),
    (19, '2.5 m'),
    (20, '2.6 m'),
    (21, '2.7 m'),
    (22, '2.8 m'),
    (23, '2.9 m');

-- Material de perfil
INSERT INTO MaterialDePerfil (id, nombre) VALUES
    (1, 'Aluminio'),
    (2, 'PVC'),
    (3, 'Madera');

-- Tipo de vidrio
INSERT INTO TipoDeVidrio (id, nombre) VALUES
    (1, 'Simple'),
    (2, 'Doble'),
    (3, 'Laminado'),
    (4, 'Templado');

-- Color
INSERT INTO Color (id, nombre) VALUES
    (1, 'Blanco'),
    (2, 'Gris'),
    (3, 'Marrón'),
    (4, 'Negro');     

-- ===============================================
-- USUARIOS BASE
-- ===============================================
INSERT INTO Usuario (
    id, nombre, apellido, username, email, password, telefono, direccion,
    fechaCreacion, rol, activo, estado
) VALUES
(1, 'Admin', 'Principal', 'admin', 'admin@email.com',
 '$2a$10$9rO6fX2qRUcVOYB7R7/B/uFvI6NoRp1L7wUcFHunCqTD0s9gJ5wKq',
 '1234567890', 'Av. Principal 1', CURRENT_DATE, 'ADMIN', true, 'ACTIVO'),

(2, 'Juan', 'Pérez', 'cliente1', 'juan.perez@email.com',
 '$2a$10$9rO6fX2qRUcVOYB7R7/B/uFvI6NoRp1L7wUcFHunCqTD0s9gJ5wKq',
 '111222333', 'Calle Falsa 123', CURRENT_DATE, 'CLIENTE', true, 'ACTIVO'),

(3, 'Pedro', 'Gómez', 'proveedor1', 'pedro.gomez@email.com',
 '$2a$10$9rO6fX2qRUcVOYB7R7/B/uFvI6NoRp1L7wUcFHunCqTD0s9gJ5wKq',
 '444555666', 'Calle Empresa 45', CURRENT_DATE, 'PROVEEDOR', true, 'ACTIVO'),

(4, 'Homero', 'Simpson', 'proveedor2', 'homero.simpson@email.com',
 '$2a$10$9rO6fX2qRUcVOYB7R7/B/uFvI6NoRp1L7wUcFHunCqTD0s9gJ5wKq',
 '444555666', 'Calle Siempre Viva 145', CURRENT_DATE, 'PROVEEDOR', true, 'ACTIVO'),

(5, 'Pedro', 'Simpson', 'proveedor3', 'pedro.simpson@email.com',
 '$2a$10$9rO6fX2qRUYB7R7/B/uFvI6NoRp1L7wUcFHunCqTD0s9gJ5wKq',
 '444555666', 'Calle Córdoba 245', CURRENT_DATE, 'PROVEEDOR', true, 'ACTIVO'),

(6, 'Juan', 'Fulano', 'proveedor4', 'juan.fulano@email.com',
 '$2a$10$9rO6fX2qRUYB7R7/B/uFvI6NoRp1L7wUcFHunCqTD0s9gJ5wKq',
 '444555666', 'Calle Santa Fe 2245', CURRENT_DATE, 'PROVEEDOR', true, 'ACTIVO');

-- ===============================================
-- SUBCLASES (JOINED)
-- ===============================================
INSERT INTO Admin (id) VALUES (1);
INSERT INTO Cliente (id) VALUES (2);

INSERT INTO Proveedor (
    id, razonSocial, cuit, rubro, sitioWeb, documento, ubicacion, latitud, longitud
) VALUES
(3, 'Gómez Servicios S.A.', '30123456789', NULL, 'https://www.gomezservicios.com', NULL, 'Buenos Aires', -34.60, -58.38),
(4, 'Simpson Proveedores', '30222222222', NULL, 'https://www.simpsonpro.com', NULL, 'Springfield', -34.61, -58.40),
(5, 'Pedro Construcciones', '30333333333', NULL, 'https://www.pedroconstruye.com', NULL, 'Córdoba', -31.42, -64.18),
(6, 'Fulano Materiales', '30444444444', NULL, 'https://www.fulano.com', NULL, 'Rosario', -32.95, -60.66);

-- ===============================================
-- PRODUCTOS (las FK deben existir antes)
-- ===============================================
INSERT INTO Producto (
    id, nombre, precio, descripcion, imagenUrl, stock, proveedor_id,
    tipo_producto_id, marca_id, presentacion_id, modelo, aceptaEnvio,
    tipo_ventana_id, ancho_id, alto_id, material_perfil_id, tipo_vidrio_id, color_id
) VALUES
-- Puerta de madera
(1, 'Puerta de Madera', 15000.00, 'Puerta maciza de madera', '/uploads/imagenes/puerta-madera.jpg', 20, 3,
 1, 1, 1, 'PMA-01', true,
 NULL, NULL, NULL, NULL, NULL, 3), -- color marrón

-- Ventana de aluminio
(2, 'Ventana de Aluminio', 10000.00, 'Ventana corrediza de aluminio', '/uploads/imagenes/ventana-aluminio.jpg', 30, 3,
 2, 2, 2, 'VAL-01', true,
 1, 10, 10, 1, 1, 1), -- corrediza, 1.5x1.5m, aluminio, vidrio simple, blanco

-- Techo de chapa
(3, 'Techo de chapa', 25000.00, 'Techo acanalado galvanizado', '/uploads/imagenes/techo-chapa.jpeg', 15, 3,
 3, 3, 3, 'TCH-01', false,
 NULL, NULL, NULL, NULL, NULL, 2), -- gris

-- Puerta económica
(4, 'Puerta económica', 14500.00, 'Puerta igual pero de otro proveedor', '/uploads/imagenes/puerta-madera.jpg', 10, 4,
 1, 1, 1, 'PMA-02', true,
 NULL, NULL, NULL, NULL, NULL, 3), -- marrón

-- Ventana de aluminio reforzada
(5, 'Ventana de Aluminio reforzada', 12000.00, 'Ventana igual de otro proveedor', '/uploads/imagenes/ventana-aluminio-2.jpg', 25, 4,
 2, 2, 2, 'VAL-02', true,
 1, 12, 12, 1, 2, 1), -- corrediza, 1.7x1.7m, aluminio, doble vidrio, blanco

-- Pintura blanca
(6, 'Pintura blanca', 5000.00, 'Pintura para interiores', '/uploads/imagenes/pintura-blanca.jpg', 40, 5,
 3, 2, 2, 'PIN-BL', true,
 NULL, NULL, NULL, NULL, NULL, 1), -- blanco

-- Puerta de PVC
(7, 'Puerta de PVC', 17000.00, 'Puerta plástica económica', '/uploads/imagenes/puerta-pvc.jpeg', 20, 5,
 1, 3, 1, 'PVP-02', true,
 NULL, NULL, NULL, 2, NULL, 1), -- material PVC, color blanco

-- Techo aislante
(8, 'Techo aislante', 30000.00, 'Techo con aislante térmico', '/uploads/imagenes/techo-aislante.jpg', 10, 6,
 3, 2, 3, 'TCH-A2', false,
 NULL, NULL, NULL, NULL, NULL, 2), -- gris

-- Ventana doble vidrio
(9, 'Ventana doble vidrio', 20000.00, 'Ventana doble vidrio templado', '/uploads/imagenes/ventana-doble-vidrio.jpg', 5, 6,
 2, 1, 2, 'VDV-01', true,
 2, 15, 15, 1, 4, 1), -- oscilobatiente, 2.0x2.0m, aluminio, vidrio templado, blanco

-- Puerta blindada
(10, 'Puerta blindada', 45000.00, 'Puerta de seguridad', '/uploads/imagenes/puerta-blindada.jpg', 8, 6,
 1, 3, 1, 'PB-02', false,
 NULL, NULL, NULL, 1, NULL, 4); -- aluminio, color negro




-- COTIZACIONES DE PRUEBA
INSERT INTO Cotizacion (id, fecha_creacion, fecha_expiracion, cliente_id, proveedor_id, monto_total, estado)
VALUES (1, CURRENT_DATE, DATE_ADD(CURRENT_DATE, INTERVAL 10 DAY), 2, 3, 95000.00, 'PENDIENTE');

INSERT INTO Cotizacion (id, fecha_creacion, fecha_expiracion, cliente_id, proveedor_id, monto_total, estado)
VALUES (2, CURRENT_DATE, DATE_ADD(CURRENT_DATE, INTERVAL 10 DAY), 2, 3, 37000.00, 'APROBADA');

INSERT INTO Cotizacion (id, fecha_creacion, fecha_expiracion, cliente_id, proveedor_id, monto_total, estado)
VALUES (3, CURRENT_DATE, DATE_ADD(CURRENT_DATE, INTERVAL 7 DAY), 2, 3, 37000.00, 'PENDIENTE');

INSERT INTO Cotizacion (id, fecha_creacion, fecha_expiracion, cliente_id, proveedor_id, monto_total, estado)
VALUES (4, CURRENT_DATE, DATE_ADD(CURRENT_DATE, INTERVAL 7 DAY), 2, 3, 52000.00, 'COMPLETADA');




-- ITEMS DE LA COTIZACIÓN 1
INSERT INTO CotizacionItem (id, cotizacion_id, producto_id, cantidad, precioUnitario)
VALUES
(1, 1, 1, 2, 15000.00),  -- Puerta de Madera x2
(2, 1, 2, 3, 10000.00),  -- Ventana de Aluminio x3
(3, 1, 6, 5, 5000.00);   -- Pintura Blanca x5

-- ITEMS DE LA COTIZACIÓN 2
INSERT INTO CotizacionItem (id, cotizacion_id, producto_id, cantidad, precioUnitario)
VALUES
(4, 2, 7, 1, 17000.00),  -- Puerta de PVC x1
(5, 2, 9, 1, 20000.00);  -- Ventana doble vidrio x1

-- ITEMS DE LA COTIZACIÓN 3
INSERT INTO CotizacionItem (id, cotizacion_id, producto_id, cantidad, precioUnitario)
VALUES
(6, 3, 7, 3, 17000.00),  -- Puerta de PVC x1
(7, 3, 9, 1, 20000.00);  -- Ventana doble vidrio x1

-- ITEMS DE LA COTIZACIÓN 4
INSERT INTO CotizacionItem (id, cotizacion_id, producto_id, cantidad, precioUnitario)
VALUES
(8, 4, 1, 2, 15000.00),  -- Puerta de Madera x2
(9, 4, 7, 3, 17000.00), 
(10, 4, 9, 1, 20000.00);  -- Ventana doble vidrio x1


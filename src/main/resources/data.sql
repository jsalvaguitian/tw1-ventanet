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
    tipo_producto_id, marca_id, presentacion_id, modelo, aceptaEnvio
) VALUES
(1, 'Puerta de Madera', 15000.00, 'Puerta maciza de madera', '/uploads/imagenes/puerta-madera.jpg', 20, 3, 1, 1, 1, 'PMA-01', true),
(2, 'Ventana de Aluminio', 10000.00, 'Ventana corrediza de aluminio', '/uploads/imagenes/ventana-aluminio.jpg', 30, 3, 2, 2, 2, 'VAL-01', true),
(3, 'Techo de chapa', 25000.00, 'Techo acanalado galvanizado', '/uploads/imagenes/techo-chapa.jpeg', 15, 3, 3, 3, 3, 'TCH-01', false),
(4, 'Puerta económica', 14500.00, 'Puerta igual pero de otro proveedor', '/uploads/imagenes/puerta-madera.jpg', 10, 4, 1, 1, 1, 'PMA-02', true),
(5, 'Ventana de Aluminio reforzada', 12000.00, 'Ventana igual de otro proveedor', '/uploads/imagenes/ventana-aluminio-2.jpg', 25, 4, 2, 2, 2, 'VAL-02', true),
(6, 'Pintura blanca', 5000.00, 'Pintura para interiores', '/uploads/imagenes/pintura-blanca.jpg', 40, 5, 3, 2, 2, 'PIN-BL', true),
(7, 'Puerta de PVC', 17000.00, 'Puerta plástica económica', '/uploads/imagenes/puerta-pvc.jpeg', 20, 5, 1, 3, 1, 'PVP-02', true),
(8, 'Techo aislante', 30000.00, 'Techo con aislante térmico', '/uploads/imagenes/techo-aislante.jpg', 10, 6, 3, 2, 3, 'TCH-A2', false),
(9, 'Ventana doble vidrio', 20000.00, 'Ventana doble vidrio templado', '/uploads/imagenes/ventana-doble-vidrio.jpg', 5, 6, 2, 1, 2, 'VDV-01', true),
(10, 'Puerta blindada', 45000.00, 'Puerta de seguridad', '/uploads/imagenes/puerta-blindada.jpg', 8, 6, 1, 3, 1, 'PB-02', false);

INSERT INTO
    Cotizacion(
        id,
        clienteId,
        nombre,
        cantidad,
        ubicacion,
        alto,
        ancho,
        materialPerfil,
        tipoVidrio,
        color,
        fechaCotizacion
    )
VALUES
    (   1,
        2,
        'Ventana Corrediza 2 hojas',
        2,
        'CABA',
        1.00,
        1.20,
        'Aluminio',
        'Simple',
        'BLANCO',
        CURRENT_DATE
    ),
    (
        2,
        2,
        'Ventana Oscilobatiente aluminio',
        1,
        'San Justo',
        0.80,
        1.00,
        'Aluminio',
        'Simple',
        'GRIS',
        CURRENT_DATE
    ),
    (
        3,
        2,
        'Paño Fijo DVH',
        3,
        'Morón',
        1.20,
        1.50,
        'Aluminio',
        'DVH',
        'NEGRO',
        CURRENT_DATE
    ),
    (
        4,
        2,
        'Ventana corrediza PVC',
        4,
        'La Plata',
        1.20,
        1.80,
        'PVC',
        'Simple',
        'BLANCO',
        CURRENT_DATE
    ),
    (
        5,
        2,
        'Ventana batiente con mosquitero',
        2,
        'Lomas de Zamora',
        0.60,
        1.00,
        'Aluminio',
        'Simple',
        'GRIS',
        CURRENT_DATE
    ),
    (
        6,
        2,
        'Ventana corrediza 3 hojas',
        1,
        'Quilmes',
        1.20,
        2.00,
        'Aluminio',
        'Simple',
        'NEGRO',
        CURRENT_DATE
    ),
    (
        7,
        2,
        'Ventana paño fijo aluminio',
        2,
        'Avellaneda',
        0.80,
        1.20,
        'Aluminio',
        'Simple',
        'BLANCO',
        CURRENT_DATE
    ),
    (
        8,
        2,
        'Ventana oscilobatiente PVC',
        1,
        'Lanús',
        0.80,
        1.00,
        'PVC',
        'Simple',
        'GRIS',
        CURRENT_DATE
    );
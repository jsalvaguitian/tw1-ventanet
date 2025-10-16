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
INSERT INTO
    Usuario (
        id,
        nombre,
        apellido,
        username,
        email,
        password,
        telefono,
        direccion,
        fechaCreacion,
        rol,
        activo
    )
VALUES
    (
        1,
        'Admin',
        'Principal',
        'admin',
        'admin@email.com',
        '$2a$10$9rO6fX2qRUcVOYB7R7/B/uFvI6NoRp1L7wUcFHunCqTD0s9gJ5wKq',
        '1234567890',
        'Av. Principal 1',
        CURRENT_DATE,
        'ADMIN',
        true
    );

INSERT INTO
    Admin (id)
VALUES
    (1);

-- =====================================================
-- INSERTAR CLIENTE  PASSWORD: Totoro1!
-- =====================================================
INSERT INTO
    Usuario (
        id,
        nombre,
        apellido,
        username,
        email,
        password,
        telefono,
        direccion,
        fechaCreacion,
        rol,
        activo
    )
VALUES
    (
        2,
        'Juan',
        'Pérez',
        'cliente1',
        'juan.perez@email.com',
        '$2a$10$9rO6fX2qRUcVOYB7R7/B/uFvI6NoRp1L7wUcFHunCqTD0s9gJ5wKq',
        '111222333',
        'Calle Falsa 123',
        CURRENT_DATE,
        'CLIENTE',
        true
    );

INSERT INTO
    Cliente (id)
VALUES
    (2);

-- =====================================================
-- INSERTAR PROVEEDOR  PASSWORD: Totoro1!
-- =====================================================
INSERT INTO
    Usuario (
        id,
        nombre,
        apellido,
        username,
        email,
        password,
        telefono,
        direccion,
        fechaCreacion,
        rol,
        activo
    )
VALUES
    (
        3,
        'Pedro',
        'Gómez',
        'proveedor1',
        'pedro.gomez@email.com',
        '$2a$10$9rO6fX2qRUcVOYB7R7/B/uFvI6NoRp1L7wUcFHunCqTD0s9gJ5wKq',
        '444555666',
        'Calle Empresa 45',
        CURRENT_DATE,
        'PROVEEDOR',
        true
    );

INSERT INTO
    Proveedor (
        id,
        razonsocial,
        cuit,
        rubro,
        sitioWeb,
        estado,
        documento
    )
VALUES
    (
        3,
        'Gómez Servicios S.A.',
        '30123456789',
        null,
        'https://www.gomezservicios.com',
        'PENDIENTE',
        null
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
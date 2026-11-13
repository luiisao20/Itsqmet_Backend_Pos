-- ============================================================================
-- SCRIPT DE CARGA INICIAL DE DATOS PARA RESTAURANTE POS (ALINEADO A ODS 8)
-- Tablas: usuarios, categorias_platos, platos, distribuidores, insumos,
--         recetas, turnos, compras_insumos, detalles_compras, clientes, mesas
-- ============================================================================

BEGIN;

-- ----------------------------------------------------------------------------
-- 0. USUARIOS (Admin, Mesero, Cocina)
-- ----------------------------------------------------------------------------
INSERT INTO usuarios (uuid, nombre, apellido, cedula, email, password, rol, activo, created_at)
VALUES 
    (
        '9960cfbb-50ec-4caa-bad3-1c0fb0b2a43e',
        'Guillermo',
        'Administrador',
        '1700000001',
        'admin@restaurant.com',
        '$2a$10$wTqK0h/4s0yJg8V7s9kFbe5tqA7zH3m4c1r4Y0j4vK.m1uE1iQ8sW',
        'ADMIN',
        true,
        NOW()
    ),
    (
        'a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d',
        'Mateo',
        'Salazar (Mesero)',
        '1723456789',
        'mesero@restaurant.com',
        '$2a$10$wTqK0h/4s0yJg8V7s9kFbe5tqA7zH3m4c1r4Y0j4vK.m1uE1iQ8sW',
        'MESERO',
        true,
        NOW()
    ),
    (
        'f7e8d9c0-b1a2-3f4e-5d6c-7b8a9f0e1d2c',
        'Chef Lucía',
        'Morales (Cocina)',
        '1734567890',
        'cocina@restaurant.com',
        '$2a$10$wTqK0h/4s0yJg8V7s9kFbe5tqA7zH3m4c1r4Y0j4vK.m1uE1iQ8sW',
        'COCINA',
        true,
        NOW()
    )
ON CONFLICT (email) DO UPDATE SET
    nombre = EXCLUDED.nombre,
    apellido = EXCLUDED.apellido,
    rol = EXCLUDED.rol,
    activo = EXCLUDED.activo;

-- ----------------------------------------------------------------------------
-- 1. MESAS DEL SALÓN
-- ----------------------------------------------------------------------------
INSERT INTO mesas (id, numero_mesa, capacidad, ubicacion, estado, created_at)
VALUES
    (1, 1, 4, 'Salón Principal', 'LIBRE', NOW()),
    (2, 2, 2, 'Salón Principal', 'LIBRE', NOW()),
    (3, 3, 6, 'Terraza Exterior', 'LIBRE', NOW()),
    (4, 4, 4, 'Terraza Exterior', 'LIBRE', NOW()),
    (5, 5, 8, 'Área VIP Familiar', 'LIBRE', NOW()),
    (6, 6, 2, 'Barra Central',   'LIBRE', NOW())
ON CONFLICT (numero_mesa) DO UPDATE SET
    capacidad = EXCLUDED.capacidad,
    ubicacion = EXCLUDED.ubicacion;

-- ----------------------------------------------------------------------------
-- 2. CATEGORÍAS DE PLATOS (areas: COCINA, BAR, CAJA)
-- ----------------------------------------------------------------------------
INSERT INTO categorias_platos (id, nombre, area_impresion, orden_visual, created_at)
VALUES
    (1, 'Entradas y Piqueos', 'COCINA', 1, NOW()),
    (2, 'Platos Fuertes',     'COCINA', 2, NOW()),
    (3, 'Pizzas y Pastas',    'COCINA', 3, NOW()),
    (4, 'Bebidas y Cócteles', 'BAR',    4, NOW()),
    (5, 'Postres',            'COCINA', 5, NOW()),
    (6, 'Cafetería',          'BAR',    6, NOW())
ON CONFLICT (id) DO UPDATE SET
    nombre = EXCLUDED.nombre,
    area_impresion = EXCLUDED.area_impresion,
    orden_visual = EXCLUDED.orden_visual;

-- ----------------------------------------------------------------------------
-- 3. PLATOS
-- ----------------------------------------------------------------------------
INSERT INTO platos (id, id_categoria, nombre, descripcion, precio_unitario, aplica_iva, disponible, url_imagen, created_at)
VALUES
    (1, 1, 'Ceviche Mixto', 'Camarón y pescado fresco marinado en limón sutil con chulpi y patacones', 8.50, true, true, 'https://images.unsplash.com/photo-1535400255456-984241443b29', NOW()),
    (2, 1, 'Tequeños con Guacamole', '6 deditos de queso mozzarella acompañados de guacamole tradicional', 5.00, true, true, 'https://images.unsplash.com/photo-1541592106381-b31e9677c0e5', NOW()),
    (3, 2, 'Lomo a la Pimienta', 'Corte de lomo fino 250g con salsa cremosa de pimienta negra y puré rústico', 14.50, true, true, 'https://images.unsplash.com/photo-1544025162-d76694265947', NOW()),
    (4, 2, 'Seco de Pollo Criollo', 'Tradicional seco de pollo cocinado a fuego lento con cerveza y naranjilla, arroz y maduro', 9.00, true, true, 'https://images.unsplash.com/photo-1598515214211-89d3c73ae83b', NOW()),
    (5, 3, 'Pizza Margarita Suprema', 'Masa artesanal, salsa pomodoro, queso mozzarella fresco y albahaca', 12.00, true, true, 'https://images.unsplash.com/photo-1574071318508-1cdbab80d002', NOW()),
    (6, 3, 'Fettuccine Alfredo', 'Pasta larga en salsa blanca cremosa con queso parmesano y pechuga a la plancha', 10.50, true, true, 'https://images.unsplash.com/photo-1645112411341-6c4fd023714a', NOW()),
    (7, 4, 'Limonada de Hierbabuena', 'Limonada frappé refrescante infusionada con hojas de hierbabuena', 3.00, true, true, 'https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd', NOW()),
    (8, 4, 'Cerveza Artesanal IPA', 'Botella 330ml de cerveza rubia artesanal lupulada', 4.50, true, true, 'https://images.unsplash.com/photo-1608270586620-248524c67de9', NOW()),
    (9, 5, 'Tiramisú Clásico', 'Bizcotelas embebidas en café espresso y licor con crema mascarpone y cacao', 4.00, true, true, 'https://images.unsplash.com/photo-1571877227200-a0d98ea607e9', NOW()),
    (10, 6, 'Café Americano Doble', 'Extracción doble de café de altura lojano servido con agua caliente', 2.25, true, true, 'https://images.unsplash.com/photo-1509042239860-f550ce710b93', NOW())
ON CONFLICT (id) DO UPDATE SET
    nombre = EXCLUDED.nombre,
    precio_unitario = EXCLUDED.precio_unitario,
    disponible = EXCLUDED.disponible;

-- ----------------------------------------------------------------------------
-- 4. DISTRIBUIDORES (Con clasificación ODS 8: es_proveedor_local)
-- ----------------------------------------------------------------------------
INSERT INTO distribuidores (id, ruc, razon_social, nombre_contacto, telefono, email, direccion, categoria_proveedor, es_proveedor_local, created_at)
VALUES
    (1, '1790016919001', 'PRONACA S.A.', 'Carlos Mendoza', '022998877', 'pedidos@pronaca.com', 'Av. Amazonas y Naciones Unidas, Quito', 'Carnes y Embutidos', false, NOW()),
    (2, '1790033221001', 'ALPINA ECUADOR S.A.', 'Mariana Torres', '022445566', 'ventas@alpina.com', 'Km 14.5 Vía Daule, Guayaquil', 'Lácteos y Quesos', false, NOW()),
    (3, '0992154879001', 'DISTRIBUIDORA MARÍTIMA DEL PACÍFICO', 'Roberto Cevallos', '042112233', 'ventas@maritimapacifico.com', 'Puerto Pesquero Artesanal, Manta', 'Mariscos y Pescados', true, NOW()),
    (4, '1791234567001', 'LA FABRIL S.A.', 'Andrea Paredes', '052678900', 'comercial@lafabril.com', 'Av. 4 de Noviembre, Manta', 'Aceites y Abarrotes', false, NOW()),
    (5, '1190045612001', 'CAFETERA LOJANA CIA. LTDA.', 'Galo Espinosa', '072571234', 'ventas@cafelojano.ec', 'Av. Cuxibamba, Loja', 'Cafetería y Granos', true, NOW()),
    (6, '1792349012001', 'HUERTO AGROECOLÓGICO ANDINO', 'Rosa Chimbolema', '0991234567', 'ventas@huertoandino.ec', 'Valle de Los Chillos, Pichincha', 'Vegetales y Hortalizas', true, NOW())
ON CONFLICT (ruc) DO UPDATE SET
    razon_social = EXCLUDED.razon_social,
    telefono = EXCLUDED.telefono,
    email = EXCLUDED.email,
    es_proveedor_local = EXCLUDED.es_proveedor_local;

-- ----------------------------------------------------------------------------
-- 5. INSUMOS (unidades: KG, LT, UNIDAD)
-- ----------------------------------------------------------------------------
INSERT INTO insumos (id, nombre, unidad_medida, stock_actual, stock_minimo, costo_promedio, created_at)
VALUES
    (1,  'Lomo Fino de Res',                'KG',     25.000,  5.000, 7.5000, NOW()),
    (2,  'Pechuga de Pollo',                'KG',     30.000,  8.000, 3.8667, NOW()),
    (3,  'Camarón Mediano Pelado',          'KG',     20.000,  5.000, 8.2000, NOW()),
    (4,  'Queso Mozzarella en Bloque',      'KG',     15.000,  3.000, 6.0000, NOW()),
    (5,  'Crema de Leche Industrial',       'LT',     12.000,  4.000, 3.2000, NOW()),
    (6,  'Pasta Fettuccine Grano Duro',     'KG',     18.000,  5.000, 2.1000, NOW()),
    (7,  'Aceite Vegetal Palma 20L',        'LT',     40.000, 10.000, 2.5000, NOW()),
    (8,  'Limón Sutil Fresco',              'KG',     25.000,  6.000, 1.2000, NOW()),
    (9,  'Café Arábica Grano Tostado',      'KG',     10.000,  2.000, 11.5000, NOW()),
    (10, 'Pan de Hamburguesa con Ajonjolí', 'UNIDAD', 60.000, 20.000, 0.4500, NOW())
ON CONFLICT (id) DO UPDATE SET
    stock_actual = EXCLUDED.stock_actual,
    costo_promedio = EXCLUDED.costo_promedio;

-- ----------------------------------------------------------------------------
-- 6. RECETAS (Vinculación Plato <-> Insumos para costo real y descuento automático)
-- ----------------------------------------------------------------------------
INSERT INTO recetas (id, id_plato, id_insumo, cantidad_requerida, created_at)
VALUES
    -- Ceviche Mixto (id: 1)
    (1,  1, 3, 0.150, NOW()), -- Camarón 150g
    (2,  1, 8, 0.100, NOW()), -- Limón 100g
    -- Tequeños con Guacamole (id: 2)
    (3,  2, 4, 0.120, NOW()), -- Mozzarella 120g
    (4,  2, 7, 0.050, NOW()), -- Aceite 50ml
    -- Lomo a la Pimienta (id: 3)
    (5,  3, 1, 0.250, NOW()), -- Lomo Fino 250g
    (6,  3, 5, 0.060, NOW()), -- Crema 60ml
    -- Seco de Pollo Criollo (id: 4)
    (7,  4, 2, 0.250, NOW()), -- Pollo 250g
    (8,  4, 7, 0.030, NOW()), -- Aceite 30ml
    -- Pizza Margarita Suprema (id: 5)
    (9,  5, 4, 0.180, NOW()), -- Mozzarella 180g
    (10, 5, 7, 0.020, NOW()), -- Aceite 20ml
    -- Fettuccine Alfredo (id: 6)
    (11, 6, 6, 0.150, NOW()), -- Pasta 150g
    (12, 6, 2, 0.150, NOW()), -- Pollo 150g
    (13, 6, 5, 0.080, NOW()), -- Crema 80ml
    (14, 6, 4, 0.040, NOW()), -- Mozzarella 40g
    -- Limonada de Hierbabuena (id: 7)
    (15, 7, 8, 0.120, NOW()), -- Limón 120g
    -- Tiramisú Clásico (id: 9)
    (16, 9, 5, 0.080, NOW()), -- Crema 80ml
    (17, 9, 9, 0.020, NOW()), -- Café 20g
    -- Café Americano Doble (id: 10)
    (18, 10, 9, 0.025, NOW()) -- Café 25g
ON CONFLICT (id) DO UPDATE SET
    cantidad_requerida = EXCLUDED.cantidad_requerida;

-- ----------------------------------------------------------------------------
-- 7. TURNOS LABORALES (Meta ODS 8.5: Trabajo Decente)
-- ----------------------------------------------------------------------------
INSERT INTO turnos (id, id_usuario, fecha_inicio, fecha_fin, total_horas, observaciones, created_at)
VALUES
    (
        1,
        'a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d', -- Mesero
        NOW() - INTERVAL '2 days' - INTERVAL '8 hours',
        NOW() - INTERVAL '2 days',
        8.00,
        'Turno completo salón tarde-noche',
        NOW() - INTERVAL '2 days'
    ),
    (
        2,
        'f7e8d9c0-b1a2-3f4e-5d6c-7b8a9f0e1d2c', -- Cocina
        NOW() - INTERVAL '2 days' - INTERVAL '7.5 hours',
        NOW() - INTERVAL '2 days',
        7.50,
        'Turno de preparación y servicio cocina',
        NOW() - INTERVAL '2 days'
    ),
    (
        3,
        'a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d', -- Mesero
        NOW() - INTERVAL '1 day' - INTERVAL '8 hours',
        NOW() - INTERVAL '1 day',
        8.00,
        'Turno normal salón',
        NOW() - INTERVAL '1 day'
    )
ON CONFLICT (id) DO NOTHING;

-- ----------------------------------------------------------------------------
-- 8. COMPRAS DE INSUMOS
-- ----------------------------------------------------------------------------
INSERT INTO compras_insumos (id, id_distribuidor, id_usuario, numero_factura_proveedor, fecha_compra, total_compra, estado, created_at)
VALUES
    (
        1,
        1, -- Pronaca (Nacional)
        '9960cfbb-50ec-4caa-bad3-1c0fb0b2a43e',
        '001-102-000045812',
        NOW() - INTERVAL '4 days',
        266.00,
        'RECIBIDA',
        NOW() - INTERVAL '4 days'
    ),
    (
        2,
        3, -- Distribuidora Marítima (Proveedor Local Artesanal)
        '9960cfbb-50ec-4caa-bad3-1c0fb0b2a43e',
        '002-010-000012904',
        NOW() - INTERVAL '3 days',
        164.00,
        'PAGADA',
        NOW() - INTERVAL '3 days'
    ),
    (
        3,
        5, -- Cafetera Lojana (Proveedor Local Artesanal)
        '9960cfbb-50ec-4caa-bad3-1c0fb0b2a43e',
        '001-002-000007812',
        NOW() - INTERVAL '1 day',
        115.00,
        'PAGADA',
        NOW() - INTERVAL '1 day'
    )
ON CONFLICT (id) DO NOTHING;

-- ----------------------------------------------------------------------------
-- 9. DETALLES DE COMPRAS
-- ----------------------------------------------------------------------------
INSERT INTO detalles_compras (id, id_compra, id_insumo, cantidad, costo_unitario, subtotal, created_at)
VALUES
    (1, 1, 1, 20.000, 7.5000, 150.00, NOW() - INTERVAL '4 days'),
    (2, 1, 2, 30.000, 3.8667, 116.00, NOW() - INTERVAL '4 days'),
    (3, 2, 3, 20.000, 8.2000, 164.00, NOW() - INTERVAL '3 days'),
    (4, 3, 9, 10.000, 11.5000, 115.00, NOW() - INTERVAL '1 day')
ON CONFLICT (id) DO NOTHING;

-- ----------------------------------------------------------------------------
-- 10. CLIENTES (tipos: CONSUMIDOR_FINAL, CEDULA, RUC, PASAPORTE)
-- ----------------------------------------------------------------------------
INSERT INTO clientes (uuid, tipo_identificacion, identificacion, razon_social, direccion, telefono, email, password, activo, created_at)
VALUES
    (gen_random_uuid()::text, 'CONSUMIDOR_FINAL', '9999999999999', 'CONSUMIDOR FINAL', 'S/N', '9999999999', 'consumidor@final.com', '$2a$10$wTqK0h/4s0yJg8V7s9kFbe5tqA7zH3m4c1r4Y0j4vK.m1uE1iQ8sW', true, NOW()),
    (gen_random_uuid()::text, 'CEDULA', '1718293041', 'Juan Fernando Pérez Andrade', 'Av. Shyris y República, Quito', '0998765432', 'juan.perez@gmail.com', '$2a$10$wTqK0h/4s0yJg8V7s9kFbe5tqA7zH3m4c1r4Y0j4vK.m1uE1iQ8sW', true, NOW()),
    (gen_random_uuid()::text, 'CEDULA', '0928374651', 'Ana María Gómez Salazar', 'Urdesa Central, Guayaquil', '0981122334', 'ana.gomez@hotmail.com', '$2a$10$wTqK0h/4s0yJg8V7s9kFbe5tqA7zH3m4c1r4Y0j4vK.m1uE1iQ8sW', true, NOW()),
    (gen_random_uuid()::text, 'RUC', '1792345678001', 'TECHSOLUTIONS S.A.S.', 'Av. 12 de Octubre y Cordero, Quito', '022554433', 'contabilidad@techsolutions.ec', '$2a$10$wTqK0h/4s0yJg8V7s9kFbe5tqA7zH3m4c1r4Y0j4vK.m1uE1iQ8sW', true, NOW()),
    (gen_random_uuid()::text, 'PASAPORTE', 'A12894567', 'Michael John Smith', 'Hotel Marriott, Orellana, Quito', '0963344556', 'm.smith@outlook.com', '$2a$10$wTqK0h/4s0yJg8V7s9kFbe5tqA7zH3m4c1r4Y0j4vK.m1uE1iQ8sW', true, NOW())
ON CONFLICT (identificacion) DO UPDATE SET
    razon_social = EXCLUDED.razon_social,
    direccion = EXCLUDED.direccion,
    telefono = EXCLUDED.telefono,
    email = EXCLUDED.email,
    password = EXCLUDED.password,
    activo = EXCLUDED.activo;

-- ----------------------------------------------------------------------------
-- 11. REAJUSTE DE SECUENCIAS IDENTITY
-- ----------------------------------------------------------------------------
SELECT setval(pg_get_serial_sequence('mesas', 'id'),              COALESCE(MAX(id), 1)) FROM mesas;
SELECT setval(pg_get_serial_sequence('categorias_platos', 'id'),  COALESCE(MAX(id), 1)) FROM categorias_platos;
SELECT setval(pg_get_serial_sequence('platos', 'id'),             COALESCE(MAX(id), 1)) FROM platos;
SELECT setval(pg_get_serial_sequence('distribuidores', 'id'),      COALESCE(MAX(id), 1)) FROM distribuidores;
SELECT setval(pg_get_serial_sequence('insumos', 'id'),             COALESCE(MAX(id), 1)) FROM insumos;
SELECT setval(pg_get_serial_sequence('recetas', 'id'),             COALESCE(MAX(id), 1)) FROM recetas;
SELECT setval(pg_get_serial_sequence('turnos', 'id'),              COALESCE(MAX(id), 1)) FROM turnos;
SELECT setval(pg_get_serial_sequence('compras_insumos', 'id'),     COALESCE(MAX(id), 1)) FROM compras_insumos;
SELECT setval(pg_get_serial_sequence('detalles_compras', 'id'),    COALESCE(MAX(id), 1)) FROM detalles_compras;

COMMIT;

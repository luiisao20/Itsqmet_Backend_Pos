-- ============================================================================
-- SCRIPT DE CARGA INICIAL DE DATOS PARA RESTAURANTE POS
-- Tablas: usuarios, categorias_platos, platos, distribuidores,
--         insumos, compras_insumos, detalles_compras, clientes
-- ============================================================================

BEGIN;

-- ----------------------------------------------------------------------------
-- 0. USUARIO ADMIN DE RESPALDO (Requerido para la FK de compras_insumos)
-- ----------------------------------------------------------------------------
INSERT INTO usuarios (uuid, nombre, apellido, cedula, email, password, rol, activo, created_at)
VALUES (
    '9960cfbb-50ec-4caa-bad3-1c0fb0b2a43e',
    'Guillermo',
    'Administrador',
    '1700000001',
    'admin@restaurant.com',
    '$2a$10$wTqK0h/4s0yJg8V7s9kFbe5tqA7zH3m4c1r4Y0j4vK.m1uE1iQ8sW',
    'ADMIN',
    true,
    NOW()
)
ON CONFLICT (email) DO NOTHING;

-- ----------------------------------------------------------------------------
-- 1. CATEGORÍAS DE PLATOS (areas: COCINA, BAR, CAJA)
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
-- 2. PLATOS
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
-- 3. DISTRIBUIDORES
-- ----------------------------------------------------------------------------
INSERT INTO distribuidores (id, ruc, razon_social, nombre_contacto, telefono, email, direccion, categoria_proveedor, created_at)
VALUES
    (1, '1790016919001', 'PRONACA S.A.', 'Carlos Mendoza', '022998877', 'pedidos@pronaca.com', 'Av. Amazonas y Naciones Unidas, Quito', 'Carnes y Embutidos', NOW()),
    (2, '1790033221001', 'ALPINA ECUADOR S.A.', 'Mariana Torres', '022445566', 'ventas@alpina.com', 'Km 14.5 Vía Daule, Guayaquil', 'Lácteos y Quesos', NOW()),
    (3, '0992154879001', 'DISTRIBUIDORA MARÍTIMA DEL PACÍFICO', 'Roberto Cevallos', '042112233', 'ventas@maritimapacifico.com', 'Puerto Pesquero, Manta', 'Mariscos y Pescados', NOW()),
    (4, '1791234567001', 'LA FABRIL S.A.', 'Andrea Paredes', '052678900', 'comercial@lafabril.com', 'Av. 4 de Noviembre, Manta', 'Aceites y Abarrotes', NOW()),
    (5, '1190045612001', 'CAFETERA LOJANA CIA. LTDA.', 'Galo Espinosa', '072571234', 'ventas@cafelojano.ec', 'Av. Cuxibamba, Loja', 'Cafetería y Granos', NOW())
ON CONFLICT (ruc) DO UPDATE SET
    razon_social = EXCLUDED.razon_social,
    telefono = EXCLUDED.telefono,
    email = EXCLUDED.email;

-- ----------------------------------------------------------------------------
-- 4. INSUMOS (unidades: KG, LT, UNIDAD)
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
-- 5. COMPRAS DE INSUMOS (estados: RECIBIDA, PENDIENTE_PAGO, PAGADA)
-- ----------------------------------------------------------------------------
INSERT INTO compras_insumos (id, id_distribuidor, id_usuario, numero_factura_proveedor, fecha_compra, total_compra, estado, created_at)
VALUES
    (
        1,
        1,
        (SELECT uuid FROM usuarios WHERE rol = 'ADMIN' LIMIT 1),
        '001-102-000045812',
        NOW() - INTERVAL '2 days',
        266.00,
        'RECIBIDA',
        NOW() - INTERVAL '2 days'
    ),
    (
        2,
        2,
        (SELECT uuid FROM usuarios WHERE rol = 'ADMIN' LIMIT 1),
        '002-050-000078123',
        NOW() - INTERVAL '1 day',
        122.00,
        'PAGADA',
        NOW() - INTERVAL '1 day'
    )
ON CONFLICT (id) DO NOTHING;

-- ----------------------------------------------------------------------------
-- 6. DETALLES DE COMPRAS
-- ----------------------------------------------------------------------------
INSERT INTO detalles_compras (id, id_compra, id_insumo, cantidad, costo_unitario, subtotal, created_at)
VALUES
    (1, 1, 1, 20.000, 7.5000, 150.00, NOW() - INTERVAL '2 days'),
    (2, 1, 2, 30.000, 3.8667, 116.00, NOW() - INTERVAL '2 days'),
    (3, 2, 4, 15.000, 6.0000,  90.00, NOW() - INTERVAL '1 day'),
    (4, 2, 5, 10.000, 3.2000,  32.00, NOW() - INTERVAL '1 day')
ON CONFLICT (id) DO NOTHING;

-- ----------------------------------------------------------------------------
-- 7. CLIENTES (tipos: CONSUMIDOR_FINAL, CEDULA, RUC, PASAPORTE)
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
-- 8. REAJUSTE DE SECUENCIAS IDENTITY (Previene colisión de IDs en el backend)
-- ----------------------------------------------------------------------------
SELECT setval(pg_get_serial_sequence('categorias_platos', 'id'), COALESCE(MAX(id), 1)) FROM categorias_platos;
SELECT setval(pg_get_serial_sequence('platos', 'id'),            COALESCE(MAX(id), 1)) FROM platos;
SELECT setval(pg_get_serial_sequence('distribuidores', 'id'),     COALESCE(MAX(id), 1)) FROM distribuidores;
SELECT setval(pg_get_serial_sequence('insumos', 'id'),            COALESCE(MAX(id), 1)) FROM insumos;
SELECT setval(pg_get_serial_sequence('compras_insumos', 'id'),    COALESCE(MAX(id), 1)) FROM compras_insumos;
SELECT setval(pg_get_serial_sequence('detalles_compras', 'id'),   COALESCE(MAX(id), 1)) FROM detalles_compras;

COMMIT;

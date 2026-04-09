-- 1. НАПОЛНЕНИЕ ТАБЛИЦЫ PRODUCERS (ПРОИЗВОДИТЕЛИ)
INSERT INTO producers (id, name, country, logo_url)
VALUES (nextval('producers_seq'), 'Intel', 'USA', 'https://example.com/logos/intel.png'),
       (nextval('producers_seq'), 'AMD', 'USA', 'https://example.com/logos/amd.png'),
       (nextval('producers_seq'), 'NVIDIA', 'USA', 'https://example.com/logos/nvidia.png'),
       (nextval('producers_seq'), 'ASUS', 'Taiwan', 'https://example.com/logos/asus.png'),
       (nextval('producers_seq'), 'Gigabyte', 'Taiwan', 'https://example.com/logos/gigabyte.png'),
       (nextval('producers_seq'), 'MSI', 'Taiwan', 'https://example.com/logos/msi.png'),
       (nextval('producers_seq'), 'Corsair', 'USA', 'https://example.com/logos/corsair.png'),
       (nextval('producers_seq'), 'Kingston', 'USA', 'https://example.com/logos/kingston.png'),
       (nextval('producers_seq'), 'Samsung', 'South Korea', 'https://example.com/logos/samsung.png'),
       (nextval('producers_seq'), 'Seasonic', 'Taiwan', 'https://example.com/logos/seasonic.png'),
       (nextval('producers_seq'), 'Noctua', 'Austria', 'https://example.com/logos/noctua.png'),
       (nextval('producers_seq'), 'Fractal Design', 'Sweden', 'https://example.com/logos/fractal.png');


-- 2. НАПОЛНЕНИЕ COMPONENT_TYPES (ТИПЫ КОМПЛЕКТУЮЩИХ)

INSERT INTO component_types (id, name, component_parent_type_id)
VALUES (nextval('component_types_seq'), 'CPU', null),
       (nextval('component_types_seq'), 'GPU', null),
       (nextval('component_types_seq'), 'Motherboard', null),
       (nextval('component_types_seq'), 'RAM', null),
       (nextval('component_types_seq'), 'Storage', null),
       (nextval('component_types_seq'), 'Power Supply', null),
       (nextval('component_types_seq'), 'Cooling', null),
       (nextval('component_types_seq'), 'Case', null);

-- Подкатегории для Storage (чтобы проверить работу внешнего ключа на самого себя)
INSERT INTO component_types (id, name, component_parent_type_id)
VALUES (nextval('component_types_seq'), 'SSD', (SELECT id FROM component_types WHERE name = 'Storage')),
       (nextval('component_types_seq'), 'HDD', (SELECT id FROM component_types WHERE name = 'Storage')),
       (nextval('component_types_seq'), 'NVMe', (SELECT id FROM component_types WHERE name = 'Storage'));

-- Подкатегории для Cooling
INSERT INTO component_types (id, name, component_parent_type_id)
VALUES (nextval('component_types_seq'), 'Air Cooler', (SELECT id FROM component_types WHERE name = 'Cooling')),
       (nextval('component_types_seq'), 'Liquid Cooler', (SELECT id FROM component_types WHERE name = 'Cooling'));

-- 3. НАПОЛНЕНИЕ COMPONENTS (КОМПЛЕКТУЮЩИЕ)

-- CPU (Процессоры)
INSERT INTO components (id, name, price, stock_quantity, component_type_id, producer_id, image_url)
VALUES (nextval('components_seq'), 'Intel Core i5-13600K', 319.99, 25,
        (SELECT id FROM component_types WHERE name = 'CPU'), (SELECT id FROM producers WHERE name = 'Intel'),
        'https://example.com/img/13600k.jpg'),
       (nextval('components_seq'), 'AMD Ryzen 7 7800X3D', 449.99, 15,
        (SELECT id FROM component_types WHERE name = 'CPU'), (SELECT id FROM producers WHERE name = 'AMD'),
        'https://example.com/img/7800x3d.jpg'),
       (nextval('components_seq'), 'Intel Core i9-14900K', 589.99, 5,
        (SELECT id FROM component_types WHERE name = 'CPU'), (SELECT id FROM producers WHERE name = 'Intel'),
        'https://example.com/img/14900k.jpg'),
       (nextval('components_seq'), 'AMD Ryzen 5 7600X', 229.99, 30, (SELECT id FROM component_types WHERE name = 'CPU'),
        (SELECT id FROM producers WHERE name = 'AMD'), 'https://example.com/img/7600x.jpg');

-- GPU (Видеокарты)
INSERT INTO components (id, name, price, stock_quantity, component_type_id, producer_id, image_url)
VALUES (nextval('components_seq'), 'ASUS TUF Gaming RTX 4070 Ti', 849.99, 10,
        (SELECT id FROM component_types WHERE name = 'GPU'), (SELECT id FROM producers WHERE name = 'ASUS'),
        'https://example.com/img/tuf4070ti.jpg'),
       (nextval('components_seq'), 'MSI RTX 4090 SUPRIM X', 1799.99, 3,
        (SELECT id FROM component_types WHERE name = 'GPU'), (SELECT id FROM producers WHERE name = 'MSI'),
        'https://example.com/img/4090suprim.jpg'),
       (nextval('components_seq'), 'Gigabyte Radeon RX 7800 XT', 529.99, 12,
        (SELECT id FROM component_types WHERE name = 'GPU'), (SELECT id FROM producers WHERE name = 'Gigabyte'),
        'https://example.com/img/7800xt.jpg'),
       (nextval('components_seq'), 'NVIDIA RTX 4060 Founders Edition', 299.99, 20,
        (SELECT id FROM component_types WHERE name = 'GPU'), (SELECT id FROM producers WHERE name = 'NVIDIA'),
        'https://example.com/img/4060fe.jpg');

-- Motherboard (Материнские платы)
INSERT INTO components (id, name, price, stock_quantity, component_type_id, producer_id, image_url)
VALUES (nextval('components_seq'), 'ASUS ROG STRIX Z790-E GAMING WIFI', 499.99, 8,
        (SELECT id FROM component_types WHERE name = 'Motherboard'), (SELECT id FROM producers WHERE name = 'ASUS'),
        'https://example.com/img/z790e.jpg'),
       (nextval('components_seq'), 'MSI B650 TOMAHAWK WIFI', 219.99, 15,
        (SELECT id FROM component_types WHERE name = 'Motherboard'), (SELECT id FROM producers WHERE name = 'MSI'),
        'https://example.com/img/b650tomahawk.jpg');

-- RAM (Оперативная память)
INSERT INTO components (id, name, price, stock_quantity, component_type_id, producer_id, image_url)
VALUES (nextval('components_seq'), 'Corsair Vengeance RGB 32GB (2x16) DDR5 6000MHz', 129.99, 40,
        (SELECT id FROM component_types WHERE name = 'RAM'), (SELECT id FROM producers WHERE name = 'Corsair'),
        'https://example.com/img/vengeance.jpg'),
       (nextval('components_seq'), 'Kingston Fury Beast 16GB (2x8) DDR5 5200MHz', 79.99, 25,
        (SELECT id FROM component_types WHERE name = 'RAM'), (SELECT id FROM producers WHERE name = 'Kingston'),
        'https://example.com/img/furybeast.jpg');

-- Storage (Накопители)
INSERT INTO components (id, name, price, stock_quantity, component_type_id, producer_id, image_url)
VALUES (nextval('components_seq'), 'Samsung 990 PRO 1TB NVMe M.2', 109.99, 50,
        (SELECT id FROM component_types WHERE name = 'NVMe'), (SELECT id FROM producers WHERE name = 'Samsung'),
        'https://example.com/img/990pro.jpg'),
       (nextval('components_seq'), 'Kingston KC3000 2TB NVMe M.2', 159.99, 20,
        (SELECT id FROM component_types WHERE name = 'NVMe'), (SELECT id FROM producers WHERE name = 'Kingston'),
        'https://example.com/img/kc3000.jpg');

-- Power Supply (Блоки питания)
INSERT INTO components (id, name, price, stock_quantity, component_type_id, producer_id, image_url)
VALUES (nextval('components_seq'), 'Seasonic FOCUS GX-850 850W 80+ Gold', 159.99, 18,
        (SELECT id FROM component_types WHERE name = 'Power Supply'),
        (SELECT id FROM producers WHERE name = 'Seasonic'), 'https://example.com/img/seasonic850.jpg'),
       (nextval('components_seq'), 'Corsair RM1000e 1000W 80+ Gold', 189.99, 12,
        (SELECT id FROM component_types WHERE name = 'Power Supply'), (SELECT id FROM producers WHERE name = 'Corsair'),
        'https://example.com/img/rm1000e.jpg');

-- Cooling (Охлаждение)
INSERT INTO components (id, name, price, stock_quantity, component_type_id, producer_id, image_url)
VALUES (nextval('components_seq'), 'Noctua NH-D15 Chromax.Black', 119.99, 10,
        (SELECT id FROM component_types WHERE name = 'Air Cooler'), (SELECT id FROM producers WHERE name = 'Noctua'),
        'https://example.com/img/nhd15.jpg');

-- Case (Корпуса)
INSERT INTO components (id, name, price, stock_quantity, component_type_id, producer_id, image_url)
VALUES (nextval('components_seq'), 'Fractal Design North Charcoal', 149.99, 7,
        (SELECT id FROM component_types WHERE name = 'Case'), (SELECT id FROM producers WHERE name = 'Fractal Design'),
        'https://example.com/img/fractalnorth.jpg');

-- 4. НАПОЛНЕНИЕ SPECIFICATION (ХАРАКТЕРИСТИКИ)
INSERT INTO specification (id, name, description)
VALUES (nextval('specification_seq'), 'Core Count', 'Количество ядер процессора'),
       (nextval('specification_seq'), 'Clock Speed', 'Тактовая частота (GHz)'),
       (nextval('specification_seq'), 'Socket', 'Сокет процессора'),
       (nextval('specification_seq'), 'Memory Type', 'Тип поддерживаемой памяти'),
       (nextval('specification_seq'), 'VRAM', 'Объем видеопамяти (GB)'),
       (nextval('specification_seq'), 'Chipset', 'Чипсет материнской платы'),
       (nextval('specification_seq'), 'Capacity', 'Емкость накопителя (GB)'),
       (nextval('specification_seq'), 'Wattage', 'Мощность (Вт)');

-- 5. НАПОЛНЕНИЕ COMPONENTS_SPECIFICATIONS (СВЯЗКА)

WITH comp AS (SELECT id, name
              FROM components),
     spec AS (SELECT id, name
              FROM specification)
INSERT
INTO components_specifications (component_id, specification_id, value)
SELECT c.id,
       s.id,
       v.val
FROM (VALUES ('Intel Core i5-13600K', 'Core Count', '14 (6P+8E)'),
             ('Intel Core i5-13600K', 'Clock Speed', '3.5 GHz (5.1 GHz Turbo)'),
             ('Intel Core i5-13600K', 'Socket', 'LGA1700'),
             ('Intel Core i5-13600K', 'Memory Type', 'DDR4/DDR5'),

             ('AMD Ryzen 7 7800X3D', 'Core Count', '8'),
             ('AMD Ryzen 7 7800X3D', 'Clock Speed', '4.2 GHz (5.0 GHz Turbo)'),
             ('AMD Ryzen 7 7800X3D', 'Socket', 'AM5'),
             ('AMD Ryzen 7 7800X3D', 'Memory Type', 'DDR5'),

             ('ASUS TUF Gaming RTX 4070 Ti', 'VRAM', '12'),
             ('MSI RTX 4090 SUPRIM X', 'VRAM', '24'),

             ('ASUS ROG STRIX Z790-E', 'Socket', 'LGA1700'),
             ('ASUS ROG STRIX Z790-E', 'Chipset', 'Intel Z790'),
             ('ASUS ROG STRIX Z790-E', 'Memory Type', 'DDR5'),

             ('MSI B650 TOMAHAWK WIFI', 'Socket', 'AM5'),
             ('MSI B650 TOMAHAWK WIFI', 'Chipset', 'AMD B650'),
             ('MSI B650 TOMAHAWK WIFI', 'Memory Type', 'DDR5'),

             ('Samsung 990 PRO 1TB', 'Capacity', '1000'),
             ('Kingston KC3000 2TB', 'Capacity', '2000'),

             ('Seasonic FOCUS GX-850', 'Wattage', '850'),
             ('Corsair RM1000e', 'Wattage', '1000')) AS v(comp_name, spec_name, val)
         JOIN comp c ON c.name LIKE v.comp_name || '%'
         JOIN spec s ON s.name = v.spec_name;

-- 6. НАПОЛНЕНИЕ BUILDS (СБОРКИ)
INSERT INTO builds (id, name, creation_date)
VALUES (nextval('builds_seq'), 'Игровой ПК 2026 (Средний уровень)', '2026-04-08 10:00:00'),
       (nextval('builds_seq'), 'Топовый 4K Гейминг', '2026-04-07 15:30:00'),
       (nextval('builds_seq'), 'Рабочая станция для монтажа', '2026-04-06 09:15:00');

-- 7. НАПОЛНЕНИЕ BUILD_PARTITIONS (СОСТАВ СБОРОК)

INSERT INTO build_partitions (id, build_id, component_id, quantity)
VALUES (nextval('build_partitions_seq'),
        (SELECT id FROM builds WHERE name = 'Игровой ПК 2026 (Средний уровень)'),
        (SELECT id FROM components WHERE name LIKE '%Ryzen 5 7600X%'), 1),

       (nextval('build_partitions_seq'),
        (SELECT id FROM builds WHERE name = 'Игровой ПК 2026 (Средний уровень)'),
        (SELECT id FROM components WHERE name LIKE '%RTX 4060%'), 1),

       (nextval('build_partitions_seq'),
        (SELECT id FROM builds WHERE name = 'Игровой ПК 2026 (Средний уровень)'),
        (SELECT id FROM components WHERE name LIKE '%B650 TOMAHAWK%'), 1),

       (nextval('build_partitions_seq'),
        (SELECT id FROM builds WHERE name = 'Игровой ПК 2026 (Средний уровень)'),
        (SELECT id FROM components WHERE name LIKE '%Kingston Fury Beast 16GB%'), 2),

       (nextval('build_partitions_seq'),
        (SELECT id FROM builds WHERE name = 'Игровой ПК 2026 (Средний уровень)'),
        (SELECT id FROM components WHERE name LIKE '%Samsung 990 PRO%'), 1),

       (nextval('build_partitions_seq'),
        (SELECT id FROM builds WHERE name = 'Игровой ПК 2026 (Средний уровень)'),
        (SELECT id FROM components WHERE name LIKE '%Seasonic FOCUS%'), 1);

INSERT INTO build_partitions (id, build_id, component_id, quantity)
VALUES (nextval('build_partitions_seq'),
        (SELECT id FROM builds WHERE name = 'Топовый 4K Гейминг'),
        (SELECT id FROM components WHERE name LIKE '%Ryzen 7 7800X3D%'), 1),

       (nextval('build_partitions_seq'),
        (SELECT id FROM builds WHERE name = 'Топовый 4K Гейминг'),
        (SELECT id FROM components WHERE name LIKE '%RTX 4090%'), 1),

       (nextval('build_partitions_seq'),
        (SELECT id FROM builds WHERE name = 'Топовый 4K Гейминг'),
        (SELECT id FROM components WHERE name LIKE '%Corsair Vengeance RGB 32GB%'), 1),

       (nextval('build_partitions_seq'),
        (SELECT id FROM builds WHERE name = 'Топовый 4K Гейминг'),
        (SELECT id FROM components WHERE name LIKE '%Corsair RM1000e%'), 1),

       (nextval('build_partitions_seq'),
        (SELECT id FROM builds WHERE name = 'Топовый 4K Гейминг'),
        (SELECT id FROM components WHERE name LIKE '%Fractal Design North%'), 1);

INSERT INTO build_partitions (id, build_id, component_id, quantity)
VALUES (nextval('build_partitions_seq'),
        (SELECT id FROM builds WHERE name = 'Рабочая станция для монтажа'),
        (SELECT id FROM components WHERE name LIKE '%Intel Core i9-14900K%'), 1),

       (nextval('build_partitions_seq'),
        (SELECT id FROM builds WHERE name = 'Рабочая станция для монтажа'),
        (SELECT id FROM components WHERE name LIKE '%ASUS TUF Gaming RTX 4070 Ti%'), 1),

       (nextval('build_partitions_seq'),
        (SELECT id FROM builds WHERE name = 'Рабочая станция для монтажа'),
        (SELECT id FROM components WHERE name LIKE '%Z790-E%'), 1),

       (nextval('build_partitions_seq'),
        (SELECT id FROM builds WHERE name = 'Рабочая станция для монтажа'),
        (SELECT id FROM components WHERE name LIKE '%Corsair Vengeance RGB 32GB%'), 2),

       (nextval('build_partitions_seq'),
        (SELECT id FROM builds WHERE name = 'Рабочая станция для монтажа'),
        (SELECT id FROM components WHERE name LIKE '%Kingston KC3000 2TB%'), 1),

       (nextval('build_partitions_seq'),
        (SELECT id FROM builds WHERE name = 'Рабочая станция для монтажа'),
        (SELECT id FROM components WHERE name LIKE '%Noctua NH-D15%'), 1);
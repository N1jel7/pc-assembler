-- =====================================================
-- ЗАПОЛНЕНИЕ ТАБЛИЦ ТЕСТОВЫМИ ДАННЫМИ
-- =====================================================

-- 1. Заполнение таблицы component_types (типы компонентов)
INSERT INTO component_types (id, name, parent_type) VALUES
                                                        (nextval('component_types_seq'), 'Процессоры', NULL),
                                                        (nextval('component_types_seq'), 'Материнские платы', NULL),
                                                        (nextval('component_types_seq'), 'Видеокарты', NULL),
                                                        (nextval('component_types_seq'), 'Оперативная память', NULL),
                                                        (nextval('component_types_seq'), 'Накопители SSD', NULL),
                                                        (nextval('component_types_seq'), 'Блоки питания', NULL),
                                                        (nextval('component_types_seq'), 'Корпуса', NULL),
                                                        (nextval('component_types_seq'), 'Системы охлаждения', NULL);

-- Подтипы процессоров
INSERT INTO component_types (id, name, parent_type) VALUES
                                                        (nextval('component_types_seq'), 'Intel Core i3', (SELECT id FROM component_types WHERE name = 'Процессоры')),
                                                        (nextval('component_types_seq'), 'Intel Core i5', (SELECT id FROM component_types WHERE name = 'Процессоры')),
                                                        (nextval('component_types_seq'), 'Intel Core i7', (SELECT id FROM component_types WHERE name = 'Процессоры')),
                                                        (nextval('component_types_seq'), 'AMD Ryzen 5', (SELECT id FROM component_types WHERE name = 'Процессоры')),
                                                        (nextval('component_types_seq'), 'AMD Ryzen 7', (SELECT id FROM component_types WHERE name = 'Процессоры'));

-- Подтипы видеокарт
INSERT INTO component_types (id, name, parent_type) VALUES
                                                        (nextval('component_types_seq'), 'NVIDIA GeForce RTX', (SELECT id FROM component_types WHERE name = 'Видеокарты')),
                                                        (nextval('component_types_seq'), 'AMD Radeon RX', (SELECT id FROM component_types WHERE name = 'Видеокарты'));

-- 2. Заполнение таблицы producers (производители)
INSERT INTO producers (id, country, logo_url, name) VALUES
                                                        (nextval('producers_seq'), 'США', 'https://example.com/logos/intel.png', 'Intel'),
                                                        (nextval('producers_seq'), 'США', 'https://example.com/logos/amd.png', 'AMD'),
                                                        (nextval('producers_seq'), 'США', 'https://example.com/logos/nvidia.png', 'NVIDIA'),
                                                        (nextval('producers_seq'), 'Тайвань', 'https://example.com/logos/asus.png', 'ASUS'),
                                                        (nextval('producers_seq'), 'Тайвань', 'https://example.com/logos/gigabyte.png', 'Gigabyte'),
                                                        (nextval('producers_seq'), 'Тайвань', 'https://example.com/logos/msi.png', 'MSI'),
                                                        (nextval('producers_seq'), 'Китай', 'https://example.com/logos/corsair.png', 'Corsair'),
                                                        (nextval('producers_seq'), 'Китай', 'https://example.com/logos/kingston.png', 'Kingston'),
                                                        (nextval('producers_seq'), 'Южная Корея', 'https://example.com/logos/samsung.png', 'Samsung'),
                                                        (nextval('producers_seq'), 'США', 'https://example.com/logos/wd.png', 'Western Digital'),
                                                        (nextval('producers_seq'), 'Германия', 'https://example.com/logos/bequiet.png', 'be quiet!'),
                                                        (nextval('producers_seq'), 'Китай', 'https://example.com/logos/deepcool.png', 'DeepCool');

-- 3. Заполнение таблицы specification_types (типы спецификаций)
INSERT INTO specification_types (id, description, name) VALUES
                                                            (nextval('specification_types_seq'), 'Тактовая частота процессора в ГГц', 'Частота'),
                                                            (nextval('specification_types_seq'), 'Количество ядер процессора', 'Количество ядер'),
                                                            (nextval('specification_types_seq'), 'Техпроцесс в нанометрах', 'Техпроцесс'),
                                                            (nextval('specification_types_seq'), 'Объем видеопамяти в ГБ', 'Объем памяти'),
                                                            (nextval('specification_types_seq'), 'Тип видеопамяти', 'Тип памяти'),
                                                            (nextval('specification_types_seq'), 'Объем оперативной памяти в ГБ', 'Объем ОЗУ'),
                                                            (nextval('specification_types_seq'), 'Тип оперативной памяти', 'Тип ОЗУ'),
                                                            (nextval('specification_types_seq'), 'Емкость накопителя в ГБ', 'Емкость'),
                                                            (nextval('specification_types_seq'), 'Скорость чтения/записи', 'Скорость'),
                                                            (nextval('specification_types_seq'), 'Мощность блока питания в Вт', 'Мощность'),
                                                            (nextval('specification_types_seq'), 'Форм-фактор', 'Форм-фактор');

-- 4. Заполнение таблицы components (компоненты)
-- Процессоры Intel
INSERT INTO components (id, price, stock_quantity, component_type_id, producer_id, image_url, name) VALUES
                                                                                                        (nextval('components_seq'), 12500.00, 45, (SELECT id FROM component_types WHERE name = 'Intel Core i5'), (SELECT id FROM producers WHERE name = 'Intel'), 'https://example.com/images/intel_i5_12400.jpg', 'Intel Core i5-12400'),
                                                                                                        (nextval('components_seq'), 18900.00, 32, (SELECT id FROM component_types WHERE name = 'Intel Core i5'), (SELECT id FROM producers WHERE name = 'Intel'), 'https://example.com/images/intel_i5_12600k.jpg', 'Intel Core i5-12600K'),
                                                                                                        (nextval('components_seq'), 28900.00, 28, (SELECT id FROM component_types WHERE name = 'Intel Core i7'), (SELECT id FROM producers WHERE name = 'Intel'), 'https://example.com/images/intel_i7_12700k.jpg', 'Intel Core i7-12700K'),
                                                                                                        (nextval('components_seq'), 8900.00, 67, (SELECT id FROM component_types WHERE name = 'Intel Core i3'), (SELECT id FROM producers WHERE name = 'Intel'), 'https://example.com/images/intel_i3_12100.jpg', 'Intel Core i3-12100');

-- Процессоры AMD
INSERT INTO components (id, price, stock_quantity, component_type_id, producer_id, image_url, name) VALUES
                                                                                                        (nextval('components_seq'), 15900.00, 38, (SELECT id FROM component_types WHERE name = 'AMD Ryzen 5'), (SELECT id FROM producers WHERE name = 'AMD'), 'https://example.com/images/amd_5600x.jpg', 'AMD Ryzen 5 5600X'),
                                                                                                        (nextval('components_seq'), 25900.00, 25, (SELECT id FROM component_types WHERE name = 'AMD Ryzen 7'), (SELECT id FROM producers WHERE name = 'AMD'), 'https://example.com/images/amd_5800x.jpg', 'AMD Ryzen 7 5800X'),
                                                                                                        (nextval('components_seq'), 34900.00, 15, (SELECT id FROM component_types WHERE name = 'AMD Ryzen 7'), (SELECT id FROM producers WHERE name = 'AMD'), 'https://example.com/images/amd_7800x3d.jpg', 'AMD Ryzen 7 7800X3D');

-- Видеокарты NVIDIA
INSERT INTO components (id, price, stock_quantity, component_type_id, producer_id, image_url, name) VALUES
                                                                                                        (nextval('components_seq'), 45900.00, 18, (SELECT id FROM component_types WHERE name = 'NVIDIA GeForce RTX'), (SELECT id FROM producers WHERE name = 'NVIDIA'), 'https://example.com/images/rtx_3060.jpg', 'NVIDIA GeForce RTX 3060'),
                                                                                                        (nextval('components_seq'), 78900.00, 12, (SELECT id FROM component_types WHERE name = 'NVIDIA GeForce RTX'), (SELECT id FROM producers WHERE name = 'NVIDIA'), 'https://example.com/images/rtx_3070.jpg', 'NVIDIA GeForce RTX 3070'),
                                                                                                        (nextval('components_seq'), 125900.00, 8, (SELECT id FROM component_types WHERE name = 'NVIDIA GeForce RTX'), (SELECT id FROM producers WHERE name = 'NVIDIA'), 'https://example.com/images/rtx_4080.jpg', 'NVIDIA GeForce RTX 4080');

-- Видеокарты ASUS
INSERT INTO components (id, price, stock_quantity, component_type_id, producer_id, image_url, name) VALUES
                                                                                                        (nextval('components_seq'), 49900.00, 15, (SELECT id FROM component_types WHERE name = 'NVIDIA GeForce RTX'), (SELECT id FROM producers WHERE name = 'ASUS'), 'https://example.com/images/asus_3060.jpg', 'ASUS GeForce RTX 3060 Dual'),
                                                                                                        (nextval('components_seq'), 85900.00, 10, (SELECT id FROM component_types WHERE name = 'NVIDIA GeForce RTX'), (SELECT id FROM producers WHERE name = 'ASUS'), 'https://example.com/images/asus_3070.jpg', 'ASUS GeForce RTX 3070 TUF');

-- Оперативная память
INSERT INTO components (id, price, stock_quantity, component_type_id, producer_id, image_url, name) VALUES
                                                                                                        (nextval('components_seq'), 5900.00, 89, (SELECT id FROM component_types WHERE name = 'Оперативная память'), (SELECT id FROM producers WHERE name = 'Kingston'), 'https://example.com/images/ram_16gb_kingston.jpg', 'Kingston Fury 16GB DDR4'),
                                                                                                        (nextval('components_seq'), 11900.00, 64, (SELECT id FROM component_types WHERE name = 'Оперативная память'), (SELECT id FROM producers WHERE name = 'Corsair'), 'https://example.com/images/ram_32gb_corsair.jpg', 'Corsair Vengeance 32GB DDR4'),
                                                                                                        (nextval('components_seq'), 14900.00, 42, (SELECT id FROM component_types WHERE name = 'Оперативная память'), (SELECT id FROM producers WHERE name = 'Samsung'), 'https://example.com/images/ram_32gb_samsung.jpg', 'Samsung 32GB DDR5');

-- Накопители SSD
INSERT INTO components (id, price, stock_quantity, component_type_id, producer_id, image_url, name) VALUES
                                                                                                        (nextval('components_seq'), 4500.00, 120, (SELECT id FROM component_types WHERE name = 'Накопители SSD'), (SELECT id FROM producers WHERE name = 'Samsung'), 'https://example.com/images/ssd_500gb.jpg', 'Samsung 980 500GB NVMe'),
                                                                                                        (nextval('components_seq'), 7900.00, 95, (SELECT id FROM component_types WHERE name = 'Накопители SSD'), (SELECT id FROM producers WHERE name = 'Samsung'), 'https://example.com/images/ssd_1tb.jpg', 'Samsung 980 1TB NVMe'),
                                                                                                        (nextval('components_seq'), 5900.00, 87, (SELECT id FROM component_types WHERE name = 'Накопители SSD'), (SELECT id FROM producers WHERE name = 'Western Digital'), 'https://example.com/images/ssd_wd_1tb.jpg', 'WD Blue SN570 1TB');

-- Материнские платы
INSERT INTO components (id, price, stock_quantity, component_type_id, producer_id, image_url, name) VALUES
                                                                                                        (nextval('components_seq'), 12500.00, 34, (SELECT id FROM component_types WHERE name = 'Материнские платы'), (SELECT id FROM producers WHERE name = 'ASUS'), 'https://example.com/images/mb_asus_b660.jpg', 'ASUS Prime B660-PLUS'),
                                                                                                        (nextval('components_seq'), 15900.00, 28, (SELECT id FROM component_types WHERE name = 'Материнские платы'), (SELECT id FROM producers WHERE name = 'MSI'), 'https://example.com/images/mb_msi_b550.jpg', 'MSI B550 TOMAHAWK'),
                                                                                                        (nextval('components_seq'), 23900.00, 19, (SELECT id FROM component_types WHERE name = 'Материнские платы'), (SELECT id FROM producers WHERE name = 'Gigabyte'), 'https://example.com/images/mb_gigabyte_z690.jpg', 'Gigabyte Z690 AORUS');

-- Блоки питания
INSERT INTO components (id, price, stock_quantity, component_type_id, producer_id, image_url, name) VALUES
                                                                                                        (nextval('components_seq'), 8900.00, 56, (SELECT id FROM component_types WHERE name = 'Блоки питания'), (SELECT id FROM producers WHERE name = 'Corsair'), 'https://example.com/images/psu_corsair_750.jpg', 'Corsair RM750x 750W'),
                                                                                                        (nextval('components_seq'), 11900.00, 43, (SELECT id FROM component_types WHERE name = 'Блоки питания'), (SELECT id FROM producers WHERE name = 'be quiet!'), 'https://example.com/images/psu_bequiet_850.jpg', 'be quiet! Straight Power 11 850W'),
                                                                                                        (nextval('components_seq'), 15900.00, 31, (SELECT id FROM component_types WHERE name = 'Блоки питания'), (SELECT id FROM producers WHERE name = 'be quiet!'), 'https://example.com/images/psu_bequiet_1000.jpg', 'be quiet! Dark Power 13 1000W');

-- Системы охлаждения
INSERT INTO components (id, price, stock_quantity, component_type_id, producer_id, image_url, name) VALUES
                                                                                                        (nextval('components_seq'), 3900.00, 78, (SELECT id FROM component_types WHERE name = 'Системы охлаждения'), (SELECT id FROM producers WHERE name = 'DeepCool'), 'https://example.com/images/cooler_deepcool.jpg', 'DeepCool AK400'),
                                                                                                        (nextval('components_seq'), 8900.00, 52, (SELECT id FROM component_types WHERE name = 'Системы охлаждения'), (SELECT id FROM producers WHERE name = 'Corsair'), 'https://example.com/images/cooler_corsair_h100.jpg', 'Corsair iCUE H100i');

-- 5. Заполнение таблицы specifications (спецификации)
-- Спецификации для процессоров
INSERT INTO specifications (id, component_id, type_id, value) VALUES
                                                                  (nextval('specifications_seq'), (SELECT id FROM components WHERE name = 'Intel Core i5-12400'), (SELECT id FROM specification_types WHERE name = 'Частота'), '2.5 ГГц (базовая), 4.4 ГГц (турбо)'),
                                                                  (nextval('specifications_seq'), (SELECT id FROM components WHERE name = 'Intel Core i5-12400'), (SELECT id FROM specification_types WHERE name = 'Количество ядер'), '6 ядер, 12 потоков'),
                                                                  (nextval('specifications_seq'), (SELECT id FROM components WHERE name = 'Intel Core i5-12400'), (SELECT id FROM specification_types WHERE name = 'Техпроцесс'), '10 нм'),

                                                                  (nextval('specifications_seq'), (SELECT id FROM components WHERE name = 'AMD Ryzen 5 5600X'), (SELECT id FROM specification_types WHERE name = 'Частота'), '3.7 ГГц (базовая), 4.6 ГГц (турбо)'),
                                                                  (nextval('specifications_seq'), (SELECT id FROM components WHERE name = 'AMD Ryzen 5 5600X'), (SELECT id FROM specification_types WHERE name = 'Количество ядер'), '6 ядер, 12 потоков'),
                                                                  (nextval('specifications_seq'), (SELECT id FROM components WHERE name = 'AMD Ryzen 5 5600X'), (SELECT id FROM specification_types WHERE name = 'Техпроцесс'), '7 нм'),

                                                                  (nextval('specifications_seq'), (SELECT id FROM components WHERE name = 'AMD Ryzen 7 7800X3D'), (SELECT id FROM specification_types WHERE name = 'Частота'), '4.2 ГГц (базовая), 5.0 ГГц (турбо)'),
                                                                  (nextval('specifications_seq'), (SELECT id FROM components WHERE name = 'AMD Ryzen 7 7800X3D'), (SELECT id FROM specification_types WHERE name = 'Количество ядер'), '8 ядер, 16 потоков');

-- Спецификации для видеокарт
INSERT INTO specifications (id, component_id, type_id, value) VALUES
                                                                  (nextval('specifications_seq'), (SELECT id FROM components WHERE name = 'NVIDIA GeForce RTX 3060'), (SELECT id FROM specification_types WHERE name = 'Объем памяти'), '12 ГБ'),
                                                                  (nextval('specifications_seq'), (SELECT id FROM components WHERE name = 'NVIDIA GeForce RTX 3060'), (SELECT id FROM specification_types WHERE name = 'Тип памяти'), 'GDDR6'),
                                                                  (nextval('specifications_seq'), (SELECT id FROM components WHERE name = 'NVIDIA GeForce RTX 3060'), (SELECT id FROM specification_types WHERE name = 'Частота'), '1.78 ГГц'),

                                                                  (nextval('specifications_seq'), (SELECT id FROM components WHERE name = 'NVIDIA GeForce RTX 4080'), (SELECT id FROM specification_types WHERE name = 'Объем памяти'), '16 ГБ'),
                                                                  (nextval('specifications_seq'), (SELECT id FROM components WHERE name = 'NVIDIA GeForce RTX 4080'), (SELECT id FROM specification_types WHERE name = 'Тип памяти'), 'GDDR6X'),
                                                                  (nextval('specifications_seq'), (SELECT id FROM components WHERE name = 'NVIDIA GeForce RTX 4080'), (SELECT id FROM specification_types WHERE name = 'Частота'), '2.51 ГГц');

-- Спецификации для ОЗУ
INSERT INTO specifications (id, component_id, type_id, value) VALUES
                                                                  (nextval('specifications_seq'), (SELECT id FROM components WHERE name = 'Kingston Fury 16GB DDR4'), (SELECT id FROM specification_types WHERE name = 'Объем ОЗУ'), '16 ГБ'),
                                                                  (nextval('specifications_seq'), (SELECT id FROM components WHERE name = 'Kingston Fury 16GB DDR4'), (SELECT id FROM specification_types WHERE name = 'Тип ОЗУ'), 'DDR4'),
                                                                  (nextval('specifications_seq'), (SELECT id FROM components WHERE name = 'Kingston Fury 16GB DDR4'), (SELECT id FROM specification_types WHERE name = 'Частота'), '3200 МГц'),

                                                                  (nextval('specifications_seq'), (SELECT id FROM components WHERE name = 'Corsair Vengeance 32GB DDR4'), (SELECT id FROM specification_types WHERE name = 'Объем ОЗУ'), '32 ГБ'),
                                                                  (nextval('specifications_seq'), (SELECT id FROM components WHERE name = 'Corsair Vengeance 32GB DDR4'), (SELECT id FROM specification_types WHERE name = 'Тип ОЗУ'), 'DDR4'),
                                                                  (nextval('specifications_seq'), (SELECT id FROM components WHERE name = 'Corsair Vengeance 32GB DDR4'), (SELECT id FROM specification_types WHERE name = 'Частота'), '3600 МГц');

-- Спецификации для SSD
INSERT INTO specifications (id, component_id, type_id, value) VALUES
                                                                  (nextval('specifications_seq'), (SELECT id FROM components WHERE name = 'Samsung 980 500GB NVMe'), (SELECT id FROM specification_types WHERE name = 'Емкость'), '500 ГБ'),
                                                                  (nextval('specifications_seq'), (SELECT id FROM components WHERE name = 'Samsung 980 500GB NVMe'), (SELECT id FROM specification_types WHERE name = 'Скорость'), 'до 3100 МБ/с'),
                                                                  (nextval('specifications_seq'), (SELECT id FROM components WHERE name = 'Samsung 980 1TB NVMe'), (SELECT id FROM specification_types WHERE name = 'Емкость'), '1 ТБ'),
                                                                  (nextval('specifications_seq'), (SELECT id FROM components WHERE name = 'Samsung 980 1TB NVMe'), (SELECT id FROM specification_types WHERE name = 'Скорость'), 'до 3500 МБ/с');

-- Спецификации для блоков питания
INSERT INTO specifications (id, component_id, type_id, value) VALUES
                                                                  (nextval('specifications_seq'), (SELECT id FROM components WHERE name = 'Corsair RM750x 750W'), (SELECT id FROM specification_types WHERE name = 'Мощность'), '750 Вт'),
                                                                  (nextval('specifications_seq'), (SELECT id FROM components WHERE name = 'Corsair RM750x 750W'), (SELECT id FROM specification_types WHERE name = 'Форм-фактор'), 'ATX'),
                                                                  (nextval('specifications_seq'), (SELECT id FROM components WHERE name = 'be quiet! Straight Power 11 850W'), (SELECT id FROM specification_types WHERE name = 'Мощность'), '850 Вт'),
                                                                  (nextval('specifications_seq'), (SELECT id FROM components WHERE name = 'be quiet! Dark Power 13 1000W'), (SELECT id FROM specification_types WHERE name = 'Мощность'), '1000 Вт');

-- 6. Заполнение таблицы components_specifications (связь компонентов и спецификаций)
INSERT INTO components_specifications (component_id, specifications_id)
SELECT c.id, s.id
FROM components c
         JOIN specifications s ON c.id = s.component_id;

-- 7. Заполнение таблицы builds (сборки)
INSERT INTO builds (id, creation_date, name) VALUES
                                                 (nextval('builds_seq'), '2024-01-15 10:30:00', 'Игровой ПК Бюджетный'),
                                                 (nextval('builds_seq'), '2024-02-20 14:45:00', 'Рабочая станция для видеомонтажа'),
                                                 (nextval('builds_seq'), '2024-03-10 09:15:00', 'Игровой ПК Премиум'),
                                                 (nextval('builds_seq'), '2024-04-05 16:20:00', 'Офисный ПК'),
                                                 (nextval('builds_seq'), '2024-05-18 11:00:00', 'Компьютер для программирования');

-- 8. Заполнение таблицы build_partitions (состав сборок)
INSERT INTO build_partitions (id, quantity, build_id, component_id) VALUES
-- Бюджетный игровой ПК
(nextval('build_partitions_seq'), 1, (SELECT id FROM builds WHERE name = 'Игровой ПК Бюджетный'), (SELECT id FROM components WHERE name = 'Intel Core i5-12400')),
(nextval('build_partitions_seq'), 1, (SELECT id FROM builds WHERE name = 'Игровой ПК Бюджетный'), (SELECT id FROM components WHERE name = 'NVIDIA GeForce RTX 3060')),
(nextval('build_partitions_seq'), 2, (SELECT id FROM builds WHERE name = 'Игровой ПК Бюджетный'), (SELECT id FROM components WHERE name = 'Kingston Fury 16GB DDR4')),
(nextval('build_partitions_seq'), 1, (SELECT id FROM builds WHERE name = 'Игровой ПК Бюджетный'), (SELECT id FROM components WHERE name = 'Samsung 980 500GB NVMe')),
(nextval('build_partitions_seq'), 1, (SELECT id FROM builds WHERE name = 'Игровой ПК Бюджетный'), (SELECT id FROM components WHERE name = 'Corsair RM750x 750W')),

-- Премиум игровой ПК
(nextval('build_partitions_seq'), 1, (SELECT id FROM builds WHERE name = 'Игровой ПК Премиум'), (SELECT id FROM components WHERE name = 'AMD Ryzen 7 7800X3D')),
(nextval('build_partitions_seq'), 1, (SELECT id FROM builds WHERE name = 'Игровой ПК Премиум'), (SELECT id FROM components WHERE name = 'NVIDIA GeForce RTX 4080')),
(nextval('build_partitions_seq'), 2, (SELECT id FROM builds WHERE name = 'Игровой ПК Премиум'), (SELECT id FROM components WHERE name = 'Corsair Vengeance 32GB DDR4')),
(nextval('build_partitions_seq'), 1, (SELECT id FROM builds WHERE name = 'Игровой ПК Премиум'), (SELECT id FROM components WHERE name = 'Samsung 980 1TB NVMe')),
(nextval('build_partitions_seq'), 1, (SELECT id FROM builds WHERE name = 'Игровой ПК Премиум'), (SELECT id FROM components WHERE name = 'be quiet! Dark Power 13 1000W')),
(nextval('build_partitions_seq'), 1, (SELECT id FROM builds WHERE name = 'Игровой ПК Премиум'), (SELECT id FROM components WHERE name = 'Gigabyte Z690 AORUS')),

-- Рабочая станция
(nextval('build_partitions_seq'), 1, (SELECT id FROM builds WHERE name = 'Рабочая станция для видеомонтажа'), (SELECT id FROM components WHERE name = 'AMD Ryzen 7 5800X')),
(nextval('build_partitions_seq'), 1, (SELECT id FROM builds WHERE name = 'Рабочая станция для видеомонтажа'), (SELECT id FROM components WHERE name = 'ASUS GeForce RTX 3070 TUF')),
(nextval('build_partitions_seq'), 4, (SELECT id FROM builds WHERE name = 'Рабочая станция для видеомонтажа'), (SELECT id FROM components WHERE name = 'Corsair Vengeance 32GB DDR4')),
(nextval('build_partitions_seq'), 2, (SELECT id FROM builds WHERE name = 'Рабочая станция для видеомонтажа'), (SELECT id FROM components WHERE name = 'Samsung 980 1TB NVMe')),
(nextval('build_partitions_seq'), 1, (SELECT id FROM builds WHERE name = 'Рабочая станция для видеомонтажа'), (SELECT id FROM components WHERE name = 'be quiet! Straight Power 11 850W')),
(nextval('build_partitions_seq'), 1, (SELECT id FROM builds WHERE name = 'Рабочая станция для видеомонтажа'), (SELECT id FROM components WHERE name = 'MSI B550 TOMAHAWK')),

-- Офисный ПК
(nextval('build_partitions_seq'), 1, (SELECT id FROM builds WHERE name = 'Офисный ПК'), (SELECT id FROM components WHERE name = 'Intel Core i3-12100')),
(nextval('build_partitions_seq'), 1, (SELECT id FROM builds WHERE name = 'Офисный ПК'), (SELECT id FROM components WHERE name = 'Kingston Fury 16GB DDR4')),
(nextval('build_partitions_seq'), 1, (SELECT id FROM builds WHERE name = 'Офисный ПК'), (SELECT id FROM components WHERE name = 'WD Blue SN570 1TB')),
(nextval('build_partitions_seq'), 1, (SELECT id FROM builds WHERE name = 'Офисный ПК'), (SELECT id FROM components WHERE name = 'ASUS Prime B660-PLUS'));

-- Проверка заполнения (вывод количества записей)
DO $$
    DECLARE
        component_types_count integer;
        producers_count integer;
        specification_types_count integer;
        components_count integer;
        specifications_count integer;
        builds_count integer;
        build_partitions_count integer;
    BEGIN
        SELECT COUNT(*) INTO component_types_count FROM component_types;
        SELECT COUNT(*) INTO producers_count FROM producers;
        SELECT COUNT(*) INTO specification_types_count FROM specification_types;
        SELECT COUNT(*) INTO components_count FROM components;
        SELECT COUNT(*) INTO specifications_count FROM specifications;
        SELECT COUNT(*) INTO builds_count FROM builds;
        SELECT COUNT(*) INTO build_partitions_count FROM build_partitions;

        RAISE NOTICE '=== СТАТИСТИКА ЗАПОЛНЕНИЯ БАЗЫ ДАННЫХ ===';
        RAISE NOTICE 'component_types: % записей', component_types_count;
        RAISE NOTICE 'producers: % записей', producers_count;
        RAISE NOTICE 'specification_types: % записей', specification_types_count;
        RAISE NOTICE 'components: % записей', components_count;
        RAISE NOTICE 'specifications: % записей', specifications_count;
        RAISE NOTICE 'builds: % записей', builds_count;
        RAISE NOTICE 'build_partitions: % записей', build_partitions_count;
        RAISE NOTICE '======================================';
    END $$;

-- Дополнительный вывод для наглядности
SELECT 'component_types' as table_name, COUNT(*) as count FROM component_types
UNION ALL
SELECT 'producers', COUNT(*) FROM producers
UNION ALL
SELECT 'specification_types', COUNT(*) FROM specification_types
UNION ALL
SELECT 'components', COUNT(*) FROM components
UNION ALL
SELECT 'specifications', COUNT(*) FROM specifications
UNION ALL
SELECT 'components_specifications', COUNT(*) FROM components_specifications
UNION ALL
SELECT 'builds', COUNT(*) FROM builds
UNION ALL
SELECT 'build_partitions', COUNT(*) FROM build_partitions;
insert into component_types (id, name, parent_type)
values (nextval('component_types_seq'), 'Процессор', null),
       (nextval('component_types_seq'), 'Материнская плата', null),
       (nextval('component_types_seq'), 'Видеокарта', null),
       (nextval('component_types_seq'), 'Оперативная память', null),
       (nextval('component_types_seq'), 'Накопитель', null),
       (nextval('component_types_seq'), 'Блок питания', null),
       (nextval('component_types_seq'), 'Охлаждение', null),
       (nextval('component_types_seq'), 'Корпус', null);

insert into component_types (id, name, parent_type)
values (nextval('component_types_seq'), 'SSD', (select id from component_types where name = 'Накопитель' limit 1)),
       (nextval('component_types_seq'), 'HDD', (select id from component_types where name = 'Накопитель' limit 1));

insert into component_types (id, name, parent_type)
values (nextval('component_types_seq'), 'Воздушное',
        (select id from component_types where name = 'Охлаждение' limit 1)),
       (nextval('component_types_seq'), 'Жидкостное',
        (select id from component_types where name = 'Охлаждение' limit 1));

-- 2. Типы спецификаций
insert into specification_types (id, name, description)
values (nextval('specification_types_seq'), 'Сокет', 'Тип разъема процессора'),
       (nextval('specification_types_seq'), 'Частота', 'Рабочая частота в ГГц'),
       (nextval('specification_types_seq'), 'Количество ядер', 'Количество физических ядер'),
       (nextval('specification_types_seq'), 'Техпроцесс', 'Технологический процесс в нм'),
       (nextval('specification_types_seq'), 'TDP', 'Тепловыделение в Ваттах'),
       (nextval('specification_types_seq'), 'Объем', 'Объем памяти в ГБ'),
       (nextval('specification_types_seq'), 'Тип памяти', 'DDR3/DDR4/DDR5'),
       (nextval('specification_types_seq'), 'Форм-фактор', 'Размер и форма устройства'),
       (nextval('specification_types_seq'), 'Интерфейс', 'Тип подключения'),
       (nextval('specification_types_seq'), 'Скорость чтения', 'MB/s'),
       (nextval('specification_types_seq'), 'Мощность', 'Мощность БП в Ваттах'),
       (nextval('specification_types_seq'), '80 Plus', 'Сертификат эффективности'),
       (nextval('specification_types_seq'), 'Объем кэша', 'Объем кэш-памяти в МБ'),
       (nextval('specification_types_seq'), 'PCIe версия', 'Версия PCI Express'),
       (nextval('specification_types_seq'), 'Поддержка RAID', 'Поддержка RAID массивов'),
       (nextval('specification_types_seq'), 'VRM фазы', 'Количество фаз питания');

-- 3. Производители
insert into producers (id, name, country)
values (nextval('producers_seq'), 'Intel', 'США'),
       (nextval('producers_seq'), 'AMD', 'США'),
       (nextval('producers_seq'), 'NVIDIA', 'США'),
       (nextval('producers_seq'), 'ASUS', 'Тайвань'),
       (nextval('producers_seq'), 'MSI', 'Тайвань'),
       (nextval('producers_seq'), 'Gigabyte', 'Тайвань'),
       (nextval('producers_seq'), 'Samsung', 'Южная Корея'),
       (nextval('producers_seq'), 'Kingston', 'США'),
       (nextval('producers_seq'), 'Corsair', 'США'),
       (nextval('producers_seq'), 'Seasonic', 'Тайвань'),
       (nextval('producers_seq'), 'Noctua', 'Австрия'),
       (nextval('producers_seq'), 'Deepcool', 'Китай'),
       (nextval('producers_seq'), 'Western Digital', 'США'),
       (nextval('producers_seq'), 'be quiet!', 'Германия');

-- 4. Компоненты
-- Процессоры
insert into components (id, name, price, stock_quantity, component_type_id, producer_id)
values (nextval('components_seq'), 'Intel Core i9-13900K', 589.99, 50,
        (select id from component_types where name = 'Процессор' limit 1),
        (select id from producers where name = 'Intel' limit 1)),
       (nextval('components_seq'), 'Intel Core i7-13700K', 409.99, 75,
        (select id from component_types where name = 'Процессор' limit 1),
        (select id from producers where name = 'Intel' limit 1)),
       (nextval('components_seq'), 'Intel Core i5-13600K', 319.99, 100,
        (select id from component_types where name = 'Процессор' limit 1),
        (select id from producers where name = 'Intel' limit 1)),
       (nextval('components_seq'), 'AMD Ryzen 9 7950X', 549.99, 60,
        (select id from component_types where name = 'Процессор' limit 1),
        (select id from producers where name = 'AMD' limit 1)),
       (nextval('components_seq'), 'AMD Ryzen 7 7800X3D', 449.99, 45,
        (select id from component_types where name = 'Процессор' limit 1),
        (select id from producers where name = 'AMD' limit 1)),
       (nextval('components_seq'), 'AMD Ryzen 5 7600X', 249.99, 85,
        (select id from component_types where name = 'Процессор' limit 1),
        (select id from producers where name = 'AMD' limit 1));

-- Материнские платы
insert into components (id, name, price, stock_quantity, component_type_id, producer_id)
values (nextval('components_seq'), 'ASUS ROG Maximus Z790 Hero', 629.99, 30,
        (select id from component_types where name = 'Материнская плата' limit 1),
        (select id from producers where name = 'ASUS' limit 1)),
       (nextval('components_seq'), 'ASUS PRIME B760-PLUS', 159.99, 80,
        (select id from component_types where name = 'Материнская плата' limit 1),
        (select id from producers where name = 'ASUS' limit 1)),
       (nextval('components_seq'), 'MSI MPG B650 Carbon WiFi', 299.99, 40,
        (select id from component_types where name = 'Материнская плата' limit 1),
        (select id from producers where name = 'MSI' limit 1)),
       (nextval('components_seq'), 'MSI PRO Z790-A WiFi', 219.99, 65,
        (select id from component_types where name = 'Материнская плата' limit 1),
        (select id from producers where name = 'MSI' limit 1)),
       (nextval('components_seq'), 'Gigabyte Z790 AORUS Master', 499.99, 35,
        (select id from component_types where name = 'Материнская плата' limit 1),
        (select id from producers where name = 'Gigabyte' limit 1)),
       (nextval('components_seq'), 'Gigabyte B650 AORUS Elite AX', 219.99, 70,
        (select id from component_types where name = 'Материнская плата' limit 1),
        (select id from producers where name = 'Gigabyte' limit 1));

-- Видеокарты
insert into components (id, name, price, stock_quantity, component_type_id, producer_id)
values (nextval('components_seq'), 'NVIDIA RTX 4090', 1599.99, 25,
        (select id from component_types where name = 'Видеокарта' limit 1),
        (select id from producers where name = 'NVIDIA' limit 1)),
       (nextval('components_seq'), 'NVIDIA RTX 4080', 1199.99, 35,
        (select id from component_types where name = 'Видеокарта' limit 1),
        (select id from producers where name = 'NVIDIA' limit 1)),
       (nextval('components_seq'), 'NVIDIA RTX 4070 Ti', 799.99, 45,
        (select id from component_types where name = 'Видеокарта' limit 1),
        (select id from producers where name = 'NVIDIA' limit 1)),
       (nextval('components_seq'), 'ASUS Radeon RX 7900 XTX', 999.99, 40,
        (select id from component_types where name = 'Видеокарта' limit 1),
        (select id from producers where name = 'ASUS' limit 1)),
       (nextval('components_seq'), 'ASUS Radeon RX 7800 XT', 499.99, 55,
        (select id from component_types where name = 'Видеокарта' limit 1),
        (select id from producers where name = 'ASUS' limit 1)),
       (nextval('components_seq'), 'MSI GeForce RTX 4060 Ti', 389.99, 90,
        (select id from component_types where name = 'Видеокарта' limit 1),
        (select id from producers where name = 'MSI' limit 1));

-- Оперативная память
insert into components (id, name, price, stock_quantity, component_type_id, producer_id)
values (nextval('components_seq'), 'Corsair Vengeance DDR5 32GB', 119.99, 120,
        (select id from component_types where name = 'Оперативная память' limit 1),
        (select id from producers where name = 'Corsair' limit 1)),
       (nextval('components_seq'), 'Corsair Vengeance DDR5 64GB', 229.99, 80,
        (select id from component_types where name = 'Оперативная память' limit 1),
        (select id from producers where name = 'Corsair' limit 1)),
       (nextval('components_seq'), 'Kingston Fury Beast DDR5 32GB', 114.99, 100,
        (select id from component_types where name = 'Оперативная память' limit 1),
        (select id from producers where name = 'Kingston' limit 1)),
       (nextval('components_seq'), 'Kingston Fury Beast DDR4 32GB', 89.99, 110,
        (select id from component_types where name = 'Оперативная память' limit 1),
        (select id from producers where name = 'Kingston' limit 1)),
       (nextval('components_seq'), 'Samsung DDR5 16GB', 59.99, 150,
        (select id from component_types where name = 'Оперативная память' limit 1),
        (select id from producers where name = 'Samsung' limit 1));

-- Накопители SSD
insert into components (id, name, price, stock_quantity, component_type_id, producer_id)
values (nextval('components_seq'), 'Samsung 990 Pro 1TB', 119.99, 200,
        (select id from component_types where name = 'SSD' limit 1),
        (select id from producers where name = 'Samsung' limit 1)),
       (nextval('components_seq'), 'Samsung 990 Pro 2TB', 189.99, 150,
        (select id from component_types where name = 'SSD' limit 1),
        (select id from producers where name = 'Samsung' limit 1)),
       (nextval('components_seq'), 'Western Digital Black SN850X 1TB', 99.99, 180,
        (select id from component_types where name = 'SSD' limit 1),
        (select id from producers where name = 'Western Digital' limit 1)),
       (nextval('components_seq'), 'Kingston KC3000 1TB', 89.99, 160,
        (select id from component_types where name = 'SSD' limit 1),
        (select id from producers where name = 'Kingston' limit 1));

-- Накопители HDD
insert into components (id, name, price, stock_quantity, component_type_id, producer_id)
values (nextval('components_seq'), 'Western Digital Black 4TB', 149.99, 100,
        (select id from component_types where name = 'HDD' limit 1),
        (select id from producers where name = 'Western Digital' limit 1)),
       (nextval('components_seq'), 'Seagate BarraCuda 2TB', 69.99, 120,
        (select id from component_types where name = 'HDD' limit 1),
        (select id from producers where name = 'Western Digital' limit 1));

-- Блоки питания
insert into components (id, name, price, stock_quantity, component_type_id, producer_id)
values (nextval('components_seq'), 'Corsair RM1000x 1000W', 189.99, 60,
        (select id from component_types where name = 'Блок питания' limit 1),
        (select id from producers where name = 'Corsair' limit 1)),
       (nextval('components_seq'), 'Corsair RM850x 850W', 149.99, 80,
        (select id from component_types where name = 'Блок питания' limit 1),
        (select id from producers where name = 'Corsair' limit 1)),
       (nextval('components_seq'), 'Seasonic Focus GX-750 750W', 129.99, 90,
        (select id from component_types where name = 'Блок питания' limit 1),
        (select id from producers where name = 'Seasonic' limit 1)),
       (nextval('components_seq'), 'be quiet! Dark Power 13 1000W', 249.99, 40,
        (select id from component_types where name = 'Блок питания' limit 1),
        (select id from producers where name = 'be quiet!' limit 1));

-- Охлаждение
insert into components (id, name, price, stock_quantity, component_type_id, producer_id)
values (nextval('components_seq'), 'Noctua NH-D15', 99.99, 70,
        (select id from component_types where name = 'Воздушное' limit 1),
        (select id from producers where name = 'Noctua' limit 1)),
       (nextval('components_seq'), 'Deepcool AK620', 64.99, 85,
        (select id from component_types where name = 'Воздушное' limit 1),
        (select id from producers where name = 'Deepcool' limit 1)),
       (nextval('components_seq'), 'Corsair iCUE H150i Elite', 199.99, 50,
        (select id from component_types where name = 'Жидкостное' limit 1),
        (select id from producers where name = 'Corsair' limit 1)),
       (nextval('components_seq'), 'Deepcool LS720', 139.99, 55,
        (select id from component_types where name = 'Жидкостное' limit 1),
        (select id from producers where name = 'Deepcool' limit 1));

-- Корпуса
insert into components (id, name, price, stock_quantity, component_type_id, producer_id)
values (nextval('components_seq'), 'Corsair 5000D Airflow', 149.99, 65,
        (select id from component_types where name = 'Корпус' limit 1),
        (select id from producers where name = 'Corsair' limit 1)),
       (nextval('components_seq'), 'Lian Li PC-O11 Dynamic', 159.99, 60,
        (select id from component_types where name = 'Корпус' limit 1),
        (select id from producers where name = 'Corsair' limit 1)),
       (nextval('components_seq'), 'Fractal Design Meshify C', 109.99, 75,
        (select id from component_types where name = 'Корпус' limit 1),
        (select id from producers where name = 'Corsair' limit 1));

-- 5. Спецификации для компонентов
-- Спецификации для Intel Core i9-13900K
insert into specifications (id, value, component_id, type_id)
values (nextval('specifications_seq'), 'LGA1700',
        (select id from components where name = 'Intel Core i9-13900K' limit 1),
        (select id from specification_types where name = 'Сокет' limit 1)),
       (nextval('specifications_seq'), '5.8', (select id from components where name = 'Intel Core i9-13900K' limit 1),
        (select id from specification_types where name = 'Частота' limit 1)),
       (nextval('specifications_seq'), '24', (select id from components where name = 'Intel Core i9-13900K' limit 1),
        (select id from specification_types where name = 'Количество ядер' limit 1)),
       (nextval('specifications_seq'), '10', (select id from components where name = 'Intel Core i9-13900K' limit 1),
        (select id from specification_types where name = 'Техпроцесс' limit 1)),
       (nextval('specifications_seq'), '125', (select id from components where name = 'Intel Core i9-13900K' limit 1),
        (select id from specification_types where name = 'TDP' limit 1)),
       (nextval('specifications_seq'), '36', (select id from components where name = 'Intel Core i9-13900K' limit 1),
        (select id from specification_types where name = 'Объем кэша' limit 1));

-- Спецификации для AMD Ryzen 7 7800X3D
insert into specifications (id, value, component_id, type_id)
values (nextval('specifications_seq'), 'AM5', (select id from components where name = 'AMD Ryzen 7 7800X3D' limit 1),
        (select id from specification_types where name = 'Сокет' limit 1)),
       (nextval('specifications_seq'), '5.0', (select id from components where name = 'AMD Ryzen 7 7800X3D' limit 1),
        (select id from specification_types where name = 'Частота' limit 1)),
       (nextval('specifications_seq'), '8', (select id from components where name = 'AMD Ryzen 7 7800X3D' limit 1),
        (select id from specification_types where name = 'Количество ядер' limit 1)),
       (nextval('specifications_seq'), '5', (select id from components where name = 'AMD Ryzen 7 7800X3D' limit 1),
        (select id from specification_types where name = 'Техпроцесс' limit 1)),
       (nextval('specifications_seq'), '120', (select id from components where name = 'AMD Ryzen 7 7800X3D' limit 1),
        (select id from specification_types where name = 'TDP' limit 1)),
       (nextval('specifications_seq'), '96', (select id from components where name = 'AMD Ryzen 7 7800X3D' limit 1),
        (select id from specification_types where name = 'Объем кэша' limit 1));

-- Спецификации для ASUS ROG Maximus Z790 Hero
insert into specifications (id, value, component_id, type_id)
values (nextval('specifications_seq'), 'LGA1700',
        (select id from components where name = 'ASUS ROG Maximus Z790 Hero' limit 1),
        (select id from specification_types where name = 'Сокет' limit 1)),
       (nextval('specifications_seq'), 'DDR5',
        (select id from components where name = 'ASUS ROG Maximus Z790 Hero' limit 1),
        (select id from specification_types where name = 'Тип памяти' limit 1)),
       (nextval('specifications_seq'), 'ATX',
        (select id from components where name = 'ASUS ROG Maximus Z790 Hero' limit 1),
        (select id from specification_types where name = 'Форм-фактор' limit 1)),
       (nextval('specifications_seq'), '5.0',
        (select id from components where name = 'ASUS ROG Maximus Z790 Hero' limit 1),
        (select id from specification_types where name = 'PCIe версия' limit 1)),
       (nextval('specifications_seq'), '20+1',
        (select id from components where name = 'ASUS ROG Maximus Z790 Hero' limit 1),
        (select id from specification_types where name = 'VRM фазы' limit 1));

-- Спецификации для NVIDIA RTX 4090
insert into specifications (id, value, component_id, type_id)
values (nextval('specifications_seq'), '24', (select id from components where name = 'NVIDIA RTX 4090' limit 1),
        (select id from specification_types where name = 'Объем' limit 1)),
       (nextval('specifications_seq'), '4.0', (select id from components where name = 'NVIDIA RTX 4090' limit 1),
        (select id from specification_types where name = 'PCIe версия' limit 1)),
       (nextval('specifications_seq'), '450', (select id from components where name = 'NVIDIA RTX 4090' limit 1),
        (select id from specification_types where name = 'TDP' limit 1));

-- Спецификации для Corsair Vengeance DDR5 32GB
insert into specifications (id, value, component_id, type_id)
values (nextval('specifications_seq'), '32',
        (select id from components where name = 'Corsair Vengeance DDR5 32GB' limit 1),
        (select id from specification_types where name = 'Объем' limit 1)),
       (nextval('specifications_seq'), 'DDR5',
        (select id from components where name = 'Corsair Vengeance DDR5 32GB' limit 1),
        (select id from specification_types where name = 'Тип памяти' limit 1)),
       (nextval('specifications_seq'), '5.2',
        (select id from components where name = 'Corsair Vengeance DDR5 32GB' limit 1),
        (select id from specification_types where name = 'Частота' limit 1));

-- Спецификации для Samsung 990 Pro 1TB
insert into specifications (id, value, component_id, type_id)
values (nextval('specifications_seq'), '1000', (select id from components where name = 'Samsung 990 Pro 1TB' limit 1),
        (select id from specification_types where name = 'Объем' limit 1)),
       (nextval('specifications_seq'), 'NVMe PCIe 4.0',
        (select id from components where name = 'Samsung 990 Pro 1TB' limit 1),
        (select id from specification_types where name = 'Интерфейс' limit 1)),
       (nextval('specifications_seq'), '7450', (select id from components where name = 'Samsung 990 Pro 1TB' limit 1),
        (select id from specification_types where name = 'Скорость чтения' limit 1));

-- Спецификации для Corsair RM1000x
insert into specifications (id, value, component_id, type_id)
values (nextval('specifications_seq'), '1000', (select id from components where name = 'Corsair RM1000x 1000W' limit 1),
        (select id from specification_types where name = 'Мощность' limit 1)),
       (nextval('specifications_seq'), 'Gold', (select id from components where name = 'Corsair RM1000x 1000W' limit 1),
        (select id from specification_types where name = '80 Plus' limit 1)),
       (nextval('specifications_seq'), 'ATX', (select id from components where name = 'Corsair RM1000x 1000W' limit 1),
        (select id from specification_types where name = 'Форм-фактор' limit 1));

-- 6. Сборки (builds)
insert into builds (id, name, creation_date)
values (nextval('builds_seq'), 'Игровой ПК High-End', CURRENT_TIMESTAMP),
       (nextval('builds_seq'), 'Рабочая станция для видеомонтажа', CURRENT_TIMESTAMP),
       (nextval('builds_seq'), 'Бюджетный игровой ПК', CURRENT_TIMESTAMP),
       (nextval('builds_seq'), 'Мощный ПК для стриминга', CURRENT_TIMESTAMP);

-- 7. Связи компонентов в сборках (build_partitions)
-- Игровой ПК High-End
insert into build_partitions (id, quantity, build_id, component_id)
values (nextval('build_partitions_seq'), 1, (select id from builds where name = 'Игровой ПК High-End' limit 1),
        (select id from components where name = 'Intel Core i9-13900K' limit 1)),
       (nextval('build_partitions_seq'), 1, (select id from builds where name = 'Игровой ПК High-End' limit 1),
        (select id from components where name = 'ASUS ROG Maximus Z790 Hero' limit 1)),
       (nextval('build_partitions_seq'), 1, (select id from builds where name = 'Игровой ПК High-End' limit 1),
        (select id from components where name = 'NVIDIA RTX 4090' limit 1)),
       (nextval('build_partitions_seq'), 2, (select id from builds where name = 'Игровой ПК High-End' limit 1),
        (select id from components where name = 'Corsair Vengeance DDR5 32GB' limit 1)),
       (nextval('build_partitions_seq'), 2, (select id from builds where name = 'Игровой ПК High-End' limit 1),
        (select id from components where name = 'Samsung 990 Pro 1TB' limit 1)),
       (nextval('build_partitions_seq'), 1, (select id from builds where name = 'Игровой ПК High-End' limit 1),
        (select id from components where name = 'Corsair RM1000x 1000W' limit 1)),
       (nextval('build_partitions_seq'), 1, (select id from builds where name = 'Игровой ПК High-End' limit 1),
        (select id from components where name = 'Corsair iCUE H150i Elite' limit 1)),
       (nextval('build_partitions_seq'), 1, (select id from builds where name = 'Игровой ПК High-End' limit 1),
        (select id from components where name = 'Corsair 5000D Airflow' limit 1));

-- Рабочая станция для видеомонтажа
insert into build_partitions (id, quantity, build_id, component_id)
values (nextval('build_partitions_seq'), 1,
        (select id from builds where name = 'Рабочая станция для видеомонтажа' limit 1),
        (select id from components where name = 'AMD Ryzen 9 7950X' limit 1)),
       (nextval('build_partitions_seq'), 1,
        (select id from builds where name = 'Рабочая станция для видеомонтажа' limit 1),
        (select id from components where name = 'Gigabyte B650 AORUS Elite AX' limit 1)),
       (nextval('build_partitions_seq'), 1,
        (select id from builds where name = 'Рабочая станция для видеомонтажа' limit 1),
        (select id from components where name = 'NVIDIA RTX 4080' limit 1)),
       (nextval('build_partitions_seq'), 4,
        (select id from builds where name = 'Рабочая станция для видеомонтажа' limit 1),
        (select id from components where name = 'Corsair Vengeance DDR5 32GB' limit 1)),
       (nextval('build_partitions_seq'), 1,
        (select id from builds where name = 'Рабочая станция для видеомонтажа' limit 1),
        (select id from components where name = 'Samsung 990 Pro 2TB' limit 1)),
       (nextval('build_partitions_seq'), 2,
        (select id from builds where name = 'Рабочая станция для видеомонтажа' limit 1),
        (select id from components where name = 'Western Digital Black 4TB' limit 1)),
       (nextval('build_partitions_seq'), 1,
        (select id from builds where name = 'Рабочая станция для видеомонтажа' limit 1),
        (select id from components where name = 'Seasonic Focus GX-750 750W' limit 1)),
       (nextval('build_partitions_seq'), 1,
        (select id from builds where name = 'Рабочая станция для видеомонтажа' limit 1),
        (select id from components where name = 'Noctua NH-D15' limit 1));

-- Бюджетный игровой ПК
insert into build_partitions (id, quantity, build_id, component_id)
values (nextval('build_partitions_seq'), 1, (select id from builds where name = 'Бюджетный игровой ПК' limit 1),
        (select id from components where name = 'AMD Ryzen 5 7600X' limit 1)),
       (nextval('build_partitions_seq'), 1, (select id from builds where name = 'Бюджетный игровой ПК' limit 1),
        (select id from components where name = 'ASUS PRIME B760-PLUS' limit 1)),
       (nextval('build_partitions_seq'), 1, (select id from builds where name = 'Бюджетный игровой ПК' limit 1),
        (select id from components where name = 'MSI GeForce RTX 4060 Ti' limit 1)),
       (nextval('build_partitions_seq'), 2, (select id from builds where name = 'Бюджетный игровой ПК' limit 1),
        (select id from components where name = 'Samsung DDR5 16GB' limit 1)),
       (nextval('build_partitions_seq'), 1, (select id from builds where name = 'Бюджетный игровой ПК' limit 1),
        (select id from components where name = 'Kingston KC3000 1TB' limit 1)),
       (nextval('build_partitions_seq'), 1, (select id from builds where name = 'Бюджетный игровой ПК' limit 1),
        (select id from components where name = 'Corsair RM850x 850W' limit 1)),
       (nextval('build_partitions_seq'), 1, (select id from builds where name = 'Бюджетный игровой ПК' limit 1),
        (select id from components where name = 'Deepcool AK620' limit 1));

-- 8. Изображения (images)
insert into images (id, location, bucket)
values (nextval('images_seq'), 'intel_i9_13900k.jpg', 'pc-builder-images'),
       (nextval('images_seq'), 'amd_ryzen_9_7950x.jpg', 'pc-builder-images'),
       (nextval('images_seq'), 'asus_rog_maximus.jpg', 'pc-builder-images'),
       (nextval('images_seq'), 'nvidia_rtx_4090.jpg', 'pc-builder-images'),
       (nextval('images_seq'), 'corsair_vengeance_ddr5.jpg', 'pc-builder-images'),
       (nextval('images_seq'), 'samsung_990_pro.jpg', 'pc-builder-images'),
       (nextval('images_seq'), 'corsair_rm1000x.jpg', 'pc-builder-images'),
       (nextval('images_seq'), 'noctua_nh_d15.jpg', 'pc-builder-images'),
       (nextval('images_seq'), 'corsair_5000d.jpg', 'pc-builder-images');

-- 9. Связи изображений с компонентами
insert into components_images (image_id, component_id)
values ((select id from images where location = 'intel_i9_13900k.jpg' limit 1),
        (select id from components where name = 'Intel Core i9-13900K' limit 1)),
       ((select id from images where location = 'amd_ryzen_9_7950x.jpg' limit 1),
        (select id from components where name = 'AMD Ryzen 9 7950X' limit 1)),
       ((select id from images where location = 'asus_rog_maximus.jpg' limit 1),
        (select id from components where name = 'ASUS ROG Maximus Z790 Hero' limit 1)),
       ((select id from images where location = 'nvidia_rtx_4090.jpg' limit 1),
        (select id from components where name = 'NVIDIA RTX 4090' limit 1)),
       ((select id from images where location = 'corsair_vengeance_ddr5.jpg' limit 1),
        (select id from components where name = 'Corsair Vengeance DDR5 32GB' limit 1)),
       ((select id from images where location = 'samsung_990_pro.jpg' limit 1),
        (select id from components where name = 'Samsung 990 Pro 1TB' limit 1)),
       ((select id from images where location = 'corsair_rm1000x.jpg' limit 1),
        (select id from components where name = 'Corsair RM1000x 1000W' limit 1)),
       ((select id from images where location = 'noctua_nh_d15.jpg' limit 1),
        (select id from components where name = 'Noctua NH-D15' limit 1)),
       ((select id from images where location = 'corsair_5000d.jpg' limit 1),
        (select id from components where name = 'Corsair 5000D Airflow' limit 1));

-- 10. Связи изображений с производителями
insert into producers_images (image_id, producer_id)
values ((select id from images where location = 'intel_i9_13900k.jpg' limit 1),
        (select id from producers where name = 'Intel' limit 1)),
       ((select id from images where location = 'amd_ryzen_9_7950x.jpg' limit 1),
        (select id from producers where name = 'AMD' limit 1)),
       ((select id from images where location = 'asus_rog_maximus.jpg' limit 1),
        (select id from producers where name = 'ASUS' limit 1)),
       ((select id from images where location = 'nvidia_rtx_4090.jpg' limit 1),
        (select id from producers where name = 'NVIDIA' limit 1)),
       ((select id from images where location = 'corsair_vengeance_ddr5.jpg' limit 1),
        (select id from producers where name = 'Corsair' limit 1)),
       ((select id from images where location = 'samsung_990_pro.jpg' limit 1),
        (select id from producers where name = 'Samsung' limit 1));

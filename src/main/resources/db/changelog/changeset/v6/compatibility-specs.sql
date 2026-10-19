-- ===== Схема =====

alter table specification_types
    add column code       varchar(64) unique,
    add column value_type varchar(16) not null default 'STRING',
    add column unit       varchar(32);

alter table component_types
    add column code varchar(32) unique;

create table component_type_specifications
(
    component_type_id     bigint not null,
    specification_type_id bigint not null,
    primary key (component_type_id, specification_type_id),
    constraint ctspec_ct foreign key (component_type_id) references component_types,
    constraint ctspec_st foreign key (specification_type_id) references specification_types
);

-- Характеристики связаны с компонентом через specifications.component_id.
-- Таблица-связка дублировала это и не заполнялась тестовыми данными, из-за чего их характеристики не были видны.
drop table components_specifications;

-- ===== Типы компонентов =====

update component_types set code = 'CPU' where name = 'Процессор';
update component_types set code = 'MOTHERBOARD' where name = 'Материнская плата';
update component_types set code = 'GPU' where name = 'Видеокарта';
update component_types set code = 'RAM' where name = 'Оперативная память';
update component_types set code = 'STORAGE' where name = 'Накопитель';
update component_types set code = 'PSU' where name = 'Блок питания';
update component_types set code = 'CASE_FAN' where name = 'Кулер';
update component_types set code = 'CPU_COOLER' where name = 'Кулер процессора';
update component_types set code = 'CASE' where name = 'Корпус';
update component_types set code = 'SSD' where name = 'SSD';
update component_types set code = 'HDD' where name = 'HDD';

-- Noctua NH-D15 — процессорный кулер, а не корпусный вентилятор
update components
set component_type_id = (select id from component_types where code = 'CPU_COOLER')
where name = 'Noctua NH-D15';

-- ===== Существующие типы характеристик =====

update specification_types set code = 'socket', value_type = 'STRING' where name = 'Сокет';
update specification_types
set code = 'cpu_frequency_ghz', value_type = 'NUMBER', unit = 'ГГц',
    name = 'Частота процессора', description = 'Максимальная частота в турбо-режиме'
where name = 'Частота';
update specification_types set code = 'cores', value_type = 'NUMBER', unit = 'шт' where name = 'Количество ядер';
update specification_types set code = 'process_nm', value_type = 'NUMBER', unit = 'нм' where name = 'Техпроцесс';
update specification_types set code = 'tdp_w', value_type = 'NUMBER', unit = 'Вт' where name = 'TDP';
update specification_types set code = 'capacity_gb', value_type = 'NUMBER', unit = 'ГБ' where name = 'Объем';
update specification_types set code = 'memory_type', value_type = 'STRING' where name = 'Тип памяти';
update specification_types set code = 'form_factor', value_type = 'STRING' where name = 'Форм-фактор';
update specification_types
set code = 'storage_interface', value_type = 'STRING', description = 'M.2 NVMe, M.2 SATA или SATA'
where name = 'Интерфейс';
update specification_types set code = 'read_speed_mbs', value_type = 'NUMBER', unit = 'МБ/с' where name = 'Скорость чтения';
update specification_types set code = 'wattage_w', value_type = 'NUMBER', unit = 'Вт' where name = 'Мощность';
update specification_types set code = 'efficiency_80plus', value_type = 'STRING' where name = '80 Plus';
update specification_types set code = 'cache_mb', value_type = 'NUMBER', unit = 'МБ' where name = 'Объем кэша';
update specification_types set code = 'pcie_version', value_type = 'NUMBER' where name = 'PCIe версия';
update specification_types set code = 'raid_support', value_type = 'BOOLEAN' where name = 'Поддержка RAID';
update specification_types set code = 'vrm_phases', value_type = 'STRING' where name = 'VRM фазы';

-- ===== Новые типы характеристик =====

insert into specification_types (id, name, description, code, value_type, unit)
values (nextval('specification_types_seq'), 'Поддерживаемая память', 'Типы памяти, с которыми работает процессор', 'supported_memory_types', 'LIST', null),
       (nextval('specification_types_seq'), 'Макс. объём памяти', 'Максимальный объём оперативной памяти', 'max_memory_gb', 'NUMBER', 'ГБ'),
       (nextval('specification_types_seq'), 'Частота памяти', 'Эффективная частота модулей', 'memory_speed_mts', 'NUMBER', 'МТ/с'),
       (nextval('specification_types_seq'), 'Макс. частота памяти', 'Максимальная поддерживаемая частота памяти', 'max_memory_speed_mts', 'NUMBER', 'МТ/с'),
       (nextval('specification_types_seq'), 'Модулей в комплекте', 'Сколько планок в комплекте памяти', 'memory_modules', 'NUMBER', 'шт'),
       (nextval('specification_types_seq'), 'Встроенная графика', 'Есть ли у процессора встроенное видеоядро', 'integrated_graphics', 'BOOLEAN', null),
       (nextval('specification_types_seq'), 'Слоты памяти', 'Количество слотов DIMM', 'ram_slots', 'NUMBER', 'шт'),
       (nextval('specification_types_seq'), 'Слоты M.2', 'Количество слотов M.2 для накопителей', 'm2_slots', 'NUMBER', 'шт'),
       (nextval('specification_types_seq'), 'Порты SATA', 'Количество портов SATA', 'sata_ports', 'NUMBER', 'шт'),
       (nextval('specification_types_seq'), 'Длина', 'Длина видеокарты', 'length_mm', 'NUMBER', 'мм'),
       (nextval('specification_types_seq'), 'Высота', 'Высота башенного кулера', 'height_mm', 'NUMBER', 'мм'),
       (nextval('specification_types_seq'), 'Размер радиатора', 'Размер радиатора жидкостного охлаждения', 'radiator_mm', 'NUMBER', 'мм'),
       (nextval('specification_types_seq'), 'Поддерживаемые сокеты', 'Сокеты, на которые ставится кулер', 'cooler_sockets', 'LIST', null),
       (nextval('specification_types_seq'), 'Отводимая мощность', 'Сколько тепла кулер способен отвести', 'cooler_max_tdp_w', 'NUMBER', 'Вт'),
       (nextval('specification_types_seq'), 'Форм-факторы плат', 'Какие материнские платы помещаются в корпус', 'case_mb_form_factors', 'LIST', null),
       (nextval('specification_types_seq'), 'Форм-факторы БП', 'Какие блоки питания помещаются в корпус', 'case_psu_form_factors', 'LIST', null),
       (nextval('specification_types_seq'), 'Макс. длина видеокарты', 'Максимальная длина видеокарты в корпусе', 'case_max_gpu_length_mm', 'NUMBER', 'мм'),
       (nextval('specification_types_seq'), 'Макс. высота кулера', 'Максимальная высота процессорного кулера', 'case_max_cooler_height_mm', 'NUMBER', 'мм'),
       (nextval('specification_types_seq'), 'Макс. радиатор', 'Максимальный размер радиатора СЖО', 'case_max_radiator_mm', 'NUMBER', 'мм');

-- ===== Обязательные характеристики по типам компонентов =====

insert into component_type_specifications (component_type_id, specification_type_id)
select ct.id, st.id
from component_types ct
         join specification_types st on (ct.code, st.code) in (values
    ('CPU', 'socket'), ('CPU', 'supported_memory_types'), ('CPU', 'max_memory_gb'),
    ('CPU', 'max_memory_speed_mts'), ('CPU', 'tdp_w'), ('CPU', 'integrated_graphics'),
    ('MOTHERBOARD', 'socket'), ('MOTHERBOARD', 'memory_type'), ('MOTHERBOARD', 'ram_slots'),
    ('MOTHERBOARD', 'max_memory_gb'), ('MOTHERBOARD', 'max_memory_speed_mts'), ('MOTHERBOARD', 'form_factor'),
    ('MOTHERBOARD', 'm2_slots'), ('MOTHERBOARD', 'sata_ports'),
    ('GPU', 'tdp_w'), ('GPU', 'length_mm'),
    ('RAM', 'memory_type'), ('RAM', 'capacity_gb'), ('RAM', 'memory_speed_mts'), ('RAM', 'memory_modules'),
    ('STORAGE', 'storage_interface'), ('STORAGE', 'capacity_gb'),
    ('SSD', 'storage_interface'), ('SSD', 'capacity_gb'),
    ('HDD', 'storage_interface'), ('HDD', 'capacity_gb'),
    ('PSU', 'wattage_w'), ('PSU', 'form_factor'),
    ('CASE', 'case_mb_form_factors'), ('CASE', 'case_psu_form_factors'), ('CASE', 'case_max_gpu_length_mm'),
    ('CASE', 'case_max_cooler_height_mm'), ('CASE', 'case_max_radiator_mm'),
    ('CPU_COOLER', 'cooler_sockets'), ('CPU_COOLER', 'cooler_max_tdp_w'));

-- ===== Приведение существующих значений к единому формату =====

-- у памяти «Частота 5.2» означала 5200 МТ/с — это теперь отдельная характеристика
delete from specifications
where type_id = (select id from specification_types where code = 'cpu_frequency_ghz')
  and component_id in (select c.id from components c
                       join component_types ct on ct.id = c.component_type_id
                       where ct.code = 'RAM');

update specifications set value = 'M.2 NVMe'
where type_id = (select id from specification_types where code = 'storage_interface')
  and value ilike '%nvme%';

-- числа без лишних нулей: «5.0» → «5»
update specifications s
set value = regexp_replace(regexp_replace(s.value, '0+$', ''), '\.$', '')
from specification_types st
where st.id = s.type_id
  and st.value_type = 'NUMBER'
  and s.value ~ '^[0-9]+\.[0-9]*0$';

-- ===== Характеристики тестовых компонентов =====

create function pc_set_spec(p_component varchar, p_code varchar, p_value varchar) returns void
    language plpgsql
as
$$
declare
    v_component bigint;
    v_type      bigint;
begin
    select id into v_component from components where name = p_component order by id limit 1;
    select id into v_type from specification_types where code = p_code;
    if v_component is null or v_type is null then
        return;
    end if;

    update specifications set value = p_value where component_id = v_component and type_id = v_type;
    if not found then
        insert into specifications (id, value, component_id, type_id)
        values (nextval('specifications_seq'), p_value, v_component, v_type);
    end if;
end;
$$;

-- Процессоры
select pc_set_spec('AMD Ryzen 5 7600X', 'socket', 'AM5');
select pc_set_spec('AMD Ryzen 5 7600X', 'supported_memory_types', 'DDR5');
select pc_set_spec('AMD Ryzen 5 7600X', 'max_memory_gb', '192');
select pc_set_spec('AMD Ryzen 5 7600X', 'max_memory_speed_mts', '5200');
select pc_set_spec('AMD Ryzen 5 7600X', 'tdp_w', '105');
select pc_set_spec('AMD Ryzen 5 7600X', 'integrated_graphics', 'true');
select pc_set_spec('AMD Ryzen 5 7600X', 'cores', '6');
select pc_set_spec('AMD Ryzen 5 7600X', 'cpu_frequency_ghz', '5.3');

select pc_set_spec('AMD Ryzen 7 7800X3D', 'socket', 'AM5');
select pc_set_spec('AMD Ryzen 7 7800X3D', 'supported_memory_types', 'DDR5');
select pc_set_spec('AMD Ryzen 7 7800X3D', 'max_memory_gb', '192');
select pc_set_spec('AMD Ryzen 7 7800X3D', 'max_memory_speed_mts', '5200');
select pc_set_spec('AMD Ryzen 7 7800X3D', 'tdp_w', '120');
select pc_set_spec('AMD Ryzen 7 7800X3D', 'integrated_graphics', 'true');

select pc_set_spec('AMD Ryzen 9 7950X', 'socket', 'AM5');
select pc_set_spec('AMD Ryzen 9 7950X', 'supported_memory_types', 'DDR5');
select pc_set_spec('AMD Ryzen 9 7950X', 'max_memory_gb', '192');
select pc_set_spec('AMD Ryzen 9 7950X', 'max_memory_speed_mts', '5200');
select pc_set_spec('AMD Ryzen 9 7950X', 'tdp_w', '170');
select pc_set_spec('AMD Ryzen 9 7950X', 'integrated_graphics', 'true');
select pc_set_spec('AMD Ryzen 9 7950X', 'cores', '16');
select pc_set_spec('AMD Ryzen 9 7950X', 'cpu_frequency_ghz', '5.7');

select pc_set_spec('Intel Core i5-13600K', 'socket', 'LGA1700');
select pc_set_spec('Intel Core i5-13600K', 'supported_memory_types', 'DDR4, DDR5');
select pc_set_spec('Intel Core i5-13600K', 'max_memory_gb', '192');
select pc_set_spec('Intel Core i5-13600K', 'max_memory_speed_mts', '5600');
select pc_set_spec('Intel Core i5-13600K', 'tdp_w', '125');
select pc_set_spec('Intel Core i5-13600K', 'integrated_graphics', 'true');
select pc_set_spec('Intel Core i5-13600K', 'cores', '14');
select pc_set_spec('Intel Core i5-13600K', 'cpu_frequency_ghz', '5.1');

select pc_set_spec('Intel Core i7-13700K', 'socket', 'LGA1700');
select pc_set_spec('Intel Core i7-13700K', 'supported_memory_types', 'DDR4, DDR5');
select pc_set_spec('Intel Core i7-13700K', 'max_memory_gb', '192');
select pc_set_spec('Intel Core i7-13700K', 'max_memory_speed_mts', '5600');
select pc_set_spec('Intel Core i7-13700K', 'tdp_w', '125');
select pc_set_spec('Intel Core i7-13700K', 'integrated_graphics', 'true');
select pc_set_spec('Intel Core i7-13700K', 'cores', '16');
select pc_set_spec('Intel Core i7-13700K', 'cpu_frequency_ghz', '5.4');

select pc_set_spec('Intel Core i9-13900K', 'socket', 'LGA1700');
select pc_set_spec('Intel Core i9-13900K', 'supported_memory_types', 'DDR4, DDR5');
select pc_set_spec('Intel Core i9-13900K', 'max_memory_gb', '192');
select pc_set_spec('Intel Core i9-13900K', 'max_memory_speed_mts', '5600');
select pc_set_spec('Intel Core i9-13900K', 'tdp_w', '125');
select pc_set_spec('Intel Core i9-13900K', 'integrated_graphics', 'true');

-- Материнские платы
select pc_set_spec('ASUS PRIME B760-PLUS', 'socket', 'LGA1700');
select pc_set_spec('ASUS PRIME B760-PLUS', 'memory_type', 'DDR5');
select pc_set_spec('ASUS PRIME B760-PLUS', 'ram_slots', '4');
select pc_set_spec('ASUS PRIME B760-PLUS', 'max_memory_gb', '192');
select pc_set_spec('ASUS PRIME B760-PLUS', 'max_memory_speed_mts', '7000');
select pc_set_spec('ASUS PRIME B760-PLUS', 'form_factor', 'ATX');
select pc_set_spec('ASUS PRIME B760-PLUS', 'm2_slots', '3');
select pc_set_spec('ASUS PRIME B760-PLUS', 'sata_ports', '4');
select pc_set_spec('ASUS PRIME B760-PLUS', 'pcie_version', '4');

select pc_set_spec('ASUS ROG Maximus Z790 Hero', 'socket', 'LGA1700');
select pc_set_spec('ASUS ROG Maximus Z790 Hero', 'memory_type', 'DDR5');
select pc_set_spec('ASUS ROG Maximus Z790 Hero', 'ram_slots', '4');
select pc_set_spec('ASUS ROG Maximus Z790 Hero', 'max_memory_gb', '192');
select pc_set_spec('ASUS ROG Maximus Z790 Hero', 'max_memory_speed_mts', '8000');
select pc_set_spec('ASUS ROG Maximus Z790 Hero', 'form_factor', 'ATX');
select pc_set_spec('ASUS ROG Maximus Z790 Hero', 'm2_slots', '5');
select pc_set_spec('ASUS ROG Maximus Z790 Hero', 'sata_ports', '6');

select pc_set_spec('Gigabyte B650 AORUS Elite AX', 'socket', 'AM5');
select pc_set_spec('Gigabyte B650 AORUS Elite AX', 'memory_type', 'DDR5');
select pc_set_spec('Gigabyte B650 AORUS Elite AX', 'ram_slots', '4');
select pc_set_spec('Gigabyte B650 AORUS Elite AX', 'max_memory_gb', '192');
select pc_set_spec('Gigabyte B650 AORUS Elite AX', 'max_memory_speed_mts', '6600');
select pc_set_spec('Gigabyte B650 AORUS Elite AX', 'form_factor', 'ATX');
select pc_set_spec('Gigabyte B650 AORUS Elite AX', 'm2_slots', '4');
select pc_set_spec('Gigabyte B650 AORUS Elite AX', 'sata_ports', '4');
select pc_set_spec('Gigabyte B650 AORUS Elite AX', 'pcie_version', '5');

select pc_set_spec('Gigabyte Z790 AORUS Master', 'socket', 'LGA1700');
select pc_set_spec('Gigabyte Z790 AORUS Master', 'memory_type', 'DDR5');
select pc_set_spec('Gigabyte Z790 AORUS Master', 'ram_slots', '4');
select pc_set_spec('Gigabyte Z790 AORUS Master', 'max_memory_gb', '192');
select pc_set_spec('Gigabyte Z790 AORUS Master', 'max_memory_speed_mts', '8000');
select pc_set_spec('Gigabyte Z790 AORUS Master', 'form_factor', 'E-ATX');
select pc_set_spec('Gigabyte Z790 AORUS Master', 'm2_slots', '5');
select pc_set_spec('Gigabyte Z790 AORUS Master', 'sata_ports', '6');
select pc_set_spec('Gigabyte Z790 AORUS Master', 'pcie_version', '5');

select pc_set_spec('MSI MPG B650 Carbon WiFi', 'socket', 'AM5');
select pc_set_spec('MSI MPG B650 Carbon WiFi', 'memory_type', 'DDR5');
select pc_set_spec('MSI MPG B650 Carbon WiFi', 'ram_slots', '4');
select pc_set_spec('MSI MPG B650 Carbon WiFi', 'max_memory_gb', '192');
select pc_set_spec('MSI MPG B650 Carbon WiFi', 'max_memory_speed_mts', '6600');
select pc_set_spec('MSI MPG B650 Carbon WiFi', 'form_factor', 'ATX');
select pc_set_spec('MSI MPG B650 Carbon WiFi', 'm2_slots', '4');
select pc_set_spec('MSI MPG B650 Carbon WiFi', 'sata_ports', '6');
select pc_set_spec('MSI MPG B650 Carbon WiFi', 'pcie_version', '5');

select pc_set_spec('MSI PRO Z790-A WiFi', 'socket', 'LGA1700');
select pc_set_spec('MSI PRO Z790-A WiFi', 'memory_type', 'DDR5');
select pc_set_spec('MSI PRO Z790-A WiFi', 'ram_slots', '4');
select pc_set_spec('MSI PRO Z790-A WiFi', 'max_memory_gb', '192');
select pc_set_spec('MSI PRO Z790-A WiFi', 'max_memory_speed_mts', '7200');
select pc_set_spec('MSI PRO Z790-A WiFi', 'form_factor', 'ATX');
select pc_set_spec('MSI PRO Z790-A WiFi', 'm2_slots', '4');
select pc_set_spec('MSI PRO Z790-A WiFi', 'sata_ports', '6');
select pc_set_spec('MSI PRO Z790-A WiFi', 'pcie_version', '5');

-- Оперативная память (объём — на весь комплект)
select pc_set_spec('Corsair Vengeance DDR5 32GB', 'memory_type', 'DDR5');
select pc_set_spec('Corsair Vengeance DDR5 32GB', 'capacity_gb', '32');
select pc_set_spec('Corsair Vengeance DDR5 32GB', 'memory_speed_mts', '5200');
select pc_set_spec('Corsair Vengeance DDR5 32GB', 'memory_modules', '2');

select pc_set_spec('Corsair Vengeance DDR5 64GB', 'memory_type', 'DDR5');
select pc_set_spec('Corsair Vengeance DDR5 64GB', 'capacity_gb', '64');
select pc_set_spec('Corsair Vengeance DDR5 64GB', 'memory_speed_mts', '5600');
select pc_set_spec('Corsair Vengeance DDR5 64GB', 'memory_modules', '2');

select pc_set_spec('Kingston Fury Beast DDR4 32GB', 'memory_type', 'DDR4');
select pc_set_spec('Kingston Fury Beast DDR4 32GB', 'capacity_gb', '32');
select pc_set_spec('Kingston Fury Beast DDR4 32GB', 'memory_speed_mts', '3200');
select pc_set_spec('Kingston Fury Beast DDR4 32GB', 'memory_modules', '2');

select pc_set_spec('Kingston Fury Beast DDR5 32GB', 'memory_type', 'DDR5');
select pc_set_spec('Kingston Fury Beast DDR5 32GB', 'capacity_gb', '32');
select pc_set_spec('Kingston Fury Beast DDR5 32GB', 'memory_speed_mts', '6000');
select pc_set_spec('Kingston Fury Beast DDR5 32GB', 'memory_modules', '2');

select pc_set_spec('Samsung DDR5 16GB', 'memory_type', 'DDR5');
select pc_set_spec('Samsung DDR5 16GB', 'capacity_gb', '16');
select pc_set_spec('Samsung DDR5 16GB', 'memory_speed_mts', '4800');
select pc_set_spec('Samsung DDR5 16GB', 'memory_modules', '1');

-- Видеокарты
select pc_set_spec('ASUS Radeon RX 7800 XT', 'tdp_w', '263');
select pc_set_spec('ASUS Radeon RX 7800 XT', 'length_mm', '320');
select pc_set_spec('ASUS Radeon RX 7800 XT', 'pcie_version', '4');
select pc_set_spec('ASUS Radeon RX 7800 XT', 'capacity_gb', '16');

select pc_set_spec('ASUS Radeon RX 7900 XTX', 'tdp_w', '355');
select pc_set_spec('ASUS Radeon RX 7900 XTX', 'length_mm', '353');
select pc_set_spec('ASUS Radeon RX 7900 XTX', 'pcie_version', '4');
select pc_set_spec('ASUS Radeon RX 7900 XTX', 'capacity_gb', '24');

select pc_set_spec('MSI GeForce RTX 4060 Ti', 'tdp_w', '160');
select pc_set_spec('MSI GeForce RTX 4060 Ti', 'length_mm', '251');
select pc_set_spec('MSI GeForce RTX 4060 Ti', 'pcie_version', '4');
select pc_set_spec('MSI GeForce RTX 4060 Ti', 'capacity_gb', '8');

select pc_set_spec('NVIDIA RTX 4070 Ti', 'tdp_w', '285');
select pc_set_spec('NVIDIA RTX 4070 Ti', 'length_mm', '305');
select pc_set_spec('NVIDIA RTX 4070 Ti', 'pcie_version', '4');
select pc_set_spec('NVIDIA RTX 4070 Ti', 'capacity_gb', '12');

select pc_set_spec('NVIDIA RTX 4080', 'tdp_w', '320');
select pc_set_spec('NVIDIA RTX 4080', 'length_mm', '304');
select pc_set_spec('NVIDIA RTX 4080', 'pcie_version', '4');
select pc_set_spec('NVIDIA RTX 4080', 'capacity_gb', '16');

select pc_set_spec('NVIDIA RTX 4090', 'tdp_w', '450');
select pc_set_spec('NVIDIA RTX 4090', 'length_mm', '304');

-- Блоки питания
select pc_set_spec('be quiet! Dark Power 13 1000W', 'wattage_w', '1000');
select pc_set_spec('be quiet! Dark Power 13 1000W', 'form_factor', 'ATX');
select pc_set_spec('be quiet! Dark Power 13 1000W', 'efficiency_80plus', 'Titanium');

select pc_set_spec('Corsair RM1000x 1000W', 'wattage_w', '1000');
select pc_set_spec('Corsair RM1000x 1000W', 'form_factor', 'ATX');

select pc_set_spec('Corsair RM850x 850W', 'wattage_w', '850');
select pc_set_spec('Corsair RM850x 850W', 'form_factor', 'ATX');
select pc_set_spec('Corsair RM850x 850W', 'efficiency_80plus', 'Gold');

select pc_set_spec('Seasonic Focus GX-750 750W', 'wattage_w', '750');
select pc_set_spec('Seasonic Focus GX-750 750W', 'form_factor', 'ATX');
select pc_set_spec('Seasonic Focus GX-750 750W', 'efficiency_80plus', 'Gold');

-- Корпуса
select pc_set_spec('Corsair 5000D Airflow', 'case_mb_form_factors', 'E-ATX, ATX, mATX, Mini-ITX');
select pc_set_spec('Corsair 5000D Airflow', 'case_psu_form_factors', 'ATX');
select pc_set_spec('Corsair 5000D Airflow', 'case_max_gpu_length_mm', '400');
select pc_set_spec('Corsair 5000D Airflow', 'case_max_cooler_height_mm', '170');
select pc_set_spec('Corsair 5000D Airflow', 'case_max_radiator_mm', '360');

select pc_set_spec('Lian Li PC-O11 Dynamic', 'case_mb_form_factors', 'E-ATX, ATX, mATX, Mini-ITX');
select pc_set_spec('Lian Li PC-O11 Dynamic', 'case_psu_form_factors', 'ATX');
select pc_set_spec('Lian Li PC-O11 Dynamic', 'case_max_gpu_length_mm', '420');
select pc_set_spec('Lian Li PC-O11 Dynamic', 'case_max_cooler_height_mm', '155');
select pc_set_spec('Lian Li PC-O11 Dynamic', 'case_max_radiator_mm', '360');

-- Процессорные кулеры
select pc_set_spec('Noctua NH-D15', 'cooler_sockets', 'LGA1700, LGA1200, AM5, AM4');
select pc_set_spec('Noctua NH-D15', 'cooler_max_tdp_w', '250');
select pc_set_spec('Noctua NH-D15', 'height_mm', '165');

select pc_set_spec('Deepcool AK620', 'cooler_sockets', 'LGA1700, LGA1200, AM5, AM4');
select pc_set_spec('Deepcool AK620', 'cooler_max_tdp_w', '260');
select pc_set_spec('Deepcool AK620', 'height_mm', '160');

select pc_set_spec('Corsair iCUE H150i Elite', 'cooler_sockets', 'LGA1700, LGA1200, AM5, AM4');
select pc_set_spec('Corsair iCUE H150i Elite', 'cooler_max_tdp_w', '300');
select pc_set_spec('Corsair iCUE H150i Elite', 'radiator_mm', '360');

-- Накопители
select pc_set_spec('Seagate BarraCuda 2TB', 'storage_interface', 'SATA');
select pc_set_spec('Seagate BarraCuda 2TB', 'capacity_gb', '2000');
select pc_set_spec('Western Digital Black 4TB', 'storage_interface', 'SATA');
select pc_set_spec('Western Digital Black 4TB', 'capacity_gb', '4000');
select pc_set_spec('Kingston KC3000 1TB', 'storage_interface', 'M.2 NVMe');
select pc_set_spec('Kingston KC3000 1TB', 'capacity_gb', '1000');
select pc_set_spec('Samsung 990 Pro 1TB', 'storage_interface', 'M.2 NVMe');
select pc_set_spec('Samsung 990 Pro 2TB', 'storage_interface', 'M.2 NVMe');
select pc_set_spec('Samsung 990 Pro 2TB', 'capacity_gb', '2000');
select pc_set_spec('Western Digital Black SN850X 1TB', 'storage_interface', 'M.2 NVMe');
select pc_set_spec('Western Digital Black SN850X 1TB', 'capacity_gb', '1000');

drop function pc_set_spec(varchar, varchar, varchar);

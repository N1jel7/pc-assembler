-- Тестовые картинки из v4 ссылались на несуществующий бакет pc-builder-images
delete from components_images;
delete from producers_images;
update producers set image_id = null;
delete from images where bucket = 'pc-builder-images';

-- У производителя одно фото (producers.image_id), таблица со списком больше не нужна
drop table producers_images;

-- Не используется: фото компонентов лежат в components_images
alter table components drop column if exists image_id;

-- Фото сборок
create table builds_images
(
    image_id bigint not null,
    build_id bigint not null,
    primary key (image_id, build_id),
    constraint buildimages_images foreign key (image_id) references images,
    constraint buildid_builds foreign key (build_id) references builds
);

-- Чтобы отдавать картинку с правильным Content-Type без запроса метаданных в MinIO
alter table images add column content_type varchar(255);

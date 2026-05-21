create table images
(
    id       bigint       not null,
    location varchar(255) not null,
    bucket   varchar(255) not null,
    primary key (id)
);

create table producers_images
(
    image_id    bigint not null,
    producer_id bigint not null,
    primary key (image_id, producer_id),
    constraint prodimages_images foreign key (image_id) references images,
    constraint prodid_prod foreign key (producer_id) references producers
);

create table components_images
(
    image_id    bigint not null,
    component_id bigint not null,
    primary key (image_id, component_id),
    constraint compimages_images foreign key (image_id) references images,
    constraint compid_comps foreign key (component_id) references components
);

alter table if exists components
    drop column if exists image_url,
    add column image_id BIGINT,
    add constraint comp_image foreign key (image_id) references images;

alter table if exists producers
    drop column if exists logo_url,
    add column image_id BIGINT,
    add constraint producer_image foreign key (image_id) references images;

create sequence images_seq start with 1 increment by 50;
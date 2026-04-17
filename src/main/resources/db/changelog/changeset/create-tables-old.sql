create sequence build_partitions_seq start with 1 increment by 50;
create sequence builds_seq start with 1 increment by 50;
create sequence component_types_seq start with 1 increment by 50;
create sequence components_seq start with 1 increment by 50;
create sequence producers_seq start with 1 increment by 50;
create sequence specification_seq start with 1 increment by 50;

create table build_partitions
(
    quantity     integer not null,
    build_id     bigint  not null,
    component_id bigint  not null,
    id           bigint  not null,
    primary key (id),
    constraint chk_build_partitions_quantity check (quantity > 0),
    constraint uk_build_partitions_build_component unique (build_id, component_id)
);

create table builds
(
    creation_date timestamp(6) not null,
    id            bigint       not null,
    name          varchar(100) not null,
    primary key (id)
    /*constraint chk_builds_creation_date check (creation_date <= current_timestamp)*/
);

create table component_types
(
    component_parent_type_id bigint,
    id                       bigint       not null,
    name                     varchar(50) not null unique,
    primary key (id)
);

create table components
(
    price             numeric(16, 2) not null,
    stock_quantity    integer        not null,
    component_type_id bigint         not null,
    id                bigint         not null,
    producer_id       bigint         not null,
    image_url         varchar(255),
    name              varchar(200)   not null,
    primary key (id),
    constraint chk_components_price check (price >= 0),
    constraint chk_components_stock_quantity check (stock_quantity >= 0)
);

create table specifications
(
    id       bigint       not null,
    component_id     bigint not null,
    specification_id bigint not null,
    value            varchar(255),
    primary key (id),
    constraint unk_composite_key unique (component_id, specification_id)
);

create table producers
(
    id       bigint       not null,
    country  varchar(255),
    logo_url varchar(255),
    name     varchar(255) not null,
    primary key (id)
);

create table specification_types
(
    id          bigint       not null,
    name        varchar(255) not null,
    description varchar(255),
    primary key (id)
);

alter table if exists build_partitions
    add constraint FKk5q5ajlyk6j66tywov15vexrl foreign key (build_id) references builds;
alter table if exists build_partitions
    add constraint FKi97t5a8qu7e9td5erog5qsjyd foreign key (component_id) references components;
alter table if exists component_types
    add constraint FKnxnkhpb6jotyk60gcslammxtf foreign key (component_parent_type_id) references component_types;
alter table if exists components
    add constraint FK51j4a15w7but5ck8mdt7962lc foreign key (component_type_id) references component_types;
alter table if exists components
    add constraint FK7uayu0i89y1xkstbb63r2cp0r foreign key (producer_id) references producers;
alter table if exists specifications
    add constraint FK9asdm129dsfk4m9sd0vlxmasd foreign key (component_id) references components;
alter table if exists specifications
    add constraint FK7uayu0i57y2dgbshb63r2cp0r foreign key (specification_id) references specification;
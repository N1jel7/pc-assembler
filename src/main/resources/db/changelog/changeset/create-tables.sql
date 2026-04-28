create sequence build_partitions_seq start with 1 increment by 50;
create sequence builds_seq start with 1 increment by 50;
create sequence component_types_seq start with 1 increment by 50;
create sequence components_seq start with 1 increment by 50;
create sequence producers_seq start with 1 increment by 50;
create sequence specification_types_seq start with 1 increment by 50;
create sequence specifications_seq start with 1 increment by 50;

create table build_partitions
(
    quantity     integer not null,
    build_id     bigint,
    component_id bigint  not null,
    id           bigint  not null,
    primary key (id)
);

create table builds
(
    creation_date timestamp(6) not null,
    id            bigint       not null,
    name          varchar(255) not null,
    primary key (id)
);

create table component_types
(
    id   bigint       not null,
    name varchar(255) not null,
    parent_type bigint,
    primary key (id)
);

create table components
(
    price             numeric(38, 2) not null,
    stock_quantity    integer        not null,
    component_type_id bigint         not null,
    id                bigint         not null,
    producer_id       bigint         not null,
    image_url         varchar(255),
    name              varchar(255)   not null,
    primary key (id)
);

create table components_specifications
(
    component_id      bigint not null,
    specifications_id bigint not null unique
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
    description varchar(255),
    name        varchar(255) not null,
    primary key (id)
);

create table specifications
(
    component_id bigint,
    id           bigint not null,
    type_id      bigint,
    value        varchar(255),
    primary key (id)
);

alter table if exists build_partitions
    add constraint FKk5q5ajlyk6j66tywov15vexrl foreign key (build_id) references builds;
alter table if exists build_partitions
    add constraint FKi97t5a8qu7e9td5erog5qsjyd foreign key (component_id) references components;
alter table if exists components
    add constraint FK51j4a15w7but5ck8mdt7962lc foreign key (component_type_id) references component_types;
alter table if exists components
    add constraint FK7uayu0i89y1xkstbb63r2cp0r foreign key (producer_id) references producers;
alter table if exists components_specifications
    add constraint FK3ortmt1rt68py34wfse8pjefx foreign key (specifications_id) references specifications;
alter table if exists components_specifications
    add constraint FK9xc2qgjw7yysnyng381ee6s92 foreign key (component_id) references components;
alter table if exists specifications
    add constraint FKdd03kj7pf8xcay7v33meeg41o foreign key (component_id) references components;
alter table if exists specifications
    add constraint FK45wxoyhjmtvikyvygqlwet56g foreign key (type_id) references specification_types
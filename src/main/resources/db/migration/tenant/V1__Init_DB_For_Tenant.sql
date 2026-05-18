create table category
(
    id          varchar(255) not null primary key,
    created_at  timestamp(6) not null,
    created_by  varchar(255) not null,
    deleted     boolean      not null,
    updated_at  timestamp(6),
    updated_by  varchar(255),
    description text,
    tenant_id   varchar(255),
    name        varchar(255) not null
        constraint category_name_unique_constraint unique
);

create table product
(
    id              varchar(255)   not null primary key,
    created_at      timestamp(6)   not null,
    created_by      varchar(255)   not null,
    deleted         boolean        not null,
    updated_at      timestamp(6),
    updated_by      varchar(255),
    alert_threshold integer        not null,
    description     text,
    name            varchar(255)   not null,
    price           numeric(38, 2) not null,
    tenant_id       varchar(255),
    reference       varchar(255)   not null
        constraint product_reference_unique_constraint unique,
    category_id     varchar(255)
        constraint fk_category_id references category
);


create table stock_movement
(
    id         varchar(255) not null primary key,
    created_at timestamp(6) not null,
    created_by varchar(255) not null,
    deleted    boolean      not null,
    updated_at timestamp(6),
    updated_by varchar(255),
    comment    text,
    date       date         not null,
    quantity   integer      not null,
    tenant_id  varchar(255),
    product_id varchar(255)
        constraint fk_product_id references product type       varchar(255) not null
        constraint stock_movement_type_mvt_check
            check ((type)::text = ANY ((ARRAY ['IN':: character varying, 'OUT':: character varying])::text[])),
);

create table tenant
(
    id              varchar(255) not null primary key,
    admin_email     varchar(255) not null unique,
    admin_full_name varchar(255) not null,
    admin_password  varchar(255) not null,
    admin_username  varchar(255) not null unique,
    company_code    varchar(255) not null unique,
    company_name    varchar(255) not null,
    email           varchar(255) not null unique,
    deleted         boolean      not null,
    created_at      timestamp(6) not null,
    updated_at      timestamp(6),
    status          varchar(255) not null,
        constraint tenants_status_check
            check ((status)::text = ANY
        ((ARRAY ['PENDING':: character varying, 'ACTIVE':: character varying, 'SUSPENDED':: character varying, 'INACTIVE':: character varying])::text[]))
);

create table users
(
    id         varchar(255) not null primary key,
    username   varchar(255) not null unique,
    email      varchar(255) not null unique,
    first_name varchar(255) not null,
    last_name  varchar(255) not null,
    password   varchar(255) not null,
    deleted    boolean      not null,
    enabled    boolean,
    created_at timestamp(6) not null,
    updated_at timestamp(6),
    created_by varchar(255) not null,
    updated_by varchar(255),
    role       varchar(255) not null,
        constraint users_role_check
            check ((role)::text = ANY
        ((ARRAY ['ROLE_PLATFORM_ADMIN':: character varying, 'ROLE_COMPANY_ADMIN':: character varying,
        'ROLE_ADMINISTRATOR':: character varying, 'ROLE_USER':: character varying, 'ROLE_SALES_OPERATOR':: character varying])::text[])),
    tenant_id  varchar(255) constraint fk_user_tenant_id references tenant
);

-- Table: public.t_account

-- DROP TABLE public.t_account;

CREATE TABLE public.t_account
(
    id bigint NOT NULL DEFAULT nextval('t_user_id_seq'::regclass),
    username character varying COLLATE pg_catalog."default",
    email character varying COLLATE pg_catalog."default" NOT NULL,
    password character varying COLLATE pg_catalog."default" NOT NULL,
    gender smallint NOT NULL,
    status smallint NOT NULL,
    create_time timestamp without time zone NOT NULL,
    last_modify_time timestamp without time zone NOT NULL,
    enabled boolean NOT NULL,
    credentials_expired boolean NOT NULL,
    expired boolean NOT NULL,
    locked boolean NOT NULL,
    CONSTRAINT t_user_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.t_account
    OWNER to postgres;
COMMENT ON TABLE public.t_account
    IS 'User Information';



-- Table: public.t_role

-- DROP TABLE public.t_role;

CREATE TABLE public.t_role
(
    id bigint NOT NULL DEFAULT nextval('t_role_id_seq'::regclass),
    code character varying COLLATE pg_catalog."default" NOT NULL,
    label character varying COLLATE pg_catalog."default" NOT NULL,
    create_time timestamp without time zone NOT NULL,
    last_modify_time timestamp without time zone NOT NULL,
    CONSTRAINT t_role_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.t_role
    OWNER to postgres;
COMMENT ON TABLE public.t_role
    IS 'Role Information';


-- Table: public.t_permission

-- DROP TABLE public.t_permission;

CREATE TABLE public.t_permission
(
    id bigint NOT NULL DEFAULT nextval('t_permission_id_seq'::regclass),
    code character varying COLLATE pg_catalog."default" NOT NULL,
    label character varying COLLATE pg_catalog."default" NOT NULL,
    create_time timestamp without time zone NOT NULL,
    last_modify_time timestamp without time zone NOT NULL,
    CONSTRAINT t_permission_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.t_permission
    OWNER to postgres;
COMMENT ON TABLE public.t_permission
    IS 'Permission Information';


-- Table: public.t_account_role

-- DROP TABLE public.t_account_role;

CREATE TABLE public.t_account_role
(
    id bigint NOT NULL DEFAULT nextval('t_account_role_id_seq'::regclass),
    role_id bigint NOT NULL,
    account_id bigint NOT NULL,
    create_time timestamp without time zone NOT NULL,
    last_modify_time timestamp without time zone NOT NULL,
    CONSTRAINT t_account_role_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.t_account_role
    OWNER to postgres;
COMMENT ON TABLE public.t_account_role
    IS 'Account Role Information';


-- Table: public.t_role_permission

-- DROP TABLE public.t_role_permission;

CREATE TABLE public.t_role_permission
(
    id bigint NOT NULL DEFAULT nextval('t_role_permission_id_seq'::regclass),
    role_id bigint NOT NULL,
    permission_id bigint NOT NULL,
    create_time timestamp without time zone NOT NULL,
    last_modify_time timestamp without time zone NOT NULL,
    CONSTRAINT t_role_permission_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.t_role_permission
    OWNER to postgres;
COMMENT ON TABLE public.t_role_permission
    IS 'Role Permission Information';
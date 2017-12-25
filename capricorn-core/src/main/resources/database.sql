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

-- Table: public.t_content

-- DROP TABLE public.t_content;

CREATE TABLE public.t_content
(
    id bigint NOT NULL DEFAULT nextval('t_content_id_seq'::regclass),
    title character varying COLLATE pg_catalog."default" NOT NULL,
    summary character varying COLLATE pg_catalog."default" NOT NULL,
    content character varying COLLATE pg_catalog."default" NOT NULL,
    note character varying COLLATE pg_catalog."default" NOT NULL,
    user_id bigint NOT NULL,
    language jsonb NOT NULL,
    tags bigint[] NOT NULL,
    status smallint NOT NULL,
    create_time timestamp without time zone NOT NULL,
    last_modify_time timestamp without time zone NOT NULL,
    CONSTRAINT t_content_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.t_content
    OWNER to postgres;
COMMENT ON TABLE public.t_content
    IS 'Content';

-- Table: public.t_tag

-- DROP TABLE public.t_tag;

CREATE TABLE public.t_tag
(
    id bigint NOT NULL DEFAULT nextval('t_tag_id_seq'::regclass),
    code character varying COLLATE pg_catalog."default" NOT NULL,
    label character varying COLLATE pg_catalog."default" NOT NULL,
    type smallint NOT NULL,
    color character varying COLLATE pg_catalog."default" NOT NULL,
    user_id bigint NOT NULL,
    create_time timestamp without time zone NOT NULL,
    last_modify_time timestamp without time zone NOT NULL,
    CONSTRAINT t_tag_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.t_tag
    OWNER to postgres;
COMMENT ON TABLE public.t_tag
    IS 'Tag';

-- Table: public.t_share

-- DROP TABLE public.t_share;

CREATE TABLE public.t_share
(
    id bigint NOT NULL DEFAULT nextval('t_share_id_seq'::regclass),
    title character varying COLLATE pg_catalog."default" NOT NULL,
    path character varying COLLATE pg_catalog."default" NOT NULL,
    type smallint NOT NULL,
    authority smallint NOT NULL,
    open_count bigint NOT NULL,
    used_count bigint NOT NULL,
    start_time timestamp without time zone NOT NULL,
    end_time timestamp without time zone NOT NULL,
    content_id bigint NOT NULL,
    user_id bigint NOT NULL,
    scope jsonb NOT NULL,
    state smallint NOT NULL,
    create_time timestamp without time zone NOT NULL,
    last_modify_time timestamp without time zone NOT NULL,
    CONSTRAINT t_share_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.t_share
    OWNER to postgres;
COMMENT ON TABLE public.t_share
    IS 'Share';

-- Table: public.t_share_access

-- DROP TABLE public.t_share_access;

CREATE TABLE public.t_share_access
(
    id bigint NOT NULL DEFAULT nextval('t_share_access_id_seq'::regclass),
    share_id bigint NOT NULL,
    share_snapshoot jsonb NOT NULL,
    content_snapshoot character varying COLLATE pg_catalog."default" NOT NULL,
    access_by jsonb NOT NULL,
    access_result jsonb NOT NULL,
    create_time timestamp without time zone NOT NULL,
    last_modify_time timestamp without time zone NOT NULL,
    CONSTRAINT t_share_access_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.t_share_access
    OWNER to postgres;


-- Initialize Base Role
INSERT INTO public.t_role(
	code, label, create_time, last_modify_time)
	VALUES ('ROLE_CAPRICORN', 'Role Capricorn', now(), now());
INSERT INTO public.t_role(
	code, label, create_time, last_modify_time)
	VALUES ('ROLE_USER', 'User Base Role', now(), now());
INSERT INTO public.t_role(
	code, label, create_time, last_modify_time)
	VALUES ('ROLE_MANAGE', 'Role Manage', now(), now());

-- Initialize Admin User, default password capricorn
-- INSERT INTO public.t_account( username, email, password, gender, status, create_time, last_modify_time, enabled, credentials_expired, expired, locked)
-- VALUES ('username', 'email', '$2a$10$NiLXcpG06An0yRnMwohv2.Y1k5NX24QJpueIi1u02nQCH4t2AEgFe', '1', '1', now(), now(), true, false, false, false);

-- Initialize Admin User Role
-- INSERT INTO public.t_account_role( role_id, account_id, create_time, last_modify_time) VALUES (?, ?, now(), now());

-- t_content usage
-- 1. array:
-- ﻿select * from t_content where 33=any(tags)
-- 2. not json/jsonb type out json result:
-- select row_to_json(a.*) from (select b.id,b.title from t_content b) a;
-- with myInfo as (select a.id,a.title from t_content a) select row_to_json(b.*) from myInfo b;
-- 3. jsonb if data like: {"mime": "x/java", "name": "java"}
-- add prop: update t_content set language = '{"test":11}' || language where id=3;
-- set prop: update t_content set language = jsonb_set(language,'{mime}','"xx/java"',false) where id=3;
-- del prop: update t_content set language = language #- '{test}' where id=3;
-- search language.mime='x/java': select * from t_content a where a.language @> '{"mime":"x/java"}'::jsonb;
-- search create_by.id > 100000: select * from t_content a where cast(a.create_by->>'id' as int) > 100000;

-- <table schema="public" tableName="t_content" domainObjectName="Content">
--     <generatedKey column="id" sqlStatement="JDBC"/>
--     <columnOverride column="tags" property="tags" javaType="java.util.List"/>
--     <columnOverride column="language" property="language" javaType="com.benayn.constell.services.capricorn.type.Language"/>
-- </table>
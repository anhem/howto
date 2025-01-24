--
-- PostgreSQL database dump
--

-- Dumped from database version 14.5 (Debian 14.5-2.pgdg110+2)
-- Dumped by pg_dump version 14.5 (Debian 14.5-2.pgdg110+2)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: account; Type: TABLE; Schema: public; Owner: howto
--

CREATE TABLE public.account (
    account_id integer NOT NULL,
    username character varying(30) NOT NULL,
    email character varying(320) NOT NULL,
    first_name character varying(100),
    last_name character varying(100),
    created timestamp without time zone NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    last_login timestamp without time zone
);


ALTER TABLE public.account OWNER TO howto;

--
-- Name: account_account_id_seq; Type: SEQUENCE; Schema: public; Owner: howto
--

ALTER TABLE public.account ALTER COLUMN account_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.account_account_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: account_password; Type: TABLE; Schema: public; Owner: howto
--

CREATE TABLE public.account_password (
    account_password_id integer NOT NULL,
    account_id integer NOT NULL,
    password character varying(100) NOT NULL,
    created timestamp without time zone NOT NULL
);


ALTER TABLE public.account_password OWNER TO howto;

--
-- Name: account_password_account_password_id_seq; Type: SEQUENCE; Schema: public; Owner: howto
--

ALTER TABLE public.account_password ALTER COLUMN account_password_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.account_password_account_password_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: account_role; Type: TABLE; Schema: public; Owner: howto
--

CREATE TABLE public.account_role (
    account_role_id integer NOT NULL,
    account_id integer NOT NULL,
    role_id integer NOT NULL,
    created timestamp without time zone NOT NULL
);


ALTER TABLE public.account_role OWNER TO howto;

--
-- Name: account_role_account_role_id_seq; Type: SEQUENCE; Schema: public; Owner: howto
--

ALTER TABLE public.account_role ALTER COLUMN account_role_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.account_role_account_role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: category; Type: TABLE; Schema: public; Owner: howto
--

CREATE TABLE public.category (
    category_id integer NOT NULL,
    name character varying(100) NOT NULL,
    description character varying(320),
    created timestamp without time zone NOT NULL,
    last_updated timestamp without time zone NOT NULL
);


ALTER TABLE public.category OWNER TO howto;

--
-- Name: category_category_id_seq; Type: SEQUENCE; Schema: public; Owner: howto
--

ALTER TABLE public.category ALTER COLUMN category_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.category_category_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: flyway_schema_history; Type: TABLE; Schema: public; Owner: howto
--

CREATE TABLE public.flyway_schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


ALTER TABLE public.flyway_schema_history OWNER TO howto;

--
-- Name: post; Type: TABLE; Schema: public; Owner: howto
--

CREATE TABLE public.post (
    post_id integer NOT NULL,
    category_id integer NOT NULL,
    account_id integer NOT NULL,
    title character varying(100) NOT NULL,
    body text NOT NULL,
    created timestamp without time zone NOT NULL,
    last_updated timestamp without time zone NOT NULL
);


ALTER TABLE public.post OWNER TO howto;

--
-- Name: post_post_id_seq; Type: SEQUENCE; Schema: public; Owner: howto
--

ALTER TABLE public.post ALTER COLUMN post_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.post_post_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: reply; Type: TABLE; Schema: public; Owner: howto
--

CREATE TABLE public.reply (
    reply_id integer NOT NULL,
    post_id integer NOT NULL,
    account_id integer NOT NULL,
    body text NOT NULL,
    created timestamp without time zone NOT NULL,
    last_updated timestamp without time zone NOT NULL
);


ALTER TABLE public.reply OWNER TO howto;

--
-- Name: reply_reply_id_seq; Type: SEQUENCE; Schema: public; Owner: howto
--

ALTER TABLE public.reply ALTER COLUMN reply_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.reply_reply_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: role; Type: TABLE; Schema: public; Owner: howto
--

CREATE TABLE public.role (
    role_id integer NOT NULL,
    role_name character varying(30) NOT NULL,
    description character varying(320) NOT NULL,
    created timestamp without time zone NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    CONSTRAINT role_role_name_check CHECK ((upper((role_name)::text) = (role_name)::text))
);


ALTER TABLE public.role OWNER TO howto;

--
-- Name: role_role_id_seq; Type: SEQUENCE; Schema: public; Owner: howto
--

ALTER TABLE public.role ALTER COLUMN role_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.role_role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Data for Name: account; Type: TABLE DATA; Schema: public; Owner: howto
--

COPY public.account (account_id, username, email, first_name, last_name, created, last_updated, last_login) FROM stdin;
\.


--
-- Data for Name: account_password; Type: TABLE DATA; Schema: public; Owner: howto
--

COPY public.account_password (account_password_id, account_id, password, created) FROM stdin;
\.


--
-- Data for Name: account_role; Type: TABLE DATA; Schema: public; Owner: howto
--

COPY public.account_role (account_role_id, account_id, role_id, created) FROM stdin;
\.


--
-- Data for Name: category; Type: TABLE DATA; Schema: public; Owner: howto
--

COPY public.category (category_id, name, description, created, last_updated) FROM stdin;
\.


--
-- Data for Name: flyway_schema_history; Type: TABLE DATA; Schema: public; Owner: howto
--

COPY public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) FROM stdin;
1	1	account tables	SQL	V1__account_tables.sql	518055462	howto	2025-01-24 14:12:45.276043	34	t
2	2	role tables	SQL	V2__role_tables.sql	-1043952335	howto	2025-01-24 14:12:45.336193	28	t
3	3	forum tables	SQL	V3__forum_tables.sql	885053491	howto	2025-01-24 14:12:45.377604	34	t
\.


--
-- Data for Name: post; Type: TABLE DATA; Schema: public; Owner: howto
--

COPY public.post (post_id, category_id, account_id, title, body, created, last_updated) FROM stdin;
\.


--
-- Data for Name: reply; Type: TABLE DATA; Schema: public; Owner: howto
--

COPY public.reply (reply_id, post_id, account_id, body, created, last_updated) FROM stdin;
\.


--
-- Data for Name: role; Type: TABLE DATA; Schema: public; Owner: howto
--

COPY public.role (role_id, role_name, description, created, last_updated) FROM stdin;
1	ADMINISTRATOR	An Administrator account to access restricted resources	2025-01-24 14:12:45.34173	2025-01-24 14:12:45.34173
2	USER	A regular account	2025-01-24 14:12:45.34173	2025-01-24 14:12:45.34173
3	MODERATOR	An account that can access some restricted resources	2025-01-24 14:12:45.34173	2025-01-24 14:12:45.34173
\.


--
-- Name: account_account_id_seq; Type: SEQUENCE SET; Schema: public; Owner: howto
--

SELECT pg_catalog.setval('public.account_account_id_seq', 1, false);


--
-- Name: account_password_account_password_id_seq; Type: SEQUENCE SET; Schema: public; Owner: howto
--

SELECT pg_catalog.setval('public.account_password_account_password_id_seq', 1, false);


--
-- Name: account_role_account_role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: howto
--

SELECT pg_catalog.setval('public.account_role_account_role_id_seq', 1, false);


--
-- Name: category_category_id_seq; Type: SEQUENCE SET; Schema: public; Owner: howto
--

SELECT pg_catalog.setval('public.category_category_id_seq', 1, false);


--
-- Name: post_post_id_seq; Type: SEQUENCE SET; Schema: public; Owner: howto
--

SELECT pg_catalog.setval('public.post_post_id_seq', 1, false);


--
-- Name: reply_reply_id_seq; Type: SEQUENCE SET; Schema: public; Owner: howto
--

SELECT pg_catalog.setval('public.reply_reply_id_seq', 1, false);


--
-- Name: role_role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: howto
--

SELECT pg_catalog.setval('public.role_role_id_seq', 3, true);


--
-- Name: account account_email_key; Type: CONSTRAINT; Schema: public; Owner: howto
--

ALTER TABLE ONLY public.account
    ADD CONSTRAINT account_email_key UNIQUE (email);


--
-- Name: account_password account_password_account_id_key; Type: CONSTRAINT; Schema: public; Owner: howto
--

ALTER TABLE ONLY public.account_password
    ADD CONSTRAINT account_password_account_id_key UNIQUE (account_id);


--
-- Name: account_password account_password_pkey; Type: CONSTRAINT; Schema: public; Owner: howto
--

ALTER TABLE ONLY public.account_password
    ADD CONSTRAINT account_password_pkey PRIMARY KEY (account_password_id);


--
-- Name: account account_pkey; Type: CONSTRAINT; Schema: public; Owner: howto
--

ALTER TABLE ONLY public.account
    ADD CONSTRAINT account_pkey PRIMARY KEY (account_id);


--
-- Name: account_role account_role_account_id_role_id_key; Type: CONSTRAINT; Schema: public; Owner: howto
--

ALTER TABLE ONLY public.account_role
    ADD CONSTRAINT account_role_account_id_role_id_key UNIQUE (account_id, role_id);


--
-- Name: account_role account_role_pkey; Type: CONSTRAINT; Schema: public; Owner: howto
--

ALTER TABLE ONLY public.account_role
    ADD CONSTRAINT account_role_pkey PRIMARY KEY (account_role_id);


--
-- Name: account account_username_key; Type: CONSTRAINT; Schema: public; Owner: howto
--

ALTER TABLE ONLY public.account
    ADD CONSTRAINT account_username_key UNIQUE (username);


--
-- Name: category category_name_key; Type: CONSTRAINT; Schema: public; Owner: howto
--

ALTER TABLE ONLY public.category
    ADD CONSTRAINT category_name_key UNIQUE (name);


--
-- Name: category category_pkey; Type: CONSTRAINT; Schema: public; Owner: howto
--

ALTER TABLE ONLY public.category
    ADD CONSTRAINT category_pkey PRIMARY KEY (category_id);


--
-- Name: flyway_schema_history flyway_schema_history_pk; Type: CONSTRAINT; Schema: public; Owner: howto
--

ALTER TABLE ONLY public.flyway_schema_history
    ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);


--
-- Name: post post_pkey; Type: CONSTRAINT; Schema: public; Owner: howto
--

ALTER TABLE ONLY public.post
    ADD CONSTRAINT post_pkey PRIMARY KEY (post_id);


--
-- Name: reply reply_pkey; Type: CONSTRAINT; Schema: public; Owner: howto
--

ALTER TABLE ONLY public.reply
    ADD CONSTRAINT reply_pkey PRIMARY KEY (reply_id);


--
-- Name: role role_description_key; Type: CONSTRAINT; Schema: public; Owner: howto
--

ALTER TABLE ONLY public.role
    ADD CONSTRAINT role_description_key UNIQUE (description);


--
-- Name: role role_pkey; Type: CONSTRAINT; Schema: public; Owner: howto
--

ALTER TABLE ONLY public.role
    ADD CONSTRAINT role_pkey PRIMARY KEY (role_id);


--
-- Name: role role_role_name_key; Type: CONSTRAINT; Schema: public; Owner: howto
--

ALTER TABLE ONLY public.role
    ADD CONSTRAINT role_role_name_key UNIQUE (role_name);


--
-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: public; Owner: howto
--

CREATE INDEX flyway_schema_history_s_idx ON public.flyway_schema_history USING btree (success);


--
-- Name: account_password account_id; Type: FK CONSTRAINT; Schema: public; Owner: howto
--

ALTER TABLE ONLY public.account_password
    ADD CONSTRAINT account_id FOREIGN KEY (account_id) REFERENCES public.account(account_id);


--
-- Name: account_role account_id; Type: FK CONSTRAINT; Schema: public; Owner: howto
--

ALTER TABLE ONLY public.account_role
    ADD CONSTRAINT account_id FOREIGN KEY (account_id) REFERENCES public.account(account_id);


--
-- Name: reply account_id; Type: FK CONSTRAINT; Schema: public; Owner: howto
--

ALTER TABLE ONLY public.reply
    ADD CONSTRAINT account_id FOREIGN KEY (account_id) REFERENCES public.account(account_id);


--
-- Name: post category_id; Type: FK CONSTRAINT; Schema: public; Owner: howto
--

ALTER TABLE ONLY public.post
    ADD CONSTRAINT category_id FOREIGN KEY (category_id) REFERENCES public.category(category_id);


--
-- Name: reply post_id; Type: FK CONSTRAINT; Schema: public; Owner: howto
--

ALTER TABLE ONLY public.reply
    ADD CONSTRAINT post_id FOREIGN KEY (post_id) REFERENCES public.post(post_id);


--
-- Name: account_role role_id; Type: FK CONSTRAINT; Schema: public; Owner: howto
--

ALTER TABLE ONLY public.account_role
    ADD CONSTRAINT role_id FOREIGN KEY (role_id) REFERENCES public.role(role_id);


--
-- PostgreSQL database dump complete
--


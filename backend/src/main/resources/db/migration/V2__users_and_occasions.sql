-- Users
CREATE SEQUENCE IF NOT EXISTS users_id_seq;

CREATE TABLE users (
    id int8 NOT NULL DEFAULT nextval('users_id_seq'::regclass),
    name text NOT NULL,
    created timestamp NOT NULL DEFAULT now(),

    PRIMARY KEY (id)
);

-- Occasions
CREATE TABLE public."occasions" (
    user_id int8 NOT NULL,
    start_date date NOT NULL,
    end_date date,
    is_present bool NOT NULL DEFAULT true,

    CONSTRAINT FK_occasions_user_id_users_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, start_date)
);
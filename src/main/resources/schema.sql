CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(512) NOT NULL,
  CONSTRAINT pk_user PRIMARY KEY (id),
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS item_requests (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY,
  description VARCHAR(512) NOT NULL,
  requestor_id BIGINT REFERENCES users(id) ON DELETE CASCADE NOT NULL,
  created TIMESTAMP NOT NULL,
  CONSTRAINT pk_item_request PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS items (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(512) NOT NULL,
  available BOOLEAN NOT NULL,
  owner_id BIGINT REFERENCES users(id) ON DELETE CASCADE NOT NULL,
  item_request_id BIGINT REFERENCES item_requests(id),
  CONSTRAINT pk_item PRIMARY KEY (id),
  CONSTRAINT not_blank_item_name CHECK (name <> ''),
  CONSTRAINT not_blank_item_description CHECK (description <> '')
);

CREATE TABLE IF NOT EXISTS bookings (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY,
  start_time TIMESTAMP NOT NULL,
  end_time TIMESTAMP NOT NULL,
  item_id BIGINT REFERENCES items(id) ON DELETE CASCADE NOT NULL,
  booker_id BIGINT REFERENCES users(id) ON DELETE CASCADE NOT NULL,
  status VARCHAR(20) NOT NULL,
  CONSTRAINT check_status CHECK (status IN ('WAITING', 'APPROVED', 'REJECTED', 'CANCELED')),
  CONSTRAINT pk_booking PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS comments (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY,
  text VARCHAR(1800) NOT NULL,
  item_id BIGINT REFERENCES items(id) ON DELETE CASCADE NOT NULL,
  author_id BIGINT REFERENCES users(id) ON DELETE CASCADE NOT NULL,
  CONSTRAINT pk_comment PRIMARY KEY (id)
);









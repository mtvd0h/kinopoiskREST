CREATE TABLE films
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    kinopoisk_id     INT,
    name_ru          VARCHAR(255),
    year             INT,
    rating_kinopoisk DECIMAL(3, 1),
    description      TEXT
);

CREATE TABLE countries
(
    id      INT AUTO_INCREMENT PRIMARY KEY,
    country VARCHAR(100)
);

CREATE TABLE genres
(
    id    INT AUTO_INCREMENT PRIMARY KEY,
    genre VARCHAR(50)
);

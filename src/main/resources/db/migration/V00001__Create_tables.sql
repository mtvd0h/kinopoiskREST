CREATE TABLE films
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    kinopoisk_id     INT,
    name_ru          VARCHAR(255),
    year             INT,
    rating_kinopoisk DECIMAL(3, 1),
    description      VARCHAR(2048)
);

CREATE TABLE countries
(
    id      INT PRIMARY KEY,
    country VARCHAR(100)
);

CREATE TABLE genres
(
    id    INT PRIMARY KEY,
    genre VARCHAR(50)
);
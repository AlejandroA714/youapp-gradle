SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "-06:00";

CREATE TABLE `authorities` (
                               `id` int NOT NULL,
                               `name` varchar(100) NOT NULL,
                               `description` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Estructura de tabla para la tabla `roles`
--

CREATE TABLE `roles` (
                         `id` int NOT NULL,
                         `name` varchar(100) NOT NULL,
                         `description` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `roles`
--

-- INSERT INTO `roles` (`id`, `name`, `description`) VALUES
--                                                      (1, 'ROLE_ADMIN', 'Administrador con todos los permisos'),
--                                                      (2, 'ROLE_USER', 'Usuario estándar');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `role_authorities`
--

CREATE TABLE `role_authorities` (
                                    `role_id` int NOT NULL,
                                    `authority_id` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Estructura de tabla para la tabla `users`
--

CREATE TABLE `users` (
                         `id` int NOT NULL,
                         `username` varchar(100) NOT NULL,
                         `password` varchar(255) NOT NULL,
                         `email` varchar(255) NOT NULL,
                         `profile_picture_url` varchar(512) DEFAULT NULL,
                         `enabled` tinyint(1) NOT NULL,
                         `registered_at` timestamp NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- valejo: password

INSERT INTO `users` (`id`, `username`, `password`, `email`, `profile_picture_url`, `enabled`, `registered_at`) VALUES
    (1, 'admin', '$2a$12$v2.R8DoOCnFVM6VSlM0yB.J3ABSUP8CaqUdAa2D3UyrsVCK4MEvwe', 'admin@youapp.sv', NULL, 1, '2025-09-15 22:14:30');


CREATE TABLE `user_authorities` (
                                    `user_id` int NOT NULL,
                                    `authority_id` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `user_roles` (
                              `user_id` int NOT NULL,
                              `role_id` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



ALTER TABLE `authorities`
    ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);


ALTER TABLE `roles`
    ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);


ALTER TABLE `role_authorities`
    ADD PRIMARY KEY (`role_id`,`authority_id`),
  ADD KEY `authority_id` (`authority_id`);


ALTER TABLE `users`
    ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`);


ALTER TABLE `user_authorities`
    ADD PRIMARY KEY (`user_id`,`authority_id`),
  ADD KEY `authority_id` (`authority_id`);

ALTER TABLE `user_roles`
    ADD PRIMARY KEY (`user_id`,`role_id`),
  ADD KEY `role_id` (`role_id`);


ALTER TABLE `authorities`
    MODIFY `id` int NOT NULL AUTO_INCREMENT;

ALTER TABLE `roles`
    MODIFY `id` int NOT NULL AUTO_INCREMENT;

ALTER TABLE `users`
    MODIFY `id` int NOT NULL AUTO_INCREMENT;

ALTER TABLE `role_authorities`
    ADD CONSTRAINT `role_authorities_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `role_authorities_ibfk_2` FOREIGN KEY (`authority_id`) REFERENCES `authorities` (`id`) ON DELETE CASCADE;

ALTER TABLE `user_authorities`
    ADD CONSTRAINT `user_authorities_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `user_authorities_ibfk_2` FOREIGN KEY (`authority_id`) REFERENCES `authorities` (`id`) ON DELETE CASCADE;

ALTER TABLE `user_roles`
    ADD CONSTRAINT `user_roles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `user_roles_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE;
COMMIT;

-- infrastructure

CREATE TABLE client (
        id VARCHAR(255) NOT NULL,
        client_id VARCHAR(255) NOT NULL UNIQUE,
        client_id_issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
        client_secret VARCHAR(255) DEFAULT NULL,
        client_secret_expires_at TIMESTAMP DEFAULT NULL,
        client_name VARCHAR(255) NULL,
        PRIMARY KEY (id)
);

CREATE TABLE scope (
       id INT AUTO_INCREMENT PRIMARY KEY,
       name VARCHAR(200) NOT NULL UNIQUE
);

CREATE TABLE authentication_method (
       id INT AUTO_INCREMENT PRIMARY KEY,
       name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE grant_type (
        id INT AUTO_INCREMENT PRIMARY KEY,
        name VARCHAR(200) NOT NULL UNIQUE
);

CREATE TABLE client_authentication_method (
      client_id VARCHAR(255) NOT NULL,
      method_id INT NOT NULL,
      PRIMARY KEY (client_id, method_id),
      FOREIGN KEY (client_id) REFERENCES client(id),
      FOREIGN KEY (method_id) REFERENCES authentication_method(id)
);

CREATE TABLE client_grant_type (
       client_id VARCHAR(255) NOT NULL,
       grant_type_id INT NOT NULL,
       PRIMARY KEY (client_id, grant_type_id),
       FOREIGN KEY (client_id) REFERENCES client(id),
       FOREIGN KEY (grant_type_id) REFERENCES grant_type(id)
);

CREATE TABLE client_scope (
      client_id VARCHAR(255) NOT NULL,
      scope_id INT NOT NULL,
      PRIMARY KEY (client_id, scope_id),
      FOREIGN KEY (client_id) REFERENCES client(id),
      FOREIGN KEY (scope_id) REFERENCES scope(id)
);

CREATE TABLE client_redirect_uri (
     id INT AUTO_INCREMENT PRIMARY KEY,
     client_id VARCHAR(255) NOT NULL,
     redirect_uri VARCHAR(255) NOT NULL,
     FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE CASCADE
);

CREATE TABLE client_post_logout_redirect_uri (
     id INT AUTO_INCREMENT PRIMARY KEY,
     client_id VARCHAR(255) NOT NULL,
     redirect_uri VARCHAR(255) NOT NULL,
     FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE CASCADE
);

CREATE TABLE client_settings (
     id INT AUTO_INCREMENT PRIMARY KEY,
     client_id VARCHAR(255) NOT NULL,
     require_proof_key BOOLEAN NOT NULL DEFAULT TRUE,
     require_authorization_consent BOOLEAN NOT NULL DEFAULT FALSE,
     FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE CASCADE
);

CREATE TABLE token_settings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    client_id VARCHAR(255) NOT NULL,
    access_token_ttl INT UNSIGNED DEFAULT 57600,
    refresh_token_ttl INT UNSIGNED DEFAULT 691200,
    reuse_refresh_tokens BOOLEAN DEFAULT FALSE,
    access_token_format VARCHAR(32) DEFAULT 'self-contained',
    FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE CASCADE
);

INSERT INTO client (
    id, client_id, client_id_issued_at, client_secret, client_secret_expires_at, client_name
) VALUES (
             '2be391ca-c05c-4b57-8839-925b0f014ccb',
             'oidc-client',
             NOW(),
             '$2a$12$q1rY2JnWSH/xF/bGvnEM7eTbCjRfTxRw7gDd2DWZ/AeKPNfCWCvHq',
             NULL,
             'OIDC Client'
         );

INSERT INTO authentication_method (id,name) VALUES
                                             (1,'client_secret_basic'),
                                             (2,'client_secret_post'),
                                             (3,'client_secret_jwt'),
                                             (4,'private_key_jwt'),
                                             (5,'none'),
                                             (6,'tls_client_auth'),
                                             (7,'self_signed_tls_client_auth');

INSERT INTO grant_type (id, name) VALUES
                                      (1, 'authorization_code'),
                                      (2, 'client_credentials'),
                                      (3, 'refresh_token'),
                                      (4, 'urn:ietf:params:oauth:grant-type:jwt-bearer'),
                                      (5, 'urn:ietf:params:oauth:grant-type:device_code'),
                                      (6, 'urn:ietf:params:oauth:grant-type:token-exchange'),
                                      (7, 'urn:ietf:params:oauth:grant-type:native');

-- authorization_code
INSERT INTO client_grant_type (client_id, grant_type_id)
VALUES ('2be391ca-c05c-4b57-8839-925b0f014ccb', 1);

-- native
INSERT INTO client_grant_type (client_id, grant_type_id)
VALUES ('2be391ca-c05c-4b57-8839-925b0f014ccb', 7);

-- refresh_token
INSERT INTO client_grant_type (client_id, grant_type_id)
VALUES ('2be391ca-c05c-4b57-8839-925b0f014ccb', 3);

-- client_secret_basic
INSERT INTO client_authentication_method (client_id, method_id)
VALUES ('2be391ca-c05c-4b57-8839-925b0f014ccb', 1);

-- client_secret_post
INSERT INTO client_authentication_method (client_id, method_id)
VALUES ('2be391ca-c05c-4b57-8839-925b0f014ccb', 2);

-- none
INSERT INTO client_authentication_method (client_id, method_id)
VALUES ('2be391ca-c05c-4b57-8839-925b0f014ccb', 5);

INSERT INTO client_redirect_uri (client_id, redirect_uri)
VALUES ('2be391ca-c05c-4b57-8839-925b0f014ccb', 'http://authorization-server/oauth2/callback');

INSERT INTO client_settings (client_id)
VALUES ('2be391ca-c05c-4b57-8839-925b0f014ccb');

INSERT INTO token_settings (client_id)
VALUES ('2be391ca-c05c-4b57-8839-925b0f014ccb')


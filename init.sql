CREATE DATABASE keycloakdb;
CREATE USER keycloak WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE keycloakdb TO keycloak;
\c keycloakdb;
GRANT ALL ON SCHEMA public TO keycloak;
ALTER SCHEMA public OWNER TO keycloak;
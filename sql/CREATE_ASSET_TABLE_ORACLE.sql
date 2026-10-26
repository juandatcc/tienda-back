-- Script para crear la tabla ASSET en Oracle
-- Ejecutar como el schema/usuario conectado a la base de datos configurada en application.properties

CREATE TABLE ASSET (
    ID VARCHAR2(36) PRIMARY KEY,  -- UUID generado por la aplicación
    FILENAME VARCHAR2(255) NOT NULL,
    FILE_PATH VARCHAR2(4000),
    CONTENT BLOB,
    CONTENT_TYPE VARCHAR2(255),
    FILE_SIZE NUMBER,
    CREATED_AT TIMESTAMP WITH TIME ZONE
);

-- Índice para búsquedas por filename (opcional)
CREATE INDEX IDX_ASSET_FILENAME ON ASSET(FILENAME);


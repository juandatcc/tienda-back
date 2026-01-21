-- Agrega columna imagen_url a la tabla producto
ALTER TABLE producto ADD imagen_url VARCHAR2(500);

-- Verifica la columna
COLUMN imagen_url FORMAT A50
SELECT imagen_url FROM producto WHERE ROWNUM <= 1;


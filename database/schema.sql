DROP DATABASE IF EXISTS mediacloud;
CREATE DATABASE mediacloud CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mediacloud;

CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    correo VARCHAR(160) NOT NULL UNIQUE,
    clave VARCHAR(64) NOT NULL,
    rol ENUM('ADMINISTRADOR', 'USUARIO') NOT NULL DEFAULT 'USUARIO',
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categorias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

CREATE TABLE albumes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    nombre VARCHAR(120) NOT NULL,
    descripcion VARCHAR(255),
    publico BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_album_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE archivos_multimedia (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    album_id INT NULL,
    categoria_id INT NULL,
    nombre_original VARCHAR(255) NOT NULL,
    nombre_guardado VARCHAR(255) NOT NULL,
    tipo_mime VARCHAR(120) NOT NULL,
    tamanio_bytes BIGINT NOT NULL,
    ruta_relativa VARCHAR(500) NOT NULL,
    estado ENUM('ACTIVO', 'EN_PAPELERA', 'ELIMINADO') NOT NULL DEFAULT 'ACTIVO',
    fecha_carga TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_eliminacion TIMESTAMP NULL,
    CONSTRAINT fk_archivo_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    CONSTRAINT fk_archivo_album FOREIGN KEY (album_id) REFERENCES albumes(id),
    CONSTRAINT fk_archivo_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

CREATE TABLE archivos_compartidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    archivo_id INT NOT NULL,
    usuario_origen_id INT NOT NULL,
    usuario_destino_id INT NOT NULL,
    fecha_compartido TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_compartido_archivo FOREIGN KEY (archivo_id) REFERENCES archivos_multimedia(id),
    CONSTRAINT fk_compartido_origen FOREIGN KEY (usuario_origen_id) REFERENCES usuarios(id),
    CONSTRAINT fk_compartido_destino FOREIGN KEY (usuario_destino_id) REFERENCES usuarios(id)
);

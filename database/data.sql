USE mediacloud;

INSERT INTO usuarios (nombre, correo, clave, rol)
VALUES
('Administrador MediaCloud', 'admin@mediacloud.local', SHA2('admin123', 256), 'ADMINISTRADOR'),
('Usuario de Prueba', 'usuario@mediacloud.local', SHA2('usuario123', 256), 'USUARIO');

INSERT INTO categorias (nombre, descripcion)
VALUES
('Fotografias', 'Imagenes personales y familiares'),
('Videos', 'Clips y grabaciones locales'),
('Audio', 'Musica, notas de voz y podcasts'),
('Documentos visuales', 'Imagenes o documentos escaneados');

INSERT INTO albumes (usuario_id, nombre, descripcion, publico)
VALUES
(2, 'Recuerdos familiares', 'Album inicial de fotografias personales', FALSE),
(2, 'Proyecto universidad', 'Material multimedia relacionado con cursos', TRUE);

INSERT INTO archivos_multimedia
(usuario_id, album_id, categoria_id, nombre_original, nombre_guardado, tipo_mime, tamanio_bytes, ruta_relativa, estado)
VALUES
(2, 1, 1, 'playa.jpg', 'demo-playa.jpg', 'image/jpeg', 204800, 'uploads/2/demo-playa.jpg', 'ACTIVO'),
(2, 2, 2, 'presentacion.mp4', 'demo-presentacion.mp4', 'video/mp4', 10485760, 'uploads/2/demo-presentacion.mp4', 'ACTIVO');

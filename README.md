# MediaCloud - Spring Boot

Sistema web de gestión multimedia local desarrollado con Java 17, Spring Boot, Thymeleaf, JDBC y MySQL.

## Funciones principales

- Inicio y cierre de sesión.
- Manejo de sesión con permisos básicos por rol.
- Roles: ADMINISTRADOR y USUARIO.
- CRUD de usuarios.
- CRUD de categorías.
- CRUD de álbumes.
- CRUD/carga de archivos multimedia.
- Filtros por estado y categoría.
- Reporte simple de archivos por categoría.
- Conexión real con MySQL mediante JDBC/Spring JdbcTemplate.

## Requisitos

- Java 17 o superior.
- Maven.
- MySQL.
- Visual Studio Code con extensiones de Java y Spring Boot.

## Instalación

1. Crear la base de datos ejecutando:

```sql
source database/schema.sql;
source database/data.sql;
```

También puede copiar y ejecutar ambos scripts desde MySQL Workbench.

2. Revisar credenciales en:

```text
src/main/resources/application.properties
```

Por defecto:

```text
spring.datasource.username=root
spring.datasource.password=
```

3. Ejecutar el proyecto:

```bash
mvn spring-boot:run
```

4. Abrir en el navegador:

```text
http://localhost:8080
```

## Usuarios de prueba

Administrador:

```text
admin@mediacloud.local
admin123
```

Usuario regular:

```text
usuario@mediacloud.local
usuario123
```

## Arquitectura

El proyecto usa una arquitectura por capas:

- `controller`: recibe solicitudes web y devuelve vistas.
- `repository`: contiene consultas SQL mediante JdbcTemplate.
- `model`: entidades del sistema.
- `config`: interceptor de sesión y configuración web.
- `templates`: vistas Thymeleaf.
- `static/css`: estilos visuales.
- `database`: scripts SQL.

## Nota de defensa

Aunque el descriptor original menciona JSP y Servlets, esta versión usa Spring Boot MVC con Thymeleaf. La idea para defenderlo es explicar que Spring Boot mantiene el patrón MVC, mejora la organización por capas y permite una aplicación más moderna y fácil de mantener, conservando MySQL y JDBC como base de persistencia.

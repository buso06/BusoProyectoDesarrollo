# MediaCloud - Spring Boot

MediaCloud es un sistema web para gestionar archivos multimedia de forma local. La idea principal del proyecto es permitir que un usuario pueda subir archivos, organizarlos por categorías y álbumes, verlos desde el navegador y administrar la información desde una interfaz sencilla.

El proyecto fue desarrollado con Java 17, Spring Boot, Thymeleaf, JDBC, MySQL y Maven.

## Objetivo del proyecto

Este sistema funciona como una pequeña nube local. No está pensado para ser un servicio en internet como Google Drive, sino como una aplicación web que guarda los archivos en una carpeta del equipo o servidor, y registra la información de esos archivos en una base de datos MySQL.

El propósito principal es practicar el desarrollo de una aplicación web completa usando Spring Boot, conexión a base de datos, manejo de sesiones, vistas con Thymeleaf y operaciones CRUD.

## Tecnologías utilizadas

* Java 17
* Spring Boot
* Thymeleaf
* Spring JDBC / JdbcTemplate
* MySQL
* Maven
* HTML
* CSS
* Visual Studio Code

## Funciones principales

El sistema actualmente cuenta con las siguientes funciones:

* Inicio de sesión.
* Cierre de sesión.
* Manejo básico de sesión.
* Roles de usuario: ADMINISTRADOR y USUARIO.
* Registro, edición, listado y eliminación de usuarios.
* Registro, edición, listado y eliminación de categorías.
* Registro, edición, listado y eliminación de álbumes.
* Subida de archivos multimedia.
* Vista previa de archivos en la tabla de archivos.
* Visualización y descarga de archivos.
* Envío de archivos a papelera.
* Eliminación definitiva de archivos.
* Filtros por estado y categoría.
* Reporte simple de archivos por categoría.

## Estructura general del proyecto

El proyecto está organizado por capas para que sea más fácil mantenerlo.

```text
src/main/java/cr/ac/ucr/mediacloud
```

Dentro de esa carpeta están los paquetes principales:

```text
config
controller
model
repository
```

### config

Aquí se encuentra la configuración relacionada con la navegación y la sesión del usuario.

Por ejemplo, el interceptor revisa si el usuario tiene una sesión activa antes de permitirle entrar a ciertas páginas del sistema.

### controller

En esta carpeta están los controladores de Spring Boot. Estos reciben las solicitudes del navegador y devuelven las vistas correspondientes.

Algunos ejemplos:

```text
ArchivoController
AlbumController
CategoriaController
UsuarioController
AuthController
DashboardController
```

Los controladores no deberían tener demasiada lógica pesada. Su función principal es recibir datos, llamar al repositorio correspondiente y redirigir a una vista.

### model

Aquí están las clases que representan las entidades principales del sistema.

Por ejemplo:

```text
Usuario
Album
Categoria
ArchivoMultimedia
EstadoArchivo
RolUsuario
```

Estas clases se usan para transportar información entre la base de datos, los controladores y las vistas.

### repository

Aquí se encuentran las clases encargadas de comunicarse con MySQL usando `JdbcTemplate`.

Por ejemplo:

```text
ArchivoRepository
AlbumRepository
CategoriaRepository
UsuarioRepository
```

En este proyecto no se usa JPA ni Hibernate. Las consultas SQL se escriben directamente dentro de los repositorios.

### templates

Las vistas HTML están en:

```text
src/main/resources/templates
```

El sistema usa Thymeleaf, por eso los archivos tienen expresiones como:

```html
th:text
th:href
th:each
th:if
```

Cada módulo tiene su propia carpeta de vistas.

Ejemplo:

```text
templates/archivos
templates/albumes
templates/categorias
templates/usuarios
templates/dashboard
templates/auth
```

### static

Los archivos estáticos están en:

```text
src/main/resources/static
```

Actualmente el CSS principal está en:

```text
src/main/resources/static/css/styles.css
```

## Base de datos

El sistema utiliza MySQL. La información de usuarios, álbumes, categorías y archivos se guarda en la base de datos.

En el repositorio se incluyen archivos SQL para facilitar la instalación.

También se incluye un respaldo llamado:

```text
mediacloud.sql
```

Este archivo contiene una copia de la base de datos usada durante las pruebas del proyecto.

## Carpeta de archivos subidos

Los archivos multimedia no se guardan directamente dentro de la base de datos. La base de datos solo guarda información como el nombre, tipo, tamaño, estado y ruta del archivo.

Los archivos físicos se guardan en la carpeta:

```text
uploads
```

Esta carpeta debe mantenerse en el proyecto para que las vistas previas, descargas y visualización de archivos funcionen correctamente.

La ubicación de esta carpeta se configura en:

```text
src/main/resources/application.properties
```

Debe existir una línea parecida a esta:

```properties
mediacloud.upload-dir=uploads
```

Si se cambia esta ruta, también hay que asegurarse de mover los archivos físicos a la nueva ubicación.

## Requisitos para ejecutar el proyecto

Antes de ejecutar el sistema, se necesita tener instalado:

* Java 17 o superior.
* Maven.
* MySQL.
* Visual Studio Code o algún IDE compatible con Spring Boot.

También se recomienda tener instaladas las extensiones de Java y Spring Boot en Visual Studio Code.

## Instalación del proyecto

Primero se debe clonar el repositorio:

```bash
git clone URL_DEL_REPOSITORIO
```

Luego se entra a la carpeta del proyecto:

```bash
cd MediaCloudSpringBootProject
```

Después se debe crear la base de datos en MySQL.

Si se va a usar el respaldo completo, primero se crea la base:

```sql
CREATE DATABASE mediacloud;
```

Luego se importa el archivo:

```bash
mysql -u root -p mediacloud < mediacloud.sql
```

También se pueden usar los scripts de la carpeta `database`, si se desea crear la estructura y los datos iniciales por separado.

```sql
source database/schema.sql;
source database/data.sql;
```

## Configuración

La configuración principal está en:

```text
src/main/resources/application.properties
```

Ahí se revisan datos como:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mediacloud
spring.datasource.username=root
spring.datasource.password=
mediacloud.upload-dir=uploads
```

Cada programador debe adaptar el usuario y la contraseña de MySQL según su equipo.

Por ejemplo, si MySQL tiene contraseña, se debe colocar aquí:

```properties
spring.datasource.password=MI_CONTRASEÑA
```

## Ejecución

Para ejecutar el proyecto desde la terminal:

```bash
mvn spring-boot:run
```

Luego se abre el navegador en:

```text
http://localhost:8080
```

## Usuarios de prueba

El sistema incluye usuarios de prueba para ingresar rápidamente.

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

Estos usuarios pueden modificarse desde la base de datos o desde el módulo de usuarios, dependiendo del rol con el que se ingrese.

## Módulo de archivos

Este es uno de los módulos principales del sistema.

Permite:

* Subir archivos.
* Asignarles álbum.
* Asignarles categoría.
* Ver una vista previa.
* Abrir el archivo en el navegador.
* Descargar el archivo.
* Enviar el archivo a papelera.
* Eliminarlo definitivamente.

Cuando se sube un archivo, el sistema genera un nombre interno único para evitar problemas si dos archivos tienen el mismo nombre.

El nombre original se conserva para mostrarlo al usuario.

## Papelera y eliminación definitiva

El sistema maneja dos ideas diferentes:

### Enviar a papelera

Cuando un archivo se envía a papelera, no se borra completamente. Solo cambia su estado en la base de datos.

Esto permite ocultarlo o separarlo de los archivos activos.

### Eliminar definitivamente

Cuando un archivo se elimina definitivamente, el sistema borra dos cosas:

* El registro en la base de datos.
* El archivo físico dentro de la carpeta `uploads`.

Por eso es importante tener cuidado con esta acción, ya que no es reversible desde el sistema.

## Recomendaciones para futuros cambios

Si otro programador desea continuar el proyecto, se recomienda tomar en cuenta lo siguiente:

* No subir la carpeta `target`, porque Maven la genera automáticamente.
* Revisar siempre `application.properties` antes de ejecutar el proyecto.
* No cambiar la ruta de `uploads` sin mover también los archivos físicos.
* Si se agregan nuevas tablas, actualizar también los scripts SQL.
* Si se cambia la estructura de la base, revisar los métodos de los repositorios.
* Mantener las vistas organizadas por módulo dentro de `templates`.
* Probar los cambios con usuarios de ambos roles.
* Hacer commits pequeños y claros para que sea más fácil revisar el historial.

## Archivos que deberían estar en el repositorio

El repositorio debería contener:

```text
src/
database/
uploads/
mediacloud.sql
pom.xml
README.md
```

La carpeta `target/` no es necesaria en el repositorio.

## Posibles mejoras futuras

Algunas mejoras que se podrían agregar más adelante son:

* Búsqueda de archivos por nombre.
* Vista tipo galería para imágenes.
* Recuperación de archivos desde papelera.
* Validación de tamaño máximo por archivo.
* Cambio de contraseña de usuario.
* Mejor control de permisos por rol.
* Paginación en las tablas.
* Mejor diseño para dispositivos móviles.
* Registro de acciones del usuario.
* Previsualización más avanzada para videos y documentos.

## Notas finales

MediaCloud es un proyecto académico, por lo que algunas partes están hechas de forma sencilla para que se entienda mejor el funcionamiento interno.

La aplicación ya permite trabajar con usuarios, álbumes, categorías y archivos reales. Sin embargo, si se desea usar en un ambiente más serio, habría que reforzar temas como seguridad, validaciones, manejo de errores, permisos y almacenamiento de archivos.

Para trabajar sobre este proyecto, lo más importante es entender que la base de datos guarda la información, mientras que la carpeta `uploads` guarda los archivos físicos.

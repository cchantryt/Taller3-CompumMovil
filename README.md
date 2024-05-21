# Taller 3 - Computación Móvil

Este proyecto es parte del taller 3 de la asignatura Computación Móvil. El objetivo principal es desarrollar una aplicación móvil que permita a los usuarios registrarse, iniciar sesión, actualizar sus datos personales y visualizar su ubicación en un mapa, así como la de otros usuarios activos.

## Características

- **Registro de Usuarios:** Permite a los nuevos usuarios crear una cuenta proporcionando su nombre, apellido, correo electrónico, contraseña y número de identificación.

- **Inicio de Sesión:** Los usuarios pueden iniciar sesión utilizando su correo electrónico y contraseña.

- **Actualización de Datos del Usuario:** Los usuarios pueden actualizar su nombre, apellido y número de identificación. La ubicación (latitud y longitud) se actualiza automáticamente con la ubicación actual del dispositivo.

- **Visualización de Usuarios Activos:** Los usuarios pueden ver una lista de otros usuarios activos y seleccionar uno para visualizar su ubicación en un mapa.

- **Mapa:** La aplicación muestra la ubicación del usuario y de otros usuarios seleccionados en un mapa.

## Tecnologías Utilizadas

- **Kotlin:** Lenguaje de programación para el desarrollo de la aplicación.

- **Firebase Authentication:** Para el registro y autenticación de usuarios.

- **Firebase Realtime Database:** Para almacenar y recuperar los datos de los usuarios en tiempo real.

- **Google Maps API:** Para mostrar las ubicaciones en un mapa.

## Estructura del Proyecto

El proyecto está organizado en las siguientes actividades principales y sus respectivos layouts:

- `LoginActivity` y `activity_inicio_sesion.xml`: Manejan el inicio de sesión de los usuarios.

- `RegisterActivity` y `activity_registro.xml`: Se encargan del registro de nuevos usuarios.

- `UserActivity` y `activity_usuario.xml`: Permiten a los usuarios actualizar sus datos personales.

- `MainActivity` y `activity_main.xml`: Muestran la lista de usuarios activos y proporcionan acceso a las demás funciones.

- `MapActivity` y `fragment_map.xml`: Muestran la ubicación del usuario y de otros usuarios seleccionados en un mapa.

## Instalación

Para ejecutar este proyecto, es necesario tener instalado Android Studio. Clone el repositorio en su máquina local y abra el proyecto con Android Studio. Configure Firebase y Google Maps API siguiendo las instrucciones proporcionadas en sus respectivas documentaciones. Compile y ejecute la aplicación en un emulador o dispositivo Android.

## Contribuciones

Las contribuciones son bienvenidas. Si desea contribuir, por favor haga un fork del repositorio y cree un pull request con sus cambios.

## Licencia

Este proyecto está licenciado bajo la Licencia MIT. Vea el archivo `LICENSE` para más detalles.

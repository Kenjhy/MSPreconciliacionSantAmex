#Micro Servicio para el proceso de Preconciliacion Santander/Amex
#Se toma como plantilla el codigo de MS-PagosPrendarios

## Inicio

Para construir esta aplicación se requieren como versiones mínimas:
 
 * Java 8
 * Maven 3
 * En modo local (dev) se requiere también una base de datos MySQL con instalación por default.

El puerto de despliegue es el default

```
8080
```

En el perfil cloud, el puerto por defecto es

```
80
```

## Perfiles

Se configuran los siguientes perfiles que permiten orientar el código hacia diferentes ambientes, según
sea el ciclo de desarrollo:

* dev           Perfil de desarrollo local(por default), utilizando la base de datos en localhost.
* bmx           Perfil de desarrollo local, utilizando la base de datos en Bluemix.
* cloud         Perfil para despliegue en servicios de tipo CloudFoundry (Bluemix).

Para hacer uso de estos perfiles en la aplicación se utilizan las siguientes instrucciones:

### Profile Dev

```
mvn spring-boot:run
```

### Profile Bmx

Este profile permite la ejecución de código localmente:

```
mvn spring-boot:run -Dspring.profiles.active=bmx
```

### Profile Cloud

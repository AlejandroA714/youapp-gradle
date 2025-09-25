# YouApp Infrastructure BOM

Este proyecto define un **Bill of Materials (BOM)** para centralizar y unificar las versiones de dependencias dentro del ecosistema de **YouApp**.  
Al publicar este BOM, todos los módulos y aplicaciones que lo importen podrán alinear sus dependencias con versiones consistentes, evitando conflictos y problemas de compatibilidad.

---

## **¿Qué es este BOM?**
El BOM (Bill of Materials) es un proyecto Gradle con el plugin [`java-platform`](https://docs.gradle.org/current/userguide/java_platform_plugin.html) que:

- Gestiona **versiones de dependencias comunes** en un solo lugar.
- Permite asegurar que todos los proyectos del monorepo o repositorios relacionados usen las **mismas versiones**.
- Facilita actualizaciones y mantenibilidad a largo plazo.

Actualmente este BOM importa:

- **Spring Boot**:
  ```kotlin
  api(platform("org.springframework.boot:spring-boot-dependencies:3.5.5"))

Adicional expone un plugin para formateo de java, kotlin y gradle.kts

```
plugins { id("com.sv.youapp.formatter") }
```
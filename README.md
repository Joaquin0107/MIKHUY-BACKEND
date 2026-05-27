# ⚙️ MIKHUY - API REST (Back-End) con IA Generativa

<p align="center">
  <a href="https://spring.io/projects/spring-boot"><img src="https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot"></a>
  <a href="https://www.postgresql.org/"><img src="https://img.shields.io/badge/PostgreSQL-15%2B-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"></a>
  <a href="https://www.docker.com/"><img src="https://img.shields.io/badge/Docker-🐳-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker"></a>
  <a href="https://render.com"><img src="https://img.shields.io/badge/Hosted_on-Render-46E3B7?style=for-the-badge&logo=render&logoColor=black" alt="Render"></a>
</p>

---

## 📝 Descripción del Proyecto

Este repositorio contiene el **Back-End** y la capa de servicios para **Mikhuy**, un sistema web basado en Inteligencia Artificial Generativa orientado al monitoreo del comportamiento alimentario y control de parámetros de salud. 

La arquitectura está construida como una **API RESTful** sólida, escalable y robusta utilizando el ecosistema de **Java y Spring Boot**. Se encarga de procesar las solicitudes de IA, gestionar la persistencia de datos relacionales, administrar las lógicas del sistema de gamificación (beneficios, logros y perfiles de estudiantes) y centralizar las métricas de salud.

🌐 **Servidor en producción (Base URL):** [Mikhuy Backend en Render](https://mikhuy-backend.onrender.com)

---

## 🚀 Arquitectura y Funcionalidades del Servidor

* 🤖 **Core de Inteligencia Artificial:** Controladores y servicios dedicados a interactuar con modelos de IA Generativa para proveer el diagnóstico y feedback nutricional.
* 📋 **Gestión de Datos Médicos e Historial:** API endpoints estructurados para el registro seguro de variables de salud corporales y hábitos diarios de alimentación.
* 🏆 **Motor de Gamificación y Recompensas:** Lógica transaccional para la asignación de puntos, administración de la tabla de beneficios (`beneficios`) y canjes para los estudiantes.
* 🗄️ **Persistencia Relacional Avanzada:** Mapeo de entidades complejas mediante Java Persistence API (JPA / Hibernate) conectado a una base de datos PostgreSQL optimizada.

---

## 🛠️ Tecnologías y Stack Técnico

* **Lenguaje:** Java 17 / 21
* **Framework Principal:** [Spring Boot](https://spring.io/projects/spring-boot) (Spring Web, Spring Data JPA)
* **Base de Datos:** [PostgreSQL](https://www.postgresql.org/)
* **Contenerización:** [Docker](https://www.docker.com/) (Orquestación del entorno de ejecución mediante Dockerfile)
* **Gestor de Dependencias:** Maven (`pom.xml`)
* **Infraestructura Cloud:** Desplegado en **Render**

---

## 🌐 Despliegue y Cloud

El entorno de producción se encuentra completamente automatizado mediante contenedores **Docker** alojados en la plataforma **Render**. Cada actualización integrada en la rama principal desencadena un proceso de *Build & Deploy* continuo, asegurando la entrega inmediata de nuevas características a la aplicación cliente (Mikhuy Frontend).

---

## 📄 Licencia

Este proyecto está bajo la licencia que determinen los autores principales. 

---
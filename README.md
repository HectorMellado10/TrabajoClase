# TrabajoClase(ZenQuotes Android)
Descripción del proyecto

Este proyecto es una aplicación Android desarrollada en Java que consume la API pública ZenQuotes (https://zenquotes.io/api/random).
La app muestra una cita motivacional y su autor, con la posibilidad de refrescar la frase al presionar un botón.

El objetivo fue poner en práctica el uso de agentes de IA (Copilot y Gemini) para facilitar el trabajo de desarrollo a través de prompts detallados que generan código, documentación y propuestas de mejora.

Herramientas utilizadas

Lenguaje: Java

IDE: Android Studio (Giraffe / Narwhal)

Arquitectura: MVVM (ViewModel + LiveData)

Networking: Retrofit + Gson + OkHttp (con timeouts y reintentos simples)

Control de estados: LiveData con loading, data, error

Diseño de UI: Views/XML (TextView para la frase, TextView para el autor, Button para refrescar)

Rol de cada miembro

Abner Mateo

Trabajó con Copilot como agente.

Su responsabilidad fue generar el código base en Java, asegurando que el ViewModel, LiveData y Retrofit funcionaran de forma correcta.

Iteró prompts hasta obtener una app que compila, carga la cita inicial y permite refrescar frases.

Héctor

Le correspondía trabajar con Gemini como agente.

Debido a que el modo agente no estaba disponible en su cuenta, no pudo generar código directamente desde esa modalidad.

Aun así, elaboró un prompt estructurado para Gemini y desarrolló la documentación como si se hubiera trabajado con dicho agente, explicando decisiones, posibles problemas y reflexiones finales.

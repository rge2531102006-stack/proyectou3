# Control de Gastos 

Aplicación móvil nativa para Android desarrollada en **Kotlin** con **ViewBinding** y **Material 3** para el control de gastos personales, protegida mediante autenticación biométrica por huella dactilar (`BiometricPrompt`) e integrada con sensores (`Acelerómetro` y `Giroscopio`), modo oscuro/claro y cierre de sesión.

## Datos del proyecto
* **Proyecto:** Control de Gastos 
* **Autora:** ESMERALDA RUIZ GARCIA
* **Fecha:** 23 de noviembre de 2026
  
## Características principales
* **Autenticación biométrica (`BiometricPrompt`):** Pantalla de inicio protegida por huella dactilar con manejo de casos como sensor ausente o no disponible, sin huella registrada, huella no reconocida, cancelación del usuario y bloqueo por intentos fallidos.
* **Acceso protegido:** Impide el acceso a las pantallas internas sin autenticarse y limpia la pila al cerrar sesión para volver a exigir huella.
* **Gestión de gastos (CRUD):** Listado dinámico con `RecyclerView` y adaptador propio (`ExpenseAdapter`), cálculo de total acumulado, formulario con validación de campos (concepto, categoría, monto positivo y fecha `AAAA-MM-DD`), edición y eliminación.
* **Sensores y configuración:** Lectura en tiempo real del **acelerómetro** (ángulo de inclinación, estado y ejes X, Y, Z) y del **giroscopio** (ejes X, Y, Z), cambio entre **modo oscuro y modo claro** (`AppCompatDelegate`) y **cierre de sesión**.
* **Arquitectura y vistas:** Organizada en paquetes (`login`, `datos`, `repositorio`, `gasto`, `formulario`, `configuracion`, etc.) con `minSdk 24` y `ViewBinding` en todas las vistas.

## Estructura del proyecto
* `app/src/main/java/com/example/controldegastos/` — Paquetes y actividades (`login`, `MainActivity`, `formulario`, `gasto`, `datos`, `repositorio`, `configuracion`, etc.).
* `app/src/main/res/layout/` — Layouts XML (`activity_login.xml`, `activity_main.xml`, `activity_formulario.xml`, `activity_gasto.xml`, `activity_configuracion.xml`).
* `app/build.gradle.kts` — Configuración de `minSdk 24`, `viewBinding = true` y dependencias (`androidx.biometric`, Material 3, RecyclerView, ConstraintLayout).

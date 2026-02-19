# Guía de uso de Git

Esta guía explica cómo configurar y trabajar con Git en este proyecto, utilizando las ramas `main` y `develop`.

## Índice

1. [Ramas: main y develop](#1-ramas-main-y-develop)
2. [Crear ramas desde develop](#2-crear-ramas-desde-develop)
3. [Push, pull y merge](#3-push-pull-y-merge)
4. [Cómo trabajar](#4-cómo-trabajar)
5. [Flujo de trabajo resumido](#5-flujo-de-trabajo-resumido)

---

## 1. Ramas: main y develop

En este proyecto trabajamos con dos ramas principales:

| Rama | Uso |
|------|-----|
| **main** | Rama principal, código estable listo para producción |
| **develop** | Rama de desarrollo, donde se integran las nuevas funcionalidades |

### Crear la rama develop (si no existe) - mejor hacerlo desde github

```bash
git checkout main
git checkout -b develop
git push -u origin develop
```

### Cambiar entre ramas

```bash
# Ir a main
git checkout main

# Ir a develop
git checkout develop
```

---

## 2. Crear ramas desde develop - mejor hacerlo desde github

Cada tarea o feature se trabaja en una rama nueva creada desde `develop`.

## 3. Push, pull y merge

### Antes de cambiar código asegurarse de estar en sitio correcto

Antes de editar archivos, verifica en qué rama estás para no trabajar en la rama equivocada:

```bash
# Ver en qué rama estás (la actual lleva un asterisco *)
git branch

# Si necesitas cambiar de rama
git checkout nombre-de-la-rama
```

### Hacer push (subir tus cambios)

```bash
# Desde tu rama de trabajo (ej. feature/login)
git add .
git commit -m "Descripción clara del cambio"
git push origin feature/nombre-de-la-funcionalidad
```

### Hacer fetch y pull (traer cambios del remoto)

`git fetch origin` descarga los cambios del remoto sin fusionarlos. Úsalo para ver qué hay nuevo antes de hacer pull.

```bash
# Traer todas las ramas y cambios del remoto (sin fusionar)
git fetch origin

# Actualizar develop
git checkout develop
git pull origin develop

# Actualizar main
git checkout main
git pull origin main

# Actualizar tu rama con los últimos cambios de develop
git checkout feature/tu-rama
git pull origin develop
```

### Mergear a develop - Mejor desde github, si surgen conflictos resolverlos en github o en local

Cuando termines tu trabajo en la rama de feature:


### Mergear develop a main - Mejor desde github

Cuando `develop` esté listo para producción.

---

## 4. Cómo trabajar

Pasos a seguir para desarrollar código de principio a fin:


**Paso 1 — Actualizar develop**
```bash
git fetch origin
git checkout develop
git pull origin develop
```

**Paso 2 — Crear tu rama de trabajo** (desde GitHub)


**Paso 3 — Desarrollar**
- Edita los archivos necesarios

**Paso 4 — Guardar cambios (commit), también se puede hacer desde VSCode pero es más caca**
```bash
git add .
git status   # revisa qué se va a incluir
git commit -m "Descripción clara del cambio"
```

**Paso 5 — Subir al remoto (push)**
```bash
git push origin nombre_rama
```

**Paso 6 — Integrar en develop, cuando esté listo**
- Crear Pull Request en GitHub

**Paso 7 — Mantener rama actualizada** (si alguien la ha cambiado lo que sea)
```bash
git checkout feature/tu-rama
git fetch origin
git merge origin/develop
```

---

## 5. Flujo de trabajo resumido

1. Trabajar siempre partiendo de **develop**
2. Crear ramas tipo `feature/...` o `fix/...` desde develop
3. Hacer **push** de tu rama al remoto
4. Hacer **pull** de develop para estar al día
5. **Mergear** tu rama en develop cuando termines
6. **Mergear** develop en main cuando se vaya a producción

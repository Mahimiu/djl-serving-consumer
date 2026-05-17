# 🐱 DJL Serving Consumer

Spring-Boot-Webanwendung, die Bilder per REST-API entgegennimmt und an einen [DJL-Serving](https://github.com/deepjavalibrary/djl-serving)-Sidecar zur Klassifikation weiterleitet. Das Consumer-Backend ist sprachunabhängig und nutzt ein vortrainiertes ResNet18-Modell (ImageNet, 1000 Klassen) im DJL-Serving-Container.

Das Projekt entstand im Rahmen des Moduls **MDM (Model Deployment & Maintenance)** an der ZHAW (FS2026).

## 🌐 Live-Demo

**Azure App Service**: [https://djl-consumer-galmmax1-gaf2h9cnhxfrdsb4.switzerlandnorth-01.azurewebsites.net](https://djl-consumer-galmmax1-gaf2h9cnhxfrdsb4.switzerlandnorth-01.azurewebsites.net)

> Beim ersten Aufruf braucht der Sidecar 30–60 Sekunden, um das Modell zu laden.

## 🎁 Bonus-Themen Projekt 2

Dieses Projekt deckt zwei auf Moodle angemeldete Bonus-Themen ab:

| Bonus-Thema | Umsetzung |
|---|---|
| **UI / Backend** | Animierter Gradient-Hintergrund, Drag-&-Drop-Upload, Top-5-Klassifikation mit Confidence-Balken, 4 REST-Endpoints (`/ping`, `/health`, `/info`, `/analyze`), Swagger UI für interaktive API-Dokumentation, Sidecar-Pattern mit Spring WebFlux WebClient |
| **Dependency Management & Project Setup in VS Code** | Multi-Stage Dockerfile (-39% Image-Grösse, 792 MB → 486 MB), VS Code Workspace Config (`launch.json`, `tasks.json`, `settings.json`, `extensions.json`), `.editorconfig` |

### UI / Backend Highlights

- **`/health`** — Statusabfrage mit Timestamp und konfigurierter Modell-Service-URL
- **`/info`** — Modell-Metadaten (Architektur, Framework, Klassen-Anzahl, Sidecar-Endpoint)
- **Swagger UI** unter `/swagger-ui.html` — interaktive API-Doku mit Try-It-Out-Funktion (springdoc-openapi)
- **Animierter Gradient-Hintergrund** (CSS `@keyframes`-Animation, 5 Farben, 15s Loop)
- **Drag-&-Drop-Upload** mit Bootstrap-basierter Card-UI

### Multi-Stage Dockerfile

- **Stage 1 (Builder)**: JDK + Maven, baut die JAR
- **Stage 2 (Runtime)**: nur JRE, Non-Root User (`spring`), container-aware JVM-Settings (`-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0`)
- **Layer-Caching**: Dependencies werden separat geladen und gecached → schnellere Rebuilds bei reinen Code-Änderungen
- **OCI-Labels** für Image-Metadaten
- **Line-Ending-Fix** für `mvnw` (sed im Builder)

### VS Code Workspace

Beim Öffnen des Repos in VS Code werden automatisch passende Extensions vorgeschlagen (Java Pack, Spring Boot Dev Pack, Docker, EditorConfig). Über `F5` startet die App mit Dev-Profil und Debugger, über `Ctrl+Shift+B` läuft `mvn clean package`. Die Settings sorgen für konsistente Java-Formatierung — ergänzt durch `.editorconfig`, das plattform- und IDE-übergreifend wirkt.

## ✨ Features

- 🎨 **Animierter Gradient-Hintergrund** für ein modernes Look-&-Feel
- 🖼️ **Drag-&-Drop-Upload** für Bilder direkt im Browser
- 📊 **Top-5-Klassifikation** mit Wahrscheinlichkeiten
- 🔌 **REST-API** mit vier Endpoints (Ping, Health, Info, Analyze)
- 📖 **Swagger UI** für interaktive API-Dokumentation
- 🧱 **Sidecar-Architektur**: Web-Service ↔ Model-Service (DJL Serving)
- 🐳 **Multi-Stage Docker-Images** auf Docker Hub (Consumer + Serving) — Bonus
- 💻 **VS Code Workspace Setup** mit Run/Debug/Tasks — Bonus
- ☁️ **Azure App Service Deployment** mit Sidecar-Setup
- 🤖 **CI/CD via GitHub Actions** (automatischer Build & Push beider Images)
- 🧪 **Postman-Collection** für API-Tests

## 🏗️ Architektur
+---------------+  HTTP  +--------------------------+  HTTP  +----------------------+
|  Browser /    | -----> |  Consumer Service        | -----> |  Model Service       |
|  Postman      | <----- |  (Spring Boot, Port 80)  | <----- |  (DJL Serving, 8080) |
+---------------+        |                          |        |                      |
|  /ping                   |        |  /predictions/       |
|  /health                 |        |  traced_resnet18     |
|  /info                   |        |                      |
|  /analyze (image upload) |        |                      |
|  /swagger-ui.html        |        |                      |
+--------------------------+        +----------------------+
galmmax1/                            galmmax1/
djl-serving-consumer:latest          djl-serving:latest

Der Consumer-Service nimmt das Bild entgegen, leitet es an den Sidecar (DJL Serving) weiter und reicht das Resultat unverändert an den Client zurück.

## 🛠️ Tech-Stack

| Bereich | Technologie |
|---|---|
| Sprache | Java 25 |
| Framework | Spring Boot 3 + Spring WebFlux (WebClient) |
| API-Doku | springdoc-openapi (Swagger UI) |
| Modell-Server | DJL Serving (im Sidecar) |
| Modell | TorchScript ResNet18 (`traced_resnet18.zip`, ImageNet-1k) |
| Build | Maven (mit Wrapper) |
| Frontend | HTML, CSS (animierter Gradient), Bootstrap 4 |
| Containerisierung | Docker (Multi-Stage) + Docker Compose |
| Dev-Setup | VS Code Workspace + `.editorconfig` |
| CI/CD | GitHub Actions (in **beiden** Repos) |
| Hosting | Azure App Service mit Sidecar |
| Verwandtes Repo | [Mahimiu/djl-serving](https://github.com/Mahimiu/djl-serving) (Sidecar-Image) |

## 🚀 Setup & Lokal starten

### Voraussetzungen

- Java 25 (JDK)
- Maven (oder Wrapper `./mvnw`)
- Docker + Docker Compose

### Repository klonen

```bash
git clone https://github.com/Mahimiu/djl-serving-consumer.git
cd djl-serving-consumer
```

### Variante A: Lokal mit Docker Compose (empfohlen)

Im verwandten Repo [`djl-serving`](https://github.com/Mahimiu/djl-serving) ist eine `docker-compose.yml` enthalten, die **beide Services** zusammen startet:

```bash
git clone https://github.com/Mahimiu/djl-serving.git
cd djl-serving
docker-compose up
```

Anschliessend ist die App unter [http://localhost](http://localhost) erreichbar.

### Variante B: Nur Consumer lokal (Sidecar muss separat laufen)

```bash
./mvnw spring-boot:run
```

Auf Windows:

```cmd
mvnw.cmd spring-boot:run
```

In VS Code: einfach `F5` drücken — die Run-Konfiguration ist im Workspace vorbereitet.

Der Consumer erwartet, dass DJL Serving auf `http://localhost:8080/predictions/traced_resnet18` erreichbar ist (also den Sidecar separat starten).

### Variante C: Eigenes Image bauen und ausführen

```bash
docker build -t djl-serving-consumer .
docker run -p 8082:8082 djl-serving-consumer
```

## 🔌 API-Endpoints

Alle Endpoints sind unter `http://localhost:8082` (lokal, Spring) oder `http://localhost` (Docker Compose) bzw. der Azure-URL erreichbar.

### `GET /ping`

Health-Ping. Antwort: `"DJL Consumer app is up and running!"`

### `GET /health`

Detaillierter Status:

```json
{
  "status": "UP",
  "timestamp": "2026-05-17T13:53:31.133",
  "modelService": "http://localhost:8080/predictions/traced_resnet18"
}
```

### `GET /info`

Service-Metadaten:

```json
{
  "modelName": "traced_resnet18",
  "framework": "DJL Serving with TorchScript",
  "architecture": "ResNet18 (ImageNet-1k)",
  "modelService": "http://localhost:8080/predictions/traced_resnet18",
  "numClasses": 1000,
  "pattern": "Sidecar Architecture"
}
```

### `POST /analyze`

Bild hochladen und klassifizieren. Body: `multipart/form-data` mit Feld `image`.

Beispiel-Antwort (mit `kitten.jpg`):

```json
[
  {"className": "n02123045 tabby, tabby cat",     "probability": 0.357},
  {"className": "n02124075 Egyptian cat",          "probability": 0.329},
  {"className": "n02123159 tiger cat",             "probability": 0.285},
  {"className": "n02127052 lynx, catamount",       "probability": 0.006},
  {"className": "n02123394 Persian cat",           "probability": 0.004}
]
```

Die Klassen-Bezeichnungen entsprechen den ImageNet-1k-Synsets.

### Swagger UI

Interaktive API-Dokumentation: [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)

## 🧪 API-Tests mit Postman

Die Datei [`consumer-collection.json`](consumer-collection.json) enthält eine Postman-Collection mit den Endpoints.

### Import

1. Postman öffnen.
2. **Import** → JSON-Datei auswählen.
3. Variable `baseUrl` setzen:
   - Lokal (Docker Compose): `http://localhost`
   - Azure: `https://djl-consumer-galmmax1-gaf2h9cnhxfrdsb4.switzerlandnorth-01.azurewebsites.net`

### Tests durchführen

`GET /ping`, `/health`, `/info` direkt mit **Send**. Für `POST /analyze`: Tab **Body → form-data**, beim Feld `image` ein Bild auswählen (z.B. `kitten.jpg` aus dem Repo).

## 🚢 Deployment

### Azure App Service (mit Sidecar)

Die Live-Version nutzt das Azure-Sidecar-Pattern:
- **Main Container**: `galmmax1/djl-serving-consumer:latest` (Port 80)
- **Sidecar**: `galmmax1/djl-serving:latest` (Port 8080, intern)
- **Region**: Switzerland North

Beide Container laufen im selben Network und sind sich gegenseitig per `localhost` erreichbar.

### Docker Hub

| Image | URL |
|---|---|
| Consumer | [hub.docker.com/r/galmmax1/djl-serving-consumer](https://hub.docker.com/r/galmmax1/djl-serving-consumer) |
| Sidecar | [hub.docker.com/r/galmmax1/djl-serving](https://hub.docker.com/r/galmmax1/djl-serving) |

### CI/CD via GitHub Actions

In **beiden** Repos läuft bei jedem Push auf `main` automatisch der Workflow `.github/workflows/deploy.yml`:

1. Code-Checkout
2. Docker Buildx Setup
3. Login bei Docker Hub (mit Secret `DOCKERHUB_TOKEN`)
4. Build und Push des Images mit Tag `:latest`

Status:
- [Consumer Actions](https://github.com/Mahimiu/djl-serving-consumer/actions)
- [Sidecar Actions](https://github.com/Mahimiu/djl-serving/actions)

## 📁 Projektstruktur
djl-serving-consumer/
├── .github/
│   └── workflows/
│       └── deploy.yml                          # GitHub Actions Workflow
├── .vscode/                                    # Workspace-Konfig (Bonus)
│   ├── launch.json                             # Run/Debug-Konfigurationen
│   ├── tasks.json                              # Maven + Docker Tasks
│   ├── settings.json                           # Java-Format, Editor
│   └── extensions.json                         # Empfohlene Extensions
├── src/
│   └── main/
│       ├── java/ch/zhaw/djl/consumer/consumer/
│       │   ├── ConsumerApplication.java        # Spring Boot Entry-Point
│       │   └── ConsumerController.java         # REST-Endpoints + WebClient
│       └── resources/
│           ├── application.properties
│           └── static/
│               ├── index.html                  # Frontend (Bonus: Branding)
│               ├── script.js
│               └── style.css                   # Frontend (Bonus: animierter Hintergrund)
├── .editorconfig                               # IDE-übergreifende Format-Regeln (Bonus)
├── consumer-collection.json                    # Postman-Collection
├── Dockerfile                                  # Multi-Stage Build (Bonus)
├── kitten.jpg                                  # Beispielbild für Tests
├── pom.xml                                     # springdoc-openapi für Swagger UI (Bonus)
└── README.md                                   # Diese Datei

## 🔗 Verwandte Repositories

- **Sidecar / Model Service**: [Mahimiu/djl-serving](https://github.com/Mahimiu/djl-serving) (mit `traced_resnet18.zip` und `docker-compose.yml`)
- **Footwear-Klassifikator (eingebettete Variante)**: [Mahimiu/djl-footwear-classification](https://github.com/Mahimiu/djl-footwear-classification)

## 📜 Lizenz

Dieses Projekt basiert auf dem [DJL-Beispielcode](https://github.com/deepjavalibrary/djl) (Apache 2.0) und wurde im akademischen Rahmen erweitert.

## 👤 Autor

**Maximillian Galm** — ZHAW, Wirtschaftsinformatik, FS2026

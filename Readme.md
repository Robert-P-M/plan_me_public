# 📝 Plan Me

[![Build](https://img.shields.io/badge/build-passing-brightgreen)]()
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE.md)
[![Platform](https://img.shields.io/badge/platform-Android-green)]()
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-orange?logo=kotlin)]()
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-purple?logo=jetpack-compose)]()
[![Clean Architecture](https://img.shields.io/badge/Architecture-Clean%20Architecture-red)]()
[![Koin](https://img.shields.io/badge/DI-Koin-blue)]()
[![Room DB](https://img.shields.io/badge/Database-Room-lightgrey)]()
[![Coroutines & Flow](https://img.shields.io/badge/Async-Coroutines%20%26%20Flow-blueviolet)]()

## Your Advanced, Offline-First Task Planner for Structured Productivity.
## Dein fortschrittlicher, Offline-First Aufgabenplaner für strukturierte Produktivität.

---

## 📚 Table of Contents / Inhaltsverzeichnis

| 🇬🇧 English | 🇩🇪 Deutsch |
|------------|-------------|
| [App Previews](#app-previews) | [App-Vorschauen](#app-vorschauen) |
| [Features](#features) | [Funktionen](#funktionen) |
| [Technology Stack & Architectural Principles](#technology-stack--architectural-principles) | [Technologie-Stack & Architekturprinzipien](#technologie-stack--architekturprinzipien-de) |
| [Architectural Highlights](#architectural-highlights) | [Architektonische Highlights](#architektonische-highlights-de) |
| [Development](#development) | [Entwicklung](#entwicklung-de) |
| [Roadmap & Future Enhancements](#roadmap--future-enhancements) | [Roadmap & Zukünftige Erweiterungen](#roadmap--zukünftige-erweiterungen-de) |
| [Contributing](#contributing) | [Mitwirken](#mitwirken-de) |
| [License](#license) | [Lizenz](#lizenz-de) |
| [Contact](#contact) | [Kontakt](#kontakt-de) |

---

<a name="app-previews"></a>
## 📸 App Previews / App-Vorschauen

A showcase of Plan Me's clean and feature-rich interface.
Eine Demonstration der sauberen und funktionsreichen Oberfläche von Plan Me.

### Animated Previews / Animierte Vorschauen
| Board & List View Toggle | Smooth Scrolling Calendar |
|---|---|
| ![Board & List View Toggle](docs/videos/board_list_view.gif) | ![Smooth Scrolling Calendar](docs/videos/scrollable_calendar_video.gif) |

### Screenshots
| Feature / Screen | Preview |
|---|---|
| **Empty Home Screen** <br/> Initial view with quick-add buttons. | ![Empty Home Screen](docs/images/01_home_empty.png) |
| **Add Task Dialog** <br/> Comprehensive dialog for task creation. | ![Add Task Dialog](docs/images/02_add_task_dialog.png) |
| **Priority Picker** <br/> Select a priority for your task. | ![Priority Picker](docs/images/03_priority_picker_dialog.png) |
| **Hashtag Picker** <br/> Add existing or new hashtags with full-text search. | ![Hashtag Picker Dialog](docs/images/04_hashtag_picker_dialog.png) |
| **Date Picker Calendar** <br/> Select a due date from an infinitely scrollable calendar. | ![Calendar Dialog](docs/images/05_calendar_dialog.png) |
| **Home Screen with Tasks** <br/> Main view populated with sections and tasks. | ![Home Screen with Tasks](docs/images/06_home_with_task.png) |
| **Add Section Dialog** <br/> Create new sections to group your tasks. | ![Add Section Dialog](docs/images/07_add_section_dialog.png) |
| **Section Menu** <br/> Manage sections: add sub-tasks, rename, or delete. | ![Section Menu](docs/images/08_section_menu.png) |
| **Task Menu** <br/> Advanced options: stats, sub-tasks, move, archive, etc. | ![Task Menu](docs/images/09_task_menu.png) |
| **Nested Tasks (Grid View)** <br/> Visualize hierarchical tasks in a Kanban-style board. | ![Nested Tasks Grid View](docs/images/10_nested_tasks_cards_grid_view.png) |
| **Nested Tasks (List View)** <br/> A clear list view of all tasks and their sub-tasks. | ![Nested Tasks List View](docs/images/11_nested_tasks_cards_list_view.png) |
| **Task Details Dialog** <br/> Full task overview with its recursive path. | ![Task Details Dialog](docs/images/12_task_details_dialog.png) |
| **Hashtag Management** <br/> View and manage a task's associated hashtags. | ![Task Details with Hashtags](docs/images/15_task_details_dialog_after_add_hashtag_search.png) |
| **Hashtag-Task Relation** <br/> See all tasks associated with a specific hashtag. | ![Hashtag Tasks Relation Dialog](docs/images/16_hashtag_tasks_relation-dialog.png) |
| **Move Task Dialog** <br/> Easily move tasks between sections or other tasks. | ![Move Task Dialog](docs/images/18_move_task_dialog.png) |
| **Task Statistics & History** <br/> A complete audit trail of every change made to a task. | ![Task Statistics Dialog](docs/images/19_task_statistics_dialog.png) |

---

**This project serves as a comprehensive portfolio piece demonstrating expert-level skills in modern Android development, with a focus on Jetpack Compose, a robust offline-first Clean Architecture, and advanced database and concurrency patterns.**

<a name="features"></a>
## ✨ Features

* **Hierarchical Task Management:** Break down complex projects into manageable steps with infinitely nested sub-tasks. Group related tasks into custom, user-defined sections.
* **Intelligent & Precise Notifications:** Schedule time-sensitive reminders for any task. Alarms are exact, work even in Doze mode, and are automatically restored after a device reboot, ensuring no deadline is ever missed.
* **Dynamic Tagging System:** Organize and categorize tasks with a flexible hashtag system. Find all related tasks across different sections with a single tap.
* **Complete Task History & Statistics:** A dedicated statistics screen provides a detailed audit trail, tracking every change to a task's title, description, priority, and completion status for complete transparency.
* **Adaptive UI & Powerful Views:**
    * **List & Board Views:** Visualize your workflow in either a traditional list or a Kanban-style board layout.
    * **Powerful Filtering & Sorting:** Instantly filter tasks by completion or archived status and sort them by creation date.
* **Advanced Task Operations:**
    * **Move & Duplicate:** Easily reorganize tasks by moving them between sections or create copies of existing tasks.
    * **Archive:** Keep your workspace clean by archiving completed or irrelevant tasks without deleting them permanently.
* **Intuitive UX:**
    * **Event-Bus Pattern:** Seamless communication between screens and dialogs without relying on the backstack for data transfer.
    * **Recursive Breadcrumbs:** The task details dialog dynamically calculates and displays the full nested path of a task (e.g., `Project A / Milestone 2 / Task X`).

<a name="technology-stack--architectural-principles"></a>
## 🚀 Technology Stack & Architectural Principles

* **Kotlin-First:** Built entirely with Kotlin, leveraging Coroutines and a fully reactive `Flow`-based data pipeline from the Room DAO to the ViewModel.
* **Jetpack Compose:** A 100% declarative UI built with Jetpack Compose for a modern, reactive, and maintainable user interface.
* **Clean Architecture:** A strict, multi-layered architecture separating the app into `data`, `domain`, and `presentation` layers, promoting testability and scalability.
* **MVVM (Model-View-ViewModel):** A clean separation of concerns between the UI (View) and business logic (ViewModel).
* **Room Persistence Library:** A normalized relational database schema to handle complex relationships (many-to-many via cross-references) and recursive queries.
* **Koin:** Lightweight and pragmatic dependency injection framework to manage dependencies and promote decoupled code.
* **Core Principles:** Consistent application of **SOLID**, **DRY** (Don't Repeat Yourself), and **KISS** (Keep It Simple, Stupid).

<a name="architectural-highlights"></a>
## 🏗️ Architectural Highlights

This project tackles complex architectural challenges to ensure high performance and robustness.

* **Performance by Design:**
    * **N+1 Problem Solved:** Proactively solves the N+1 query problem by using efficient "batch" database queries to fetch related data (like all hashtags for a list of tasks) in a single roundtrip.
    * **Parallel Execution:** Computationally expensive operations, like loading a deep recursive hierarchy of sub-tasks, are executed in parallel using `coroutineScope` with `async`/`awaitAll` to maximize performance and ensure a smooth UI.
* **Robust State Management:** UI state is exposed from ViewModels as a custom `AppResource<T>` wrapper, providing clear, unambiguous states (`Loading`, `Success`, `Error`), which are handled gracefully in the Composables.
* **Resilient Notification System:** The notification scheduler is built to be robust, correctly handling `BOOT_COMPLETED` events and modern Android permission requirements (`SCHEDULE_EXACT_ALARM`).
* **Decoupled Domain Logic:** Business logic is encapsulated in single-responsibility Use Cases, which are composed together to handle complex operations like recursive data fetching or history logging.

<a name="development"></a>
## 🛠️ Development

### Requirements

* Android Studio Jellyfish | 2023.3.1 or newer
* Android SDK 34 (UpsideDownCake)
* Java 17 (or higher)

### Build

1.  Clone the repository: `git clone https://github.com/Robert-P-M/Plan_Me_App.git` (Bitte den Link anpassen, falls er anders lautet)
2.  Open the project in Android Studio.
3.  Sync Gradle files.
4.  Run the app on an emulator or device.

<a name="roadmap--future-enhancements"></a>
## 🗺️ Roadmap & Future Enhancements

### V1.0 (Portfolio Ready)
* **Comprehensive Testing:** Write unit tests for all Use Cases and ViewModels to guarantee business logic correctness.
* **Code Polish:** Add KDoc documentation to all public interfaces and methods.
* **Git Hygiene:** Ensure all features are on separate branches with a clean, conventional commit history before merging into `main`.

### Future Ambitions (Post-V1.0)
* Refactor into a Multi-Module architecture (cleanup data_source folder).
* Responsive/Adaptive Ui.
* Implement Drag & Drop for reordering tasks and sections.
* Build a custom, lightweight Markdown editor for task descriptions.
* Introduce collaborative features using a backend service.

<a name="contributing"></a>
## 🤝 Contributing

This is a portfolio project, but feedback and suggestions are always welcome! Please open an issue to discuss your ideas.

<a name="license"></a>
## 📄 License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

<a name="contact"></a>
## 📧 Contact

For questions or feedback, you can connect with me on
<a href="http://www.linkedin.com/in/robert-petyrek-mair" target="_blank" rel="noopener noreferrer">
<img src="https://img.shields.io/badge/-LinkedIn-0A66C2?style=flat&logo=linkedin&logoColor=white" alt="LinkedIn">
</a>

---
---

# 🇩🇪 Plan Me

## Dein fortschrittlicher, Offline-First Aufgabenplaner für strukturierte Produktivität. / Your Advanced, Offline-First Task Planner for Structured Productivity.

---

<a name="app-vorschauen"></a>
## 📸 App-Vorschauen / App Previews

Eine Demonstration der sauberen und funktionsreichen Oberfläche von Plan Me.
A showcase of Plan Me's clean and feature-rich interface.


### Animierte Vorschauen / Animated Previews
| Board- & Listenansicht | Flüssig scrollender Kalender |
|---|---|
| ![Board- & Listenansicht Umschalter](docs/videos/board_list_view.gif) | ![Flüssig scrollender Kalender](docs/videos/scrollable_calendar_video.gif) |

### Screenshots
| Feature / Bildschirm | Vorschau |
|---|---|
| **Leerer Startbildschirm** <br/> Anfangsansicht mit Schnellzugriff-Buttons. | ![Leerer Startbildschirm](docs/images/01_home_empty.png) |
| **Aufgabe-hinzufügen-Dialog** <br/> Umfassender Dialog zur Aufgabenerstellung. | ![Aufgabe-hinzufügen-Dialog](docs/images/02_add_task_dialog.png) |
| **Prioritätsauswahl** <br/> Wähle eine Priorität für deine Aufgabe. | ![Prioritätsauswahl](docs/images/03_priority_picker_dialog.png) |
| **Hashtag-Auswahl** <br/> Füge bestehende oder neue Hashtags mit Volltextsuche hinzu. | ![Hashtag-Auswahl-Dialog](docs/images/04_hashtag_picker_dialog.png) |
| **Datumsauswahl-Kalender** <br/> Wähle ein Fälligkeitsdatum aus einem endlos scrollbaren Kalender. | ![Kalender-Dialog](docs/images/05_calendar_dialog.png) |
| **Startbildschirm mit Aufgaben** <br/> Hauptansicht, gefüllt mit Sektionen und Aufgaben. | ![Startbildschirm mit Aufgaben](docs/images/06_home_with_task.png) |
| **Sektion-hinzufügen-Dialog** <br/> Erstelle neue Sektionen, um deine Aufgaben zu gruppieren. | ![Sektion-hinzufügen-Dialog](docs/images/07_add_section_dialog.png) |
| **Sektionsmenü** <br/> Verwalte Sektionen: Unteraufgaben hinzufügen, umbenennen oder löschen. | ![Sektionsmenü](docs/images/08_section_menu.png) |
| **Aufgabenmenü** <br/> Erweiterte Optionen: Verlauf, Unteraufgaben, Verschieben, Archivieren etc. | ![Aufgabenmenü](docs/images/09_task_menu.png) |
| **Verschachtelte Aufgaben (Grid-Ansicht)** <br/> Visualisiere hierarchische Aufgaben in einem Kanban-Board. | ![Verschachtelte Aufgaben Grid-Ansicht](docs/images/10_nested_tasks_cards_grid_view.png) |
| **Verschachtelte Aufgaben (Listenansicht)** <br/> Eine klare Listenansicht aller Aufgaben und Unteraufgaben. | ![Verschachtelte Aufgaben Listenansicht](docs/images/11_nested_tasks_cards_list_view.png) |
| **Aufgabendetails-Dialog** <br/> Vollständige Aufgabenübersicht mit ihrem rekursiven Pfad. | ![Aufgabendetails-Dialog](docs/images/12_task_details_dialog.png) |
| **Hashtag-Verwaltung** <br/> Zeige und verwalte die Hashtags einer Aufgabe. | ![Aufgabendetails mit Hashtags](docs/images/15_task_details_dialog_after_add_hashtag_search.png) |
| **Hashtag-Aufgaben-Beziehung** <br/> Sieh alle Aufgaben, die mit einem bestimmten Hashtag verknüpft sind. | ![Hashtag-Aufgaben-Beziehungsdialog](docs/images/16_hashtag_tasks_relation-dialog.png) |
| **Aufgabe-verschieben-Dialog** <br/> Verschiebe Aufgaben einfach zwischen Sektionen oder anderen Aufgaben. | ![Aufgabe-verschieben-Dialog](docs/images/18_move_task_dialog.png) |
| **Aufgabenstatistik & Verlauf** <br/> Ein vollständiger Prüfpfad jeder Änderung an einer Aufgabe. | ![Aufgabenstatistik-Dialog](docs/images/19_task_statistics_dialog.png) |

---

**Dieses Projekt dient als umfassendes Portfolio-Projekt und demonstriert Fähigkeiten auf Expertenniveau in der modernen Android-Entwicklung, mit einem Fokus auf Jetpack Compose, einer robusten Offline-First Clean Architecture sowie fortgeschrittenen Datenbank- und Nebenläufigkeitsmustern.**

<a name="funktionen"></a>
## ✨ Funktionen

* **Hierarchisches Aufgabenmanagement:** Zerlege komplexe Projekte in überschaubare Schritte mit unendlich verschachtelbaren Unteraufgaben. Gruppiere zusammengehörige Aufgaben in benutzerdefinierten Sektionen.
* **Intelligente & präzise Benachrichtigungen:** Plane zeitkritische Erinnerungen für jede Aufgabe. Alarme sind exakt, funktionieren auch im Doze-Modus und werden nach einem Geräteneustart automatisch wiederhergestellt, sodass keine Frist verpasst wird.
* **Dynamisches Tagging-System:** Organisiere und kategorisiere Aufgaben mit einem flexiblen Hashtag-System. Finde alle zugehörigen Aufgaben über verschiedene Sektionen hinweg mit einem einzigen Klick.
* **Vollständiger Aufgabenverlauf & Statistik:** Ein dedizierter Statistikbildschirm bietet einen detaillierten Prüfpfad (Audit Trail), der jede Änderung am Titel, der Beschreibung, der Priorität und dem Abschlussstatus einer Aufgabe für vollständige Transparenz nachverfolgt.
* **Adaptive UI & leistungsstarke Ansichten:**
    * **Listen- & Board-Ansicht:** Visualisiere deinen Workflow entweder in einer traditionellen Liste oder einem Kanban-Board-Layout.
    * **Leistungsstarkes Filtern & Sortieren:** Filtere Aufgaben sofort nach Abschluss- oder Archivierungsstatus und sortiere sie nach Erstellungsdatum.
* **Erweiterte Aufgabenoperationen:**
    * **Verschieben & Duplizieren:** Organisiere Aufgaben einfach neu, indem du sie zwischen Sektionen verschiebst oder Kopien vorhandener Aufgaben erstellst.
    * **Archivieren:** Halte deinen Arbeitsbereich sauber, indem du erledigte oder irrelevante Aufgaben archivierst, ohne sie endgültig zu löschen.
* **Intuitive UX:**
    * **Event-Bus-Pattern:** Nahtlose Kommunikation zwischen Bildschirmen und Dialogen, ohne auf den Backstack zur Datenübertragung angewiesen zu sein.
    * **Rekursive Breadcrumbs:** Der Aufgabendetails-Dialog berechnet und zeigt dynamisch den vollständigen verschachtelten Pfad einer Aufgabe an (z. B. `Projekt A / Meilenstein 2 / Aufgabe X`).

<a name="technologie-stack--architekturprinzipien-de"></a>
## 🚀 Technologie-Stack & Architekturprinzipien

* **Kotlin-First:** Vollständig mit Kotlin entwickelt, unter Nutzung von Coroutines und einer vollständig reaktiven `Flow`-basierten Datenpipeline vom Room-DAO bis zum ViewModel.
* **Jetpack Compose:** Eine zu 100 % deklarative UI, die mit Jetpack Compose für eine moderne, reaktive und wartbare Benutzeroberfläche erstellt wurde.
* **Clean Architecture:** Eine strikte, mehrschichtige Architektur, die die App in `data`, `domain` und `presentation`-Layer trennt und Testbarkeit sowie Skalierbarkeit fördert.
* **MVVM (Model-View-ViewModel):** Eine saubere Trennung der Verantwortlichkeiten zwischen der UI (View) und der Geschäftslogik (ViewModel).
* **Room Persistence Library:** Ein normalisiertes relationales Datenbankschema zur Handhabung komplexer Beziehungen (Many-to-Many über Querverweise) und rekursiver Abfragen.
* **Koin:** Leichtgewichtiges und pragmatisches Dependency-Injection-Framework zur Verwaltung von Abhängigkeiten und zur Förderung von entkoppeltem Code.
* **Kernprinzipien:** Konsequente Anwendung von **SOLID**, **DRY** (Don't Repeat Yourself) und **KISS** (Keep It Simple, Stupid).

<a name="architektonische-highlights-de"></a>
## 🏗️ Architektonische Highlights

Dieses Projekt meistert komplexe architektonische Herausforderungen, um hohe Leistung und Robustheit zu gewährleisten.

* **Performance by Design:**
    * **N+1-Problem gelöst:** Löst proaktiv das N+1-Abfrageproblem durch die Verwendung effizienter "Batch"-Datenbankabfragen, um verwandte Daten (wie alle Hashtags für eine Liste von Aufgaben) in einem einzigen Roundtrip abzurufen.
    * **Parallele Ausführung:** Rechenintensive Operationen, wie das Laden einer tiefen rekursiven Hierarchie von Unteraufgaben, werden parallel mit `coroutineScope` und `async`/`awaitAll` ausgeführt, um die Leistung zu maximieren und eine flüssige Benutzeroberfläche zu gewährleisten.
* **Robustes State Management:** Der UI-Status wird von ViewModels als benutzerdefinierter `AppResource<T>`-Wrapper bereitgestellt, der klare, eindeutige Zustände (`Loading`, `Success`, `Error`) liefert, die in den Composables elegant behandelt werden.
* **Widerstandsfähiges Benachrichtigungssystem:** Der Benachrichtigungsplaner ist robust aufgebaut und behandelt `BOOT_COMPLETED`-Ereignisse sowie moderne Android-Berechtigungsanforderungen (`SCHEDULE_EXACT_ALARM`) korrekt.
* **Entkoppelte Domain-Logik:** Die Geschäftslogik ist in Use Cases mit einer einzigen Verantwortung gekapselt, die zur Handhabung komplexer Operationen wie rekursiver Datenabfragen oder der Verlaufsprotokollierung zusammengesetzt werden.

<a name="entwicklung-de"></a>
## 🛠️ Entwicklung

### Anforderungen

* Android Studio Jellyfish | 2023.3.1 oder neuer
* Android SDK 34 (UpsideDownCake)
* Java 17 (oder höher)

### Build

1.  Klone das Repository: `git clone https://github.com/Robert-P-M/Plan_Me_App.git` (Please adapt the link if it's different)
2.  Öffne das Projekt in Android Studio.
3.  Synchronisiere die Gradle-Dateien.
4.  Führe die App auf einem Emulator oder Gerät aus.

<a name="roadmap--zukünftige-erweiterungen-de"></a>
## 🗺️ Roadmap & Zukünftige Erweiterungen

### V1.0 (Portfolio-bereit)
* **Umfassendes Testing:** Schreiben von Unit-Tests für alle Use Cases und ViewModels, um die Korrektheit der Geschäftslogik zu garantieren.
* **Code-Feinschliff:** Hinzufügen von KDoc-Dokumentation zu allen öffentlichen Schnittstellen und Methoden.
* **Git-Hygiene:** Sicherstellen, dass alle Features auf separaten Branches mit einer sauberen, konventionellen Commit-Historie liegen, bevor sie in `main` gemerged werden.

### Zukünftige Ambitionen (Nach V1.0)
* Refactoring in eine Multi-Modul-Architektur (data_source Folder aufräumen).
* Responsive/Adaptive Ui.
* Implementierung von Drag & Drop zum Neuanordnen von Aufgaben und Sektionen.
* Erstellung eines eigenen, leichtgewichtigen Markdown-Editors für Aufgabenbeschreibungen.
* Einführung von Kollaborationsfunktionen über einen Backend-Service.

<a name="mitwirken-de"></a>
## 🤝 Mitwirken

Dies ist ein Portfolio-Projekt, aber Feedback und Vorschläge sind jederzeit willkommen! Bitte öffne ein Issue, um deine Ideen zu diskutieren.

<a name="lizenz-de"></a>
## 📄 Lizenz

Dieses Projekt ist unter der MIT-Lizenz lizenziert – siehe die [LICENSE.md](LICENSE.md) Datei für Details.

<a name="kontakt-de"></a>
## 📧 Kontakt

Für Fragen oder Feedback verbinde dich mit mir auf
<a href="http://www.linkedin.com/in/robert-petyrek-mair" target="_blank" rel="noopener noreferrer">
<img src="https://img.shields.io/badge/-LinkedIn-0A66C2?style=flat&logo=linkedin&logoColor=white" alt="LinkedIn">
</a>
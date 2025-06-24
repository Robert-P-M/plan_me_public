# ✨ Plan Me - An Advanced, Offline-First Android Task Planner

## 🚀 Vision

`Plan Me` is a professional-grade task planner built to demonstrate expert Android development with
Jetpack Compose and Clean Architecture.

[IMG]

## ⭐ Core Features

* **Hierarchical Task Management:** Break down any complex project into manageable steps with
  infinitely nested sub-tasks. Group related tasks into custom, user-defined sections.
* **Intelligent & Precise Notifications:** Schedule time-sensitive reminders for any task. Alarms
  are exact, work even in Doze mode, and are automatically restored after a device reboot, ensuring
  no deadline is ever missed.
* **Dynamic Tagging System:** Organize and categorize tasks with a flexible hashtag system. Find all
  related tasks across different sections with a single tap.
* **Complete Task History:** Every task maintains a full audit trail, tracking changes to its title,
  description, priority, and completion status for complete transparency.
* **Adaptive UI:**
    * **List & Board Views:** Visualize your workflow in either a traditional list or a Kanban-style
      board layout.
    * **Powerful Filtering & Sorting:** Instantly filter tasks by completion or archived status and
      sort them by creation date.
* **Data-Driven Insights:** A dedicated statistics screen provides a detailed, historical flow of
  every action performed on a task.

## 🛠️ Technical Architecture & Principles

This project was architected from the ground up to be scalable, testable, and maintainable, adhering
to industry best practices.

* **Kotlin-First:** Kotlin, Jetpack Compose, Coroutines, Flow, Room.
* **Clean Architecture:** A strict implementation, separating the app into `data`, `domain`, and
  `presentation` layers.
* **UI Pattern:** **MVVM** to ensure a clean separation of concerns between the UI and business
  logic.
* **Asynchronicity:** Heavy use of **Kotlin Coroutines** and **Flow** to create a fully reactive,
  non-blocking data pipeline from the Room DAO to the ViewModel.
* **Database:** Persistence via a normalized **Room** database, handling complex relationships and
  recursive queries.
* **Dependency Injection:** **Koin** for managing dependencies and promoting decoupled, testable
  code.
* **Principles:** Consistent application of **SOLID**, **DRY** (Don't Repeat Yourself), and **KISS
  ** (Keep It Simple, Stupid).

## 🧠 Architectural Highlights & Key Decisions

This project goes beyond a standard implementation by tackling complex architectural challenges:

* **Reactive Data Layer:** A fully reactive data pipeline built with Kotlin Flow and Room, ensuring
  the UI is always in sync with the underlying data.
* **Decoupled Domain Logic:** Business logic is encapsulated in single-responsibility Use Cases,
  composed together to handle complex operations like recursive data fetching.
* **Performance by Design:**
    * **N+1 Problem Solved:** The app proactively solves the N+1 query problem by using efficient "
      batch" database queries to fetch related data (like all hashtags for a list of tasks) in a
      single roundtrip.
    * **Parallel Execution:** Computationally expensive operations, like loading a deep recursive
      hierarchy of sub-tasks, are executed in parallel using `coroutineScope` with `async`/
      `awaitAll` to maximize performance and ensure a smooth UI.
* **Robust State Management:** UI state is exposed from ViewModels as a custom `AppResource<T>`
  wrapper, providing clear, unambiguous states (`Loading`, `Success`, `Error`), which are handled
  gracefully in the Composables.
* **Resilient Notifications:** The notification system is built to be robust, correctly handling
  `BOOT_COMPLETED` events and modern Android permission requirements (`SCHEDULE_EXACT_ALARM`).

## 🧪 Roadmap to V1.0

The following steps will complete the initial, portfolio-ready version of the application:

* **Complete Feature Set:** Implement the final UI for displaying the detailed task history in the
  statistics dialog.
* **Comprehensive Testing:** Write unit tests for all Use Cases and ViewModels to guarantee business
  logic correctness.
* **Code Polish:** Add KDoc documentation to all public interfaces and methods.
* **Git Hygiene:** Ensure all features are on separate branches with a clean, conventional commit
  history before merging into `main`.

### Future Ambitions (Post-V1.0)

* Refactor into a Multi-Module architecture.
* Implement Drag & Drop for reordering tasks and sections.
* Build a custom, lightweight Markdown editor for task descriptions and comments.
* Introduce collaborative features using a backend service.

## TODO

- [ ] Images / Videos for github
- [ ] Responsive/Adaptive Layout for large screens
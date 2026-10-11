# Smart Fitness & Meal Tracker — Технически проектен план и архитектура

Подробно ръководство за архитектурата, структурите от данни, бизнеслогиката и хронологичната разработка на мобилното приложение.

---

## 📋 Technical Overview & Specifications

* **App Name:** Smart Fitness & Meal Tracker
* **Repository Name (GitHub):** `MobileApps2025-<ФН>`
* **Primary Language:** Kotlin
* **Min SDK:** 24 (Android 7.0) | **Target SDK:** 34 (Android 14)
* **Architecture Pattern:** MVVM (Model-View-ViewModel) + Clean Architecture + Repository Pattern
* **UI Framework:** Jetpack Compose (или Material 3 XML Views)
* **Design Guidelines:** Material 3 (с поддръжка на адаптивна Light и Dark тема)

---

## 🗄️ Database Architecture (Room Persistence)

Базата данни се състои от **3 свързани таблици** с Foreign Key релации и пълно поддържане на CRUD операции през потребителския интерфейс.

### 1. Database Schema (`data/local/entity/`)

#### A. `WorkoutEntity` (Таблица: `workouts`)
* `id`: `Long` (Primary Key, AutoGenerate)
* `title`: `String` (напр. *"Силова тренировка - Гърди"*)
* `date`: `Long` (Timestamp в милисекунди)
* `durationMinutes`: `Int`
* `caloriesBurned`: `Int`

#### B. `ExerciseEntity` (Таблица: `exercises`)
* `id`: `Long` (Primary Key, AutoGenerate)
* `workoutId`: `Long` (Foreign Key към `workouts.id` с `CASCADE` изтриване)
* `name`: `String` (напр. *"Повдигане от лег"*)
* `sets`: `Int`
* `reps`: `Int`
* `weightKg`: `Double`

#### C. `MealEntity` (Таблица: `meals`)
* `id`: `Long` (Primary Key, AutoGenerate)
* `date`: `String` (Формат: YYYY-MM-DD)
* `foodName`: `String` (напр. *"Овесени ядки"*)
* `weightGrams`: `Int` (Тегло на порцията в грамове)
* `calories`: `Int` (Изчислени калории за въведения грамаж)
* `proteinG`: `Double` (Изчислен протеин за въведения грамаж)
* `carbsG`: `Double` (Изчислени въглехидрати за въведения грамаж)
* `fatG`: `Double` (Изчислени мазнини за въведения грамаж)
* `barcode`: `String?` (Забележка: Незадължително поле за запазване на баркода)

---

## 🔄 CRUD Операции за всяка таблица (UI Layer)

### 1. Таблица `Meals`
* **Create (Добавяне):**
  * **Път А (Чрез REST API):** Сканиране на баркод с камерата $\rightarrow$ заявка към Open Food Facts API $\rightarrow$ получаване на макронутриенти за $100\text{g}$ $\rightarrow$ въвеждане на грамове $\rightarrow$ автоматично преизчисляване $\rightarrow$ запис в Room.
  * **Път Б (Ръчно въвеждане):** Форма с полета за Име на храната, Грамаж, Калории, Протеин, Въглехидрати и Мазнини $\rightarrow$ директен запис в Room.
* **Read (Преглед):** Дневен списък с консумираните храни за избраната дата с разпределение на калории и макроси.
* **Update (Редакция):** Избор на съществуваща храна $\rightarrow$ промяна на грамажа (с автоматично преизчисляване на калориите и макросите на жива промяна в текстовото поле) $\rightarrow$ обновяване в базата.
* **Delete (Изтриване):** Премахване на хранителен запис от дневника.

### 2. Таблица `Workouts`
* **Create:** Добавяне на нова тренировка с име, дата и времетраене.
* **Read:** Хронологичен списък на преминалите тренировки.
* **Update:** Редактиране на заглавие, дата или времетраене.
* **Delete:** Изтриване на тренировка (с автоматично каскадно премахване на свързаните упражнения).

### 3. Таблица `Exercises`
* **Create:** Добавяне на упражнение към конкретна тренировка.
* **Read:** Преглед на списъка с упражнения вътре в детайлите на дадена тренировка.
* **Update:** Коригиране на серии, повторения и килограми.
* **Delete:** Премахване на упражнение от тренировката.

---

## 🌐 External Services, REST API & Hardare Integration

### 1. REST API Communication (Open Food Facts)
* **Endpoint:** `https://world.openfoodfacts.org/api/v0/product/{barcode}.json`
* **Библиотека:** Retrofit2 + Gson Converter + Coroutines
* **Логика:** Извличане на базовите хранителни стойности ($100\text{g}$) за съответния продукт.

### 2. Динамично Преизчисление спрямо Грамажа (Math Logic)
Когато данните за храната пристигнат от REST API (или се въвеждат ръчно), базата данни получава преизчислените стойности спрямо реалното тегло `N` (в грамове):

$$\text{Calories}_{\text{total}} = \text{Calories}_{100\text{g}} \times \left( \frac{\text{weightGrams}}{100} \right)$$

$$\text{Protein}_{\text{total}} = \text{Protein}_{100\text{g}} \times \left( \frac{\text{weightGrams}}{100} \right)$$

$$\text{Carbs}_{\text{total}} = \text{Carbs}_{100\text{g}} \times \left( \frac{\text{weightGrams}}{100} \right)$$

$$\text{Fat}_{\text{total}} = \text{Fat}_{100\text{g}} \times \left( \frac{\text{weightGrams}}{100} \right)$$

*Във ViewModel / UI слой при всяка промяна на цифрата в полето „Грамаж“ тези формули реагират в реално време, преди натискане на бутона „Запази“.*

### 3. Sensors & Hardware API Integration
* **Step Counter / Accelerometer Sensor:**
  * Достъп през `SensorManager`.
  * Регистриране на стъпките през деня и динамично изчисляване на изгорени калории от движение:
    $$\text{BurnedCalories} = \text{StepCount} \times 0.04$$
* **Camera Intent / Barcode Scanning:**
  * Интеграция с ML Kit Barcode Scanning за четене на баркода чрез камерата.
* **Share Intent:**
  * Използване на `Intent.ACTION_SEND` за споделяне на дневния фитнес и хранителен отчет през месинджър приложения.

---

## 🏗️ Architecture & Package Structure

```text
app/
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   ├── dao/          # WorkoutDao.kt, MealDao.kt
│   │   └── entity/       # WorkoutEntity.kt, ExerciseEntity.kt, MealEntity.kt
│   ├── remote/
│   │   ├── OpenFoodFactsApi.kt
│   │   └── dto/          # Network response models
│   ├── repository/       # WorkoutRepositoryImpl.kt, MealRepositoryImpl.kt
│   └── sensor/           # StepSensorManager.kt
├── ui/
│   ├── dashboard/        # DashboardViewModel, DashboardScreen (Sensor & Summary)
│   ├── workouts/         # WorkoutsViewModel, WorkoutsScreen, ExerciseDetailScreen
│   ├── meals/            # MealsViewModel, MealsScreen, AddEditMealDialog
│   └── theme/            # Color.kt, Theme.kt, Type.kt (Material 3)
├── di/                   # AppModule.kt / Dependency Injection Setup
└── util/                 # CalorieCalculator.kt, DateFormatter.kt
```

---

## 🚀 Chronological Development Guide

### Фаза 1: Инициализация и Проектна Настройка
1. Инициализиране на Git репозитори с име `MobileApps2025-<ФН>`.
2. Конфигуриране на `build.gradle.kts` с нужните зависимости:
   * Room Database
   * Retrofit + Gson
   * Navigation Component
   * Coroutines & Flow
   * CameraX / ML Kit Barcode Scanning
   * JUnit & Espresso
3. Настройване на Material 3 Light/Dark цветова палитра.

### Фаза 2: База данни и Repository Слой
1. Дефиниране на Entities (`WorkoutEntity`, `ExerciseEntity`, `MealEntity`).
2. Създаване на DAOs с reactive заявки (`Flow<List<T>>`).
3. Създаване на Repository класове за капсулиране на базата данни и мрежовите заявки.

### Фаза 3: REST API и Сензорен Модул
1. Реализиране на Retrofit клиента за Open Food Facts API.
2. Имплементиране на `StepSensorManager` за четене на стъпки в реално време.
3. Разработване на математически модул за автоматично скалиране на макронутриенти спрямо грамажа.

### Фаза 4: UI Навигация и Екрани
1. **Dashboard Screen:** Брояч на стъпки, дневен преглед на калориите и Share Intent бутон.
2. **Workouts Screen:** Пълен CRUD за тренировки и упражнения.
3. **Meals Screen:**
   * Списък с дневната храна.
   * Форма за сканиране с камера + авто-попълване.
   * Форма за напълно ръчно въвеждане.
   * Динамична настройка на грамаж с автоматично преизчисляване.

### Фаза 5: Автоматизирано Тестване
1. **Unit Тестове (JUnit + Mockito/MockK):**
   * Тест на математическите преизчисления при промяна на грамажа.
   * Тест на калкулатора за стъпки/калории.
   * Тест на Repository слоя с фалшиви (mocked) REST API данни.
2. **UI Тест (Espresso):**
   * Автоматизиран тест за добавяне на тренировка и проверка дали тя се показва в UI списъка.

### Фаза 6: Финализиране
1. Билдване на APK файл: `/apk/app-release.apk`.
2. Попълване на корневия `/README.md` файл с пълните инструкции за стартиране и работа.
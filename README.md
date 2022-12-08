# Трекер задач

## 1. Объекты:
* Task (Задача)
* Subtask (Подзадача) extends Task
* Epic (Эпик) extends Task

Задача:
* Title (Название)
* Description (Описание)
* ID (уникальный номер)
* Status (NEW, IN_PROGRESS, DONE)

Подзадача: 
* Title (Название)
* Description (Описание)
* ID (уникальный номер)
* Status (NEW, IN_PROGRESS, DONE)
* Epic

Эпик:
* Title (Название)
* Description (Описание)
* ID (уникальный номер)
* Status (NEW, IN_PROGRESS, DONE)
* List of subtasks

## 2. Менеджер
* separate HashMaps for Tasks
* тестирование из main 
* отдельные методы для каждого из типа задач 
  * список 
  * удаление
  * получение по id
  * создание
  * обновление
  * удаление
* получение всех подзадач эпика

## 3. Статусы








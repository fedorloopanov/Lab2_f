# Лаба 2 — компактная версия (v3)

Что сделано:
- основа сохранена из первой лабораторной: `app.model`, `app.parser`, `app.service`, `app.ui`
- поддержаны все форматы из архива с миссиями: JSON, XML, YAML, секционный TXT и событийный A5
- паттерны, которые здесь действительно можно защищать по теории:
  - Strategy — `MissionParserContext`, `MissionParser` и конкретные парсеры
  - Factory Method — `MissionParserCreator` и конкретные creator-ы
  - Builder — `MissionBuilder` + `DefaultMissionBuilder`
  - Director — `MissionDirector`
  - Decorator — `MissionReportDecorator` + `Detailed/Risk/Statistics`
  - Facade — `MissionLoaderService`
- `MissionModelFactory` оставлен как централизованный создатель доменных объектов, но не заявляется как отдельный GoF-паттерн
- GUI на Swing, добавлен выбор типа отчёта

Что важно для защиты:
- в Strategy контекст хранит ссылку на стратегию парсинга и делегирует ей разбор файла
- в Decorator базовый декоратор реализует тот же интерфейс, что и компонент, и хранит ссылку на `MissionReport`

Количество Java-файлов: 28.

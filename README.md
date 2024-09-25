# RegionParser
Утилита упрощает импорт участков для Чистой Логистики. 

Возможности:
- Получает GeoJSON участка из названия Региона и Названия самого участка.
- Тестирует целостность участка, и указывает количество полигонов
  - Может указать, что в участке есть дырка и указать на это 
- Использует запрос graphql для создания участков на сервере.
- Выводит SQL запрос для импорта участка. 
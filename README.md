# RegionParser
Утилита упрощает импорт участков для Чистой Логистики. 

## Быстрый старт

При запуске программы потребуется:
- Домен сайта приложения ЧЛ (Н:disp.t1.groupstp.ru)
- Логин пользователя от приложения ЧЛ
- Пароль пользователя от приложения ЧЛ
- Указать папку для сохранения файла с GeoJSON (Например: D:\GeJson\test)
- Следовать подказкам из консоли.

## Возможности:
- Получает GeoJSON участка из названия Региона и Названия самого участка.
- Тестирует целостность участка, и указывает количество полигонов
  - Может указать, что в участке есть дырка и указать на это 
- Использует запрос graphql для создания участков на сервере.
- Выводит SQL запрос для импорта участка. 

## Фото работы программы:
- ![Подготовка программы](D:\Styding\Java Spring\NodeParcer\images\Prepare.png)
- ![1 - Свой файл](D:\Styding\Java Spring\NodeParcer\images\Your_file.png)
- ![Запуск поиска](D:\Styding\Java Spring\NodeParcer\images\NotPrepare.png)
- ![2 -Автоматическое опредление](D:\Styding\Java Spring\NodeParcer\images\Repeat.png)
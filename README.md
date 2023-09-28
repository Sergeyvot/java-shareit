# ShareIt (учебный проект)

![Изображение](https://s3.amazonaws.com/offlinepost/wp-media-folder-offlinepost/wp-content/uploads/2021/06/air2-652x420.png)

## Функциональность

Backend приложения - сервиса для шеринга.
Обеспечивает возможность для пользователей рассказать, какими вещами они готовы поделиться, 
находить нужную вещь и брать её в аренду на какое-то время. 
Вещь бронируется для аренды на определенные даты/время. Владелец вещи должен подтвердить
бронирование, статус доступности на время бронирования закрывается.
Для нахождения вещей организован поиск. Возвращает доступные для аренды вещи, содержащие
текст из строки запроса в названии или описании.
Если нужная вещь не найдена при поиске, пользователь может создать запрос, в котором
указывается, что именно он ищет. В ответ на запрос другие пользовали могут добавить вещь.
После того как вещь возвращена, у пользователя, который её арендовал, есть возможность 
оставить отзыв.

Для владельца вещи доступны: 
- добавление новой вещи;
- редактирование вещи;
- просмотр списка всех вещей владельца с указанием названия и описания для каждой;
- подтверждение или отклонение запроса на бронирование;
- получение списка бронирований для всех вещей владельца;
- получение данных о конкретном бронировании (включая его статус);
- просмотр отзывов для всех вещей данного владельца;

Для пользователя доступны:
- просмотр информации о конкретной вещи по её идентификатору;
- поиск вещи по тексту в названии или описании;
- добавление запроса на бронирование;
- получение данных о конкретном бронировании (для автора бронирования);
- получение списка всех бронирований текущего пользователя;
- добавление нового запроса вещи;
- получение списка своих запросов вместе с данными об ответах на них;
- получение списка запросов, созданных другими пользователями(сортировка по дате запроса);
- получение данных об одном конкретном запросе;
- добавление отзыва (для пользователя, бравшего вещь в аренду);
- просмотр отзывов для конкретной вещи;

### Технологии, стек

Многомодульный проект Maven. Spring Boot.
Основная логика расположена в модуле ***server***.
Пакеты разделены по сущностям (*user*, *item*, *request*, *booking*). Внутри каждого пакета
контроллеры для обработки эндпоинтов, сервисная логика, уровни работы с репозиториями.
Аналогично организованы классы тестирования.
Взаимодействие с БД - Hibernate.

Согласно ТЗ, в отдельном модуле расположена часть приложения ***gateway***.
В нем находятся контроллеры, с которыми непосредственно работают пользователи. Приложение
служит для валидации входных данных. Также может использоваться для кэширования запросов
(в данном проекте не реализовано).
Взаимодействие с основным приложением *server* - через REST.
Каждое приложение запускается в отдельном Docker-контейнере

Использовалась Java 11






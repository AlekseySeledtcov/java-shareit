# java-shareit
##### Приложение предоставляет возможность одним пользователям сдавать, а другми брать, в аренду вещи. 

## В приложении реализованы следующие функции: 
#### Пользователи 
    POST/users - добавлени пользователя 

    PATCH/users/userId - обновление полей пользователя
параметры: 
userId - id пользователя

    GET/users/userId - получение пользователя по id
параметры:
userId - id пользователя

    DELETE/users/userId - удаление пользователя по id
параметры:
userId - id пользователя

    GET/users - получение списка пользователей 

#### Вещи
    POST/items - добавление вещи пользователя с userId
параметры:
X-Sharer-User-Id - userId пользователя

    PATCH/items/itemId - обновление полей вещи с itemId пользователя с userId
параметры:
itemId - id вещи
X-Sharer-User-Id - userId пользователя

    GET/items/itemId - получение вещи с itemId пользователя userId

параметры:
itemId - id вещи
X-Sharer-User-Id - userId пользователя

    GET/items - получение списка вещей пользователя userId
параметры:
X-Sharer-User-Id - userId пользователя

GET/items/search - поиск вещи по переменной строки запроса
параметры:
text - строка запроса


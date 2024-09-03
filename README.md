## Референс по FastCGI для Лабы №1 по вебу

### Запуск окружения

Пререквизиты:
- Java 17
- Docker
- не занятый порт 8800

1. `./gradlew clean build`
2. `docker compose up --force-recreate --build -d`

В примере поднимается Apache Httpd 2.4.64 из официального докер-образа + официальный модуль mod_fastcgi (собирается 
при сборке образа из исходников) + Java FastCGI Server на основе некогда официальной библиотеки от разработчиков 
FastCGI. Либы нет в Maven central, поэтому ее исходники, найденные на просторых Интернетов, помещены в отдельный 
gradle-модуль `fastcgi-lib`. Из модификаций в либу -- совместимость с Java 17 (ну или это баг от самих разработчиков, 
мне было лень разбираться).

### Что умеет референс?

Референс демонстрирует:
1. Настройку Apache httpd для работы с FastCGI external servers
2. Как при помощи либы от разработчиков FastCGI и Java написать FastCGI сервер на Java на коленке. (Для целей 
изучения подхода студента считаю достаточным и этой либы, хотя видел либу от Netty)

Как потыкать:
1. Запустить демонстрационный стенд при помощи докера
2. Вооружиться курлом:
```
curl -i http://localhost:8800/fcgi-bin/hello-world.jar - простая hello-world страничка
curl -i http://localhost:8800/fcgi-bin/hello-world.jar?debug=1 - echo с доступными параметрами FastCGI
curl -d "x=1&y=5" -i http://localhost:8800/fcgi-bin/hello-world.jar - демонстрирует возможность реализации DHTML и AJAX (работа с POST-ом и телом запроса)
```

### Как по идее будет выглядеть Лаба №1

- студенты клепают html, css, js статику как обычно
- вооружаясь упорством, пишут FastCGI сервер на Java
- поднимают на гелиосе Apache httpd, настраивают его на обслужение статического контента + на перенаправление
запросов за динамическим контентом к FastCGI серверу. FastCGI сервер также поднимают на гелиосе
- js-ый скрипт, следуая канонам AJAX или DHTML, обращается POST-ом или GET-ом к Java серверу; получает в ответ
данные или сразу html-фрагмент для таблицы результатов; вставляет данные в DOM текущей страницы.

### Как развернуть на Helios?

Для того чтобы задеплоить свою лабу на гелиос, студентам нужно будет запустить инстанс Apache Httpd на любом порту 
своего portbase от имени своего пользователя. Httpd нужно настроить так, чтобы он мог раздавать статические файлы 
(html, css, js) и чтобы он мог перенаправлять FastCGI запросы на Java-сервер, умеющий эти запросы обрабатывать.

С учетом некоторой нетривиальности конфигурации Apache Httpd на гелиосе, для студентов будет предложенный шаблон 
конфигурационного файла `httpd.conf`. В этом файле описана настройка httpd таким образом, чтобы он поселился в хомяке 
студента на отдельном порту + для httpd уже добавлен модуль `mod_fastcgi`, необходимый для корректной работы с 
предлагаемой библиотекой.

Итого алгоритм действий студента:
1. Написать статическую часть лабы (HTML, CSS, JS)
2. Поднять httpd на гелиосе и настроить его
3. Задеплоить статическую часть лабы на гелиос под httpd
4. Написать FastCGI сервер на Java
5. Запустить написанный FastCGI сервер на гелиосе
6. Натравить httpd на этот сервер.

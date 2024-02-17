# Занятие «Работа с реляционными базами данных»

## Цели занятия

Научиться работать с SQL базами данных используя Clojure библиотеки.

## Краткое содержание

- познакомимся с протоколом jdbc и подключим библиотеку next.jdbc;
- подключимся к базе данных и научимся выполнять транзакции;
- научимся использовать библиотеку honey-sql для работы с языком запросов SQL;
- создадим базу данных для проекта URL Shortener, напишем SQL запросы для реализации бизнес логики приложения.

## JDBC

[Java Database Connectivity](https://en.wikipedia.org/wiki/Java_Database_Connectivity) is an application programming interface (API) for the Java programming language which defines how a client may access a database.

## next.jdbc

[next.jdbc repo](https://github.com/seancorfield/next-jdbc)

db-spec -> DataSource -> Connection

### JDBC drivers

- [PostgreSQL](https://central.sonatype.com/artifact/org.postgresql/postgresql)
- [H2](https://central.sonatype.com/artifact/com.h2database/h2)
- [SQLite](https://central.sonatype.com/artifact/org.xerial/sqlite-jdbc)

### Connection Pool

[HikariCP](https://github.com/brettwooldridge/HikariCP) or [c3p0](https://github.com/swaldman/c3p0)

### H2 Database

[H2 documentation](https://www.h2database.com/html/main.html)

- Very fast, open source, JDBC API;
- Embedded and server modes; in-memory databases;
- Browser based Console application;
- Small footprint: around 2.5 MB jar file size.

### The primary SQL execution API in next.jdbc is

- plan — yields an IReduceInit that, when reduced with an initial value, executes the SQL statement and then reduces over the ResultSet with as little overhead as possible.
- execute-one! — executes the SQL or DDL statement and produces a single realized hash map. The realized hash map returned by execute-one! is Datafiable and thus Navigable.
- execute — executes the SQL statement and produces a vector of realized hash maps, that use qualified keywords for the column names, of the form :`<table>/<column>`.

## HoneySQL

[HoneySQL repo](https://github.com/seancorfield/honeysql)

## HugSQL

[HugSQL repo](https://github.com/layerware/hugsql)

## Migratus

[Migratus repo](https://github.com/yogthos/migratus)

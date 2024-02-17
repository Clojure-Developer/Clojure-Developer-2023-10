# URL Shortener

## Установка зависимостей

```bash
lein deps with-profile cljs
npm install
```

## Универсальный запуск из командной строки

### Сборка клиентской части

```bash
npx shadow-cljs compile app
```

### Запуск в режиме разработки (REPL)

```bash
lein repl
```

Теперь можно запустить веб-сервер следующими образом

```clojure
user=> (start-server)
Server started on port: 8000
#object[org.eclipse.jetty.server.Server ..."]
```

Или остановить

```clojure
user=> (stop-server)
Server stopped
nil
```

## Запуск через Jack-in: VSCode + Calva

1. Запускаем Jack-in через Command Palette.
2. Выбираем project type: `shadow-cljs`.
3. Выбираем build to compile: `app`.
4. Выбираем build to connect: `app`.
   В этот момент в `output.calva-repl` будет выводиться

   ```text
   ; Waiting for Shadow CLJS runtimes, start your CLJS app...`
   ```
  
5. Запускаем сервер с помощью `(user/start-server)`.
6. Открываем в браузере `localhost:8000` — это и есть наш CLJS runtime.

После этого мы должны подключиться к CLJS реплу.

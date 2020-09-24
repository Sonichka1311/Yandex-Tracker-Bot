# Yandex-Tracker-Bot
Java telegram bot for working with Yandex.Tracker.

# Deploy
0. In `task-03-telegram-random-coffee/src/main/java/ru/hse/cs/java2020/task03/Bot.java` put bot name at line 21 and bot token at line 26.
1. Up DB: from root diretory do
```
cd task-03-telegram-random-coffee/src/main/java/ru/hse/cs/java2020/task03
docker-compose up
```
2. Up bot: from root directory do
```
gradle :task-03-telegram-random-coffee:run
```

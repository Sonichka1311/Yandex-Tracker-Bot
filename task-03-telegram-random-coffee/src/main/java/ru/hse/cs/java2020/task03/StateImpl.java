package ru.hse.cs.java2020.task03;

public class StateImpl implements State {
    private final Long userId;
    private final String user;
    private final Bot bot;
    private final Database database;

    StateImpl(Long userId, Bot bot) {
        this.userId = userId;
        this.user = userId.toString();
        this.bot = bot;
        this.database = bot.getDatabase();
    }

    @Override
    public void action(String message) {

    }

    public Long getUserId() {
        return userId;
    }

    public String getUser() {
        return user;
    }

    public Bot getBot() {
        return bot;
    }

    public Database getDatabase() {
        return database;
    }
}

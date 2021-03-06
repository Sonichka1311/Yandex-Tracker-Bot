package ru.hse.cs.java2020.task03;

public class StateUnauthorized extends StateImpl {
    StateUnauthorized(Long userId, Bot bot) {
        super(userId, bot);
    }

    @Override
    public void action(String message) {
        getBot().sendMessage(getUser(), Constants.NOT_AUTH);
    }
}

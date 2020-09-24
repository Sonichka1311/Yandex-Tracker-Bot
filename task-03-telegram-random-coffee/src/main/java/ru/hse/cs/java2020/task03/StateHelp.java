package ru.hse.cs.java2020.task03;

public class StateHelp extends StateImpl {
    StateHelp(Long userId, Bot bot) {
        super(userId, bot);
    }

    @Override
    public void action(String message) {
        getBot().sendMessage(getUser(), Constants.HELP);
    }
}

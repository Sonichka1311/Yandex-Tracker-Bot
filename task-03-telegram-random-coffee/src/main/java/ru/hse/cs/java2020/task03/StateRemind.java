package ru.hse.cs.java2020.task03;

public class StateRemind extends StateImpl {
    StateRemind(Long userId, Bot bot) {
        super(userId, bot);
    }

    @Override
    public void action(String message) {
        getBot().sendMessage(getUser(), Constants.NEED_HELP);
    }
}

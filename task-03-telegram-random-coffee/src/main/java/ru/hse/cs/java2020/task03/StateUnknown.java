package ru.hse.cs.java2020.task03;

public class StateUnknown extends StateImpl {
    StateUnknown(Long userId, Bot bot) {
        super(userId, bot);
    }

    @Override
    public void action(String message) {
        getBot().sendMessage(getUser(), Constants.NOT_UNDERSTAND);
        new StateRemind(getUserId(), getBot()).action(message);
    }
}

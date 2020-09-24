package ru.hse.cs.java2020.task03;

public class StateEnd extends StateImpl {
    StateEnd(Long userId, Bot bot) {
        super(userId, bot);
    }

    @Override
    public void action(String message) {
        getDatabase().updateUserState(getUserId(), "main");
        new StateRemind(getUserId(), getBot()).action(message);
    }
}

package ru.hse.cs.java2020.task03;

public class StateStart extends StateImpl {
    StateStart(Long userId, Bot bot) {
        super(userId, bot);
    }

    @Override
    public void action(String message) {
        getDatabase().updateUserState(getUserId(), "waitToken");
        getBot().sendMessage(getUser(), Constants.START);
        getBot().sendMessage(getUser(), Constants.TOKEN);
    }
}

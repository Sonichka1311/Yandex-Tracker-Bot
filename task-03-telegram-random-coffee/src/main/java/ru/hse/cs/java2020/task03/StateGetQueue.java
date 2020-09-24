package ru.hse.cs.java2020.task03;

public class StateGetQueue extends StateImpl {
    StateGetQueue(Long userId, Bot bot) {
        super(userId, bot);
    }

    @Override
    public void action(String message) {
        getDatabase().setQueue(getUserId(), message);
        getDatabase().updateUserState(getUserId(), "waitSummary");
        getBot().sendMessage(getUser(), Constants.SUMMARY);
    }
}

package ru.hse.cs.java2020.task03;

public class StateGetTask extends StateImpl {
    StateGetTask(Long userId, Bot bot) {
        super(userId, bot);
    }

    @Override
    public void action(String message) {
        getDatabase().updateUserState(getUserId(), "waitTask");
        getBot().sendMessage(getUser(), Constants.WHICH_TASK);
    }
}

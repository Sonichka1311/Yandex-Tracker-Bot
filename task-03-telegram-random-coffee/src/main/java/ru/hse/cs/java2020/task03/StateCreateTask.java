package ru.hse.cs.java2020.task03;

public class StateCreateTask extends StateImpl {
    StateCreateTask(Long userId, Bot bot) {
        super(userId, bot);
    }

    @Override
    public void action(String message) {
        getDatabase().updateUserState(getUserId(), "waitQueue");
        getBot().sendMessage(getUser(), Constants.QUEUE);
    }
}

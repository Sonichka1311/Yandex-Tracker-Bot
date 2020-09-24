package ru.hse.cs.java2020.task03;

public class StateGetTasks extends StateImpl {
    StateGetTasks(Long userId, Bot bot) {
        super(userId, bot);
    }

    @Override
    public void action(String message) {
        getDatabase().updateUserState(getUserId(), "waitPage");
        getBot().sendMessage(getUser(), Constants.TASKS_PAGE);
    }
}

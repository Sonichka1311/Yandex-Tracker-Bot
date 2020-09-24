package ru.hse.cs.java2020.task03;

public class StateGetComments extends StateImpl {
    StateGetComments(Long userId, Bot bot) {
        super(userId, bot);
    }

    @Override
    public void action(String message) {
        getDatabase().updateUserState(getUserId(), "waitTaskComment");
        getBot().sendMessage(getUser(), Constants.WHICH_TASK_COM);
    }
}

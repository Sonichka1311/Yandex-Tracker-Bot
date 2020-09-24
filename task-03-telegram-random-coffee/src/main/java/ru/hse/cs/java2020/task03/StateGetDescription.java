package ru.hse.cs.java2020.task03;

public class StateGetDescription extends StateImpl {
    StateGetDescription(Long userId, Bot bot) {
        super(userId, bot);
    }

    @Override
    public void action(String message) {
        getDatabase().setDescription(getUserId(), message);
        getDatabase().updateUserState(getUserId(), "waitAssignee");
        getBot().sendMessage(getUser(), Constants.ASSIGNEE);
    }
}

package ru.hse.cs.java2020.task03;

public class StateGetCommentsWithKey extends StateImpl {
    StateGetCommentsWithKey(Long userId, Bot bot) {
        super(userId, bot);
    }

    @Override
    public void action(String message) {
        getDatabase().setTask(getUserId(), message);
        getDatabase().updateUserState(getUserId(), "waitPageComment");
        getBot().sendMessage(getUser(), Constants.COMMENT_PAGE);
    }
}

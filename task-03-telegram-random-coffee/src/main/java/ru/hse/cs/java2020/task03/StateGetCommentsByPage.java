package ru.hse.cs.java2020.task03;

import java.util.Objects;

public class StateGetCommentsByPage extends StateImpl {
    StateGetCommentsByPage(Long userId, Bot bot) {
        super(userId, bot);
    }

    @Override
    public void action(String message) {
        String token = getDatabase().getToken(getUserId());
        String task = getDatabase().getTask(getUserId());
        String companyId = getDatabase().getCompanyId(getUserId());
        TrackerClient client = new TrackerClient(token, companyId);

        int start = Integer.parseInt(message);
        int num = getDatabase().getCount(getUserId());
        String comments = client.getComments(task, start, start + num);

        getDatabase().updatePage(getUserId(), start + num);
        getDatabase().updateUserState(getUserId(), "waitNextComment");

        getBot().sendMessage(getUser(), Objects.requireNonNullElse(comments, Constants.NOT_FOUND_COM));
    }
}

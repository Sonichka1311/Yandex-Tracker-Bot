package ru.hse.cs.java2020.task03;

import java.util.Objects;

public class StateGetTasksByPage extends StateImpl {
    StateGetTasksByPage(Long userId, Bot bot) {
        super(userId, bot);
    }

    @Override
    public void action(String message) {
        String token = getDatabase().getToken(getUserId());
        String companyId = getDatabase().getCompanyId(getUserId());
        TrackerClient client = new TrackerClient(token, companyId);

        int start = Integer.parseInt(message);
        int num = getDatabase().getCount(getUserId());
        System.out.println(start);
        System.out.println(start + num);
        String tasks = client.getTasks(start, start + num);
        getDatabase().updatePage(getUserId(), start + num);
        getDatabase().updateUserState(getUserId(), "waitNext");
        getBot().sendMessage(getUser(), Objects.requireNonNullElse(tasks, Constants.NOT_FOUND));
    }
}

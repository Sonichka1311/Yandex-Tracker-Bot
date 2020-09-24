package ru.hse.cs.java2020.task03;

import java.util.Objects;

public class StateGetTaskWithKey extends StateImpl {
    StateGetTaskWithKey(Long userId, Bot bot) {
        super(userId, bot);
    }

    @Override
    public void action(String message) {
        String token = getDatabase().getToken(getUserId());
        String companyId = getDatabase().getCompanyId(getUserId());
        TrackerClient client = new TrackerClient(token, companyId);
        String tasks = client.getTask(message);
        getDatabase().updateUserState(getUserId(), "main");
        getBot().sendMessage(getUser(), Objects.requireNonNullElse(tasks, Constants.NOT_FOUND_ONE));
        getBot().sendMessage(getUser(), "Напомнить, что я умею? Нажми /help");
    }
}

package ru.hse.cs.java2020.task03;

public class StateCreateTaskWithData extends StateImpl {
    StateCreateTaskWithData(Long userId, Bot bot) {
        super(userId, bot);
    }

    @Override
    public void action(String message) {
        boolean assignee = message.equals("да");
        String summary = getDatabase().getSummary(getUserId());
        String queue = getDatabase().getQueue(getUserId());
        String description = getDatabase().getDescription(getUserId());
        String token = getDatabase().getToken(getUserId());
        String companyId = getDatabase().getCompanyId(getUserId());
        TrackerClient client = new TrackerClient(token, companyId);
        String task = client.createTask(summary, queue, description, assignee);
        getDatabase().updateUserState(getUserId(), "main");
        if (task == null) {
            getBot().sendMessage(getUser(), Constants.NOT_CREATED);
        } else {
            getBot().sendMessage(getUser(), Constants.created(summary, task));
        }
        new StateRemind(getUserId(), getBot()).action(message);
    }
}

package ru.hse.cs.java2020.task03;

public class StateGetAssignee extends StateImpl {
    StateGetAssignee(Long userId, Bot bot) {
        super(userId, bot);
    }

    @Override
    public void action(String message) {
        message = message.toLowerCase();
        if (!message.equals("да") && !message.equals("нет")) {
            getBot().sendMessage(getUser(), Constants.YES_OR_NO);
            return;
        }
        new StateCreateTaskWithData(getUserId(), getBot()).action(message);
    }
}

package ru.hse.cs.java2020.task03;

public class StateMain extends StateImpl {
    private static final int TASK_LENGTH = 1;
    private static final int TASK_KEY_LENGTH = 3;

    StateMain(Long userId, Bot bot) {
        super(userId, bot);
    }

    @Override
    public void action(String message) {
        String[] commands = message.split("_");
        State state;
        switch (commands[0]) {
            case "/create":
                state = new StateCreateTask(getUserId(), getBot());
                break;
            case "/tasks":
                state = new StateGetTasks(getUserId(), getBot());
                break;
            case "/task":
                if (commands.length == TASK_LENGTH) {
                    state = new StateGetTask(getUserId(), getBot());
                } else if (commands.length == TASK_KEY_LENGTH) {
                    message = String.join("-", commands[1], commands[2]);
                    state = new StateGetTaskWithKey(getUserId(), getBot());
                } else {
                    state = new StateWrongAction(getUserId(), getBot());
                }
                break;
            case "/comments":
                if (commands.length == TASK_LENGTH) {
                    state = new StateGetComments(getUserId(), getBot());
                } else if (commands.length == TASK_KEY_LENGTH) {
                    message = String.join("-", commands[1], commands[2]);
                    state = new StateGetCommentsWithKey(getUserId(), getBot());
                } else {
                    state = new StateWrongAction(getUserId(), getBot());
                }
                break;
            default:
                state = new StateUnknown(getUserId(), getBot());
        }
        state.action(message);
    }
}

package ru.hse.cs.java2020.task03;

public class StateGetCompanyId extends StateImpl {
    StateGetCompanyId(Long userId, Bot bot) {
        super(userId, bot);
    }

    @Override
    public void action(String message) {
        getDatabase().setCompanyId(getUserId(), message);
        getDatabase().updateUserState(getUserId(), "main");
        getDatabase().authorizeUser(getUserId());
        getBot().sendMessage(getUser(), Constants.WELCOME);
    }
}

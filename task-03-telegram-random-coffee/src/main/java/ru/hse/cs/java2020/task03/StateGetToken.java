package ru.hse.cs.java2020.task03;

public class StateGetToken extends StateImpl {
    StateGetToken(Long userId, Bot bot) {
        super(userId, bot);
    }

    @Override
    public void action(String message) {
        getDatabase().setToken(getUserId(), message);
        getDatabase().updateUserState(getUserId(), "waitCompany");
        getBot().sendMessage(getUser(), Constants.COMPANY);
    }
}

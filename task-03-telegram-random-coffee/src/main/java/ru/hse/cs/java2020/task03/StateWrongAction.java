package ru.hse.cs.java2020.task03;

public class StateWrongAction extends StateImpl {
    StateWrongAction(Long userId, Bot bot) {
        super(userId, bot);
    }

    @Override
    public void action(String message) {
        getBot().sendMessage(getUser(), Constants.SMTH_WRONG);
    }
}

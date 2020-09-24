package ru.hse.cs.java2020.task03;

public class StateGetPageNumber extends StateImpl {
    private static final int MIN_NUM = 1;
    private static final int MAX_NUM = 25;

    StateGetPageNumber(Long userId, Bot bot) {
        super(userId, bot);
    }

    @Override
    public void action(String message) {
        int num;
        try {
            num = Integer.parseInt(message);
            if (num > MAX_NUM || num < MIN_NUM) {
                throw new Exception("Invalid number.");
            }
        } catch (Exception e) {
            getBot().sendMessage(getUser(), Constants.WRONG_COUNT);
            return;
        }
        getDatabase().updateCount(getUserId(), num);
        new StateGetTasksByPage(getUserId(), getBot()).action("0");
    }
}

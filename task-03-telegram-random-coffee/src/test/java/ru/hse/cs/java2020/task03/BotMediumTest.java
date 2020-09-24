package ru.hse.cs.java2020.task03;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.DefaultBotOptions;

import java.util.Objects;

import static org.junit.Assert.*;

@PowerMockIgnore({"javax.net.ssl.*"})
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Database.class, TrackerClient.class,
    StateCreateTaskWithData.class, StateGetTasksByPage.class,
    StateGetTaskWithKey.class, StateGetCommentsByPage.class })
public class BotMediumTest {
    String firstMessage = "";
    String secondMessage = "";
    String id;

    private class TestBot extends Bot {
        TestBot(DefaultBotOptions options) {
            super(options);
        }

        @Override
        public synchronized void sendMessage(String chatId, String text) {
            id = chatId;
            if (firstMessage.isEmpty()) {
                firstMessage = text;
            } else {
                secondMessage = text;
            }
        }
    }

    private Bot init(Database database) {
        Bot bot = new TestBot(null);
        bot.init(database);
        return bot;
    }

    private void testSetAssigneeFish(String message) {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getChatId()).thenReturn(1L);
        Mockito.when(msg.getText()).thenReturn(message);

        Update update = Mockito.mock(Update.class);
        Mockito.when(update.getMessage()).thenReturn(msg);

        String name = "task name";
        String queue = "QUEUE";
        String description = "some task description";
        String token = "Some Token";
        String company = "1234567";

        Database database = Mockito.mock(Database.class);
        Mockito.when(database.isAuthorized(1L)).thenReturn(true);
        Mockito.when(database.getUserState(1L)).thenReturn("waitAssignee");
        Mockito.when(database.getSummary(1L)).thenReturn(name);
        Mockito.when(database.getQueue(1L)).thenReturn(queue);
        Mockito.when(database.getDescription(1L)).thenReturn(description);
        Mockito.when(database.getToken(1L)).thenReturn(token);
        Mockito.when(database.getCompanyId(1L)).thenReturn(company);

        TrackerClient tracker = PowerMockito.mock(TrackerClient.class);
        PowerMockito.when(tracker.createTask(name, queue, description, message.equals("да"))).thenReturn("1");

        try {
            PowerMockito.whenNew(TrackerClient.class)
                .withAnyArguments()
                .thenReturn(tracker);
        } catch (Exception e) {
            System.out.printf("Can't create tracker client: %s\n", e.toString());
        }

        Bot bot = init(database);
        bot.onUpdateReceived(update);

        assertEquals(Constants.created(name, "1"), firstMessage);
        assertEquals("1", id);

        Mockito.verify(database, Mockito.times(1)).updateUserState(1L, "main");

        firstMessage = "";
    }

    private void testSetPageFish(String tasks) {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getChatId()).thenReturn(1L);
        Mockito.when(msg.getText()).thenReturn("2");

        Update update = Mockito.mock(Update.class);
        Mockito.when(update.getMessage()).thenReturn(msg);

        Database database = Mockito.mock(Database.class);
        Mockito.when(database.isAuthorized(1L)).thenReturn(true);
        Mockito.when(database.getUserState(1L)).thenReturn("waitPage");
        Mockito.when(database.getCount(1L)).thenReturn(2);

        TrackerClient tracker = PowerMockito.mock(TrackerClient.class);
        PowerMockito.when(tracker.getTasks(0, 2)).thenReturn(tasks);

        try {
            PowerMockito.whenNew(TrackerClient.class)
                    .withAnyArguments()
                    .thenReturn(tracker);
        } catch (Exception e) {
            System.out.printf("Can't create tracker client: %s\n", e.toString());
        }

        Bot bot = init(database);
        bot.onUpdateReceived(update);

        assertEquals( Objects.requireNonNullElse(tasks, Constants.NOT_FOUND), firstMessage);
        assertEquals("1", id);

        Mockito.verify(database, Mockito.times(1)).updateUserState(1L, "waitNext");
        Mockito.verify(database, Mockito.times(1)).updateCount(1L, 2);
        Mockito.verify(database, Mockito.times(1)).updatePage(1L, 2);

        firstMessage = "";
    }

    private void testSetTaskNameFish(String task) {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getChatId()).thenReturn(1L);
        Mockito.when(msg.getText()).thenReturn("QUEUE-1234");

        Update update = Mockito.mock(Update.class);
        Mockito.when(update.getMessage()).thenReturn(msg);

        Database database = Mockito.mock(Database.class);
        Mockito.when(database.isAuthorized(1L)).thenReturn(true);
        Mockito.when(database.getUserState(1L)).thenReturn("waitTask");

        TrackerClient tracker = PowerMockito.mock(TrackerClient.class);
        PowerMockito.when(tracker.getTask("QUEUE-1234")).thenReturn(task);

        try {
            PowerMockito.whenNew(TrackerClient.class)
                .withAnyArguments()
                .thenReturn(tracker);
        } catch (Exception e) {
            System.out.printf("Can't create tracker client: %s\n", e.toString());
        }

        Bot bot = init(database);
        bot.onUpdateReceived(update);

        assertEquals(Objects.requireNonNullElse(task, Constants.NOT_FOUND_ONE), firstMessage);
        assertEquals("1", id);

        Mockito.verify(database, Mockito.times(1)).updateUserState(1L, "main");

        firstMessage = "";
    }

    private void testSetPageCommentFish(String comments) {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getChatId()).thenReturn(1L);
        Mockito.when(msg.getText()).thenReturn("2");

        Update update = Mockito.mock(Update.class);
        Mockito.when(update.getMessage()).thenReturn(msg);

        Database database = Mockito.mock(Database.class);
        Mockito.when(database.isAuthorized(1L)).thenReturn(true);
        Mockito.when(database.getUserState(1L)).thenReturn("waitPageComment");
        Mockito.when(database.getToken(1L)).thenReturn("token");
        Mockito.when(database.getTask(1L)).thenReturn("QUEUE-1234");
        Mockito.when(database.getCompanyId(1L)).thenReturn("1234567");
        Mockito.when(database.getCount(1L)).thenReturn(2);

        TrackerClient tracker = PowerMockito.mock(TrackerClient.class);
        PowerMockito.when(tracker.getComments("QUEUE-1234", 0, 2)).thenReturn(comments);

        try {
            PowerMockito.whenNew(TrackerClient.class)
                .withAnyArguments()
                .thenReturn(tracker);
        } catch (Exception e) {
            System.out.printf("Can't create tracker client: %s\n", e.toString());
        }

        Bot bot = init(database);
        bot.onUpdateReceived(update);

        assertEquals(Objects.requireNonNullElse(comments, Constants.NOT_FOUND_COM), firstMessage);
        assertEquals("1", id);

        Mockito.verify(database, Mockito.times(1)).updateUserState(1L, "waitNextComment");
        Mockito.verify(database, Mockito.times(1)).updateCount(1L, 2);
        Mockito.verify(database, Mockito.times(1)).updatePage(1L, 2);

        firstMessage = "";
    }

    @Test
    public void testSetAssignee() {
        testSetAssigneeFish("да");
        testSetAssigneeFish("нет");
    }

    @Test
    public void testCreateTaskNull() {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getChatId()).thenReturn(1L);
        Mockito.when(msg.getText()).thenReturn("да");

        Update update = Mockito.mock(Update.class);
        Mockito.when(update.getMessage()).thenReturn(msg);

        String name = "task name";
        String queue = "QUEUE";
        String description = "some task description";
        String token = "Some Token";
        String company = "1234567";

        Database database = Mockito.mock(Database.class);
        Mockito.when(database.isAuthorized(1L)).thenReturn(true);
        Mockito.when(database.getUserState(1L)).thenReturn("waitAssignee");
        Mockito.when(database.getSummary(1L)).thenReturn(name);
        Mockito.when(database.getQueue(1L)).thenReturn(queue);
        Mockito.when(database.getDescription(1L)).thenReturn(description);
        Mockito.when(database.getToken(1L)).thenReturn(token);
        Mockito.when(database.getCompanyId(1L)).thenReturn(company);

        TrackerClient tracker = PowerMockito.mock(TrackerClient.class);
        PowerMockito.when(tracker.createTask(name, queue, description, true)).thenReturn(null);

        try {
            PowerMockito.whenNew(TrackerClient.class)
                    .withAnyArguments()
                    .thenReturn(tracker);
        } catch (Exception e) {
            System.out.printf("Can't create tracker client: %s\n", e.toString());
        }

        Bot bot = init(database);
        bot.onUpdateReceived(update);

        assertEquals(Constants.NOT_CREATED, firstMessage);
        assertEquals("1", id);

        Mockito.verify(database, Mockito.times(1)).updateUserState(1L, "main");

        firstMessage = "";
    }

    @Test
    public void testSetPage() {
        testSetPageFish("Задача 1\nЗадача 2");
        testSetPageFish(null);
    }

    @Test
    public void testNextTasks() {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getChatId()).thenReturn(1L);
        Mockito.when(msg.getText()).thenReturn("/next");

        Update update = Mockito.mock(Update.class);
        Mockito.when(update.getMessage()).thenReturn(msg);

        Database database = Mockito.mock(Database.class);
        Mockito.when(database.isAuthorized(1L)).thenReturn(true);
        Mockito.when(database.getUserState(1L)).thenReturn("waitNext");
        Mockito.when(database.getCount(1L)).thenReturn(2);
        Mockito.when(database.getPage(1L)).thenReturn(2);

        TrackerClient tracker = PowerMockito.mock(TrackerClient.class);
        PowerMockito.when(tracker.getTasks(2, 4)).thenReturn("Задача 3\nЗадача 4");

        try {
            PowerMockito.whenNew(TrackerClient.class)
                    .withAnyArguments()
                    .thenReturn(tracker);
        } catch (Exception e) {
            System.out.printf("Can't create tracker client: %s\n", e.toString());
        }

        Bot bot = init(database);
        bot.onUpdateReceived(update);

        assertEquals("Задача 3\nЗадача 4", firstMessage);
        assertEquals("1", id);

        Mockito.verify(database, Mockito.times(1)).updateUserState(1L, "waitNext");
        Mockito.verify(database, Mockito.times(1)).updatePage(1L, 4);

        firstMessage = "";
    }

    @Test
    public void testSetTaskName() {
        testSetTaskNameFish("Какая-то задача");
        testSetTaskNameFish(null);
    }

    @Test
    public void testGetTaskByName() {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getChatId()).thenReturn(1L);
        Mockito.when(msg.getText()).thenReturn("/task_QUEUE_1234");

        Update update = Mockito.mock(Update.class);
        Mockito.when(update.getMessage()).thenReturn(msg);

        Database database = Mockito.mock(Database.class);
        Mockito.when(database.isAuthorized(1L)).thenReturn(true);
        Mockito.when(database.getUserState(1L)).thenReturn("main");

        TrackerClient tracker = PowerMockito.mock(TrackerClient.class);
        PowerMockito.when(tracker.getTask("QUEUE-1234")).thenReturn("Какая-то задача");

        try {
            PowerMockito.whenNew(TrackerClient.class)
                .withAnyArguments()
                .thenReturn(tracker);
        } catch (Exception e) {
            System.out.printf("Can't create tracker client: %s\n", e.toString());
        }

        Bot bot = init(database);
        bot.onUpdateReceived(update);

        assertEquals("Какая-то задача", firstMessage);
        assertEquals("1", id);

        Mockito.verify(database, Mockito.times(1)).updateUserState(1L, "main");

        firstMessage = "";
    }

    @Test
    public void testSetPageComment() {
        testSetPageCommentFish("Какие-то комментарии\nЕще какие-то комментарии");
        testSetPageCommentFish(null);
    }

    @Test
    public void testNextComment() {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getChatId()).thenReturn(1L);
        Mockito.when(msg.getText()).thenReturn("/next");

        Update update = Mockito.mock(Update.class);
        Mockito.when(update.getMessage()).thenReturn(msg);

        Database database = Mockito.mock(Database.class);
        Mockito.when(database.isAuthorized(1L)).thenReturn(true);
        Mockito.when(database.getUserState(1L)).thenReturn("waitNextComment");
        Mockito.when(database.getToken(1L)).thenReturn("token");
        Mockito.when(database.getTask(1L)).thenReturn("QUEUE-1234");
        Mockito.when(database.getCompanyId(1L)).thenReturn("1234567");
        Mockito.when(database.getCount(1L)).thenReturn(2);

        TrackerClient tracker = PowerMockito.mock(TrackerClient.class);
        PowerMockito.when(tracker.getComments("QUEUE-1234", 0, 2)).thenReturn("Какие-то комментарии");

        try {
            PowerMockito.whenNew(TrackerClient.class)
                .withAnyArguments()
                .thenReturn(tracker);
        } catch (Exception e) {
            System.out.printf("Can't create tracker client: %s\n", e.toString());
        }

        Bot bot = init(database);
        bot.onUpdateReceived(update);

        assertEquals("Какие-то комментарии", firstMessage);
        assertEquals("1", id);

        Mockito.verify(database, Mockito.times(1)).updateUserState(1L, "waitNextComment");
        Mockito.verify(database, Mockito.times(1)).updatePage(1L, 2);

        firstMessage = "";
    }
}

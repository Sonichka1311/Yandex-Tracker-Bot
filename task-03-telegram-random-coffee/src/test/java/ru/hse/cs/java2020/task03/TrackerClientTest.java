package ru.hse.cs.java2020.task03;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Connector.class, TrackerClient.class })
public class TrackerClientTest {
    private void testCreateFish(boolean assigner, JSONObject object, JSONObject me, boolean ok) {
        String name = "task name";
        String queue = "QUEUE";
        String description = "some task description";
        String token = "Some Token";
        String company = "1234567";

        JSONObject task = new JSONObject();
        task.put("summary", name);
        task.put("queue", queue);
        task.put("description", description);
        if (assigner) {
            task.put("assignee", 1);
        }

        Connector connector = PowerMockito.mock(Connector.class);
        PowerMockito.when(connector.create(task.toString())).thenReturn(object);
        PowerMockito.when(connector.me()).thenReturn(me);

        try {
            PowerMockito.whenNew(Connector.class)
                .withAnyArguments()
                .thenReturn(connector);
        } catch (Exception e) {
            System.out.printf("Can't create connector: %s\n", e.toString());
        }

        TrackerClient tracker = new TrackerClient(token, company);
        String res = tracker.createTask(name, queue, description, assigner);

        assertEquals(ok ? "1234" : null , res);
    }

    private void testGetTasksFish(JSONArray objects, JSONObject me, String ans, int start) {
        String token = "Token";
        String company = "1234567";

        JSONObject req = new JSONObject();
        req.put("filter", new JSONObject().put("assignee", 1));

        Connector connector = PowerMockito.mock(Connector.class);
        PowerMockito.when(connector.getAll(req.toString())).thenReturn(objects);
        PowerMockito.when(connector.me()).thenReturn(me);

        try {
            PowerMockito.whenNew(Connector.class)
                .withAnyArguments()
                .thenReturn(connector);
        } catch (Exception e) {
            System.out.printf("Can't create connector: %s\n", e.toString());
        }

        TrackerClient tracker = new TrackerClient(token, company);
        String res = tracker.getTasks(start, start + 2);

        assertEquals(ans, res);
    }

    private void testGetTaskFish(JSONArray objects, JSONObject person, String ans, JSONObject assignee, JSONObject... followers) {
        String token = "Token";
        String company = "1234567";

        Connector connector = PowerMockito.mock(Connector.class);
        PowerMockito.when(connector.getAll(new JSONObject().put("filter", new JSONObject().put("key", "QUEUE-1234")).toString())).thenReturn(objects);
        PowerMockito.when(connector.getPerson("person-1-LINK")).thenReturn(person);
        PowerMockito.when(connector.getPerson("person-2-LINK")).thenReturn(assignee);
        for (int i = 0; i < followers.length; ++i) {
            PowerMockito.when(connector.getPerson("person-" + (i + 3) + "-LINK")).thenReturn(followers[i]);
        }

        try {
            PowerMockito.whenNew(Connector.class)
                .withAnyArguments()
                .thenReturn(connector);
        } catch (Exception e) {
            System.out.printf("Can't create connector: %s\n", e.toString());
        }

        TrackerClient tracker = new TrackerClient(token, company);
        String res = tracker.getTask("QUEUE-1234");

        assertEquals(ans, res);
    }

    private void testGetCommentsFish(JSONArray objects, JSONObject person, String ans, int start) {
        String token = "Token";
        String company = "1234567";

        Connector connector = PowerMockito.mock(Connector.class);
        PowerMockito.when(connector.getComments("QUEUE-1234")).thenReturn(objects);
        PowerMockito.when(connector.getPerson("person-1-LINK")).thenReturn(person);

        try {
            PowerMockito.whenNew(Connector.class)
                .withAnyArguments()
                .thenReturn(connector);
        } catch (Exception e) {
            System.out.printf("Can't create connector: %s\n", e.toString());
        }

        TrackerClient tracker = new TrackerClient(token, company);
        String res = tracker.getComments("QUEUE-1234", start, start + 2);

        assertEquals(ans, res);
    }

    @Test
    public void testCreate() {
        testCreateFish(false, new JSONObject().put("key", "1234"), null, true);
        testCreateFish(true, new JSONObject().put("key", "1234"), new JSONObject().put("uid", 1), true);
        testCreateFish(false, new JSONObject().put("something", "wrong"), null, false);
        testCreateFish(true, new JSONObject().put("key", "1234"), new JSONObject().put("something", "wrong"), false);
        testCreateFish(true, new JSONObject().put("key", "1234"), new JSONObject().put("uid", "abc"), false);
    }

    @Test
    public void testGetTasks() {
        testGetTasksFish(null, new JSONObject(), null, 0);
        testGetTasksFish(null, new JSONObject().put("key", "abc"), null, 0);
        testGetTasksFish(null, new JSONObject().put("uid", "abc"), null, 0);
        testGetTasksFish(new JSONArray(), new JSONObject().put("uid", 1),
        "Задач не найдено. \nЧтобы создать задачу, нажми /create ", 0);
        testGetTasksFish(
            new JSONArray()
                .put(
                    0,
                    new JSONObject()
                        .put("key", "QUEUE-1")
                        .put("something", "else")
                    ),
            new JSONObject().put("uid", 1),
            null,
            0
        );
        testGetTasksFish(
            new JSONArray()
                .put(
                    0,
                    new JSONObject()
                        .put("key", "QUEUE-1")
                        .put("summary", "Name")
                ),
            new JSONObject().put("uid", 1),
            "1. *QUEUE-1*: [Name](https://tracker.yandex.ru/QUEUE-1) /task\\_QUEUE\\_1\n"
            + "\nНажми /end для завершения просмотра\n",
            0
        );
        testGetTasksFish(
            new JSONArray()
                .put(
                    0,
                    new JSONObject()
                        .put("key", "QUEUE-1")
                        .put("summary", "Name")
                )
                .put(
                    1,
                    new JSONObject()
                        .put("key", "QUEUE-2")
                        .put("summary", "Also Name")
                )
                .put(
                    2,
                    new JSONObject()
                        .put("key", "QUEUE-3")
                        .put("summary", "N a m e")
                ),
            new JSONObject().put("uid", 1),
            "1. *QUEUE-1*: [Name](https://tracker.yandex.ru/QUEUE-1) /task\\_QUEUE\\_1\n"
            +"2. *QUEUE-2*: [Also Name](https://tracker.yandex.ru/QUEUE-2) /task\\_QUEUE\\_2\n"
            +"\nНажми /next для перехода на следующую страницу"
            + "\nНажми /end для завершения просмотра\n",
            0
        );
        testGetTasksFish(
            new JSONArray()
                .put(
                    0,
                    new JSONObject()
                        .put("key", "QUEUE-1")
                        .put("summary", "Name")
                )
                .put(
                    1,
                    new JSONObject()
                        .put("key", "QUEUE-2")
                        .put("summary", "Also Name")
                )
                .put(
                    2,
                    new JSONObject()
                        .put("key", "QUEUE-3")
                        .put("summary", "N a m e")
                ),
            new JSONObject().put("uid", 1),
                "3. *QUEUE-3*: [N a m e](https://tracker.yandex.ru/QUEUE-3) /task\\_QUEUE\\_3\n"
                + "\nНажми /end для завершения просмотра\n",
            2
        );
    }

    @Test
    public void testGetTask() {
        testGetTaskFish(new JSONArray(), null, null, null);
        testGetTaskFish(
            new JSONArray()
                .put(
                    0,
                    new JSONObject()
                        .put("key", "QUEUE-1")
                        .put("something", "else")
                ),
            new JSONObject().put("uid", 1),
            null,
            null
        );
        testGetTaskFish(
            new JSONArray()
                .put(
                    0,
                    new JSONObject()
                        .put("key", "QUEUE-1")
                        .put("summary", "Name")
                        .put("description", "Description")
                        .put("createdBy", new JSONObject().put("self", "person-1-LINK"))
                ),
            new JSONObject()
                .put("display", "John Doe")
                .put("login", "john-doe"),
            "*Задача*: [Name](https://tracker.yandex.ru/QUEUE-1)\n"
            + "*Описание*: Description\n"
            + "*Автор*: [John Doe](https://staff.yandex.ru/john-doe?org_id=1234567)\n"
            + "*Исполнитель*: --\n"
            + "*Наблюдатели*: --"
            + "\n*Комментарии*: /comments\\_QUEUE\\_1",
            null
        );
        testGetTaskFish(
            new JSONArray()
                .put(
                    0,
                    new JSONObject()
                        .put("key", "QUEUE-1")
                        .put("summary", "Name")
                        .put("description", "Description")
                        .put("createdBy", new JSONObject().put("self", "person-1-LINK"))
                        .put("assignee", new JSONObject().put("self", "person-2-LINK"))
                ),
            new JSONObject()
                .put("display", "John Doe")
                .put("login", "john-doe"),
            "*Задача*: [Name](https://tracker.yandex.ru/QUEUE-1)\n"
            + "*Описание*: Description\n"
            + "*Автор*: [John Doe](https://staff.yandex.ru/john-doe?org_id=1234567)\n"
            + "*Исполнитель*: [John Doe](https://staff.yandex.ru/john-doe?org_id=1234567)\n"
            + "*Наблюдатели*: --"
            + "\n*Комментарии*: /comments\\_QUEUE\\_1",
            new JSONObject()
                .put("display", "John Doe")
                .put("login", "john-doe")
        );
        testGetTaskFish(
            new JSONArray()
                .put(
                    0,
                    new JSONObject()
                        .put("key", "QUEUE-1")
                        .put("summary", "Name")
                        .put("description", "Description")
                        .put("createdBy", new JSONObject().put("self", "person-1-LINK"))
                        .put(
                            "followers",
                            new JSONArray()
                                .put(0, new JSONObject().put("self", "person-3-LINK"))
                                .put(1, new JSONObject().put("self", "person-4-LINK"))
                        )
                ),
            new JSONObject()
                .put("display", "John Doe")
                .put("login", "john-doe"),
            "*Задача*: [Name](https://tracker.yandex.ru/QUEUE-1)\n"
            + "*Описание*: Description\n"
            + "*Автор*: [John Doe](https://staff.yandex.ru/john-doe?org_id=1234567)\n"
            + "*Исполнитель*: --\n"
            + "*Наблюдатели*: [John Doe](https://staff.yandex.ru/john-doe?org_id=1234567), "
            +"[Bill Gates](https://staff.yandex.ru/bill-gates?org_id=1234567)"
            + "\n*Комментарии*: /comments\\_QUEUE\\_1",
            null,
            new JSONObject()
                .put("display", "John Doe")
                .put("login", "john-doe"),
            new JSONObject()
                .put("display", "Bill Gates")
                .put("login", "bill-gates")
        );
    }

    @Test
    public void testGetComments() {
        testGetCommentsFish(
                new JSONArray(),
                new JSONObject(),
                "К задаче QUEUE-1234 нет комментариев\n",
                0
        );
        testGetCommentsFish(
            new JSONArray()
                .put(
                    0,
                    new JSONObject()
                        .put("createdBy", new JSONObject().put("self", "person-1-LINK"))
                        .put("text", "test comment")
                )
                .put(
                    1,
                    new JSONObject()
                        .put("createdBy", new JSONObject().put("self", "person-1-LINK"))
                        .put("text", "some text")
                ),
            new JSONObject()
                .put("display", "John Doe")
                .put("login", "john-doe"),
            "[John Doe](https://staff.yandex.ru/john-doe?org_id=1234567):\n"
            + "test comment\n\n"
            +"[John Doe](https://staff.yandex.ru/john-doe?org_id=1234567):\n"
            + "some text\n\n"
            + "Нажми /end для завершения просмотра\n",
            0
        );
        testGetCommentsFish(
            new JSONArray()
                .put(
                    0,
                    new JSONObject()
                        .put("createdBy", new JSONObject().put("self", "person-1-LINK"))
                        .put("text", "test comment")
                )
                .put(
                    1,
                    new JSONObject()
                        .put("createdBy", new JSONObject().put("self", "person-1-LINK"))
                        .put("text", "some text")
                )
                .put(
                    2,
                    new JSONObject()
                        .put("createdBy", new JSONObject().put("self", "person-1-LINK"))
                        .put("text", "some next text")
                ),
            new JSONObject()
                .put("display", "John Doe")
                .put("login", "john-doe"),
            "[John Doe](https://staff.yandex.ru/john-doe?org_id=1234567):\n"
            + "test comment\n\n"
            + "[John Doe](https://staff.yandex.ru/john-doe?org_id=1234567):\n"
            + "some text\n\n"
            + "Нажми /next для перехода на следующую страницу\n"
            + "Нажми /end для завершения просмотра\n",
            0
        );
        testGetCommentsFish(
            new JSONArray()
                .put(
                    0,
                    new JSONObject()
                        .put("createdBy", new JSONObject().put("self", "person-1-LINK"))
                        .put("text", "test comment")
                )
                .put(
                    1,
                    new JSONObject()
                        .put("createdBy", new JSONObject().put("self", "person-1-LINK"))
                        .put("text", "some text")
                )
                .put(
                    2,
                    new JSONObject()
                        .put("createdBy", new JSONObject().put("self", "person-1-LINK"))
                        .put("text", "some next text")
                ),
            new JSONObject()
                .put("display", "John Doe")
                .put("login", "john-doe"),
            "[John Doe](https://staff.yandex.ru/john-doe?org_id=1234567):\n"
            + "some next text\n\n"
            + "Нажми /end для завершения просмотра\n",
            2
        );
    }
}

package taskmanager;

import org.junit.jupiter.api.BeforeEach;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager();
        historyManager = Managers.getDefaultHistory();
    }
}

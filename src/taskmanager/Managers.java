package taskmanager;

import taskhistory.HistoryManager;
import taskhistory.InMemoryHistoryManager;

public final class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}

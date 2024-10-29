package taskhistory;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    static class Node<Task> {
        public Task data;
        public Node<Task> next;
        public Node<Task> prev;

        public Node(Task data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }
    }

    private Node<Task> first;
    private Node<Task> last;

    private final HashMap<Integer, Node<Task>> historyHashMap;

    public InMemoryHistoryManager() {
        this.first = null;
        this.last = null;
        this.historyHashMap = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        remove(task.getId());

        Node<Task> newNode = new Node<>(task);
        if (this.first == null) {
            this.first = newNode;
            this.last = newNode;
        } else {
            newNode.prev = this.last;
            this.last.next = newNode;
            this.last = newNode;
        }
        historyHashMap.put(task.getId(), newNode);
    }

    @Override
    public void remove(int id) {
        Node<Task> removeNode = historyHashMap.get(id);
        if (removeNode != null) {
            if (this.first == removeNode) {
                Node<Task> next = this.first.next;
                if (next != null) {
                    next.prev = null;
                    this.first = next;
                } else {
                    this.first = null;
                }
            }
            if (this.last == removeNode) {
                Node<Task> prev = this.last.prev;
                if (prev != null) {
                    prev.next = null;
                    this.last = prev;
                } else {
                    this.last = null;
                }
            } else {
                Node<Task> next = removeNode.next;
                Node<Task> prev = removeNode.prev;
                if (next != null && prev != null) {
                    next.prev = prev;
                    prev.next = next;
                }
            }
            historyHashMap.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        ArrayList<Task> history = new ArrayList<>();
        if (!historyHashMap.isEmpty()) {
            Node<Task> iteratedTaskNode = this.first;
            do {
                history.add(iteratedTaskNode.data);
                iteratedTaskNode = iteratedTaskNode.next;
            } while (iteratedTaskNode != null);
        }
        return history;
    }
}

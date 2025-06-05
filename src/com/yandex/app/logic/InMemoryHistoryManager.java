package com.yandex.app.logic;

import com.yandex.app.entities.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private CustomLinkedList historyList = new CustomLinkedList();

    @Override
    public List<Task> getHistory() {
        return historyList.getTasks();
    }

    @Override
    public void add(Task task) {
        historyList.linkLast(task);
    }

    @Override
    public void remove(int id) {
        historyList.removeNode(id);
    }

    public static class CustomLinkedList {

        private Node<Task> head;
        private Node<Task> tail;
        private final Map<Integer, CustomLinkedList.Node<Task>> nodeMap = new HashMap<>();

        public static class Node<T> {
            public T task;
            public Node<T> next;
            public Node<T> prev;

            public Node(Node<T> prev, T task, Node<T> next) {
                this.task = task;
                this.prev = prev;
                this.next = next;
            }
        }

        public void linkLast(Task task) {
            if (nodeMap.containsKey(task.getTaskId())) {
                removeNode(nodeMap.get(task.getTaskId()));
            }

            final Node<Task> oldTail = tail;
            final Node<Task> newnode = new Node<>(oldTail, task, null);

            tail = newnode;

            if (oldTail == null) {
                head = newnode;
            } else {
                oldTail.next = newnode;
            }
            nodeMap.put(task.getTaskId(), newnode);
        }

        public void removeNode(Node<Task> node) {
            final Node<Task> prev = node.prev;
            final Node<Task> next = node.next;
            if (prev == null) {
                head = next;
            } else {
                prev.next = next;
                prev.prev = null;
            }
            if (next == null) {
                tail = prev;
            } else {
                next.prev = prev;
                node.next = null;
            }
            node.task = null;
        }

        public void removeNode(int id) {
            if (nodeMap.containsKey(id)) {
                removeNode(nodeMap.get(id));
            }
        }

        public List<Task> getTasks() {
            List<Task> tasks = new ArrayList<>();
            for (Node<Task> node = head; node != null; node = node.next) {
                tasks.add(node.task);
            }
            return tasks;
        }
    }
}

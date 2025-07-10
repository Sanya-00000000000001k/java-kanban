package com.yandex.app.logic;

public class PrioritizedHandler extends BaseGetHandler {

    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected boolean isSupportedPath(String path) {
        return path.equals("/prioritized");
    }

    @Override
    protected Object getData() {
        return taskManager.getPrioritizedTasks();
    }
}

package com.yandex.app.logic;

public class HistoryHandler extends BaseGetHandler {

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected boolean isSupportedPath(String path) {
        return path.equals("/history");
    }

    @Override
    protected Object getData() {
        return taskManager.getHistory();
    }
}

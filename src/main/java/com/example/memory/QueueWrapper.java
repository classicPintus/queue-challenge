package com.example.memory;

import java.util.Objects;

class QueueWrapper {
    private String name;
    private int visibilityTimeout;

    public QueueWrapper(String name, int visibilityTimeout) {
        this.name = name;
        this.visibilityTimeout = visibilityTimeout;
    }

    public String getName() {
        return name;
    }

    public int getVisibilityTimeout() {
        return visibilityTimeout;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueueWrapper that = (QueueWrapper) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

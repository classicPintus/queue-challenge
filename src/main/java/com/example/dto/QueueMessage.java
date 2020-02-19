package com.example.dto;

public class QueueMessage {

    private String body;
    private String uniqueIdentifier;

    public QueueMessage(String body, String uniqueIdentifier){
        this.body = body;
        this.uniqueIdentifier = uniqueIdentifier;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    public void setUniqueIdentifier(String uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }
}

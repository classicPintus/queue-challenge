package com.example.message;

public class MessageReceived {

    private String body;
    private String uniqueIdentifier;

    public MessageReceived(String body, String uniqueIdentifier){
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

package com.example.lumos.model;

public class Chat {

    private String sender;
    private String receiver;
    private String message;
    private boolean Isseen;

    public Chat() {}

    public Chat(String sender, String receiver, String message, boolean Isseen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.Isseen = Isseen;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getIsseen() {
        return Isseen;
    }

    public void setIsseen(boolean Isseen) {
        this.Isseen = Isseen;
    }
}

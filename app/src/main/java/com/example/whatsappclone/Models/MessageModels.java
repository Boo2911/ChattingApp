package com.example.whatsappclone.Models;

public class MessageModels {

    String uId, message, messageId, msgTime;
    Long timeStamp;

    public MessageModels(String uId, String message, Long timeStamp) {
        this.uId = uId;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public MessageModels(String uId, String message, String messageId, Long timeStamp, String msgTime) {
        this.uId = uId;
        this.message = message;
        this.messageId = messageId;
        this.timeStamp = timeStamp;
        this.msgTime = msgTime;
    }

    public MessageModels(String uId, String message) {
        this.uId = uId;
        this.message = message;
    }

    public MessageModels() {
    }

    public String getuId() {
        return uId;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMessage() {
        return message;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMsgTime() {
        return msgTime;
    }
}

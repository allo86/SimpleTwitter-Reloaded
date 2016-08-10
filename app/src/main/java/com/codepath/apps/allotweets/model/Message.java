package com.codepath.apps.allotweets.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ALLO on 9/8/16.
 */
@Parcel
public class Message {

    @SerializedName("id")
    @Expose
    Long messageId;

    @SerializedName("created_at")
    @Expose
    Date createdAt;

    @SerializedName("recipient")
    @Expose
    TwitterUser recipient;

    @SerializedName("sender")
    @Expose
    TwitterUser sender;

    @SerializedName("text")
    @Expose
    String text;

    @SerializedName("entities")
    @Expose
    Entities entities;

    public Message() {

    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public TwitterUser getRecipient() {
        return recipient;
    }

    public void setRecipient(TwitterUser recipient) {
        this.recipient = recipient;
    }

    public TwitterUser getSender() {
        return sender;
    }

    public void setSender(TwitterUser sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Entities getEntities() {
        return entities;
    }

    public void setEntities(Entities entities) {
        this.entities = entities;
    }

    /* Helpers */

    public String getFormattedCreatedAtDate() {
        if (getCreatedAt() != null) {
            return DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(getCreatedAt());
        }
        return null;
    }

    @Override
    public boolean equals(Object object) {
        if (object != null && object instanceof Message) {
            Message message = (Message) object;
            return this.getMessageId().equals(message.getMessageId());
        }
        return false;
    }
}

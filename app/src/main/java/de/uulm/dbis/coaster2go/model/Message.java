package de.uulm.dbis.coaster2go.model;

/**
 * A test Object for writing messages to the firebase DB
 * Created by Luis on 29.04.2017.
 */
public class Message {
    public String name;
    public String text;

    public Message() {
    }

    public Message(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

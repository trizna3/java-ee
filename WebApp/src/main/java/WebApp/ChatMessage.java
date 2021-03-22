/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WebApp;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author adamt
 */
public class ChatMessage {
    private String username;
    private String text;
    private Map<String,Boolean> visibleTo;

    public ChatMessage(String username, String text) {
        this.username = username;
        this.text = text;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<String, Boolean> getVisibleTo() {
        if (visibleTo == null) {
            visibleTo = new HashMap<>();
        }
        return visibleTo;
    }

    public void setVisibleTo(Map<String, Boolean> visibleTo) {
        this.visibleTo = visibleTo;
    }
    
    public void hideFrom(String user) {
        visibleTo.put(user,Boolean.FALSE);
    }
    
    public boolean isVisibleTo(String user) {
        return !Boolean.FALSE.equals(getVisibleTo().get(user));
    }
}

package algonquin.cst2335.rose0230;

public class ChatMessage {

    String message;
    String timeSent;
    boolean isSentButton;


    void ChatRoom(String m, String t, boolean sent) {
        message = m;
        timeSent = t;
        isSentButton = sent;
    }
    public String getMessage() {
        return message;
    }

    public String getTimeSent() {
        return timeSent;
    }

    public boolean isSentButton() {
        return isSentButton;
    }
}

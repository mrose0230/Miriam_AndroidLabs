package algonquin.cst2335.rose0230;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ChatMessage {

    @PrimaryKey(autoGenerate = true) //the database will increment them for us
    @ColumnInfo(name="id")
    long id;


    @ColumnInfo(name="MessageColumn")
    String message;
    @ColumnInfo(name="TimeSentColumn")
    String timeSent;

    @ColumnInfo(name="SendReceiveColumn")
    boolean sentOrReceive;


    boolean isSent;


    public ChatMessage() { }
    public ChatMessage(String m, String tm, boolean sr){
        message= m;
        timeSent = tm;
        sentOrReceive = sr;
    }
    public String getMessage() {
        return message;
    }

    public String getTimeSent() {
        return timeSent;
    }

   // public boolean isSentButton() {
     //   return isSent;
    }



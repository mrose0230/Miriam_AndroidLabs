package algonquin.cst2335.rose0230;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ChatMessageDAO {

    @Insert //id                 //@Entity
    public long insertMessage(ChatMessage m); //for inserting

    @Query("Select * from ChatMessage;")
    public List<ChatMessage> getAllMessages();

    @Delete //number of rows deleted
    public int deleteThisMessage(ChatMessage m); //delete this message  by id
}
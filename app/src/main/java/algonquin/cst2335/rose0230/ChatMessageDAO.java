package algonquin.cst2335.rose0230;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ChatMessageDAO {

    @Insert //id                 //@Entity
    void insertMessage(ChatMessage m); //for inserting

    @Query("Select * from ChatMessage;")
    public List<ChatMessage> getAllMessages();

    @Delete //number of rows deleted
    void deleteThisMessage(ChatMessage m); //delete this message  by id
}
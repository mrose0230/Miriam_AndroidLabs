package algonquin.cst2335.rose0230;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class ChatRoomViewModel extends ViewModel {
    public MutableLiveData<ArrayList<ChatRoom.ChatMessage>> messages = new MutableLiveData<>();
}

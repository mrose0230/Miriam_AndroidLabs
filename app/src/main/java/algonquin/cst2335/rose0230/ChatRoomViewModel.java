package algonquin.cst2335.rose0230;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

/*public class ChatRoomViewModel extends ViewModel {
    public MutableLiveData<ArrayList<ChatMessage>> messages = new MutableLiveData<>();
}*/

public class ChatRoomViewModel extends ViewModel {

   // public ArrayList<ChatMessage> messages = new MutableLiveData< >();
    public MutableLiveData<ChatMessage> selectedMessage = new MutableLiveData< >();

    public ArrayList<ChatMessage> messages;
}
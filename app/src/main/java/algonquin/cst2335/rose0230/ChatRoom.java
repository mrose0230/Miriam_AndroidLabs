package algonquin.cst2335.rose0230;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import algonquin.cst2335.rose0230.databinding.ActivityChatRoomBinding;
import algonquin.cst2335.rose0230.databinding.RecMessageBinding;
import algonquin.cst2335.rose0230.databinding.SentMessageBinding;

public class ChatRoom extends AppCompatActivity {

    ArrayList<ChatMessage> messages;
    ActivityChatRoomBinding binding;
    RecyclerView.Adapter<MyRowHolder> myAdapter;

    ChatRoomViewModel chatModel ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        chatModel = new ViewModelProvider(this).get(ChatRoomViewModel.class);

        messages = chatModel.messages.getValue();
        if(messages == null)
        {
            chatModel.messages.postValue( messages = new ArrayList<>());
        }


        myAdapter = new RecyclerView.Adapter<MyRowHolder>() {
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                if (viewType == 0) {
                    SentMessageBinding rowBinding = SentMessageBinding.inflate(getLayoutInflater(), parent, false);
                    return new MyRowHolder(rowBinding.getRoot());
                } else {
                    RecMessageBinding messageBinding = RecMessageBinding.inflate(getLayoutInflater(), parent, false);
                    return new MyRowHolder(messageBinding.getRoot());
                }
            }

            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {
                ChatMessage forRow = messages.get(position);
                holder.messageText.setText(forRow.message);
                holder.timeText.setText(forRow.timeSent);
            }

            @Override
            public int getItemCount() {
                return messages.size();
            }

            @Override
            public int getItemViewType(int position) {
                ChatMessage message = messages.get(position);
                return message.isSent ? 0 : 1;
            }
        };

        binding.recycleView.setLayoutManager(new LinearLayoutManager(this));
        binding.recycleView.setAdapter(myAdapter);

        binding.sendButton.setOnClickListener(click -> {
            String textInput = binding.textInput.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");
            String currentDateandTime = sdf.format(new Date());
            ChatMessage chatMessage = new ChatMessage(textInput, currentDateandTime, true);

            messages.add(chatMessage);
            myAdapter.notifyItemInserted(messages.size() - 1);
            binding.textInput.setText("");
            myAdapter.notifyDataSetChanged(); // will redraw
        });

        // Assuming you have a receiveButton in your layout, add a click handler for it
        binding.recButton.setOnClickListener(click -> {
            String textInput = binding.textInput.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");
            String currentDateandTime = sdf.format(new Date());
            ChatMessage chatMessage = new ChatMessage(textInput, currentDateandTime, false);

            messages.add(chatMessage);
            myAdapter.notifyItemInserted(messages.size() - 1);
            binding.textInput.setText("");
            myAdapter.notifyDataSetChanged(); // will redraw
        });
    }

    class MyRowHolder extends RecyclerView.ViewHolder {
        public TextView messageText;
        public TextView timeText;

        public MyRowHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            timeText = itemView.findViewById(R.id.timeText);
        }
    }

    class ChatMessage {
        String message;
        String timeSent;
        boolean isSent;

        public ChatMessage(String message, String timeSent, boolean isSent) {
            this.message = message;
            this.timeSent = timeSent;
            this.isSent = isSent;
        }
    }
}

package algonquin.cst2335.rose0230;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Room;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.rose0230.databinding.ActivityChatRoomBinding;
import algonquin.cst2335.rose0230.databinding.RecMessageBinding;
import algonquin.cst2335.rose0230.databinding.SentMessageBinding;


public class ChatRoom extends AppCompatActivity {

    ArrayList<ChatMessage> messages;
    ActivityChatRoomBinding binding;
    RecyclerView.Adapter<MyRowHolder> myAdapter;

    ChatRoomViewModel chatModel;

    ChatMessageDAO mDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        chatModel = new ViewModelProvider(this).get(ChatRoomViewModel.class);
        messages = chatModel.messages.getValue();

        //observefunction
        chatModel.selectedMessage.observe(this, (newMessageValue) -> {

            if (selected != null) {
                MessageDetailsFragment newMessage = new MessageDetailsFragment(selected);

                FragmentManager fMgr = getSupportFragmentManager();
                FragmentTransaction transaction = fMgr.beginTransaction();
                transaction.addToBackStack("any string here");
                transaction.add(R.id.fragmentLocation, newMessage); //first is the FrameLayout id
                transaction.commit();//loads it
            }

            MessageDatabase db = Room.databaseBuilder(getApplicationContext(), MessageDatabase.class, "database-name").build();
            mDao = db.cmDAO();


            List<ChatMessage> fromDatabase = mDao.getAllMessages();//return a List
            messages.addAll(fromDatabase);//this adds all messages from the database

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
                algonquin.cst2335.rose0230.ChatMessage chatMessage = new algonquin.cst2335.rose0230.ChatMessage(textInput, currentDateandTime, true);
                Executor thread = Executors.newSingleThreadExecutor();
                thread.execute(() -> mDao.insertMessage(chatMessage));
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
                algonquin.cst2335.rose0230.ChatMessage chatMessage = new algonquin.cst2335.rose0230.ChatMessage(textInput, currentDateandTime, false);
                Executor thread = Executors.newSingleThreadExecutor();
                thread.execute(() -> mDao.insertMessage(chatMessage));
                messages.add(chatMessage);
                myAdapter.notifyItemInserted(messages.size() - 1);
                binding.textInput.setText("");
                myAdapter.notifyDataSetChanged(); // will redraw
            });
        });

        class MyRowHolder extends RecyclerView.ViewHolder {
            public TextView messageText;
            public TextView timeText;

            public MyRowHolder(@NonNull View itemView) {
                super(itemView);

                itemView.setOnClickListener(click -> {
                    int position = getAbsoluteAdapterPosition();
                    ChatMessage selected = messages.get(position);

                    chatModel.selectedMessage.postValue(selected);
                });

           /* AlertDialog.Builder builder = new AlertDialog.Builder(ChatRoom.this);

                builder.setNegativeButton("No" , (btn, obj)->{ *//* if no is clicked *//*  }  );
                builder.setMessage("Do you want to delete this message?");
                builder.setTitle("Delete");

                builder.setPositiveButton("Yes", (p1, p2)-> {
                    *//*is yes is clicked*//*
                    Executor thread1 = Executors.newSingleThreadExecutor();
                    thread1.execute(( ) -> {
                        //delete from database
                        mDao.deleteThisMessage(toDelete);//which chat message to delete?

        });
                    messages.remove(rowNum);//remove from the array list
                    myAdapter.notifyDataSetChanged();//redraw the list


                    //give feedback:anything on screen
                    Snackbar.make( itemView , "You deleted the row", Snackbar.LENGTH_LONG)
                            .setAction("Undo", (btn) -> {
                                Executor thread2 = Executors.newSingleThreadExecutor();
                                thread2.execute(( ) -> {
                                    mDao.insertMessage(toDelete);
                                });

                                messages.add(rowNum, toDelete);
                                myAdapter.notifyDataSetChanged();//redraw the list
                            })
                            .show();
                });

                builder.create().show(); //this has to be last*/
            }

            ;
//            messageText =itemView.findViewById(R.id.messageText);
//            timeText =itemView.findViewById(R.id.timeText);
        }
    }

    /*class ChatMessage {
        String message;
        String timeSent;
        boolean isSent;

        public ChatMessage(String message, String timeSent, boolean isSent) {
            this.message = message;
            this.timeSent = timeSent;
            this.isSent = isSent;
        }*/


}


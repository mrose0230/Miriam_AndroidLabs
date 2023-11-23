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
import android.util.Log;
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

    ArrayList<ChatMessage> messages = null;;
    ActivityChatRoomBinding binding;
    RecyclerView.Adapter myAdapter =null;
    ChatMessageDAO mDao;  //declare dao
    ChatRoomViewModel chatModel;


    //this is called when we click the back arrow, sets the selected message to null
   @Override
   public void onBackPressed(){
       chatModel.selectedMessage.postValue(null);
       super.onBackPressed();
   }


   //sets up the UI and data
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //sets up bindings and ViewModel
        binding = ActivityChatRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        chatModel = new ViewModelProvider(this).get(ChatRoomViewModel.class);

        //could add  getValue() part to the end
        messages = chatModel.messages;

        //observe changes to selectedMessage, handles fragments
        chatModel.selectedMessage.observe(this, selectedMessage -> {
            if(selectedMessage !=null){

                MessageDetailsFragment newMessage = new MessageDetailsFragment(selectedMessage);

                FragmentManager fMgr = getSupportFragmentManager();
                FragmentTransaction transaction = fMgr.beginTransaction();
                transaction.addToBackStack("any string here");
                transaction.add(R.id.fragmentLocation, newMessage);
                transaction.commit();
            }
        });


        //below is to load messages from the DB
        MessageDatabase db = Room.databaseBuilder(getApplicationContext(), MessageDatabase.class, "database-name").build();
        mDao = db.cmDAO(); //initializes the variable
        Executor thread=Executors.newSingleThreadExecutor();
        thread.execute(() -> {

            List<ChatMessage> fromDatabase = mDao.getAllMessages();//return list
            messages.addAll(fromDatabase);//all messaged from db are added

        });

        //end of loading from db

        // on click listener for the send button, handles when new messages are sent
        binding.sendButton.setOnClickListener(click -> {
            String textInput = binding.textInput.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");
            String currentDateandTime = sdf.format(new Date());
            ChatMessage thisMessage = new ChatMessage(textInput, currentDateandTime, true);
            messages.add(thisMessage);
            binding.textInput.setText(""); //removes what you typed before
            myAdapter.notifyDataSetChanged(); //redraws


            //add more to db on another thread
            Executor thread1 = Executors.newSingleThreadExecutor();
            thread1.execute(() -> {

                mDao.insertMessage(thisMessage);
                Log.d("TAG", "The id created is:" + thisMessage.id);
            });
        });


        //sets up the recycler view adapter and layout, this is what displays the list of messages
        binding.recycleView.setAdapter(
                myAdapter= new RecyclerView.Adapter<MyRowHolder>(){
                    @NonNull
                    @Override
                    public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
                        if(viewType==0){
                            SentMessageBinding rowBinding=SentMessageBinding.inflate(getLayoutInflater(),parent,false);
                            return new MyRowHolder(rowBinding.getRoot());
                        }
                        else{
                            RecMessageBinding messageBinding=RecMessageBinding.inflate(getLayoutInflater(),parent,false);
                            return new MyRowHolder(messageBinding.getRoot());
                        }
                    }
                    @Override
                    public int getItemViewType(int position) {
                        if(position < 3)
                            return 0;
                        else
                            return 1;
                    }
                    @Override
                    public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {
                        ChatMessage forRow = messages.get(position);
                        holder.messageText.setText(forRow.message);
                        holder.timeText.setText(forRow.timeSent);
                    }

                    @Override //fatal error here, null pointer exception
                    public int getItemCount() {

                       // return messages.size();
                        //should fix the null pointer...
                        return (messages != null) ? messages.size() : 0;

                    }
                });

        binding.recycleView.setLayoutManager(new LinearLayoutManager(this));
    }

    //my row holder class holds references to the text views

    class MyRowHolder extends RecyclerView.ViewHolder {
        public TextView messageText;
        public TextView timeText;

        public MyRowHolder(@NonNull View itemView) {
            super(itemView);

            //handles what happens when a message is clicked
            itemView.setOnClickListener(click -> {
                int position = getAbsoluteAdapterPosition();
                ChatMessage toDelete = messages.get(position);

                if(chatModel.selectedMessage.getValue()==null)
                    //this starts the loading
                    chatModel.selectedMessage.postValue(toDelete);

                AlertDialog.Builder builder = new AlertDialog.Builder( ChatRoom.this );

                builder.setNegativeButton("No" , (btn, obj)->{ // if no is clicked
                }  );
                builder.setMessage("Do you want to delete this message?");
                builder.setTitle("Delete");

                builder.setPositiveButton("Yes", (p1, p2)-> {
                    //is yes is clicked* /
                    Executor thread1 = Executors.newSingleThreadExecutor();
                    thread1.execute(( ) -> {
                        //delete from database
                        mDao.deleteThisMessage(toDelete);//which chat message to delete?

                    });
                    messages.remove(position);//remove from the array list
                    myAdapter.notifyDataSetChanged();//redraw the list


                    //give feedback:anything on screen
                    Snackbar.make( itemView , "You deleted the row", Snackbar.LENGTH_LONG)
                            .setAction("Undo", (btn) -> {
                                Executor thread2 = Executors.newSingleThreadExecutor();
                                thread2.execute(( ) -> {
                                            mDao.insertMessage(toDelete);
                                        });


                                messages.add(position, toDelete);
                                myAdapter.notifyDataSetChanged();//redraw the list
                            })
                            .show();
                });

                builder.create().show(); //this has to be last


            });

//finds and assigns text views based on the layout files
               messageText = itemView.findViewById(R.id.messageText);
               timeText = itemView.findViewById(R.id.timeText);
//find ids from xml to java

        }
   }
}





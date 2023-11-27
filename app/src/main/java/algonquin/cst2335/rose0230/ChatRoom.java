package algonquin.cst2335.rose0230;

import static android.app.ProgressDialog.show;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
        int position ;
    ChatMessage toDelete;
    ArrayList<ChatMessage> messages =null;
    ActivityChatRoomBinding binding;
    RecyclerView.Adapter myAdapter =null;
    ChatMessageDAO mDao;
    ChatRoomViewModel chatModel;

   @Override
   public void onBackPressed(){
       chatModel.selectedMessage.postValue(null);
       super.onBackPressed();
   }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //onCreateOptionsMenu is called
        setSupportActionBar(binding.myToolbar);
        chatModel = new ViewModelProvider(this).get(ChatRoomViewModel.class);
        //example code doesn't have the getValue() part
        messages = chatModel.messages;

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

        //load all messages from the DB
            Executor thread=Executors.newSingleThreadExecutor();
            thread.execute(() -> {
        List<ChatMessage> fromDatabase = mDao.getAllMessages();//return list
        messages.addAll(fromDatabase);//all messaged from db are added

    });

            //this is where loading from the DB ends

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

    binding.recycleView.setAdapter(
            myAdapter= new RecyclerView.Adapter<MyRowHolder>(){
    /////////////////////
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

                @Override
                public int getItemCount() {
                    return messages.size();
                }
            });

            binding.recycleView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
//inflate menu into the toolbar
        getMenuInflater().inflate(R.menu.my_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch( item.getItemId() )
        {
            case R.id.item_1:

                //put your ChatMessage deletion code here. If you select this item, you should show the alert dialog
                //asking if the user wants to delete this message.
                AlertDialog.Builder builder = new AlertDialog.Builder( ChatRoom.this );

                builder.setNegativeButton("No" , (btn, obj)->{ //if no is clicked

                }  );
                builder.setMessage("Do you want to delete this message?");
                builder.setTitle("Delete");

                builder.setPositiveButton("Yes", (p1, p2)-> {
                    //if yes is clicked
                    Executor thread1 = Executors.newSingleThreadExecutor();
                    thread1.execute(( ) -> {
                        //delete from database
                        mDao.deleteThisMessage(toDelete);//which chat message to delete?

                    });
                    messages.remove(position);//remove from the array list
                    myAdapter.notifyDataSetChanged();//redraw the list


                    //give feedback:anything on screen
                    Snackbar.make( binding.sendButton , "You deleted the row", Snackbar.LENGTH_LONG)
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



                break;

            case R.id.item_2:
                Toast.makeText(this, "Version 1.0 created by Miriam", Toast.LENGTH_LONG);show();

                break;
        }

        return true;
    }

    private Object show() {
        return null;
    }
    /////

    class MyRowHolder extends RecyclerView.ViewHolder {
        public TextView messageText;
        public TextView timeText;

        public MyRowHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(click -> {
                position = getAbsoluteAdapterPosition();
                 toDelete = messages.get(position);

                if(chatModel.selectedMessage.getValue()==null)
                //this starts the loading
                 chatModel.selectedMessage.postValue(toDelete);

            });

               messageText = itemView.findViewById(R.id.messageText);
              timeText = itemView.findViewById(R.id.timeText);
                //find ids from xml to java

        }
   }
}





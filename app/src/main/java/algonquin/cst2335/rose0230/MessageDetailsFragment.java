package algonquin.cst2335.rose0230;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import algonquin.cst2335.rose0230.databinding.DetailsLayoutBinding;

public class MessageDetailsFragment extends Fragment {

    ChatMessage selected;
    public MessageDetailsFragment(ChatMessage thisMessage) {
        selected = thisMessage;

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //inflate an XML layout for this Fragment

        DetailsLayoutBinding binding = DetailsLayoutBinding.inflate(inflater, container, false);
//set the text views:
        binding.messageId.setText( selected.message );
        binding.timeId.setText(selected.timeSent);
        binding.sendId.setText(""+ selected.sentOrReceive);
        binding.databaseId.setText( Long.toString(  selected.id)  );


        return binding.getRoot();
    }
}


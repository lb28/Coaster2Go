package de.uulm.dbis.coaster2go;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.uulm.dbis.coaster2go.model.Message;

public class MessageActivity extends AppCompatActivity {

    private DatabaseReference database;
    private DatabaseReference messageRef;

    public static final String TAG = "messages test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        database = FirebaseDatabase.getInstance().getReference();
        messageRef = database.child("messages");

        messageRef.limitToLast(3).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    r.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                List<Message> messageList = new ArrayList<>(3);

                // Get Message objects...
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    Message message = messageSnapshot.getValue(Message.class);
                    messageList.add(message);
                }

                // ...and use the values to update the UI
                TextView textViewName1 = (TextView) findViewById(R.id.textViewName1);
                TextView textViewMsg1 = (TextView) findViewById(R.id.textViewMsg1);
                TextView textViewName2 = (TextView) findViewById(R.id.textViewName2);
                TextView textViewMsg2 = (TextView) findViewById(R.id.textViewMsg2);
                TextView textViewName3 = (TextView) findViewById(R.id.textViewName3);
                TextView textViewMsg3 = (TextView) findViewById(R.id.textViewMsg3);

                if (messageList.size() >= 3) {

                    textViewName3.setText(messageList.get(2).getName());
                    textViewMsg3.setText(messageList.get(2).getText());
                } else {
                    textViewName3.setText("");
                    textViewMsg3.setText("");
                }
                if (messageList.size() >= 2) {
                    textViewName2.setText(messageList.get(1).getName());
                    textViewMsg2.setText(messageList.get(1).getText());
                } else {
                    textViewName2.setText("");
                    textViewMsg2.setText("");
                }
                if (messageList.size() >= 1) {

                    textViewName1.setText(messageList.get(0).getName());
                    textViewMsg1.setText(messageList.get(0).getText());
                } else {
                    textViewName1.setText("");
                    textViewMsg1.setText("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // read failed
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    public void writeMessage(View view) {
        EditText nameEditText = (EditText) findViewById(R.id.nameEditText);
        EditText messageEditText = (EditText) findViewById(R.id.messageEditText);

        String name = nameEditText.getText().toString();
        String text = messageEditText.getText().toString();
        Message message = new Message(name, text);

        // use push() to auto generate an id
        messageRef.push().setValue(message);

        // clear the text field
        messageEditText.setText("");

    }



}

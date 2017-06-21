package de.uulm.dbis.coaster2go.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.UserProfileChangeRequest;

import de.uulm.dbis.coaster2go.R;

public class MyAccountActivity extends BaseActivity {

    private static final String TAG = "MyAccountActivity";
    String currentUserName;

    EditText editTextUserName;
    Button buttonSaveUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        editTextUserName = (EditText) findViewById(R.id.editTextUserName);
        buttonSaveUserData = (Button) findViewById(R.id.buttonSaveUserData);

        if (user == null) {
            Toast.makeText(this, "Nicht eingeloggt", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ParkOverviewActivity.class);
            startActivity(intent);
        } else {
            currentUserName = user.getDisplayName();
            editTextUserName.setText(currentUserName);
            editTextUserName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    boolean hasUserNameChanged = !s.toString().equals(currentUserName);
                    buttonSaveUserData.setEnabled(hasUserNameChanged);
                }
            });
        }
    }

    public void saveUserData(View view) {
        // start the progress bar
        progressBar.setVisibility(View.VISIBLE);

        // update the username field
        final String newUserName = editTextUserName.getText().toString();

        // update the user's profile
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(newUserName).build();

        user.updateProfile(profileChangeRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                            currentUserName = newUserName;
                            buttonSaveUserData.setEnabled(false);

                            if (textViewUserName != null) {
                                textViewUserName.setText(currentUserName);
                            }
                        }

                        // hide the progress bar
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
}

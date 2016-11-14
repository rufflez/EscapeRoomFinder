package com.escaperoom.rufflez.escaperoomfinder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by rufflez on 11/10/16.
 */

public class NewEscapeRoom extends AppCompatActivity {

    private Button save;
    private EditText name, address, url;
    private DatabaseReference mDatabaseReference;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_escape_room);
        name = (EditText)findViewById(R.id.name);
        address = (EditText)findViewById(R.id.address);
        url = (EditText)findViewById(R.id. url);
        save = (Button)findViewById(R.id.save);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameText = "";
                String addressText = "";
                String urlText = "";
                if (name.getText().toString() != null){
                    nameText = name.getText().toString();
                }
                if (address.getText().toString() != null){
                    addressText = address.getText().toString();
                }
                if (url.getText().toString() != null){
                    urlText = url.getText().toString();
                }
                EscapeRoom escapeRoom = new EscapeRoom(nameText, addressText, urlText);
                mDatabaseReference.child("rooms").push().setValue(escapeRoom);
                finish();
            }
        });

    }
}

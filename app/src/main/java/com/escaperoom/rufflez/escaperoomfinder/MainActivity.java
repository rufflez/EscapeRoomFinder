package com.escaperoom.rufflez.escaperoomfinder;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener{

    public static class RoomViewHolder extends RecyclerView.ViewHolder{
        TextView roomName;
        TextView roomAddress;
        TextView roomUrl;

        public RoomViewHolder(View v){
            super(v);
            roomName = (TextView)itemView.findViewById(R.id.name);
            roomAddress = (TextView)itemView.findViewById(R.id.address);
            roomUrl = (TextView)itemView.findViewById(R.id.url);
        }
    }
    public static final String ROOMS = "rooms";
    private String mUsername;
    private String mPhotoUrl;
    private GoogleApiClient mGoogleApiClient;
    public static final String ANONYMOUS = "anonymous";
    private static final String TAG = "MainActivity";
    //Recycler View Setup
    private RecyclerView mRoomRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseAnalytics mFirebaseAnalytics;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<EscapeRoom, RoomViewHolder> mFirebaseAdapter;

    private FloatingActionButton new_room;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //mUsername = ANONYMOUS;
        //Initialize RecyclerView
        mRoomRecyclerView = (RecyclerView)findViewById(R.id.roomRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        //Database Initialization
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<EscapeRoom, RoomViewHolder>(
                EscapeRoom.class,
                R.layout.escape_room,
                RoomViewHolder.class,
                mFirebaseDatabaseReference.child(ROOMS)) {
            @Override
            protected void populateViewHolder(RoomViewHolder viewHolder, EscapeRoom model, int position) {
                viewHolder.roomName.setText(model.getName());
                viewHolder.roomAddress.setText(model.getAddress());
                viewHolder.roomUrl.setText(model.getUrl());
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver(){
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount){
                super.onItemRangeInserted(positionStart, itemCount);
                int roomCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 || (positionStart >= (roomCount -1) && lastVisiblePosition == (positionStart -1))){
                    mRoomRecyclerView.scrollToPosition(positionStart);
                }
            }
        });
        mRoomRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRoomRecyclerView.setAdapter(mFirebaseAdapter);

        new_room = (FloatingActionButton)findViewById(R.id.new_room);
        new_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewEscapeRoom.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.sign_out:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mFirebaseUser = null;
                mUsername = ANONYMOUS;
                mPhotoUrl = null;
                startActivity(new Intent(this, SignInActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in.
        // TODO: Add code to check if user is signed in.
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}

package com.example.lumos;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lumos.model.Adapter;
import com.example.lumos.model.Post;
import com.example.lumos.note.AddToChatActivity;
import com.example.lumos.note.NoteDetails;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class BlogActivity extends AppCompatActivity {

    TextView toHomePage;

    RecyclerView postList;

    FirestoreRecyclerAdapter<Post, PostViewHolder> postAdapter;
    FirebaseFirestore fStore;
    FirebaseUser user;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);

        toHomePage = findViewById(R.id.toHomePage);

        postList = findViewById(R.id.postList);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        Query query = fStore.collection("GetAllNotes").orderBy("title", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Post> allNotes = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query,Post.class)
                .build();

        postAdapter = new FirestoreRecyclerAdapter<Post, PostViewHolder>(allNotes) {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull BlogActivity.PostViewHolder holder, int position, @NonNull Post model) {
                holder.noteTitle.setText(model.getTitle());
                holder.noteContent.setText(model.getContent());
                holder.username.setText(model.getUser());
                final int code = getRandomColor();
                holder.mCardView.setCardBackgroundColor(holder.view.getResources().getColor(code,null));

                holder.view.findViewById(R.id.menuEdit).setVisibility(View.GONE);
            }

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_view, parent, false);
                return new PostViewHolder(view);
            }
        };

        postList.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        postList.setAdapter(postAdapter);

        toHomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
        });
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle, noteContent, username;
        View view;
        CardView mCardView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            noteTitle = itemView.findViewById(R.id.titles);
            noteContent = itemView.findViewById(R.id.content);
            username = itemView.findViewById(R.id.username);
            mCardView = itemView.findViewById(R.id.noteCard);
            view = itemView;
        }
    }

    private int getRandomColor() {

        List<Integer> colorCode = new ArrayList<>();
        colorCode.add(R.color.blue);
        colorCode.add(R.color.yellow);
        colorCode.add(R.color.skyblue);
        colorCode.add(R.color.lightPurple);
        colorCode.add(R.color.lightGreen);
        colorCode.add(R.color.gray);
        colorCode.add(R.color.pink);
        colorCode.add(R.color.red);
        colorCode.add(R.color.greenlight);
        colorCode.add(R.color.notgreen);

        Random randomColor = new Random();
        int number = randomColor.nextInt(colorCode.size());
        return colorCode.get(number);

    }

    // kiedy aplikacja jest włączona zaczynamy nasłuchiwać na nowe dane, np. gdy dodamy nowy post
    @Override
    protected void onStart() {
        super.onStart();
        postAdapter.startListening();
    }

    // Kiedy zamykamy aplikacjię stopujemy nasłuchiwanie
    @Override
    protected void onStop() {
        super.onStop();
        if (postAdapter != null) {
            postAdapter.stopListening();
        }
    }

    public void toAddMessage(View view) {
        startActivity(new Intent(getApplicationContext(), AddToChatActivity.class));
        finish();
    }
}
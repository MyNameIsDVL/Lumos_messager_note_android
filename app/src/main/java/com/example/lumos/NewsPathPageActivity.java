package com.example.lumos;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lumos.model.Post;
import com.example.lumos.note.AddToChatActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class NewsPathPageActivity extends AppCompatActivity {

    RecyclerView postList;

    FirestoreRecyclerAdapter<Post, NewsPathPageActivity.PostViewHolder> postAdapter;
    FirebaseFirestore fStore;
    FirebaseUser user;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_path_page);

        postList = findViewById(R.id.postList);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        Query query = fStore.collection("newsPatch").orderBy("title", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Post> allNotes = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query,Post.class)
                .build();

        postAdapter = new FirestoreRecyclerAdapter<Post, NewsPathPageActivity.PostViewHolder>(allNotes) {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull NewsPathPageActivity.PostViewHolder holder, int position, @NonNull Post model) {

                // if is not admin logged then setVisibility to gone - edit and delete

                if (!user.getUid().equals("nKt5rUzUjaUFcTAcmfM6ftRNTXj1")) {
                    holder.deleteNews.setVisibility(View.GONE);
                    holder.newsEdit.setVisibility(View.GONE);
                }

                holder.noteTitle.setText(model.getTitle());
                holder.noteContent.setText(model.getContent());
                holder.username.setText(model.getUser());
            }

            @NonNull
            @Override
            public NewsPathPageActivity.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_patch_view, parent, false);
                return new NewsPathPageActivity.PostViewHolder(view);
            }
        };

        postList.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        postList.setAdapter(postAdapter);
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle, noteContent, username;
        ImageView deleteNews, newsEdit;
        View view;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            noteTitle = itemView.findViewById(R.id.titles);
            noteContent = itemView.findViewById(R.id.content);
            username = itemView.findViewById(R.id.username);
            deleteNews = itemView.findViewById(R.id.deleteNews);
            newsEdit = itemView.findViewById(R.id.newsEdit);
            view = itemView;
        }
    }

    public void toHomePage(View view) {
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        finish();
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
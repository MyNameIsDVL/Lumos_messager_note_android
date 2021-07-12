package com.example.lumos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lumos.Fragments.ChatsFragment;
import com.example.lumos.Fragments.UsersFragment;
import com.example.lumos.model.Chat;
import com.example.lumos.model.Post;
import com.example.lumos.note.AddNote;
import com.example.lumos.note.EditNote;
import com.example.lumos.note.NoteDetails;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class HomeActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    private ViewPager2 viewPager2;
    private Handler sliderHandler = new Handler();
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    StorageReference storageReference;

    String docBlogId;

    RecyclerView postList;

    TextView userLoggedName, resendMsg, toHomePage, count_newPosts;
    Button btnResendCode;
    RelativeLayout resedComponent;
    ImageView imageToDisplayIn;

    CardView AdminAddNewsbtn, count_newPosts_cardview;

    FirestoreRecyclerAdapter<Post, HomeActivity.PostViewHolder> postAdapter;
    FirebaseUser user;

    FirebaseUser firebaseUser;
    DatabaseReference reference, count_reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statusBarColor();
        setContentView(R.layout.activity_home);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userLoggedName = (TextView) findViewById(R.id.userLoggedName);
        resendMsg = (TextView) findViewById(R.id.resendMsg);
        btnResendCode = (Button) findViewById(R.id.btnResendCode);
        resedComponent = (RelativeLayout) findViewById(R.id.resedComponent);

        imageToDisplayIn = (ImageView) findViewById(R.id.imageToDisplayIn);

        AdminAddNewsbtn = (CardView) findViewById(R.id.AdminAddNewsbtn);

        postList = findViewById(R.id.postList);

        userId = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        // check if admin is logged to display special tools

        if (userId.equals("vPjQ7YkAEdZzJSwHMn6zXCIb9o82")) {
            AdminAddNewsbtn.setVisibility(View.VISIBLE);
        }

        // set img profile

        StorageReference profileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/default.png");

        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imageToDisplayIn);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Picasso.get().load(R.mipmap.ic_profile_foreground).into(imageToDisplayIn);
                Log.d("tag", "Ustawiony został awatar domyślny"+e.getMessage());
            }
        });

        // set logged user name in header

        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e == null) {
                    if (documentSnapshot.exists()) {
                        userLoggedName.setText(documentSnapshot.getString("UserName"));

                    } else {
                        Log.d("tag", "Document nie istnieje");
                    }
                }
            }
        });

        if (!user.isEmailVerified()) {
            resedComponent.setVisibility(View.VISIBLE);

            btnResendCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send verif to user email

                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(view.getContext(), "Link weryfikacyjny został wysłany na Twojego maila", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Błąd podczas wysyłania linku weryfikacyjnego. Spróbuj ponownie póżniej");
                        }
                    });
                }
            });
        }


        // Slider
        viewPager2 = findViewById(R.id.imageSlide);

        // Set images for slider
        List<SliderItem> sliderItem = new ArrayList<>();

        sliderItem.add(new SliderItem(R.mipmap.ic_tho_ok_bg_foreground));
        sliderItem.add(new SliderItem(R.mipmap.ic_prog_design_bg_foreground));
        sliderItem.add(new SliderItem(R.mipmap.ic_share_k_bg_foreground));
        sliderItem.add(new SliderItem(R.mipmap.ic_tho_ok_bg_foreground));
        sliderItem.add(new SliderItem(R.mipmap.ic_prog_design_bg_foreground));
        sliderItem.add(new SliderItem(R.mipmap.ic_share_k_bg_foreground));

        viewPager2.setAdapter(new SliderAdapter(sliderItem, viewPager2));
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.20f);
            }
        });

        viewPager2.setPageTransformer(compositePageTransformer);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
            }
        });


        // display user posts

        Query query = fStore.collection("notes").document(user.getUid()).collection("myNotes").orderBy("title", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Post> allNotes = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query,Post.class)
                .build();

        postAdapter = new FirestoreRecyclerAdapter<Post, HomeActivity.PostViewHolder>(allNotes) {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull HomeActivity.PostViewHolder holder,final int position, @NonNull Post model) {
                holder.noteTitle.setText(model.getTitle());
                holder.noteContent.setText(model.getContent());
                holder.username.setText(userLoggedName.getText());
                final int code = getRandomColor();
                holder.mCardView.setCardBackgroundColor(holder.view.getResources().getColor(code,null));
                final String docId = postAdapter.getSnapshots().getSnapshot(position).getId();

                holder.view.findViewById(R.id.menuEdit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(v.getContext(), NoteDetails.class);
                        i.putExtra("title", model.getTitle());
                        i.putExtra("content", model.getContent());
                        i.putExtra("code",code);
                        i.putExtra("noteId",docId);
                        v.getContext().startActivity(i);
                    }
                });

                holder.view.findViewById(R.id.deleteNote).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DocumentReference docref = fStore.collection("notes").document(user.getUid()).collection("myNotes").document(docId);

                        docref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(HomeActivity.this, "Usunięto pomyślnie.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(HomeActivity.this, "Błąd, nie udało się usunąć, spróbuj ponownie później.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

            @NonNull
            @Override
            public HomeActivity.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_view, parent, false);
                return new HomeActivity.PostViewHolder(view);
            }
        };

        postList.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        postList.setAdapter(postAdapter);

        // count of new unread posts

        count_newPosts_cardview = findViewById(R.id.count_newPosts_cardview);
        count_newPosts = findViewById(R.id.count_newPosts);

        count_reference = FirebaseDatabase.getInstance("https://lumos-e4859-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Chats");
        count_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int unread = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(user.getUid()) && !chat.getIsseen()) {
                        unread++;
                    }
                }

                if (unread == 0) {
                    count_newPosts_cardview.setVisibility(View.GONE);
                } else {
                    count_newPosts_cardview.setVisibility(View.VISIBLE);
                    count_newPosts.setText(Integer.toString(unread));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
        status("offline");
    }

    @Override
    protected  void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
        status("online");
    }

    public void statusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.whiteTextColor));

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    public void profile(View view) {
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        finish();
    }

    public void toBlogPage(View view) {
        startActivity(new Intent(getApplicationContext(), BlogActivity.class));
        finish();
    }

    public void toNewPostPage(View view) {
        startActivity(new Intent(getApplicationContext(), AddNote.class));
        finish();
    }

    public void toAdminAddNews(View view) {
        startActivity(new Intent(getApplicationContext(), AddToNewsPatchActivity.class));
        finish();
    }

    public void AppNewsbtn(View view) {
        startActivity(new Intent(getApplicationContext(), NewsPathPageActivity.class));
        finish();
    }

    public void Chatbtn(View view) {
        startActivity(new Intent(getApplicationContext(), PrivateChatMainPageActivity.class));
        finish();
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

    private void status(String status) {
        reference = FirebaseDatabase.getInstance("https://lumos-e4859-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users").child(user.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Status", status);

        reference.updateChildren(hashMap);
    }
}
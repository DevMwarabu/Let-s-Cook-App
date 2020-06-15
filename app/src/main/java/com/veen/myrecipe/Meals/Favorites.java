package com.veen.myrecipe.Meals;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.veen.myrecipe.FavsList;
import com.veen.myrecipe.FavsMealsAdapter;
import com.veen.myrecipe.R;

import java.util.ArrayList;
import java.util.List;

public class Favorites extends AppCompatActivity {
    private List<FavsList> favsLists;
    private FavsMealsAdapter favsMealsAdapter;
    private RecyclerView mRecyclerView;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;

    public static ProgressBar progressBar;

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;


    private DocumentSnapshot lastVisible;
    private boolean isFirstPageFirstLoad = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        favsLists = new ArrayList<>();
        favsMealsAdapter = new FavsMealsAdapter(favsLists);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        user_id = user.getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        progressBar = findViewById(R.id.progressBar);

        mRecyclerView = findViewById(R.id.recycler_main);


        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(favsMealsAdapter);


        MobileAds.initialize(this, getString(R.string.appid));
        mAdView =(AdView) findViewById(R.id.adview);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        LoadData(user_id);

    }

    private void LoadData(final String user_id) {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean aBoolean = !recyclerView.canScrollVertically(1);

                if (aBoolean) {
                    loadMorePosts(user_id);
                }
            }
        });

        Query firstQuery = firebaseFirestore.collection("Favorites")
                .document(user.getUid()).collection("Favorites").orderBy("timeStamp", Query.Direction.DESCENDING);
        firstQuery.addSnapshotListener(Favorites.this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {


                if (queryDocumentSnapshots !=null){

                    if (!queryDocumentSnapshots.isEmpty()) {

                        if (isFirstPageFirstLoad) {
                            lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                        }

                        for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                            if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                String documentId = documentChange.getDocument().getId();
                                FavsList favsList = documentChange.getDocument().toObject(FavsList.class).withId(documentId);

                                if (isFirstPageFirstLoad) {
                                    favsLists.add(favsList);
                                } else {
                                    favsLists.add(0,favsList);
                                }
                                favsMealsAdapter.notifyItemRangeChanged(0, favsMealsAdapter.getItemCount());
                            }
                        }

                        isFirstPageFirstLoad = false;
                    }
                }
            }
        });
    }

    private void loadMorePosts(String user_id) {
        Query secondQuery = firebaseFirestore.collection("Favorites")
                .document(user.getUid()).collection("Favorites").orderBy("timeStamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(3);
        secondQuery.addSnapshotListener(Favorites.this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (queryDocumentSnapshots !=null){
                    if (!queryDocumentSnapshots.isEmpty()) {
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                        for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                            if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                String documentId = documentChange.getDocument().getId();
                                FavsList favsList = documentChange.getDocument().toObject(FavsList.class).withId(documentId);
                                favsLists.add(favsList);
                                favsMealsAdapter.notifyItemRangeChanged(0, favsMealsAdapter.getItemCount());
                            }
                        }
                    }
                }


            }
        });
    }


}

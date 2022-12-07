package com.smile.watchmovie.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.smile.watchmovie.MainActivity;
import com.smile.watchmovie.R;
import com.smile.watchmovie.databinding.FragmentHomeBinding;
import com.smile.watchmovie.databinding.FragmentPersonBinding;

import org.json.JSONException;

import java.util.Objects;


public class PersonFragment extends Fragment {

    private FragmentPersonBinding binding;
    private MainActivity mMainActivity;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;
    private String idUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainActivity = (MainActivity) getActivity();
        binding = FragmentPersonBinding.inflate(inflater, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("film_favorite_"+ idUser);


        return binding.getRoot();
    }

    private void getIdUser(){
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(mMainActivity);
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(acct == null && accessToken == null){
            //binding.ivNoFavorite.setVisibility(View.INVISIBLE);
        }
        else if(acct != null){
            this.idUser = acct.getId();
        }
        else if(!accessToken.isExpired()) {
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    (object, response) -> {
                        // Application code
                        try {
                            assert object != null;
                            this.idUser = (String) object.get("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,link");
            request.setParameters(parameters);
            request.executeAsync();
        }
    }

//    public void isFilmFavorite(){
//        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isSuccessful()){
//                    int check = 0;
//                    QuerySnapshot snapshot = task.getResult();
//                    for(QueryDocumentSnapshot doc : snapshot){
//                        String idFilm1 = Objects.requireNonNull(doc.get("idFilm")).toString();
//                        if(Integer.parseInt(idFilm1) == idFilm){
//                            check = 1;
//                            changeImage = R.drawable.ic_baseline_favorite_24;
//                            currentImage = R.drawable.ic_baseline_favorite_24;
//                            binding.ivNoFavorite.setImageResource(currentImage);
//                        }
//                    }
//                    if(check == 0){
//                        currentImage = R.drawable.ic_baseline_favorite_border_24;
//                        changeImage = R.drawable.ic_baseline_favorite_border_24;
//                    }
//                }
//            }
//        });
//    }
}
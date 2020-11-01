package com.gtr.easycashregister;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.gtr.easycashregister.API.Api;
import com.gtr.easycashregister.Adapter.DataAdapter;
import com.gtr.easycashregister.Model.OurDataSet;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserProfileActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private String phoneNumber;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        progressDialog = new ProgressDialog(UserProfileActivity.this);
        progressDialog.setMessage("Waiting");
        progressDialog.setCancelable(false);

        phoneNumber=getIntent().getStringExtra("mainNumber");

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://101.2.165.189:96/api/EasyCashItem/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Api ourRetrofit= retrofit.create(Api.class);

        Call<List<OurDataSet>> listCall= ourRetrofit.getDataSet(phoneNumber);
        listCall.enqueue(new Callback<List<OurDataSet>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<List<OurDataSet>> call, Response<List<OurDataSet>> response) {
                progressDialog.dismiss();

                List<OurDataSet> ourDataSets = response.body();

                for (OurDataSet ourDataSet:ourDataSets){

                    showIt(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<OurDataSet>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(UserProfileActivity.this, "Failed", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void showIt(List<OurDataSet> response) {


        DataAdapter dataAdapter = new DataAdapter(response,getApplicationContext());

        recyclerView.setAdapter(dataAdapter);
    }
}
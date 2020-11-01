package com.gtr.easycashregister.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gtr.easycashregister.Model.OurDataSet;
import com.gtr.easycashregister.R;

import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataViewHolder> {

    private List<OurDataSet> list;
    private Context context;


    public DataAdapter(List<OurDataSet> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.product_show,viewGroup,false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder dataViewHolder, int i) {

        byte[] orderImage2;
        final OurDataSet currentdata = list.get(i);

        dataViewHolder.itemNameShow.setText(currentdata.getItemName());
        dataViewHolder.itemPriceShow.setText(String.valueOf(currentdata.getPrice()));
        dataViewHolder.itemQuantityShow.setText(String.valueOf(currentdata.getQuantity()));



        orderImage2= Base64.decode(String.valueOf(currentdata),Base64.DEFAULT);

        Bitmap decoded= BitmapFactory.decodeByteArray(orderImage2,0,orderImage2.length);
        dataViewHolder.itemImageShow.setImageBitmap(decoded);



    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

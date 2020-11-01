package com.gtr.easycashregister.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gtr.easycashregister.R;


public class DataViewHolder extends RecyclerView.ViewHolder {

    TextView itemNameShow,itemPriceShow,itemQuantityShow;
    ImageView itemImageShow;

    public DataViewHolder(@NonNull View itemView) {
        super(itemView);

        itemNameShow=itemView.findViewById(R.id.itemNameShow);
        itemPriceShow=itemView.findViewById(R.id.itemPriceShow);
        itemQuantityShow=itemView.findViewById(R.id.itemQuantityShow);
        /*orderImage=itemView.findViewById(R.id.orderImage);*/
        itemImageShow = itemView.findViewById(R.id.itemImageShow);


    }

}

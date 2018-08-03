package com.example.matnguyen.elcom_trafficgo.searchRoutes.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matnguyen.elcom_trafficgo.R;
import com.example.matnguyen.elcom_trafficgo.searchRoutes.model.ItemList;

import java.util.ArrayList;

public class ListLocationAdapter extends ArrayAdapter<ItemList> {

    public ListLocationAdapter(@NonNull Context context, ArrayList<ItemList> arr) {
        super(context, 0, arr);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemList list = getItem(position);
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_list_location, parent, false);

        }
        ImageView image =  convertView.findViewById(R.id.image);
        TextView text =  convertView.findViewById(R.id.edtName);
        text.setText(list.getName());
        image.setImageResource(list.getImage());

        return convertView;
    }


}

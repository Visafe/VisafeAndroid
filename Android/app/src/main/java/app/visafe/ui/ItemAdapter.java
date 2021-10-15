package app.visafe.ui;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import app.visafe.R;
import app.visafe.domain.DomainVisafe;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends ArrayAdapter<Item> {
    private Context mContext;
    private List<Item> itemList = new ArrayList<>();

    public ItemAdapter(@NonNull Context context, @SuppressLint("SupportAnnotationUsage") @LayoutRes ArrayList<Item> list) {
        super(context, 0 , list);
        mContext = context;
        itemList = list;
    }
    private int count = 0;
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.single_row,parent,false);

        Item currentItem = itemList.get(position);

        ImageView image = (ImageView)listItem.findViewById(R.id.imageview);
        image.setImageResource(currentItem.getmImageDrawable());

        TextView name = (TextView) listItem.findViewById(R.id.textview);
        name.setText(currentItem.getmName());

        Switch switch_vip = listItem.findViewById(R.id.switch_vip);
        SharedPreferences sharedPreferences1 = mContext.getSharedPreferences("switchVIP",Context.MODE_PRIVATE);
        boolean switchVIP = sharedPreferences1.getBoolean("status",false);
        if (switchVIP == true) {
            switch_vip.setChecked(true);
        } else {
            switch_vip.setChecked(false);
        }
        if(position != 3) {
            switch_vip.setVisibility(View.GONE);
        }

        return listItem;
    }
}

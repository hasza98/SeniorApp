package android.seniorapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.seniorapp.R;
import android.seniorapp.model.Person;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class PersonListAdapter extends RecyclerView.Adapter {
    ArrayList<Person> personList;
    private final OnItemClickListener listener;
    public PersonListAdapter(ArrayList pList, OnItemClickListener listener) {
        this.personList = pList;
        this.listener = listener;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        // set the data in items
        Person p = personList.get(position);
        myViewHolder.name.setText(p.getName());
        myViewHolder.year.setText(p.getYear() + ". year");
        switch (p.getType())
        {
            case electrical:
            {
                myViewHolder.fab.setBackgroundTintList(ColorStateList.valueOf(myViewHolder.itemView.getContext().getColor(R.color.Orange)));
                myViewHolder.fab.setImageDrawable(myViewHolder.itemView.getContext().getDrawable(R.drawable.ic_baseline_bolt_48));
            } break;
            case informatics:
            {
                myViewHolder.fab.setBackgroundTintList(ColorStateList.valueOf(myViewHolder.itemView.getContext().getColor(R.color.darkBlue)));
                myViewHolder.fab.setImageDrawable(myViewHolder.itemView.getContext().getDrawable(R.drawable.ic_baseline_computer_48w));
            } break;
            case bprof:
            {
                myViewHolder.fab.setBackgroundTintList(ColorStateList.valueOf(myViewHolder.itemView.getContext().getColor(R.color.Turq)));
                myViewHolder.fab.setImageDrawable(myViewHolder.itemView.getContext().getDrawable(R.drawable.ic_baseline_engineering_48));
            } break;
        }

        try {
            if(p.getImgSource().equals("null"))
                myViewHolder.image.setImageBitmap(getBitmapFromAssets("Images/placeholder.jpg", myViewHolder.itemView.getContext()));
            else
                myViewHolder.image.setImageBitmap(getBitmapFromAssets("Images/" + p.getImgSource(), myViewHolder.itemView.getContext()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // implement setOnClickListener event on item view.
        myViewHolder.itemView.setOnClickListener( (view) -> {
            listener.onItemClick(p);
        });
    }

    @Override
    public int getItemCount() {
        return personList.size();
    }

    public void updateData(ArrayList<Person> persons) {
        personList.clear();
        personList.addAll(persons);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        TextView name;
        ImageView image;
        FloatingActionButton fab;
        TextView year;
        public MyViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            name = itemView.findViewById(R.id.personName);
            image = itemView.findViewById(R.id.personImage);
            fab = itemView.findViewById(R.id.personFab);
            year = itemView.findViewById(R.id.personYear);
        }
    }

    private Bitmap getBitmapFromAssets(String fileName, Context context) throws IOException {
        AssetManager assetManager = context.getAssets();
        InputStream istr = assetManager.open(fileName);
        Bitmap bitmap = BitmapFactory.decodeStream(istr);
        istr.close();
        return bitmap;
    }
    public interface OnItemClickListener {
        void onItemClick(Person item);
    }
}

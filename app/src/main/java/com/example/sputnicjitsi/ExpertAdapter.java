package com.example.sputnicjitsi;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sputnicjitsi.activity.CallActivity;
import com.example.sputnicjitsi.experts.AvailableExperts;
import java.util.List;

public class ExpertAdapter extends RecyclerView.Adapter<ExpertAdapter.ViewHolder>{
    private final LayoutInflater inflater;
    private final List<AvailableExperts> experts;

    public ExpertAdapter(Context context, @NonNull List<AvailableExperts> experts) {
        this.experts = experts;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ExpertAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AvailableExperts expert = experts.get(position);
        //TODO добавить картинки
        holder.iconView.setImageResource(expert.getUser().getUserIcon());
        holder.nameView.setText(expert.getFullName());
        //TODO в каком виде выводить?
        String str = String.valueOf(expert.getUser().getUserId());
        holder.button.setText(str);
    }

    @Override
    public int getItemCount() {
        if(experts == null) {
            return 0;
        } else {
            return experts.size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconView;
        TextView nameView;
        Button button;

        public ViewHolder(View view) {
            super(view);
            iconView = view.findViewById(R.id.icon);
            nameView = view.findViewById(R.id.name);
            button = view.findViewById(R.id.callButton);

            Button button = view.findViewById(R.id.callButton);
            button.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), CallActivity.class);
                intent.putExtra("num", button.getText().toString());
                intent.putExtra("fio", nameView.getText());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent);
            });
        }
    }
}

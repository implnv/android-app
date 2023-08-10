package com.example.sputnicjitsi.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sputnicjitsi.R;
import com.example.sputnicjitsi.WebSocket;
import com.example.sputnicjitsi.experts.AvailableExperts;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;
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

    public static class ViewHolder extends RecyclerView.ViewHolder  {
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
                WebSocket.getInstance().emit("call expert", "hello");

                try {
                    URL serverURL = new URL("https://pseudogu.ru/");

                    JitsiMeetConferenceOptions options  = new JitsiMeetConferenceOptions.Builder()
                                                                    .setServerURL(serverURL)
                                                                    .setFeatureFlag("welcomepage.enabled", false)
                                                                    .setFeatureFlag("toolbox.enabled", false)
                                                                    .setFeatureFlag("filmstrip.enabled", false)
                                                                    .setFeatureFlag("chat.enabled", false)
                                                                    .setFeatureFlag("lobby-mode.enabled", false)
                                                                    .setFeatureFlag("help.enabled", false)
                                                                    .setFeatureFlag("overflow-menu.enabled", false)
                                                                    .setFeatureFlag("prejoinpage.enabled", false)
                                                                    .setFeatureFlag("pip.enabled", false)
                                                                    .setFeatureFlag("pip-while-screen-sharing.enabled", false)
                                                                    .setRoom("dasw221dc3")
                                                                    .build();

                    JitsiMeetActivity.launch(v.getContext(), options);
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }


}

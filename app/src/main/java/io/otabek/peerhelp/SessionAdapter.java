package io.otabek.peerhelp;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionHolder> {

    private final OnItemClickListener listener;
    private ArrayList<Session> sessionsList;

    public SessionAdapter(ArrayList<Session> sessionsList, OnItemClickListener listener) {
        this.sessionsList = sessionsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SessionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new SessionHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.session_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SessionHolder holder, int position) {
        Session session = sessionsList.get(position);
        holder.setData(session.getName(), session.getDetails(), session.getTimestamp());
        holder.bind(session, listener);
    }

    @Override
    public int getItemCount() {
        return sessionsList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Session item);
    }

    public class SessionHolder extends RecyclerView.ViewHolder {

        private TextView sessionNameTextView;
        private TextView sessionDetailsTextView;
        private TextView sessionDateTextView;


        public SessionHolder(@NonNull View itemView) {
            super(itemView);
            sessionNameTextView = itemView.findViewById(R.id.sessionNameTextView);
            sessionDetailsTextView = itemView.findViewById(R.id.sessionDetailsTextView);
            sessionDateTextView = itemView.findViewById(R.id.sessionDateTextView);

        }

        public void bind(final Session item, final OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

        private Date getDate(long time) {
            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();//get your local time zone.
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            sdf.setTimeZone(tz);//set time zone.
            String localTime = sdf.format((time * 1000));
            Date date = new Date();
            try {
                date = sdf.parse(localTime);//get local date
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return date;
        }

        public void setData(String name, String details, Date date) {

            sessionNameTextView.setText(name);
            sessionDetailsTextView.setText(details);
            sessionDateTextView.setText(date.toString());
        }
    }
}

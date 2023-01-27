package com.example.textfinder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private List<Note> notes = new ArrayList<Note>();

    public NotesAdapter(List<Note> notes)
    {
        this.notes = notes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater
                = LayoutInflater.from(parent.getContext());

        // Inflate the layout

        View listItem = inflater.inflate(R.layout.notes_list_view,parent, false);

        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position)
    {
        final Note note = notes.get(position);
        viewHolder.titleView.setText(notes.get(position).getTitle());
        viewHolder.contentView.setText(notes.get(position).getContent());
        viewHolder.dateView.setText(notes.get(position).getDate());
        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                int pos = viewHolder.getAdapterPosition();
//                Toast.makeText(view.getContext(), notes.get(pos).getTitle(), Toast.LENGTH_SHORT).show();
                Intent viewNoteActivity = new Intent(view.getContext(),ViewNote.class);
                viewNoteActivity.putExtra("id", notes.get(pos).getId());
                viewNoteActivity.putExtra("title", notes.get(pos).getTitle());
                viewNoteActivity.putExtra("content",notes.get(pos).getContent());

//                Toast.makeText(view.getContext(), notes.get(pos).getId() + " is the primary key", Toast.LENGTH_SHORT).show();
                view.getContext().startActivity(viewNoteActivity);
            }
        });

    }

    @Override
    public int getItemCount()
    {
        if(notes != null){
            return notes.size();
        }
        return 0;
    }

    @Override
    public void onAttachedToRecyclerView(
            RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleView;
        public TextView contentView;
        public TextView dateView;
        public LinearLayout linearLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.titleView = (TextView) itemView.findViewById(R.id.titleTxt);
            this.contentView = (TextView) itemView.findViewById(R.id.contentTxt);
            this.dateView = (TextView) itemView.findViewById(R.id.dateTxt);
            this.linearLayout = (LinearLayout)itemView.findViewById(R.id.linearView);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    int pos = getLayoutPosition();
//                    Toast.makeText(view.getContext(), notes.get(pos).getTitle(), Toast.LENGTH_SHORT).show();
//                    Intent i = new Intent(view.getContext(),ViewNote.class);
//                    i.putExtra("title", notes.get(pos).getTitle());
//                    i.putExtra("content",notes.get(pos).getContent());
//                    view.getContext().startActivity(i);
//
//                }
//            });

//            itemView.setOnLongClickListener(new View.OnLongClickListener(){
//                @Override
//                public boolean onLongClick(View view) {
//                    Toast.makeText(view.getContext(), "Long Pressed", Toast.LENGTH_SHORT).show();
//                    return false;
//                }
//            });
        }


    }
}

package com.example.textfinder;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FoldersAdapter extends RecyclerView.Adapter<FoldersAdapter.FolderViewHolder> {

    private List<Folder> folder = new ArrayList<Folder>();


    @Override
    public FoldersAdapter.FolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater
                = LayoutInflater.from(parent.getContext());

        // Inflate the layout

        View listItem = inflater.inflate(R.layout.notes_list_view,parent, false);

        FoldersAdapter.FolderViewHolder viewHolder = new FolderViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final FoldersAdapter.FolderViewHolder viewHolder, final int position)
    {
        final Folder f = folder.get(position);
        viewHolder.titleView.setText(folder.get(position).getTitle());
        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                int pos = viewHolder.getAdapterPosition();
//                Toast.makeText(view.getContext(), notes.get(pos).getTitle(), Toast.LENGTH_SHORT).show();
//                Intent i = new Intent(view.getContext(),ViewNote.class);
//                i.putExtra("title", notes.get(pos).getTitle());
//                i.putExtra("content",notes.get(pos).getContent());
//                view.getContext().startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount()
    {
        if(folder != null){
            return folder.size();
        }
        return 0;
    }

    @Override
    public void onAttachedToRecyclerView(
            RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public class FolderViewHolder extends RecyclerView.ViewHolder {
        public TextView titleView;
        public LinearLayout linearLayout;
        public FolderViewHolder(View itemView) {
            super(itemView);
            this.titleView = (TextView) itemView.findViewById(R.id.folderName);
            this.linearLayout = (LinearLayout)itemView.findViewById(R.id.linearViewFolder);

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

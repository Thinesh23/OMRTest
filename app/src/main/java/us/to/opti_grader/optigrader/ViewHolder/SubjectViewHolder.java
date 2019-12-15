package us.to.opti_grader.optigrader.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import us.to.opti_grader.optigrader.R;
import us.to.opti_grader.optigrader.Common.Common;
import us.to.opti_grader.optigrader.Interface.ItemClickListener;


public class SubjectViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener,
        View.OnCreateContextMenuListener{

    public TextView subject_name;
    public ImageView subject_image;

    private ItemClickListener itemClickListener;

    public SubjectViewHolder (View itemView) {

        super (itemView);

        subject_name = (TextView) itemView.findViewById(R.id.subject_name);
        subject_image = (ImageView) itemView.findViewById(R.id.subject_image);

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select the action");

        contextMenu.add(0,0,getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0,0,getAdapterPosition(), Common.DELETE);
    }

    @Override
    public void onClick(View view){
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}
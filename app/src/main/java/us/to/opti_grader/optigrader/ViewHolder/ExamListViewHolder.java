package us.to.opti_grader.optigrader.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import us.to.opti_grader.optigrader.Common.Common;
import us.to.opti_grader.optigrader.Interface.ItemClickListener;
import us.to.opti_grader.optigrader.R;

public class ExamListViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener,
        View.OnCreateContextMenuListener{

    public TextView txtSubjectName, txtExamType, txtStudentNo, txtTotalQues;

    private ItemClickListener itemClickListener;

    public ExamListViewHolder(View itemView) {
        super(itemView);
        txtSubjectName = (TextView)itemView.findViewById(R.id.txtSubjectName);
        txtExamType = (TextView)itemView.findViewById(R.id.txtExamType);
        txtStudentNo = (TextView)itemView.findViewById(R.id.txtStudentNo);
        txtTotalQues = (TextView) itemView.findViewById(R.id.txtExamQuestion);

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
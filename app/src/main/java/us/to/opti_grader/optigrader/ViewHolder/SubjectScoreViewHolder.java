package us.to.opti_grader.optigrader.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import us.to.opti_grader.optigrader.Interface.ItemClickListener;
import us.to.opti_grader.optigrader.R;

public class SubjectScoreViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener{

    public TextView subjectName, averageScore;

    private ItemClickListener itemClickListener;

    public SubjectScoreViewHolder(View itemView) {
        super(itemView);
        subjectName = (TextView) itemView.findViewById(R.id.txt_subject_name);
        averageScore = (TextView) itemView.findViewById(R.id.txt_average_score);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view){
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}

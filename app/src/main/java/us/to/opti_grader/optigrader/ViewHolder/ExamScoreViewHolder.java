package us.to.opti_grader.optigrader.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import us.to.opti_grader.optigrader.Interface.ItemClickListener;
import us.to.opti_grader.optigrader.R;

public class ExamScoreViewHolder extends RecyclerView.ViewHolder {

    public TextView examType, totalQues, score;

    public ExamScoreViewHolder(View itemView) {
        super(itemView);
        examType = (TextView) itemView.findViewById(R.id.txt_exam_type);
        totalQues = (TextView) itemView.findViewById(R.id.txt_total_quest);
        score = (TextView) itemView.findViewById(R.id.txt_score);
    }
}

package us.to.opti_grader.optigrader.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import us.to.opti_grader.optigrader.R;

public class LeaderBoardViewHolder extends RecyclerView.ViewHolder {

    public TextView id, score;

    public LeaderBoardViewHolder(View itemView) {
        super(itemView);
        id = (TextView) itemView.findViewById(R.id.student_id);
        score = (TextView) itemView.findViewById(R.id.txt_total_score);
    }
}

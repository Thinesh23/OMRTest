package us.to.opti_grader.optigrader.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import us.to.opti_grader.optigrader.Common.Common;
import us.to.opti_grader.optigrader.Model.Chat;
import us.to.opti_grader.optigrader.R;

public class ChatAdapter  extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    public static final int MSG_TITLE_LEFT = 0;
    public static final int MSG_TITLE_RIGHT = 1;

    private Context context;
    private List<Chat> chats;

    public ChatAdapter(Context context, List<Chat> chats){
        this.chats = chats;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == MSG_TITLE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Chat chat = chats.get(position);

        holder.show_message.setText(chat.getMessage());

        if (position == chats.size()-1){
            if(chat.isSeen()){
                holder.txt_seen.setText("Seen");

            } else {
                holder.txt_seen.setText("Delivered");
            }
        } else {
            holder.txt_seen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;
        public TextView txt_seen;

        public ViewHolder(View itemView){
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            txt_seen = itemView.findViewById(R.id.txt_seen);
        }
    }

    @Override
    public int getItemViewType(int position){
        if(chats.get(position).getSender().equals(Common.currentUser.getPhone())){
            return MSG_TITLE_RIGHT;
        } else {
            return MSG_TITLE_LEFT;
        }
    }
}

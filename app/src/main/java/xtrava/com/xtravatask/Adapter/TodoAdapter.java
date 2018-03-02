package xtrava.com.xtravatask.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import xtrava.com.xtravatask.R;
import xtrava.com.xtravatask.Model.TodoModel;

/**
 * Created by Fehoo on 3/2/2018.
 */

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ImgViewHolder> {

    ArrayList<TodoModel> itemList = new ArrayList<>();
    LayoutInflater inflater;
    Context context;

    public TodoAdapter(Context context, ArrayList<TodoModel> objects) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.itemList = objects;
    }

    @Override
    public TodoAdapter.ImgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.todo_row, parent, false);
        TodoAdapter.ImgViewHolder viewHolder = new TodoAdapter.ImgViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ImgViewHolder holder, int position) {
        TodoModel todo = itemList.get(position);
        holder.title.setText(todo.getTitle());
        if (todo.getCompleted().equals("true")) {
            holder.state.setText("Mission Completed");
            holder.state.setTextColor(Color.GREEN);
        } else {
            holder.state.setText("Mission In Progress");
            holder.state.setTextColor(Color.RED);
        }
        holder.itemView.setTag(holder);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void addItem(TodoModel todoModel) {
        itemList.add(todoModel);
        notifyItemInserted(itemList.size());
    }

    public void removeItem(int position) {
        itemList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, itemList.size());
    }

    static class ImgViewHolder extends RecyclerView.ViewHolder {
        TextView title, state;

        public ImgViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.todo_title);
            state = itemView.findViewById(R.id.todo_state);
        }
    }
}

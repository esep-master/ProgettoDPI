package it.bleb.dpi.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import it.bleb.dpi.R;
import it.bleb.dpi.database.entity.Task;

public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewAdapter.TaskViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Task item);
    }

    private List<Task> taskDetailsList;
    private Context context;
    private OnItemClickListener listener;

    public TaskRecyclerViewAdapter(List<Task> taskDetailsList, Context context, OnItemClickListener listener) {
        this.taskDetailsList = taskDetailsList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row_task, parent, false);
        return new TaskViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TaskViewHolder holder, int position) {
        Task task = this.taskDetailsList.get(position);
        holder.id.setText(String.valueOf(task.getIdApp()));
        holder.name.setText(task.getName());
        holder.settore.setText(task.getSettore());
        holder.bind(task, listener);
        //gestione dinamica dell'item della recycler view
        holder.highlight(holder, task);
    }


    @Override
    public int getItemCount() {
        return taskDetailsList.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {

        private TextView id, name, settore;

        public TaskViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.task_id);
            name = itemView.findViewById(R.id.task_name);
            settore = itemView.findViewById(R.id.task_settore);
        }

        public void bind(final Task item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

        public void highlight(TaskViewHolder holder, Task task) {
            holder.itemView.setClickable(true);

            //Background colour
            Drawable background = itemView.getBackground();
            if (background instanceof GradientDrawable) {
                GradientDrawable gradientDrawable = (GradientDrawable) background;
                if (task.isStarted() && !task.isCompleted()) {
                    gradientDrawable.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
                } else if (task.isCompleted()) {
                    gradientDrawable.setColor(ContextCompat.getColor(context, R.color.colorGps));
                    //Se intervento è terminato, non è più cliccabile
                    //holder.itemView.setClickable(false);
                } else {
                    gradientDrawable.setColor(ContextCompat.getColor(context, R.color.colorLightGray));
                }
            }
            //Text colour
            if (task.isStarted() || task.isCompleted()) {
                task.setCompleted(false);
                holder.id.setTextColor(context.getResources().getColor(R.color.colorBehindCard));
                holder.name.setTextColor(context.getResources().getColor(R.color.colorBehindCard));
                holder.settore.setTextColor(context.getResources().getColor(R.color.colorBehindCard));
            } else {
                holder.id.setTextColor(context.getResources().getColor(android.R.color.primary_text_light));
                holder.name.setTextColor(context.getResources().getColor(android.R.color.primary_text_light));
                holder.settore.setTextColor(context.getResources().getColor(android.R.color.primary_text_light));
            }
        }
    }
}

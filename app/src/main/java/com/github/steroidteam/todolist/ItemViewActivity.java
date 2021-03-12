package com.github.steroidteam.todolist;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.github.steroidteam.todolist.todo.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        Toolbar toolbar = findViewById(R.id.activity_itemview_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        String todoListTitle = "Example list";
        setTitle(todoListTitle);

        // Placeholder
        List<Task> items = Arrays.asList(
                new Task("Change passwords"),
                new Task("Replace old server"),
                new Task("Set up firewall"),
                new Task("Fix router"),
                new Task("Change passwords"),
                new Task("Replace old server"),
                new Task("Set up firewall")
                );

        TasksAdapter adapter = new TasksAdapter(this, items);
        ListView listView = findViewById(R.id.activity_itemview_itemlist);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);
        return true;
    }

    private class TasksAdapter extends ArrayAdapter<Task> {
        public TasksAdapter(Context context, List<Task> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Task task = getItem(position);

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_task_item, parent, false);
            }

            CheckBox taskView = convertView.findViewById(R.id.layout_task_checkbox);
            taskView.setText(task.getBody());
            taskView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Task task = (Task) buttonView.getTag();
                    // Notify database?
                }
            });

            return convertView;
        }
    }
}
package com.github.steroidteam.todolist;

import com.github.steroidteam.todolist.database.FileStorageDatabase;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.viewmodel.NoteViewModel;
import com.github.steroidteam.todolist.viewmodel.NoteViewModelFactory;
import com.github.steroidteam.todolist.viewmodel.TodoListViewModel;
import com.github.steroidteam.todolist.viewmodel.TodoViewModelFactory;

import org.junit.Test;

import static org.junit.Assert.assertThrows;

public class ViewModelFactoryTest {

    @Test
    public void todoViewModelFactoryRejectUnknownModel() {
        TodoViewModelFactory todoViewModelFactory = new TodoViewModelFactory(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    todoViewModelFactory.create(NoteViewModel.class);
                });
    }

    @Test
    public void noteViewModelFactoryRejectUnknownModel() {
        NoteViewModelFactory noteViewModelFactory = new NoteViewModelFactory(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    noteViewModelFactory.create(TodoListViewModel.class);
                });
    }
}

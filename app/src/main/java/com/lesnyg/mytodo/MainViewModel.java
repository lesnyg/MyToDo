package com.lesnyg.mytodo;

import android.app.Application;
import android.graphics.Paint;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.lesnyg.mytodo.repository.AppDatabase;
import com.lesnyg.mytodo.repository.Todo;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private AppDatabase db;
    public MainViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
    }

    public LiveData<List<Todo>> getTodo(){
        return db.todoDao().getAll();
    }
    public LiveData<List<Todo>> completeTodo(){
        return db.todoDao().getComplete();
    }

    public void update(List<Todo> todos){
        for (int i = 0; i < todos.size(); i++) {
            todos.get(i).setOrder(i);
        }
        db.todoDao().deleteAll();
        db.todoDao().insertTodo(todos);

    }

    public void deleteTodo(Todo todo){
        db.todoDao().deleteTodo(todo);

    }

    public void isChecked(Todo todo){

    }
}

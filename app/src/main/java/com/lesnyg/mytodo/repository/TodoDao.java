package com.lesnyg.mytodo.repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TodoDao {
    boolean isDone = true;


    @Query("SELECT * FROM todo ORDER BY `order`")
    LiveData<List<Todo>> getAll();

    @Insert
    void insertTodo(Todo... todo);

    @Insert
    void insertTodo(List<Todo> todo);

    @Query("DELETE FROM todo")
    void deleteAll();

    @Delete
    void deleteTodo(Todo todo);

    @Query("SELECT MAX(`order`) FROM todo ")
    int getOrderMax();

    @Query("SELECT * FROM todo WHERE isDone=1")
    LiveData<List<Todo>> getComplete();

}

package com.lesnyg.mytodo.repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TodoDao {
    @Query("SELECT * FROM todo ORDER BY `order`")
    LiveData<List<Todo>> getAll();

    @Insert
    void insertTodo(Todo...todo);

    @Insert
    void insertTodo(List<Todo> todo);

    @Query("DELETE FROM todo")
    void deleteAll();

    @Delete
    void deleteTodo(Todo todo);

}

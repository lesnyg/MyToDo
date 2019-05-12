package com.lesnyg.mytodo.ui;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.lesnyg.mytodo.MainViewModel;
import com.lesnyg.mytodo.R;
import com.lesnyg.mytodo.databinding.ItemTodoBinding;
import com.lesnyg.mytodo.repository.AppDatabase;
import com.lesnyg.mytodo.repository.Todo;

import java.nio.charset.MalformedInputException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    private boolean isSwapped = false;
    private MainViewModel mModel;
    private TodoAdapter mAdapter;
    private Todo mTodo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String getTime = sdf.format(date);
        TextView textView = view.findViewById(R.id.text_date);
        textView.setText(getTime);


        mModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        mAdapter = new TodoAdapter();
        recyclerView.setAdapter(mAdapter);

        view.findViewById(R.id.btn_todoadd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = view.findViewById(R.id.edit_todo);
                String todoTitle = editText.getText().toString();
                AppDatabase.getInstance(requireActivity()).todoDao().insertTodo(
                        new Todo(todoTitle, mAdapter.mItems.size())

                );
                editText.setText("");
            }
        });
        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                Collections.swap(mAdapter.getItems(),
                        viewHolder.getAdapterPosition(),
                        target.getAdapterPosition());

                mAdapter.notifyItemMoved(viewHolder.getAdapterPosition(),
                        target.getAdapterPosition());

                isSwapped = true;

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                mTodo = mAdapter.mItems.get(viewHolder.getAdapterPosition());
                mModel.deleteTodo(mTodo);

                isSwapped = true;

            }
        });
        touchHelper.attachToRecyclerView(recyclerView);
        mModel.getTodo().observe(this, todos -> mAdapter.setItems(todos));


        return view;
    }


    private static class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {
        interface OnTodoClickListener {
            void onTodoClicked(Todo model);
        }

        private OnTodoClickListener mListener;

        private List<Todo> mItems = new ArrayList<>();

        public TodoAdapter() {
        }

        public TodoAdapter(OnTodoClickListener listener) {
            mListener = listener;
        }

        public void setItems(List<Todo> items) {
            this.mItems = items;
            notifyDataSetChanged();
        }

        public Todo getItem(int position) {
            return mItems.get(position);
        }

        public List<Todo> getItems() {
            return mItems;
        }

        @NonNull
        @Override
        public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_todo, parent, false);
            final TodoViewHolder viewHolder = new TodoViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        final Todo item = mItems.get(viewHolder.getAdapterPosition());
                        mListener.onTodoClicked(item);
                    }
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
            Todo item = mItems.get(position);
            holder.binding.setTodo(item);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public static class TodoViewHolder extends RecyclerView.ViewHolder {
            ItemTodoBinding binding;

            public TodoViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemTodoBinding.bind(itemView);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isSwapped) {
            mModel.update(mAdapter.getItems());
        }
    }
}


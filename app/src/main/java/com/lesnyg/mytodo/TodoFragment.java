package com.lesnyg.mytodo;


import android.animation.Animator;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.lesnyg.mytodo.R;
import com.lesnyg.mytodo.databinding.ItemTodoBinding;
import com.lesnyg.mytodo.repository.AppDatabase;
import com.lesnyg.mytodo.repository.Todo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TodoFragment extends Fragment {
    private boolean isSwapped = false;
    private MainViewModel mModel;
    private TodoAdapter mAdapter;
    private Todo mTodo;
    private EditText mEditText;
    private String mTodoTitle;
    private SwipeController swipeController = null;
    public Boolean isDone ;

    public TodoFragment() {
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd");

        String getTime = sdf.format(date);
        String getTime2 = sdf2.format(date);
        TextView textView = view.findViewById(R.id.text_date);
        textView.setText(getTime);
        mEditText = view.findViewById(R.id.edit_todo);

        mModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        mAdapter=new TodoAdapter(new TodoAdapter.OnTodoClickListener() {
            @Override
            public void onTodoClicked(Todo model) {
                Toast.makeText(requireActivity(), "아이템 클릭됨", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChedkBoxClicked(Todo model) {
                mModel.update(mAdapter.getItems());
                LottieAnimationView lottie = (LottieAnimationView) view.findViewById(R.id.lottie);
                if(isDone){
                lottie.setVisibility(View.VISIBLE);
                lottie.playAnimation();}
                lottie.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lottie.setVisibility(View.GONE);
                    }
                });
            lottie.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                lottie.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

            }
        });

        recyclerView.setAdapter(mAdapter);


        view.findViewById(R.id.btn_todoadd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mTodoTitle = mEditText.getText().toString();
                AppDatabase.getInstance(requireActivity()).todoDao().insertTodo(
                        new Todo(mTodoTitle, mAdapter.mItems.size(),getTime2)

                );
                mEditText.setText("");
            }
        });

        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    default:
                        mTodoTitle = mEditText.getText().toString();
                        AppDatabase.getInstance(requireActivity()).todoDao().insertTodo(
                                new Todo(mTodoTitle, mAdapter.mItems.size(),getTime2)
                        );

                        mEditText.setText("");

                }
                return false;
            }
        });

        swipeController = new SwipeController(new SwipeControllerActions() {
            //삭제버튼
            @Override
            public void onRightClicked(int position) {
                mTodo = mAdapter.mItems.get(position);
                mModel.deleteTodo(mTodo);
            }

            //공유 버튼
            @Override
            public void onRightClicked2(int position) {
                mTodo = mAdapter.mItems.get(position);
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                String text = "원하는 텍스트를 입력하세요";
                intent.putExtra(Intent.EXTRA_TEXT, mTodo.getTitle());
                Intent chooser = Intent.createChooser(intent, "친구에게 공유하기");
                startActivity(chooser);
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });

        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
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

            }

            private void setItemsClickable(RecyclerView recyclerView,
                                           boolean isClickable) {
                for (int i = 0; i < recyclerView.getChildCount(); ++i) {
                    recyclerView.getChildAt(i).setClickable(isClickable);
                }
            }
        });
        touchHelper.attachToRecyclerView(recyclerView);
        mModel.getTodo().observe(this, (List<Todo> todos) -> {
            mAdapter.setItems(todos);
        });



        return view;





    }


    static class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

        interface OnTodoClickListener {
            void onTodoClicked(Todo model);
            void onChedkBoxClicked(Todo model);
        }

        private OnTodoClickListener mListener;

        public List<Todo> mItems = new ArrayList<>();

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

            viewHolder.binding.checkboxTodo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        final Todo item = mItems.get(viewHolder.getAdapterPosition());
                        item.setDone(!item.isDone());
                        mListener.onChedkBoxClicked(item);
                    }
                }
            });


            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
            Todo item = mItems.get(position);
            holder.binding.setTodo(item);

            if (mItems.get(position).isDone() == true) {
                holder.binding.textTodo.setPaintFlags(holder.binding.textTodo.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }else{
                holder.binding.textTodo.setPaintFlags(0);
            }
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

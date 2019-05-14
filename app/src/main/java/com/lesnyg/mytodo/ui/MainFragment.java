package com.lesnyg.mytodo.ui;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.lesnyg.mytodo.MainViewModel;
import com.lesnyg.mytodo.R;
import com.lesnyg.mytodo.SwipeControllerActions;
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
public class MainFragment extends Fragment {
    private boolean isSwapped = false;
    private MainViewModel mModel;
    private TodoAdapter mAdapter;
    private Todo mTodo;
    private EditText mEditText;
    private String mTodoTitle;

    enum ButtonsState {
        GONE,
        LEFT_VISIBLE,
        RIGHT_VISIBLE
    }

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

//        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                switch (actionId) {
//                    case EditorInfo.IME_ACTION_SEARCH:
//                        break;
//                    default:
//                        mEditText = view.findViewById(R.id.edit_todo);
//                        mTodoTitle = mEditText.getText().toString();
//                        AppDatabase.getInstance(requireActivity()).todoDao().insertTodo(
//                                new Todo(mTodoTitle, mAdapter.mItems.size())
//                        );
//                        mEditText.setText("");
//                }
//                return true;
//            }
//        });


        view.findViewById(R.id.btn_todoadd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText = view.findViewById(R.id.edit_todo);
                mTodoTitle = mEditText.getText().toString();
                AppDatabase.getInstance(requireActivity()).todoDao().insertTodo(
                        new Todo(mTodoTitle, mAdapter.mItems.size())

                );
                mEditText.setText("");
            }
        });
        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            private ButtonsState buttonShowedState = ButtonsState.GONE;
            private static final float buttonWidth = 150;
            private RectF buttonInstance = null;
            private RecyclerView.ViewHolder currentItemViewHolder = null;
            private SwipeControllerActions buttonsActions = null;


            @Override
            public int convertToAbsoluteDirection(int flags, int layoutDirection) {
                if (isSwapped) {
                    isSwapped = buttonShowedState != ButtonsState.GONE;
                    return 0;
                }
                return super.convertToAbsoluteDirection(flags, layoutDirection);
            }


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


            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    if (buttonShowedState != ButtonsState.GONE) {
                        if (buttonShowedState == ButtonsState.LEFT_VISIBLE)
                            dX = Math.max(dX, buttonWidth*2);
                        if (buttonShowedState == ButtonsState.RIGHT_VISIBLE)
                            dX = Math.min(dX, -buttonWidth*2);
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    } else {
                        setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                }

                if (buttonShowedState == ButtonsState.GONE) {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
                currentItemViewHolder = viewHolder;

                //                Bitmap icon;
//                View itemView = viewHolder.itemView;
//                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
//                    float width = height / 3;
//
//                    Paint p = new Paint();
//                    if (dX > 0) {
//                        p.setColor(Color.parseColor("#388E3C"));
//                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
//                        c.drawRect(background, p);
//                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.icons8_trash_50);
//                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
//                        c.drawBitmap(icon, null, icon_dest, p);
//                    } else if (dX < 0) {
//                        p.setColor(Color.parseColor("#D32F2F"));
//                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
//                        c.drawRect(background, p);
//                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.icons8_trash_50);
//                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
//                        c.drawBitmap(icon, null, icon_dest, p);
//                        c.drawBitmap(icon, null, icon_dest, p);
//
//                    }
//                }
                drawButtons(c, viewHolder);
            }

            private void setTouchListener(Canvas c, RecyclerView recyclerView1,
                                          RecyclerView.ViewHolder viewHolder,
                                          float dX,
                                          float dY,
                                          int actionState,
                                          boolean isCurrentlyActive) {
                recyclerView.setOnTouchListener((v, event) -> {
                    isSwapped = event.getAction() == MotionEvent.ACTION_CANCEL ||
                            event.getAction() == MotionEvent.ACTION_UP;
                    if (isSwapped) {
                        if (dX < -buttonWidth) {
                            buttonShowedState = ButtonsState.RIGHT_VISIBLE;
                        } else if (dX > buttonWidth) {
                            buttonShowedState = ButtonsState.LEFT_VISIBLE;
                        }

                        if (buttonShowedState != ButtonsState.GONE) {
                            setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                            setItemsClickable(recyclerView, false);
                        }
                    }
                    return false;
                });
            }

            private void setTouchDownListener(final Canvas c,
                                              final RecyclerView recyclerView,
                                              final RecyclerView.ViewHolder viewHolder,
                                              final float dX, final float dY,
                                              final int actionState,
                                              final boolean isCurrentlyActive) {
                recyclerView.setOnTouchListener((v, event) -> {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                    return false;
                });
            }

            private void setTouchUpListener(final Canvas c,
                                            final RecyclerView recyclerView,
                                            final RecyclerView.ViewHolder viewHolder,
                                            final float dX, final float dY,
                                            final int actionState, final boolean isCurrentlyActive) {
                recyclerView.setOnTouchListener((v, event) -> {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        super.onChildDraw(c, recyclerView, viewHolder, 0F, dY, actionState, isCurrentlyActive);
                        recyclerView.setOnTouchListener((v1, event1) -> false);
                        setItemsClickable(recyclerView, true);
                        isSwapped = false;

                        if (buttonsActions != null && buttonInstance != null && buttonInstance.contains(event.getX(), event.getY())) {
                            if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {
                                buttonsActions.onLeftClicked(viewHolder.getAdapterPosition());
                            } else if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
                                buttonsActions.onRightClicked(viewHolder.getAdapterPosition());
                            }
                        }

                        buttonShowedState = ButtonsState.GONE;
                        currentItemViewHolder = null;
                    }
                    return false;
                });
            }

            private void setItemsClickable(RecyclerView recyclerView,
                                           boolean isClickable) {
                for (int i = 0; i < recyclerView.getChildCount(); ++i) {
                    recyclerView.getChildAt(i).setClickable(isClickable);
                }
            }

            private void drawButtons(Canvas c, RecyclerView.ViewHolder viewHolder) {
                float buttonWidthWithoutPadding = buttonWidth;
                float corners = 16;
                Bitmap icon;

                View itemView = viewHolder.itemView;
                Paint p = new Paint();

                RectF leftButton = new RectF(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + buttonWidthWithoutPadding, itemView.getBottom());
                p.setColor(Color.parseColor("#388E3C"));
                c.drawRect(leftButton, p);
                icon = BitmapFactory.decodeResource(getResources(), R.drawable.trash_white);
                c.drawBitmap(icon, null, leftButton, p);

                RectF leftButton2 = new RectF(itemView.getLeft()+(buttonWidth*2), itemView.getTop(), itemView.getLeft()+(buttonWidth) , itemView.getBottom());
                p.setColor(Color.DKGRAY);
                c.drawRect(leftButton2, p);
                icon = BitmapFactory.decodeResource(getResources(), R.drawable.trash_white);
                c.drawBitmap(icon, null, leftButton2, p);



                RectF rightButton = new RectF(itemView.getRight() - buttonWidth, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                p.setColor(Color.RED);
                c.drawRect(rightButton, p);
                icon = BitmapFactory.decodeResource(getResources(), R.drawable.trash_white);
                c.drawBitmap(icon, null, rightButton, p);

                RectF rightButton2 = new RectF(itemView.getRight() - (buttonWidth*2), itemView.getTop(), itemView.getRight()-buttonWidth, itemView.getBottom());
                p.setColor(Color.GREEN);
                c.drawRect(rightButton2, p);
                icon = BitmapFactory.decodeResource(getResources(), R.drawable.trash_white);
                c.drawBitmap(icon, null, rightButton2, p);

                buttonInstance = null;
                if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {
                    buttonInstance = leftButton;
                } else if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
                    buttonInstance = rightButton;
                }
            }

            private void drawText(String text, Canvas c, RectF button, Paint p) {
                float textSize = 60;
                p.setColor(Color.WHITE);
                p.setAntiAlias(true);
                p.setTextSize(textSize);

                float textWidth = p.measureText(text);
                c.drawText(text, button.centerX() - (textWidth / 2), button.centerY() + (textSize / 2), p);
            }

            public void onDraw(Canvas c) {
                if (currentItemViewHolder != null) {
                    drawButtons(c, currentItemViewHolder);
                }
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


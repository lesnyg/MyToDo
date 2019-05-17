package com.lesnyg.mytodo;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lesnyg.mytodo.repository.AppDatabase;
import com.lesnyg.mytodo.repository.Todo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class CalendarFragment extends Fragment {
    private boolean isSwapped = false;
    private MainViewModel mModel;
    private TodoFragment.TodoAdapter mAdapter;
    private Todo mTodo;
    private EditText mEditText;
    private String mTodoTitle;
    private SwipeController swipeController = null;
    public Boolean isDone ;
    private String mCalendarDate;
    private int mOrderSize;


    public CalendarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        CalendarView calendar = view.findViewById(R.id.calendarView);
        TextView datetext = view.findViewById(R.id.text_date_calendar);
        mEditText = view.findViewById(R.id.edit_todo_calendar);
        mModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String getTime = sdf.format(date);
        datetext.setText(getTime);

        int mOrderSize = mModel.orderMaxSize();
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Toast.makeText(requireActivity(), year+"/"+(month+1)+"/"+dayOfMonth, Toast.LENGTH_SHORT).show();

                mCalendarDate = year+"/"+(month+1)+"/"+dayOfMonth;
                datetext.setText(mCalendarDate);

            }

        });

        view.findViewById(R.id.btn_todoadd_calendar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mTodoTitle = mEditText.getText().toString();
                AppDatabase.getInstance(requireActivity()).todoDao().insertTodo(
                        new Todo(mTodoTitle, mOrderSize+1,mCalendarDate)

                );
                mEditText.setText("");
            }
        });

//        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                switch (actionId) {
//                    default:
//                        mTodoTitle = mEditText.getText().toString();
//                        AppDatabase.getInstance(requireActivity()).todoDao().insertTodo(
//                                new Todo(mTodoTitle, mAdapter.mItems.size(),getTime2)
//                        );
//
//                        mEditText.setText("");
//
//                }
//                return false;
//            }
//        });

        return view;
    }



}

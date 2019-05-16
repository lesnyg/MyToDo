package com.lesnyg.mytodo;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lesnyg.mytodo.databinding.ItemCompleteBinding;
import com.lesnyg.mytodo.repository.Todo;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class completeFragment extends Fragment {
    MainViewModel mModel;
    CompleteAdapter mAdapter;

    public completeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complete, container, false);

        mModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.complete_recycler_view);
        mAdapter = new CompleteAdapter();
        recyclerView.setAdapter(mAdapter);

        mModel.completeTodo().observe(this, (List<Todo> todos) -> {
            mAdapter.setItems(todos);
        });



        return view;
    }

    private static class CompleteAdapter extends RecyclerView.Adapter<CompleteAdapter.CompleteViewHolder> {
        interface OnCompleteClickListener {
            void onCompleted(Todo model);
        }

        private OnCompleteClickListener mListener;

        private List<Todo> mItems = new ArrayList<>();

        public CompleteAdapter() {
        }

        public CompleteAdapter(OnCompleteClickListener listener) {
            mListener = listener;
        }

        public void setItems(List<Todo> items) {
            this.mItems = items;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public CompleteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_complete, parent, false);
            final CompleteViewHolder viewHolder = new CompleteViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        final Todo item = mItems.get(viewHolder.getAdapterPosition());
                        mListener.onCompleted(item);
                    }
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull CompleteViewHolder holder, int position) {
            Todo item = mItems.get(position);
            holder.binding.setTodo(item);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public static class CompleteViewHolder extends RecyclerView.ViewHolder {
            ItemCompleteBinding binding;

            public CompleteViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemCompleteBinding.bind(itemView);
        }
        }
    }

}

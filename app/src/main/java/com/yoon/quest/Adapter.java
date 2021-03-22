package com.yoon.quest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.yoon.quest.databinding.ItemBinding;

public class Adapter extends ListAdapter<DataModel, Adapter.ItemViewHolder> implements ItemTouchHelperCallback.ItemTouchHelperListener {

    public Adapter() {
        super(DataModel.DIFF_CALLBACK);
    }

    private DataModel mDataModel;

    private Listener mListener;

    public void setListener(Listener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new Adapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        mDataModel = getItem(position);
        if (mDataModel != null) {
            holder.onBind(mDataModel);
        }
    }

    @Override
    public boolean onItemMove(int from_position, int to_position) {
        return false;
    }

    @Override
    public void onItemSwipe(int position) {
        if (mListener != null) {
            mListener.eventRemoveItem(getItem(position));
            notifyDataSetChanged();
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private ItemBinding binding;

        ItemViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        void onBind(DataModel DataModel) {
            binding.title.setText(DataModel.getTitle());
            binding.content.setText(DataModel.getContent() + "");
            binding.id.setText(DataModel.getId() + "");
        }
    }

    public interface Listener {
        public void eventRemoveItem(DataModel dataModel);
    }
}
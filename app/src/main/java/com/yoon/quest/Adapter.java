package com.yoon.quest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.yoon.quest.databinding.RecyclerEntityItemBinding;

public class Adapter extends ListAdapter<DataModel, Adapter.ItemViewHolder> {

    public Adapter() {
        super(DataModel.DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_entity_item, parent, false);
        return new Adapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        DataModel DataModel= getItem(position);
        if(DataModel!=null) {
            holder.onBind(DataModel);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private RecyclerEntityItemBinding binding;
        ItemViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
        void onBind(DataModel DataModel) {
            binding.title.setText(DataModel.getTitle());
            binding.id.setText(DataModel.getId()+"");
        }
    }
}
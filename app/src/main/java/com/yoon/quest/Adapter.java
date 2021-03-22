package com.yoon.quest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.yoon.quest.databinding.ItemBinding;

import java.util.Collections;
import java.util.List;

public class Adapter extends ListAdapter<DataModel, Adapter.ItemViewHolder> implements ItemTouchHelperCallback.ItemTouchHelperListener {

    private Context mContext;

    public Adapter(Context context) {
        super(DataModel.DIFF_CALLBACK);
        mContext = context;
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

        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        // 마지막 아이템에 마진추가.
        if (position == AppData.GetInstance().mDataCnt - 1) {
            mParams.topMargin = 30;
            mParams.leftMargin = 30;
            mParams.rightMargin = 30;
            mParams.bottomMargin = 40;
        } else {
            mParams.topMargin = 30;
            mParams.leftMargin = 30;
            mParams.rightMargin = 30;
        }
        holder.itemView.setLayoutParams(mParams);
    }

    @Override
    public boolean onItemMove(int from_position, int to_position) {
//        Collections.swap((List<?>) mDataModel, from_position, to_position);
//        notifyItemMoved(from_position, to_position);
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

            // 클릭이벤트
            itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    int position = getAdapterPosition();
                    mListener.eventItemClick(getItem(position));
                }
            });
        }

        void onBind(DataModel dataModel) {
            binding.title.setText(dataModel.getTitle());
            binding.content.setText(dataModel.getContent() + "");
            setContainerColor(itemView, dataModel.getColor());
        }
    }

    // 선택한 컬러로 카드뷰 띄우기
    @SuppressLint("ResourceType")
    private void setContainerColor(View v, String color) {
        if (color.equals("#" + Integer.toHexString(ContextCompat.getColor(mContext, R.color.schedule1) & 0x00ffffff))) {
            v.setBackgroundResource(R.drawable.card_item_box1);
        } else if (color.equals("#" + Integer.toHexString(ContextCompat.getColor(mContext, R.color.schedule2) & 0x00ffffff))) {
            v.setBackgroundResource(R.drawable.card_item_box2);
        } else if (color.equals("#" + Integer.toHexString(ContextCompat.getColor(mContext, R.color.schedule3) & 0x00ffffff))) {
            v.setBackgroundResource(R.drawable.card_item_box3);
        } else if (color.equals("#" + Integer.toHexString(ContextCompat.getColor(mContext, R.color.schedule4) & 0x00ffffff))) {
            v.setBackgroundResource(R.drawable.card_item_box4);
        } else if (color.equals("#" + Integer.toHexString(ContextCompat.getColor(mContext, R.color.schedule5) & 0x00ffffff))) {
            v.setBackgroundResource(R.drawable.card_item_box5);
        } else if (color.equals("#" + Integer.toHexString(ContextCompat.getColor(mContext, R.color.schedule6) & 0x00ffffff))) {
            v.setBackgroundResource(R.drawable.card_item_box6);
        } else if (color.equals("#" + Integer.toHexString(ContextCompat.getColor(mContext, R.color.schedule7) & 0x00ffffff))) {
            v.setBackgroundResource(R.drawable.card_item_box7);
        } else if (color.equals("#" + Integer.toHexString(ContextCompat.getColor(mContext, R.color.schedule8) & 0x00ffffff))) {
            v.setBackgroundResource(R.drawable.card_item_box18);
        }
    }

    public interface Listener {
        public void eventRemoveItem(DataModel dataModel);

        public void eventItemClick(DataModel dataModel);
    }
}
package com.yoon.quest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private ItemTouchHelperListener listener;
    private Paint p = new Paint();
    private boolean mOrderChanged;


    // 개구리 애니메이션
    private ImageView mFrogImage;
    private AnimationDrawable mJumpFrogAnim = null;

    // 백그라운드 이미지 애니메이션
    private ImageView mBGImage;
    private AnimationDrawable mBGAnim = null;

    public ItemTouchHelperCallback(ItemTouchHelperListener listener) {
        this.listener = listener;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
//        int drag_flags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int drag_flags = ItemTouchHelper.ACTION_STATE_IDLE;
        int swipe_flags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT |ItemTouchHelper.ACTION_STATE_IDLE;
//        int swipe_flags = ItemTouchHelper.LEFT;
//        return makeMovementFlags(drag_flags, swipe_flags);
        return makeMovementFlags(drag_flags, swipe_flags);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return listener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
//        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        mOrderChanged = true;
        if (direction == ItemTouchHelper.LEFT) {
            listener.onItemSwipe(viewHolder.getAdapterPosition());
        }
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            final View foregroundView = getView(viewHolder);
            getDefaultUIUtil().onSelected(foregroundView);
        }else{

            if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && mOrderChanged) {
                frogAnimOff();
                mOrderChanged = false;
            }

        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        final View foregroundView = getView(viewHolder);
        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);


        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
            mFrogImage = ((Adapter.ItemViewHolder) viewHolder).binding.frog;
            mBGImage = ((Adapter.ItemViewHolder) viewHolder).binding.backgroundImage;
            mJumpFrogAnim = (AnimationDrawable) mFrogImage.getBackground();
            mBGAnim = (AnimationDrawable) mBGImage.getBackground();
            frogAnimOn();
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
        final View foregroundView = getView(viewHolder);
        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final View foregroundView = getView(viewHolder);
        getDefaultUIUtil().clearView(foregroundView);
    }

    private View getView(RecyclerView.ViewHolder viewHolder) {
        if (((Adapter.ItemViewHolder) viewHolder) == null
                || ((Adapter.ItemViewHolder) viewHolder).binding == null) {
            return null;
        }
        View mmGridView = ((Adapter.ItemViewHolder) viewHolder).binding.linearItem;
        if (mmGridView != null) {
            return mmGridView;
        } else {
            return null;
        }
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    public interface ItemTouchHelperListener {
        boolean onItemMove(int from_position, int to_position);

        void onItemSwipe(int position);
    }

    private void frogAnimOn(){
        mFrogImage.post(new Runnable() {
            @Override
            public void run() {
                mJumpFrogAnim.start();
            }
        });

        mBGImage.post(new Runnable() {
            @Override
            public void run() {
                mBGAnim.start();
            }
        });
    }

    private void frogAnimOff(){
        mFrogImage.post(new Runnable() {
            @Override
            public void run() {
                mJumpFrogAnim.stop();
            }
        });
        mBGImage.post(new Runnable() {
            @Override
            public void run() {
                mBGAnim.stop();
            }
        });
    }
}
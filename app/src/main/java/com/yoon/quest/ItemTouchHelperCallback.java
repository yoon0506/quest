package com.yoon.quest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private ItemTouchHelperListener listener;
    private Paint p = new Paint();

    public ItemTouchHelperCallback(ItemTouchHelperListener listener) {
        this.listener = listener;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int drag_flags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipe_flags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
//        int swipe_flags = ItemTouchHelper.LEFT;
//        return makeMovementFlags(drag_flags, swipe_flags);
        return makeMovementFlags(0, swipe_flags);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return listener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
    }

//    @Override
//    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
//        getDefaultUIUtil().clearView(getView(viewHolder));
//    }
//
//    @Override
//    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
//        if(getView(viewHolder) != null) {
//            getDefaultUIUtil().onSelected(getView(viewHolder));
//        }
//    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (direction == ItemTouchHelper.LEFT) {
            listener.onItemSwipe(viewHolder.getAdapterPosition());
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        Bitmap icon;
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            View itemView = viewHolder.itemView;
            float height = (float) itemView.getBottom() - (float) itemView.getTop();
            float width = height / 3;

            if (dX > 0) {
                //오른쪽으로 밀었을 때
                /*
                 * icon 추가할 수 있음.
                 */
                icon = BitmapFactory.decodeResource(AppData.GetInstance().mActivity.getResources(), R.drawable.frog); //vector 불가!
                RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                c.drawBitmap(icon, null, icon_dest, p);
            } else {
//                LayoutInflater inflater = (LayoutInflater) AppData.GetInstance().mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                inflater.inflate(R.layout.item_background, getView(viewHolder), true);


                p.setColor(AppData.GetInstance().mActivity.getResources().getColor(R.color.indigo));
                icon = BitmapFactory.decodeResource(AppData.GetInstance().mActivity.getResources(), R.drawable.frog); //vector 불가!

                RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
//                RectF background =  new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop(), (float) itemView.getRight() - width, (float) itemView.getBottom());
//                RectF background =  new RectF((float)908, (float) 30, (float) 979, (float) 242);
                c.drawBitmap(icon, null, background, p);
//                c.drawRect(background, p);

                // 개구리 과도기
//                View view = getView(viewHolder);
//
//                getDefaultUIUtil().onDraw(
//                        c,
//                        recyclerView,
//                        view,
//                        dX,
//                        dY,
//                        actionState,
//                        isCurrentlyActive
//                );
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }


    private View getView(RecyclerView.ViewHolder viewHolder) {
        if(((Adapter.ItemViewHolder) viewHolder) == null
                || ((Adapter.ItemViewHolder) viewHolder).binding == null){
            return null;
        }
        View mmGridView = ((Adapter.ItemViewHolder) viewHolder).binding.linearItem;
        if(mmGridView != null){
            return mmGridView;
        }else{
            return null;
        }
    }

    public interface ItemTouchHelperListener {
        boolean onItemMove(int from_position, int to_position);

        void onItemSwipe(int position);
    }
}

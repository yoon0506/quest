package com.yoon.quest;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import timber.log.Timber;

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private ItemTouchHelperListener listener;

    public ItemTouchHelperCallback(ItemTouchHelperListener listener) {
        this.listener = listener;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int drag_flags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipe_flags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
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

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        if (direction == ItemTouchHelper.LEFT) {
            listener.onItemSwipe(viewHolder.getAdapterPosition());
            Timber.tag("checkCheck").d("direction : %s", ItemTouchHelper.LEFT);
        }else if (direction == ItemTouchHelper.RIGHT) {
            Timber.tag("checkCheck").d("direction : %s", ItemTouchHelper.RIGHT);
        }else if (direction == ItemTouchHelper.UP) {
            Timber.tag("checkCheck").d("direction : %s", ItemTouchHelper.UP);
        }else if (direction == ItemTouchHelper.DOWN) {
            Timber.tag("checkCheck").d("direction : %s", ItemTouchHelper.DOWN);
        }
    }

    public interface ItemTouchHelperListener {
        boolean onItemMove(int form_position, int to_position);
        void onItemSwipe(int position);
    }
}

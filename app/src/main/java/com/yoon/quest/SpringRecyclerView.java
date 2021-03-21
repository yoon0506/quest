package com.yoon.quest;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Implemented a custom view that can have a scrollview with a drop-down bounce
 */
public class SpringRecyclerView extends RecyclerView {
    private View inner;//child
    private float y;//coordinates
    private Rect normal = new Rect();//rectangle blank

    public SpringRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /***
     * View generation based on xml is complete.
     This function is called at the end of the generated view,After all the subviews have been added.
     Even if the subclass overrides onfinishinflate
     * Method, should also call the method of the parent class,Enable the method to execute.
     */
    @Override
    protected void onFinishInflate() {
        if (getChildCount() > 0) {
            inner = getRootView();
        }
        super.onFinishInflate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (inner != null) {
            commonTouchEvent(ev);
        }
        return super.onTouchEvent(ev);
    }

    /***
     * Touch event
     *
     * @param ev
     */
    public void commonTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                y = ev.getY();//Get the click y coordinate
                break;
            case MotionEvent.ACTION_UP:
                if (isNeedAnimation()) {
                    animation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                final float prey = y;
                float nowy = ev.getY();
                int deltay = (int) (prey - nowy);//Get the sliding distance
                y = nowy;
//will not scroll when scrolled to the top or bottom,Now move the layout
                if (isNeedMove()) {
                    if (normal.isEmpty()) {
//fill the rectangle,Purpose:Just tell this:I have it now,Remember to perform a regression animation when you release it.
                        normal.set(inner.getLeft(), inner.getTop(), inner.getRight(), inner.getBottom());
                    }
//mobile layout
                    inner.layout(inner.getLeft(), inner.getTop() - deltay / 2, inner.getRight(), inner.getBottom() - deltay / 2);
                }
                break;
            default:
                break;
        }
    }

    /***
     * Turn on animation movement
     */
    public void animation() {
//start moving animation
        TranslateAnimation ta = new TranslateAnimation(0, 0, inner.getTop(), normal.top);
        ta.setDuration(300);
        inner.startAnimation(ta);
//set back to normal layout position
        inner.layout(normal.left, normal.top, normal.right, normal.bottom);
        normal.setEmpty();//Empty the rectangle
    }

    /***
     * Do you need to enable animation
     *<p>
     * If the rectangle is not empty,Returns true, otherwise returns false.
     *
     * @return
     */
    public boolean isNeedAnimation() {
        return !normal.isEmpty();
    }

    /***
     * Do you need to move the layout inner.getmeasuredheight ():Get the height of the control
     * getheight ():Gets the height of the current control displayed on the screen
     *
     * @return
     */
    public boolean isNeedMove() {
        int offset = inner.getMeasuredHeight() - getHeight();
        int scrolly = getScrollY();
//0 is the top and the back is the bottom
        if (scrolly == 0 || scrolly == offset) {
            return true;
        }
        return false;
    }
}
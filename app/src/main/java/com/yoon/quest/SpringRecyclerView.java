package com.yoon.quest;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;

import androidx.recyclerview.widget.RecyclerView;

import timber.log.Timber;

/**
 * Implemented a custom view that can have a scrollview with a drop-down bounce
 */
public class SpringRecyclerView extends RecyclerView {
    private View mView;//child
    private float mY;//coordinates
    private float mDeltaY;//coordinates
    private Rect mNormal = new Rect();//rectangle blank
    private boolean mIsDown = false;
    private boolean mIsTop = false;

    private Listener mListener;

    public void setListener(Listener listener) {
        mListener = listener;
    }

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
        mView = getRootView();
        super.onFinishInflate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mView != null && getScrollState() == RecyclerView.SCROLL_STATE_DRAGGING) {    //SCROLL_STATE_IDLE : 스크롤하고있지 않는 상태 // SCROLL_STATE_DRAGGING : 스크롤 하고있는 상태
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
                mY = ev.getY();//Get the click mY coordinate
                break;
            case MotionEvent.ACTION_UP:
                if (isNeedAnimation()) {
                    animation();
                    Timber.tag("checkCheck").d("mIsDown : %s", mIsDown);
                    Timber.tag("checkCheck").d("mIsTop : %s", mIsTop);
//                    if (mListener != null && mIsDown && mIsTop) {
                    if (mListener != null && mIsDown ) {
                        mListener.eventAddWrite("add");
                        mIsDown = false;
                    }
//                    if (mListener != null) {
//                        mListener.eventAddWrite("add");
//                    }
//                    Timber.tag("checkCheck").d("mDeltaY : %s", mDeltaY);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                final float mPreY = mY;
                float nowY = ev.getY();
                int deltaY = (int) (mPreY - nowY);//Get the sliding distance
                mY = nowY;
                mDeltaY = deltaY;
//                Timber.tag("checkCheck").d("nowY : %s", nowY);
//                Timber.tag("checkCheck").d("deltaY : %s", deltaY);

//will not scroll when scrolled to the top or bottom,Now move the layout
                if (isNeedMove()) {
                    if (mNormal.isEmpty()) {
//fill the rectangle,Purpose:Just tell this:I have it now,Remember to perform a regression animation when you release it.
                        mNormal.set(mView.getLeft(), mView.getTop(), mView.getRight(), mView.getBottom());
                    }
//mobile layout
                    mView.layout(mView.getLeft(), mView.getTop() - deltaY / 2, mView.getRight(), mView.getBottom() - deltaY / 2);
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
        TranslateAnimation ta = new TranslateAnimation(0, 0, mView.getTop(), mNormal.top);
        ta.setDuration(300);
        mView.startAnimation(ta);
//set back to mNormal layout position
        mView.layout(mNormal.left, mNormal.top, mNormal.right, mNormal.bottom);
        mNormal.setEmpty();//Empty the rectangle
    }

    /***
     * Do you need to enable animation
     *<p>
     * If the rectangle is not empty,Returns true, otherwise returns false.
     *
     * @return
     */
    public boolean isNeedAnimation() {
        return !mNormal.isEmpty();
    }

    /***
     * Do you need to move the layout mView.getmeasuredheight ():Get the height of the control
     * getheight ():Gets the height of the current control displayed on the screen
     *
     * @return
     */
    public boolean isNeedMove() {
        int offset = mView.getMeasuredHeight() - getHeight();
        int scrollY = getScrollY();
//0 is the top and the back is the bottom
        if (scrollY == 0 || scrollY == offset) {
            return true;
        }
        return false;
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        if (dy >= 0) {  // 현재 위치를 기준으로 아래로 스크롤 함
            mIsDown = true;
        }
    }

//    @Override
//    public void onScrollStateChanged(int state) {
//        if (canScrollVertically(-1)) {
//            mIsTop = true;
//        } else if (canScrollVertically(1)) {
//            mIsTop = false;
//        } else {
//            mIsTop = false;
//        }
////        super.onScrollStateChanged(state);
//    }

    public interface Listener {
        public void eventAddWrite(String event);
    }
}
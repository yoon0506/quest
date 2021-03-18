package com.yoon.quest._Library._View;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.webkit.WebView;

/**
 * Created by develoer004 on 2018. 3. 7..
 */

public class _WebView extends WebView {
    Context context;
    GestureDetector gd;
    int gesture_stx,gesture_sty;

    public _WebViewListener listener = null;

    public _WebView(Context context) {
        super(context);
        this.context = context;
        gd = new GestureDetector(context, onGestureListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return (gd.onTouchEvent(event) || super.onTouchEvent(event));
    }

    private final GestureDetector.SimpleOnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return super.onDoubleTap(e);
        }
        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return super.onDoubleTapEvent(e);
        }
        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float deltax,deltay,velo;
            deltax = Math.abs(e1.getRawX()-e2.getRawX());
            deltay = Math.abs(e1.getRawY()-e2.getRawY());
            velo = Math.abs(velocityX);

            //pref_browser_gesturevelo is how fast finger moves.
            //pref_browser_gesturevelo set to 350 as default in my app
            if (deltax > 100 && deltay < 90 && velo > 100) {
                if (e1.getRawX() > e2.getRawX()) {
                    if(listener != null) listener.didGestureR2L();
                } else if(e1.getRawX() < e2.getRawX()){
                    if(listener != null) listener.didGestureL2R();
                }
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
        }
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
        @Override
        public void onShowPress(MotionEvent e) {
            super.onShowPress(e);
        }
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return super.onSingleTapConfirmed(e);
        }
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return super.onSingleTapUp(e);
        }
    };

    public interface _WebViewListener {
        public void didGestureL2R();
        public void didGestureR2L();
    }
}

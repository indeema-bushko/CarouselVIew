package com.indeema.carouselview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.indeema.carouselview.R;
import com.indeema.carouselview.utils.AnimationUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Created by Kostiantyn Bushko on 12/24/16.
 */

public class ItemScrollerWidget extends FrameLayout {

    public interface OnCarouselCallbackListener {
        void onItemsScroll(int selectedItemIndex, int unselectedItemIndex);
        void onItemClick(int index);
    }

    private OnCarouselCallbackListener mOnCarouselCallbackListener;

    public void setOnCarouselCallbackListener(OnCarouselCallbackListener onCarouselCallbackListener) {
        mOnCarouselCallbackListener = onCarouselCallbackListener;
    }

    private static final String TAG = ItemScrollerWidget.class.getSimpleName();

    private long mScaleDuration = 100L;
    private long mMoveDuration = 100L;

    protected Context mContext;
    private FrameLayout mContainer;

    protected List<? extends View> mListItems = new ArrayList<>();

    protected boolean doneInit = false;

    private float itemHeight = 0;
    private float itemWidth = 0;

    private float centerAnchor = 0;

    private int mVisibleItemsOnScreen = 1;

    private int selectedItemID = 0;


    public ItemScrollerWidget(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public ItemScrollerWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public ItemScrollerWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }


    private void init() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.carusel_layout, this);
        mContainer = (FrameLayout) view.findViewById(R.id.carousel_container);
    }

    public void setVisibleItemsOnScreen(int visibleItemsOnScreen) {
        mVisibleItemsOnScreen = visibleItemsOnScreen;
    }

    public int getVisibleItemsOnScreen() {
        return mVisibleItemsOnScreen;
    }

    public void addItems(List<? extends View> listItems) {
        mListItems = listItems;
        for(int i = 0; i < listItems.size(); i++) {
            View view = listItems.get(i);
            mContainer.addView(view);
            view.setVisibility(INVISIBLE);
            view.setId(i);
            Log.d(TAG, "Add item view : itemId = " + view.getId());
        }
    }


    public int getCurrentSelectedItem() {
        return selectedItemID;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        itemHeight = h;
        itemWidth = w / mVisibleItemsOnScreen;

        if (mVisibleItemsOnScreen % 2 > 0) {
            centerAnchor = w / 2;
        } else {
            centerAnchor = itemWidth / 2;
        }

        Log.d(TAG, " width = " + w + " height = " + h + ", itemWidth = " + itemWidth + ", itemHeight = " + itemHeight + "centerAnchor = " + centerAnchor);
        if (mListItems.size() > 0) {
            Iterator<? extends View>iterator = mListItems.iterator();
            int index = 0;
            while (iterator.hasNext()) {
                View view = iterator.next();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width = (int) itemWidth;
                view.setLayoutParams(layoutParams);
                view.setTranslationX(index * itemWidth);
                currentSelected(view, index);
                view.setVisibility(VISIBLE);
                Log.d(TAG, "Add item : index = " + index + ", tX = " + view.getTranslationX());
                index ++;
            }
        }
        doneInit = true;
    }

    float touchDown = 0;
    float mDelta = 0;
    boolean isMoved = false;

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int motionMask = motionEvent.getActionMasked();
        switch (motionMask) {
            case MotionEvent.ACTION_MOVE: {
                if (!isAnimate) {
                    mDelta = motionEvent.getX() - touchDown;
                    if ((selectedItemID == 0 && mDelta > 0) || (selectedItemID == mListItems.size()-1 && mDelta < 0))
                        break;
                    if (Math.abs(mDelta) >= (getWidth() * 0.05f)) {
                        animateMove();
                        isMoved = true;
                        touchDown = motionEvent.getX();
                    }
                }
                break;
            }
            case MotionEvent.ACTION_DOWN: {
                touchDown = (int) motionEvent.getX();
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (!isMoved && !isAnimate) {
                    int itemClick = getItemByScreenPosition((int)motionEvent.getX(), (int)motionEvent.getY());
                    Log.i(TAG,"ITEM CLICK = " + itemClick + " selected = " + selectedItemID);
                    if (itemClick >= 0 && itemClick == selectedItemID) {
                        onItemClick(mListItems.get(itemClick));
                    } else if(itemClick == -1) {
                        if (selectedItemID == 0) {
                            prev();
                        } else if (selectedItemID == (mListItems.size() -1)) {
                            next();
                        }
                    } else {
                        if (motionEvent.getX() > centerAnchor && (selectedItemID != mListItems.size() -1)) {
                            mDelta = -(getWidth() * 0.05f);
                            animateMove();
                        } else if (motionEvent.getX() < centerAnchor && (selectedItemID != 0)){
                            mDelta = (getWidth() * 0.05f);
                            animateMove();
                        }
                    }
                }
                isMoved = false;
                touchDown = 0;
                break;
            }
            default: break;
        }
        return true;
    }

    boolean isAnimate = false;
    private void animateMove() {
        isAnimate = true;
        Iterator<? extends View> iterator = mListItems.iterator();
        int position = 0;
        while (iterator.hasNext()) {
            final View view = iterator.next();
            final float direction = mDelta > 0 ? itemWidth : -itemWidth;
            position++;
            final int positionF = position;

            AnimationUtils.moveViewHorizontally(view,
                    (int)view.getTranslationX(),
                    (int)(view.getTranslationX() + direction),
                    mMoveDuration,
                    new AnimationUtils.AnimationEndListener() {
                        @Override
                        public void onAnimationStart() {}

                        @Override
                        public void onAnimationEnd() {
                            currentSelected(view, positionF);
                            isAnimate = false;
                        }
                    });
        }
    }

    private void onItemClick(final View view) {
        if (isAnimate) {
            if (mOnCarouselCallbackListener != null)
                mOnCarouselCallbackListener.onItemClick(selectedItemID);
            return;
        }
        isAnimate = true;
        AnimationUtils.scaleView(view, 0.8f, 0.8f, 0, mScaleDuration, new AnimationUtils.AnimationEndListener() {
            @Override
            public void onAnimationStart() { }

            @Override
            public void onAnimationEnd() {
                AnimationUtils.scaleView(view, 1.0f, 1.0f, 0, mScaleDuration, new AnimationUtils.AnimationEndListener() {
                    @Override
                    public void onAnimationStart() { }

                    @Override
                    public void onAnimationEnd() {
                        isAnimate = false;
                        if (mOnCarouselCallbackListener != null)
                            mOnCarouselCallbackListener.onItemClick(selectedItemID);
                    }
                });
            }
        });
    }

    private boolean currentSelected(View view, int position) {
        if (centerAnchor == 0)
            return false;
        boolean retValue;
        if (view.getTranslationX() < centerAnchor && (view.getTranslationX() + itemWidth) > centerAnchor) {
            if (view.getId() != selectedItemID) {
                int unselectedItemIndex = selectedItemID;
                mListItems.get(unselectedItemIndex).setSelected(false);
                selectedItemID = view.getId();
                view.setSelected(true);
                Log.d(TAG," CURRENT SELECTED = " + view.getId() + " UNSELECTED = " + unselectedItemIndex);
                AnimationUtils.scaleView(view, 1.0f, 1.0f, 0, mScaleDuration);
                if (mOnCarouselCallbackListener != null && (selectedItemID != unselectedItemIndex)) {
                    mOnCarouselCallbackListener.onItemsScroll(selectedItemID, unselectedItemIndex);
                }
                retValue = true;
            } else {
                retValue = false;
            }
        } else {
            AnimationUtils.scaleView(view, 0.8f, 0.8f, 0, mScaleDuration);
            retValue = false;
        }
        return retValue;
    }

    private int getItemByScreenPosition(int x, int y) {
        Iterator<? extends View>iterator = mListItems.iterator();
        while (iterator.hasNext()) {
            View view = iterator.next();
            if (view.getTranslationX() < x && (x < (view.getTranslationX() + itemWidth))) {
                return view.getId();
            }
        }
        return -1;
    }

    public void next() {
        if ((selectedItemID != 0)){
            mDelta = (getWidth() * 0.05f);
            animateMove();
        }
    }

    public void prev() {
        if (selectedItemID != (mListItems.size() -1)) {
            mDelta = -(getWidth() * 0.05f);
            animateMove();
        }
    }
}
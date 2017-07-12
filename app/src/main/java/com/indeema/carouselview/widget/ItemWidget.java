package com.indeema.carouselview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.indeema.carouselview.R;

/**
 * Created by Kostiantyn Bushko on 12/24/16.
 */

public class ItemWidget extends LinearLayout implements ItemWidgetInterface {

    private static final String TAG = ItemWidget.class.getSimpleName();

    private Context mContext;
    private ImageView mImageView;
    private LinearLayout mTopContainer;
    private TextView mTitle;
    private TextView mSubTitle;

    private String mTitleString = "";
    private String mSubTitleString = "";

    private int mContainerWidth = -1;

    private int mSelectedIconResourceId = -1;
    private int mUnselectedIconResourceId = -1;

    public ItemWidget(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public ItemWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public ItemWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mTopContainer != null && mContainerWidth > 0) {
            ViewGroup.LayoutParams layoutParams = mTopContainer.getLayoutParams();
            layoutParams.width = mContainerWidth;
            mTopContainer.setLayoutParams(layoutParams);
        }
        Log.d(TAG, "Item ID = " + getId() + "ON SIZE CHANGE  w = " + w + ", h = " + h);
    }

    private void init() {
        LayoutInflater layoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = layoutInflater.inflate(R.layout.item_layout,this);
        mTitle = (TextView)rootView.findViewById(R.id.tv_title);
        mSubTitle = (TextView)rootView.findViewById(R.id.tv_sub_title);

        mImageView = (ImageView)rootView.findViewById(R.id.iv_icon);
        if (mImageView != null && mSelectedIconResourceId > 0)
            mImageView.setImageResource(mSelectedIconResourceId);

        mTitle.setText(mTitleString);
        mSubTitle.setText(mSubTitleString);

    }

    public void setIcons(int iconSelectedResourceId, int iconUnselecteResourceId) {
        mSelectedIconResourceId = iconSelectedResourceId;
        mUnselectedIconResourceId = iconUnselecteResourceId;
        if (mImageView != null)
            mImageView.setImageResource(mUnselectedIconResourceId);
    }

    @Override
    public void setItemSelected(boolean isSelected) {
        if (isSelected) {
            mImageView.setImageResource(mSelectedIconResourceId);
        } else {
            mImageView.setImageResource(mUnselectedIconResourceId);
        }
    }

    public void setTitleText(String titleText) {
        mTitleString = titleText;
        if (mTitle != null)
            mTitle.setText(mTitleString);
    }

    public void setSubTitleText(String subTitle) {
        mSubTitleString = subTitle;
        if (mSubTitle != null)
            mSubTitle.setText(mSubTitleString);
    }
}

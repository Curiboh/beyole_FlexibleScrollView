package com.beyole.view;

import android.content.Context;
import android.graphics.Rect;
import android.os.Looper;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class VHFlexibleScrollView extends ScrollView {
	private static final String TAG = "FLEXIBLESCROLLVIEW";
	// ScrollViewΨһ��һ����view
	private View contentView;
	// ���ڼ�¼�����Ĳ���λ��
	private Rect originalRect = new Rect();
	// ��¼��ָ����ʱ�Ƿ��������
	private boolean canPullDown = false;
	// ��¼��ָ����ʱ�Ƿ��������
	private boolean canPullUp = false;
	private ViewDragHelper mViewDragHelper;

	public VHFlexibleScrollView(Context context) {
		this(context, null);
	}

	public VHFlexibleScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public VHFlexibleScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	/**
	 * �ڼ�����xml���ȡΨһ��һ��childview
	 */
	@Override
	protected void onFinishInflate() {
		if (getChildCount() > 0) {
			// ��ȡ��һ��childview
			contentView = getChildAt(0);
			mViewDragHelper = ViewDragHelper.create(this, 1.0f, new Callback() {

				// �����ƶ��Ĵ�ֱ��Χ
				@Override
				public int clampViewPositionVertical(View child, int top, int dy) {
					return top;
				}

				// �����childView
				@Override
				public boolean tryCaptureView(View child, int pointerId) {
					return child == contentView;
				}

				/**
				 * viewλ�øı�ʱ
				 */
				@Override
				public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
					canPullDown = top > originalRect.top ? true : false;
					canPullUp = top < -1 * (originalRect.height() - getHeight()) ? true : false;
				}

				/**
				 * ��ָ�ͷ�ʱview�ص���ʼ״̬
				 */
				@Override
				public void onViewReleased(View releasedChild, float xvel, float yvel) {
					if (releasedChild == contentView) {
						if (canPullDown) {
							mViewDragHelper.settleCapturedViewAt(originalRect.left, originalRect.top);
						}
						if (canPullUp) {
							mViewDragHelper.settleCapturedViewAt(originalRect.left, -(originalRect.height() - getHeight()));
						}
						invalidateView();
					}
				}

			});
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (contentView == null)
			return;
		// scrollviewΨһ��һ����view��λ����Ϣ�����λ����Ϣ���������������б��ֲ���
		originalRect.set(contentView.getLeft(), contentView.getTop(), contentView.getRight(), contentView.getBottom());

	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean shouldInterceptTouchEvent = mViewDragHelper.shouldInterceptTouchEvent(ev);
		return shouldInterceptTouchEvent;

	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		mViewDragHelper.processTouchEvent(ev);
		return true;
	}

	@Override
	public void computeScroll() {
		if (mViewDragHelper.continueSettling(true)) {
			invalidateView();
		}
	}

	// �ػ�view
	private void invalidateView() {
		if (Looper.getMainLooper() == Looper.myLooper()) {
			invalidate();
		} else {
			postInvalidate();
		}
	}
}

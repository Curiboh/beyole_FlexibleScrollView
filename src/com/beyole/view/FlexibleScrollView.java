package com.beyole.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

/**
 * �е��Ե�scrollView
 * 
 * @date 2016/02/22
 * @author Iceberg
 * 
 */
public class FlexibleScrollView extends ScrollView {

	private static final String TAG = "FLEXIBLESCROLLVIEW";
	// �ƶ����ӣ���һ���ٷֱȣ�������ָ�ƶ���100px����ôviewֻ�ƶ�50px��Ŀ���Ǵﵽһ���ӳٵ�Ч����
	private static final float MOVE_FACTOR = 0.5f;
	// ��ָ�ɿ�ʱ������ص�ԭʼλ�ö��������ʱ��
	private static final int ANIM_TIME = 300;
	// ScrollViewΨһ��һ����view
	private View contentView;
	// ��ָ����ʱ��Yֵ�����ڼ����ƶ��е��ƶ�����
	// �������ʱ������������������������ָ�ƶ�ʱ����Ϊ��ǰ��ָ��Yֵ��
	private float startY;
	// ���ڼ�¼�����Ĳ���λ��
	private Rect originalRect = new Rect();
	// ��¼��ָ����ʱ�Ƿ��������
	private boolean canPullDown = false;
	// ��¼��ָ����ʱ�Ƿ��������
	private boolean canPullUp = false;
	// ����ָ����ʱ�Ĺ����м�¼�Ƿ��ƶ��˲���
	private boolean isMoved = false;

	public FlexibleScrollView(Context context) {
		this(context, null);
	}

	public FlexibleScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FlexibleScrollView(Context context, AttributeSet attrs, int defStyle) {
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

	// �ڴ����¼��д����������������߼�
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (contentView == null) {
			return super.dispatchTouchEvent(ev);
		}
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			// �ж��Ƿ����������������
			canPullDown = isCanPullDown();
			canPullUp = isCanPullUp();
			// ��¼����ʱ��Yֵ
			startY = ev.getY();
			break;
		case MotionEvent.ACTION_UP:
			if (!isMoved)
				break; // ���û���ƶ����֣�������ִ��
			// ��������
			TranslateAnimation anim = new TranslateAnimation(0, 0, contentView.getTop(), originalRect.top);
			// ���ö���ʱ��
			anim.setDuration(ANIM_TIME);
			// ��view���ö���
			contentView.setAnimation(anim);
			// ���ûص������Ĳ���λ��
			contentView.layout(originalRect.left, originalRect.top, originalRect.right, originalRect.bottom);
			// ����־λ����
			canPullDown = false;
			canPullUp = false;
			isMoved = false;
			break;
		case MotionEvent.ACTION_MOVE:
			// ���ƶ����̼�û�дﵽ�����ĳ̶ȣ���û�дﵽ�����ĳ̶�
			if (!canPullDown && !canPullUp) {
				startY = ev.getY();
				canPullDown = isCanPullDown();
				canPullUp = isCanPullUp();
				break;
			}
			// ������ָ�ƶ��ľ���
			float nowY = ev.getY();
			int deltaY = (int) (nowY - startY);
			// �Ƿ�Ӧ���ƶ�����
			// 1.����������������ָ�����ƶ�
			// 2.����������������ָ�����ƶ�
			// 3.�ȿ�������Ҳ������������ScrollView�����Ŀؼ���scrollView��С
			boolean shouldMove = (canPullDown && deltaY > 0) || (canPullUp && deltaY < 0) || (canPullDown && canPullUp);
			if (shouldMove) {
				// ����ƫ����
				int offset = (int) (deltaY * MOVE_FACTOR);
				contentView.layout(originalRect.left, originalRect.top + offset, originalRect.right, originalRect.bottom + offset);
				isMoved = true;
			}
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * �ж��Ƿ����������
	 * 
	 * @return
	 */
	private boolean isCanPullDown() {
		return getScrollY() == 0 || contentView.getHeight() < getHeight() + getScrollY();
	}

	/**
	 * �ж��Ƿ�������ײ�
	 */
	private boolean isCanPullUp() {
		return contentView.getHeight() <= getScrollY() + getHeight();
	}
}

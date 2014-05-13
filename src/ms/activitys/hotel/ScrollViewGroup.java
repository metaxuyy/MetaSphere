package ms.activitys.hotel;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class ScrollViewGroup extends ViewGroup {

	private static final String TAG = "scroller";

	private Scroller scroller;

	private int currentScreenIndex;

	public int getCurrentScreenIndex() {
		return currentScreenIndex;
	}

	public void setCurrentScreenIndex(int currentScreenIndex) {
		this.currentScreenIndex = currentScreenIndex;
	}

	private GestureDetector gestureDetector;

	// 设置一个标志位，防止底层的onTouch事件重复处理UP事件
	private boolean fling;

	public ScrollViewGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public ScrollViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public ScrollViewGroup(Context context) {
		super(context);
		initView(context);
	}

	private void initView(final Context context) {
		this.scroller = new Scroller(context);

		this.gestureDetector = new GestureDetector(new OnGestureListener() {

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				return false;
			}

			@Override
			public void onShowPress(MotionEvent e) {
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {
				if ((distanceX > 0 && currentScreenIndex < getChildCount() - 1)// 防止移动过最后一页
						|| (distanceX < 0 && getScrollX() > 0)) {// 防止向第一页之前移动
					scrollBy((int) distanceX, 0);
				}
				return true;
			}

			@Override
			public void onLongPress(MotionEvent e) {
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				Log.d(TAG, "min velocity >>>"
						+ ViewConfiguration.get(context).getScaledMinimumFlingVelocity()
						+ " current velocity>>" + velocityX);
				// 判断是否达到最小轻松速度，取绝对值的
				if (Math.abs(velocityX) > ViewConfiguration.get(context).getScaledMinimumFlingVelocity()) {
						if (velocityX > 0 && currentScreenIndex > 0) {//手指从左往右划
							Log.d(TAG, ">>>>fling to left");
							fling = true;
							scrollToScreen(currentScreenIndex - 1);
						} else if (velocityX < 0 && currentScreenIndex < getChildCount() - 1) {
							Log.d(TAG, ">>>>fling to right");
							fling = true;
							scrollToScreen(currentScreenIndex + 1);
						}
				}

				return true;
			}

			@Override
			public boolean onDown(MotionEvent e) {
				return false;
			}
		});
		
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,int bottom) {
		Log.d(TAG, ">>left: " + left + " top: " + top + " right: " + right
				+ " bottom:" + bottom);

		/**
		 * 设置布局，将子视图顺序横屏排列
		 */
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			child.setVisibility(View.VISIBLE);
			child.measure(right - left, bottom - top);
			child.layout(0 + i * getWidth(), 0, getWidth() + i * getWidth(),
					getHeight());
		}
		
		//初始化显示第几个界面
		int delta = currentScreenIndex * getWidth() - getScrollX();
		scroller.startScroll(getScrollX(), 0, delta, 0, 0);
		invalidate();
	}

	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			scrollTo(scroller.getCurrX(), 0);
			postInvalidate();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			if (!fling) {
				snapToDestination();
			}
			fling = false;
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * 切换到指定屏
	 * 
	 * @param whichScreen
	 */
	private void scrollToScreen(int whichScreen) {
		if (getFocusedChild() != null && whichScreen != currentScreenIndex
				&& getFocusedChild() == getChildAt(currentScreenIndex)) {
			getFocusedChild().clearFocus();
		}

		final int delta = whichScreen * getWidth() - getScrollX();
		scroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta) * 2);
		invalidate();

		currentScreenIndex = whichScreen;
		if (onScreenChangeListener != null) {
			onScreenChangeListener.onScreenChange(currentScreenIndex);
		}
	}

	/**
	 * 根据当前x坐标位置确定切换到第几屏
	 */
	private void snapToDestination() {
		scrollToScreen((getScrollX() + (getWidth() / 2)) / getWidth());
	}

	public interface OnScreenChangeListener {
		void onScreenChange(int currentIndex);
	}
	private OnScreenChangeListener onScreenChangeListener;

	public void setOnScreenChangeListener(OnScreenChangeListener onScreenChangeListener) {
		this.onScreenChangeListener = onScreenChangeListener;
	}
}

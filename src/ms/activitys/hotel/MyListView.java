package ms.activitys.hotel;

import java.text.SimpleDateFormat;
import java.util.Date;

import ms.activitys.R;
import ms.globalclass.listviewadapter.MessageBaseAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MyListView extends ListView implements OnScrollListener {

	private static final String TAG = "listview";

	private final static int RELEASE_To_REFRESH = 0;
	private final static int PULL_To_REFRESH = 1;
	private final static int REFRESHING = 2;
	private final static int DONE = 3;
	private final static int LOADING = 4;

	private final static int RATIO = 3;

	private LayoutInflater inflater;

	private LinearLayout headView;

	private TextView tipsTextview;
	private TextView lastUpdatedTextView;
//	private ImageView arrowImageView;
	private ProgressBar progressBar;


	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;
	private boolean isRecored;


	private int headContentWidth;
	private int headContentHeight;

	private int startY;
	private int firstItemIndex;

	private int state;

	private boolean isBack;

	private OnRefreshListener refreshListener;

	private boolean isRefreshable;
	private String mylistviewlable;
	private String mylistviewlable2;
	private String mylistviewlable3;
	private String mylistviewlable4;
	
//	private SpecialAdapter myAdapter;
	private MessageBaseAdapter myAdapter;
	
	

	public MyListView(Context context) {
		super(context);
		init(context);
	}

	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public void initlable(String lable,String lable2,String lable3,String lable4)
	{
		mylistviewlable = lable;
		mylistviewlable2 = lable2;
		mylistviewlable3 = lable3;
		mylistviewlable4 = lable4;
	}

	private void init(Context context) {
		//setCacheColorHint(context.getResources().getColor(R.color.transparent));
		inflater = LayoutInflater.from(context);

		headView = (LinearLayout) inflater.inflate(R.layout.head, null);

//		arrowImageView = (ImageView) headView
//				.findViewById(R.id.head_arrowImageView);
//		arrowImageView.setMinimumWidth(70);
//		arrowImageView.setMinimumHeight(50);
		progressBar = (ProgressBar) headView
				.findViewById(R.id.head_progressBar);
		tipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);
		lastUpdatedTextView = (TextView) headView
				.findViewById(R.id.head_lastUpdatedTextView);

		measureView(headView);
		headContentHeight = headView.getMeasuredHeight();
		headContentWidth = headView.getMeasuredWidth();

		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		headView.invalidate();

//		Log.v("size", "width:" + headContentWidth + " height:"
//				+ headContentHeight);

		addHeaderView(headView, null, false);
		setOnScrollListener(this);

		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);

		state = DONE;
		isRefreshable = false;
	}

	public void onScroll(AbsListView arg0, int firstVisiableItem, int arg2,
			int arg3) {
		firstItemIndex = firstVisiableItem;
	}

	public void onScrollStateChanged(AbsListView arg0, int scrollState) {
		if(scrollState == OnScrollListener.SCROLL_STATE_IDLE)
		{
			int sart = arg0.getFirstVisiblePosition();
			int end = arg0.getLastVisiblePosition();
//			System.out.println("sart=="+sart+"end===="+end);
			if(myAdapter != null)
			{
				myAdapter.setSCROLL_STATE_TOUCH_SCROLL(false);
				System.out.println("sart==false");
				myAdapter.releaseBitmap(sart,end);
			}
		}
		else if(scrollState == OnScrollListener.SCROLL_STATE_FLING)
		{
			if(myAdapter != null)
			{
				System.out.println("sart==true");
				myAdapter.setSCROLL_STATE_TOUCH_SCROLL(true);
			}
		}
	}

	public boolean onTouchEvent(MotionEvent event) {

		if (isRefreshable) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (firstItemIndex == 0 && !isRecored) {
					isRecored = true;
					startY = (int) event.getY();
//					Log.v(TAG, "在down时候记录当前位置");
				}
				break;

			case MotionEvent.ACTION_UP:

				if (state != REFRESHING && state != LOADING) {
					if (state == DONE) {
//						Log.v(TAG,"这里是什么状态================");
					}
					if (state == PULL_To_REFRESH) {
						state = DONE;
						changeHeaderViewByState();

//						Log.v(TAG, "由下拉刷新状态，到done状态");
					}
					if (state == RELEASE_To_REFRESH) {
						state = REFRESHING;
						changeHeaderViewByState();
						onRefresh();

//						Log.v(TAG, "由松开刷新状态，到done状态");
					}
				}

				isRecored = false;
				isBack = false;

				break;

			case MotionEvent.ACTION_MOVE:
				int tempY = (int) event.getY();

				if (!isRecored && firstItemIndex == 0) {
//					Log.v(TAG, "在move时候记录下位置");
					isRecored = true;
					startY = tempY;
				}

				if (state != REFRESHING && isRecored && state != LOADING) {

					if (state == RELEASE_To_REFRESH) {

						setSelection(0);

						if (((tempY - startY) / RATIO < headContentHeight)
								&& (tempY - startY) > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();

//							Log.v(TAG, "由松开刷新状态转变到下拉刷新状态");
						}
						else if (tempY - startY <= 0) {
							state = DONE;
							changeHeaderViewByState();

//							Log.v(TAG, "由松开刷新状态转变到done状态");
						}
						else {
//							Log.v(TAG,"这里是什么状态================");
						}
					}
					if (state == PULL_To_REFRESH) {

						setSelection(0);

						if ((tempY - startY) / RATIO >= headContentHeight) {
							state = RELEASE_To_REFRESH;
							isBack = true;
							changeHeaderViewByState();

//							Log.v(TAG, "由done或者下拉刷新状态转变到松开刷新");
						}
						else if (tempY - startY <= 0) {
							state = DONE;
							changeHeaderViewByState();

//							Log.v(TAG, "由DOne或者下拉刷新状态转变到done状态");
						}
					}

					if (state == DONE) {
						if (tempY - startY > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();
						}
					}

					if (state == PULL_To_REFRESH) {
						headView.setPadding(0, -1 * headContentHeight
								+ (tempY - startY) / RATIO, 0, 0);

					}

					if (state == RELEASE_To_REFRESH) {
						headView.setPadding(0, (tempY - startY) / RATIO
								- headContentHeight, 0, 0);
					}

				}

				break;
			}
		}

		return super.onTouchEvent(event);
	}

	private void changeHeaderViewByState() {
		switch (state) {
		case RELEASE_To_REFRESH:
//			arrowImageView.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);

//			arrowImageView.clearAnimation();
//			arrowImageView.startAnimation(animation);

			tipsTextview.setText(mylistviewlable);
//			Log.v(TAG, "当前状态，松开刷新");
			break;
		case PULL_To_REFRESH:
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);
//			arrowImageView.clearAnimation();
//			arrowImageView.setVisibility(View.VISIBLE);
			if (isBack) {
				isBack = false;
//				arrowImageView.clearAnimation();
//				arrowImageView.startAnimation(reverseAnimation);

				tipsTextview.setText(mylistviewlable2);
			} else {
				tipsTextview.setText(mylistviewlable2);
			}
//			Log.v(TAG, "当前状态，下拉刷新");  
			break;
			
		case REFRESHING:

			headView.setPadding(0, 0, 0, 0);

			progressBar.setVisibility(View.VISIBLE);
//			arrowImageView.clearAnimation();
//			arrowImageView.setVisibility(View.GONE);
			tipsTextview.setText(mylistviewlable3);
			lastUpdatedTextView.setVisibility(View.VISIBLE);

//			Log.v(TAG, "当前状态,正在刷新...");
			break;
		case DONE:
			headView.setPadding(0, -1 * headContentHeight, 0, 0);

			progressBar.setVisibility(View.GONE);
//			arrowImageView.clearAnimation();
//			arrowImageView.setImageResource(R.drawable.arrow_down);
			tipsTextview.setText(mylistviewlable2);
			lastUpdatedTextView.setVisibility(View.VISIBLE);

//			 Log.v(TAG, "当前状态，done");
			break;
		}
	}

	public void setonRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
		isRefreshable = true;
	}

	public interface OnRefreshListener {
		public void onRefresh();
	}

	public void onRefreshComplete() {
		state = DONE;
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String date=format.format(new Date());
		lastUpdatedTextView.setText(mylistviewlable4 + date);
		lastUpdatedTextView.setVisibility(View.GONE);
		changeHeaderViewByState();
	}

	private void onRefresh() {
		if (refreshListener != null) {
			refreshListener.onRefresh();
		}
	}

	private void measureView(View child) {
		 ViewGroup.LayoutParams p = child.getLayoutParams();
	        if (p == null) {
	            p = new ViewGroup.LayoutParams(
	                    ViewGroup.LayoutParams.FILL_PARENT,
	                    ViewGroup.LayoutParams.WRAP_CONTENT);
	        }

	        int childWidthSpec = ViewGroup.getChildMeasureSpec(0,
	                0 + 0, p.width);
	        int lpHeight = p.height;
	        int childHeightSpec;
	        if (lpHeight > 0) {
	            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
	        } else {
	            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
	        }
	        child.measure(childWidthSpec, childHeightSpec);
	}

	public void setAdapter(BaseAdapter adapter) {
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String date=format.format(new Date());
		lastUpdatedTextView.setText(mylistviewlable4 + date);
		lastUpdatedTextView.setVisibility(View.GONE);
		myAdapter = (MessageBaseAdapter)adapter;
		super.setAdapter(adapter);
	}
	

}

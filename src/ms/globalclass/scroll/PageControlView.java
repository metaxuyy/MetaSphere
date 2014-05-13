package ms.globalclass.scroll;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import ms.activitys.R;
import ms.globalclass.scroll.ScrollViewGroup.OnScreenChangeListener;

public class PageControlView extends LinearLayout {
	private Context context;

	private int count;

	public void bindScrollViewGroup(ViewFlipper scrollViewGroup) {
		this.count=scrollViewGroup.getChildCount();
		generatePageControl(0);
//		
//		scrollViewGroup.setOnScreenChangeListener(new OnScreenChangeListener() {
//			
//			@Override
//			public void onScreenChange(int currentIndex) {
//				// TODO Auto-generated method stub
//				generatePageControl(currentIndex);
//			}
//		});
	}

	public PageControlView(Context context) {
		super(context);
		this.init(context);
	}
	public PageControlView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init(context);
	}

	private void init(Context context) {
		this.context=context;
	}

	public void generatePageControl(int currentIndex) {
		this.removeAllViews();

		for (int i = 0; i < this.count; i++) {
			ImageView imageView = new ImageView(context);
			if (currentIndex == i) {
				imageView.setImageResource(R.drawable.page_indicator_focused);
			} else {
				imageView.setImageResource(R.drawable.page_indicator);
			}
			this.addView(imageView);
		}
	}
}

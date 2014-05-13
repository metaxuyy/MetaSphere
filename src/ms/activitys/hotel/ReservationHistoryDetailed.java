package ms.activitys.hotel;


import ms.activitys.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ReservationHistoryDetailed extends Activity{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reservation_detailed_history);
		
		
		Bundle bunde = this.getIntent().getExtras();
		
		RelativeLayout roomsrl = (RelativeLayout)findViewById(R.id.rooms);
		TextView firstlabel = (TextView) roomsrl.findViewById(R.id.firstlabel);
		firstlabel.setText(this.getString(R.string.hotel_reservation_lable_7));
		TextView secondlabel = (TextView) roomsrl.findViewById(R.id.secondlabel);
		secondlabel.setText(bunde.getString("roomcount"));
		
		RelativeLayout timesrl = (RelativeLayout)findViewById(R.id.times);
		TextView firstlabel2 = (TextView) timesrl.findViewById(R.id.firstlabel);
		firstlabel2.setText(this.getString(R.string.hotel_reservation_lable_8));
		TextView secondlabel2 = (TextView) timesrl.findViewById(R.id.secondlabel);
		secondlabel2.setText(bunde.getString("arrivalTime"));
		
		RelativeLayout hotelnamerl = (RelativeLayout)findViewById(R.id.hotel_name);
		loadIncludeRelativeLayout(hotelnamerl,bunde.getString("storeName"),null);
		
		RelativeLayout checkinrl = (RelativeLayout)findViewById(R.id.checkinrl);
		loadIncludeRelativeLayout(checkinrl,this.getString(R.string.hotel_reservation_lable_1),bunde.getString("checkin"));
		
		RelativeLayout checkoutrl = (RelativeLayout)findViewById(R.id.checkoutrl);
		loadIncludeRelativeLayout(checkoutrl,this.getString(R.string.hotel_reservation_lable_2),bunde.getString("checkout"));
		
		RelativeLayout hoteltyperl = (RelativeLayout)findViewById(R.id.hotel_type);
		loadIncludeRelativeLayout(hoteltyperl,this.getString(R.string.hotel_reservation_lable_9),bunde.getString("roomtype"));
		
		RelativeLayout roomcountrl = (RelativeLayout)findViewById(R.id.room_count);
		loadIncludeRelativeLayout(roomcountrl,this.getString(R.string.hotel_reservation_lable_10),bunde.getString("roomcount"));
		
		RelativeLayout payrl = (RelativeLayout)findViewById(R.id.pay);
		loadIncludeRelativeLayout(payrl,this.getString(R.string.hotel_reservation_lable_11),"гд"+bunde.getString("total"));
		
		RelativeLayout paytyperl = (RelativeLayout)findViewById(R.id.pay_type);
		loadIncludeRelativeLayout(paytyperl,this.getString(R.string.hotel_reservation_lable_12),bunde.getString("paytype"));
		
		RelativeLayout backnowrl = (RelativeLayout)findViewById(R.id.back_now);
		loadIncludeRelativeLayout(backnowrl,this.getString(R.string.hotel_reservation_lable_3),"гд"+bunde.getString("backnow"));
		
		RelativeLayout guestnamerl = (RelativeLayout)findViewById(R.id.guest_name2);
		loadIncludeRelativeLayout(guestnamerl,this.getString(R.string.hotel_reservation_lable_13),bunde.getString("guestname"));
		
		RelativeLayout mobilerl = (RelativeLayout)findViewById(R.id.mobile2);
		loadIncludeRelativeLayout(mobilerl,this.getString(R.string.hotel_reservation_lable_15),bunde.getString("mobile"));
		
		RelativeLayout emailrl = (RelativeLayout)findViewById(R.id.email2);
		loadIncludeRelativeLayout(emailrl,this.getString(R.string.hotel_reservation_lable_16),bunde.getString("email"));
		
		TextView messagerl = (TextView)findViewById(R.id.message2);
		messagerl.setText(this.getString(R.string.hotel_reservation_lable_20)+":"+bunde.getString("message"));
		
		TextView stv = (TextView)findViewById(R.id.submit_text);
		stv.setText(this.getString(R.string.hotel_reservation_lable_22));
		
		Button submit = (Button)findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ReservationHistoryDetailed.this.setResult(RESULT_OK, getIntent());
				ReservationHistoryDetailed.this.finish();
			}
		});

	}
	
	public void loadIncludeRelativeLayout(RelativeLayout rl,String fstr,String rstr)
	{
		TextView firstlabel = (TextView) rl.findViewById(R.id.firstlabel);
		firstlabel.setText(fstr);
		TextView secondlabel = (TextView) rl.findViewById(R.id.secondlabel);
		secondlabel.setText(rstr);
		ImageView icon = (ImageView) rl.findViewById(R.id.icon);
		icon.setBackgroundDrawable(null);
	}

}

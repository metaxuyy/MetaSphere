package ms.globalclass.listviewadapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import ms.activitys.MenuSetActivity;
import ms.activitys.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class MenuBtnGridAdapter extends BaseAdapter {

	private static final String[] menuBtnType={"url", "Ó³Éä", "ÊÂ¼þ"};  
	public JSONArray iconArr;
	public JSONArray skipArr1;
	public JSONArray skipArr2;
	public ArrayList<String> iconStrArr;
	public ArrayList<String> skipStrArr1;
	public ArrayList<String> skipStrArr2;
	
	private class GridHolder {
		ImageView menuBtnIcon;
		TextView menuBtnName;
		Spinner menuBtnType;
		LinearLayout menuBtnUrlLL;
		LinearLayout menuBtnSkipLL;
		EditText menuBtnUrl;
		Spinner menuBtnSkip;
	}

	private Context context;

	private List<Map<String, Object>> list;
	private LayoutInflater mInflater;

	public MenuBtnGridAdapter(Context c) {
		super();
		this.context = c;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int index) {

		return list.get(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		GridHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.menubtn_grid_item, null);
			holder = new GridHolder();
			holder.menuBtnIcon = (ImageView) convertView.findViewById(R.id.menubtnImg);
			holder.menuBtnName = (TextView) convertView.findViewById(R.id.menubtnName);
			holder.menuBtnType = (Spinner) convertView.findViewById(R.id.menuBtnType);
			holder.menuBtnUrlLL = (LinearLayout) convertView.findViewById(R.id.menuUrlLL);
			holder.menuBtnSkipLL = (LinearLayout) convertView.findViewById(R.id.menuSkipLL);
			holder.menuBtnUrl = (EditText) convertView.findViewById(R.id.menuBtnUrl);
			holder.menuBtnSkip = (Spinner) convertView.findViewById(R.id.menuBtnSkip);
			convertView.setTag(holder);

		} else {
			holder = (GridHolder) convertView.getTag();

		}
		Map<String, Object> info = list.get(index);
		if (info != null) {
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, menuBtnType);  
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
	        holder.menuBtnType.setAdapter(adapter); 
			
	        if(info.get("menuBtnIcon")!=null){
	        	holder.menuBtnIcon.setImageDrawable((Drawable) info.get("menuBtnIcon"));
	        }else{
	        	
	        }
			holder.menuBtnName.setText((String) info.get("menuBtnName"));
			String type = (String) info.get("menuBtnType");
			if(type.equals("1")){
				holder.menuBtnUrlLL.setVisibility(View.VISIBLE);
				holder.menuBtnSkipLL.setVisibility(View.GONE);
				holder.menuBtnUrl.setText((String) info.get("menuBtnUrl"));
				holder.menuBtnType.setSelection(0);
			}else{
				holder.menuBtnUrlLL.setVisibility(View.GONE);
				holder.menuBtnSkipLL.setVisibility(View.VISIBLE);
				if(type.equals("2")){
					holder.menuBtnType.setSelection(1);
					
					ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, skipStrArr1);  
			        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
			        holder.menuBtnSkip.setAdapter(adapter1);
			        
			        int selIndex1 = 0;
			        for(int i=0; i<skipStrArr1.size(); i++){
			        	if(skipStrArr1.get(i).equals((String) info.get("menuBtnSkipName"))){
			        		selIndex1 = i;
			        		break;
			        	}
			        }
			        holder.menuBtnSkip.setSelection(selIndex1);
				}else if(type.equals("3")){
					holder.menuBtnType.setSelection(2);
					
					ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, skipStrArr2);  
			        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
			        holder.menuBtnSkip.setAdapter(adapter2);
			        
			        int selIndex2 = 0;
			        for(int j=0; j<skipStrArr2.size(); j++){
			        	if(skipStrArr2.get(j).equals((String) info.get("menuBtnSkipName"))){
			        		selIndex2 = j;
			        		break;
			        	}
			        }
			        holder.menuBtnSkip.setSelection(selIndex2);
				}
			}
		}
		return convertView;
	}

}

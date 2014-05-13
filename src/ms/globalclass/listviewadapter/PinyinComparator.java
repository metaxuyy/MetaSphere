package ms.globalclass.listviewadapter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class PinyinComparator implements Comparator<Map<String,Object>>{

	@Override
	public int compare(Map<String, Object> lhs, Map<String, Object> rhs) {
		// TODO Auto-generated method stub
		String str1 = (String)lhs.get("sortName");
		str1 = PingYinUtil.getPingYin(str1);
		String str2 = (String)rhs.get("sortName");
		str2 = PingYinUtil.getPingYin(str2);
		int compareName = str1.compareTo(str2);  
        return compareName;
	}

	

//	@Override
//	public int compare(Object o1, Object o2) {
//		String str1 = "";
//		String str2 = "";
//		try{
//			 str1 = PingYinUtil.getPingYin((String) o1);
//		     str2 = PingYinUtil.getPingYin((String) o2);
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//		 return str1.compareTo(str2);
//	}

}

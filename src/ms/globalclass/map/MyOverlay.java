//package ms.globalclass.map;
//
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.List;
//import java.util.Map;
//
//import org.json.JSONObject;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Point;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.text.Html;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.android.maps.GeoPoint;
//import com.google.android.maps.MapView;
//import com.google.android.maps.Overlay;
//import com.google.android.maps.OverlayItem;
//import com.google.android.maps.Projection;
//
//public class MyOverlay extends Overlay{
//	private GeoPoint geoPoint;
//    private Context context;
//    private int drawable;
//    private Bitmap mbit;
//
//    public MyOverlay(GeoPoint geoPoint, Context context, int drawable) {
//        super();
//        this.geoPoint = geoPoint;
//        this.context = context;
//        this.drawable = drawable;
//    }
//    
//    public MyOverlay(GeoPoint geoPoint, Context context, Bitmap bitmap) {
//        super();
//        this.geoPoint = geoPoint;
//        this.context = context;
//        this.mbit = bitmap;
//    }
//
//    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
//
//        Projection projection = mapView.getProjection();
//        Point point = new Point();
//        projection.toPixels(geoPoint, point);
//
////        if(this.mbit != null)
////        {
////	        Bitmap bitmap = this.mbit;
////	        int nx = point.x - bitmap.getWidth();
////	        int ny = point.y - bitmap.getHeight();
////	        canvas.drawBitmap(bitmap, nx , ny , null);
////        }
////        else
////        {
//        	Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),drawable); 
//        	int nx = point.x - bitmap.getWidth();
//	        int ny = point.y - bitmap.getHeight();
//	        canvas.drawBitmap(bitmap, nx , ny , null);
////        }
//        super.draw(canvas, mapView, shadow);
//    } 
//}

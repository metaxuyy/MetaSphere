//package ms.globalclass.map;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Matrix;
//import android.graphics.Point;
//import android.location.Location;
//
//import com.google.android.maps.GeoPoint;
//import com.google.android.maps.MapView;
//import com.google.android.maps.MyLocationOverlay;
//import ms.activitys.R;
//
//public class MyCustomLocationOverlay extends MyLocationOverlay{
//	private Context mContext; 
//    private float   mOrientation; 
//    private GeoPoint point;
//    public MyCustomLocationOverlay(Context context, MapView mapView) { 
//        super(context, mapView); 
//        mContext = context; 
//    } 
//    
//    public MyCustomLocationOverlay(Context context, MapView mapView,GeoPoint gp) { 
//        super(context, mapView); 
//        mContext = context; 
//        this.point = gp;
//    } 
//    
//    protected void drawMyLocation(Canvas canvas, MapView mapView, Location lastFix, GeoPoint myLocation, long when) { 
//        // translate the GeoPoint to screen pixels 
////        Point screenPts = mapView.getProjection().toPixels(point, null); 
////        // create a rotated copy of the marker 
////        Bitmap arrowBitmap = BitmapFactory.decodeResource( mContext.getResources(), R.drawable.arrow_green); 
////        Matrix matrix = new Matrix(); 
////        matrix.postRotate(mOrientation); 
////        Bitmap rotatedBmp = Bitmap.createBitmap( 
////            arrowBitmap,  
////            0, 0,  
////            arrowBitmap.getWidth(),  
////            arrowBitmap.getHeight(),  
////            matrix,  
////            true 
////        ); 
////        // add the rotated marker to the canvas 
////        canvas.drawBitmap( 
////            rotatedBmp,  
////            screenPts.x - (rotatedBmp.getWidth()  / 2),  
////            screenPts.y - (rotatedBmp.getHeight() / 2),  
////            null 
////        ); 
//    	super.drawMyLocation(canvas, mapView, lastFix, point, when);
//    } 
//    
//    public void setOrientation(float newOrientation) { 
//         mOrientation = newOrientation; 
//    } 
//}

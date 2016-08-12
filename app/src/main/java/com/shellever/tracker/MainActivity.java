package com.shellever.tracker;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.List;
import java.util.Locale;

//问题一：在点击显示覆盖物时，虽然显示了图片及其他的信息，但是当点击到右下角即原先有地图放大缩小按钮位置时，
//点击的事件会被父控件地图捕抓到
//解决方法：在父布局控件(marker_info.xml的根布局控件)中声明一个可点击的属性即可clickable = true
//即点击事件由RelativeLayout处理

//问题二：调试下载程序过程中，经常出现程序崩溃，无法启动起来
//解决方法：在手机端，卸载掉程序，然后重新下载安装即可
//
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private boolean isOpenDebug = true;     //default: false

    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;

    //定位
    private LocationClient mLocationClient;
    private MyLocationListener mLocationListener;
    private boolean isFirstIn = true;
    private double mLatitude;   //纬度
    private double mLongitude;  //经度

    //自定义定位图标
    private BitmapDescriptor mIconLocation;
    private MyOrientationListener mOrientationListener;
    private float mCurrentX;
    private MyLocationConfiguration.LocationMode mLocationMode;

    //覆盖物
    private BitmapDescriptor mMarker;
    private RelativeLayout mMarkerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.activity_main);

        //初始化百度地图控件
        initView();

        //初始化定位
        initLocation();

        //初始化覆盖物
        initMarker();
    }

    private void initMarker() {
        mMarker = BitmapDescriptorFactory.fromResource(R.mipmap.maker);   //R.drawable.icon_gcoding
        mMarkerLayout = (RelativeLayout) findViewById(R.id.marker_layout);

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Bundle bundle = marker.getExtraInfo();
                Info info = (Info) bundle.getSerializable("info");

                ImageView image = (ImageView) mMarkerLayout.findViewById(R.id.info_img);
                TextView distance = (TextView) mMarkerLayout.findViewById(R.id.info_distance);
                TextView name = (TextView) mMarkerLayout.findViewById(R.id.info_name);
                TextView zan = (TextView) mMarkerLayout.findViewById(R.id.info_zan);

                image.setImageResource(info.getImgId());
                distance.setText(info.getDistance());
                name.setText(info.getName());
                zan.setText(info.getZan() + "");

                mMarkerLayout.setVisibility(View.VISIBLE);

                //创建InfoWindow展示的View
                TextView text = new TextView(getApplicationContext());
                text.setBackgroundResource(R.drawable.loc_tip);   //R.drawable.location_tips
                text.setGravity(Gravity.CENTER);        //文字居中显示
//                text.setPadding(5, 10, 5, 10);        //设置后显示出现问题，故不设置
                text.setTextColor(Color.parseColor("#ffffff"));
//                text.setBackgroundResource(R.drawable.popup);
                text.setText(info.getName());

                //定义用于显示gaiInfoWindow的坐标点
                LatLng point = marker.getPosition();
                //创建InfoWindow，参数为(View，地理坐标，y轴偏移量)
                InfoWindow mInfoWindow = new InfoWindow(text, point, -49);
                //显示InfoWindow
                mBaiduMap.showInfoWindow(mInfoWindow);

                return true;
            }
        });

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMarkerLayout.setVisibility(View.GONE);
                mBaiduMap.hideInfoWindow();             //点击地图时隐藏InfoWindow
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }

    //初始化定位配置信息
    private void initLocation() {
        mLocationMode = MyLocationConfiguration.LocationMode.NORMAL;    //定位模式
        mLocationClient = new LocationClient(this);
        mLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);    //注册定位监听函数

        //定位服务的客户端设置
        //配置定位相关参数，比如定位模式、定位时间间隔、坐标系类型等
        LocationClientOption options = new LocationClientOption();
        options.setCoorType("bd09ll");  //设置坐标类型
        options.setIsNeedAddress(true); //设置需要地址信息，默认为无地址
        options.setOpenGps(true);       //打开gps进行定位
        options.setScanSpan(1000);      //设置扫描间隔1000毫秒，当<1000(1s)时，定时定位无效

        mLocationClient.setLocOption(options);  //设置LocationClientOption

        //初始化图标
        mIconLocation = BitmapDescriptorFactory.fromResource(R.mipmap.navi_map_gps_locked);
        mOrientationListener = new MyOrientationListener(this);
        mOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mCurrentX = x;
            }
        });
    }

    private void initView() {
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.BaiduMapView);
        //获取百度地图
        mBaiduMap = mMapView.getMap();
        //设置地图的放大级别
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f); //500m
        mBaiduMap.setMapStatus(msu);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //在activity执行onResume时执行mMapView.onResume()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //开启定位
        mBaiduMap.setMyLocationEnabled(true);
        if(!mLocationClient.isStarted()) {
            mLocationClient.start();
        }

        //开启方向传感器
        mOrientationListener.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        //在activity执行onPause时执行mMapView.onPause()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        //停止定位
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();

        //停止方向传感器
        mOrientationListener.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.commonMap:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL); //设置地图类型为普通图
                if(isOpenDebug) {
                    //(MinZoomLevel, MaxZoomLevel) = (3, 21)
                    String info = String.format(Locale.getDefault(), "(MinZoomLevel, MaxZoomLevel) = (%f, %f)", mBaiduMap.getMinZoomLevel(), mBaiduMap.getMaxZoomLevel());
                    Toast.makeText(MainActivity.this, info, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.satelliteMap:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);  //设置地图类型为卫星图
                break;
            case R.id.realTimeTraffic:
                if(mBaiduMap.isTrafficEnabled()){
                    mBaiduMap.setTrafficEnabled(false);
                    item.setTitle(R.string.rt_traffic_off);
                }else {
                    mBaiduMap.setTrafficEnabled(true);      //开启交通图
                    item.setTitle(R.string.rt_traffic_on);
                }
                break;
            case R.id.myLocation:
                positionToMyLocation();
                break;
            case R.id.lm_normal:
                mLocationMode = MyLocationConfiguration.LocationMode.NORMAL;
                break;
            case R.id.lm_following:
                mLocationMode = MyLocationConfiguration.LocationMode.FOLLOWING;
                break;
            case R.id.lm_compass:
                mLocationMode = MyLocationConfiguration.LocationMode.COMPASS;
                break;
            case R.id.add_overlay:
                addOverlay(Info.infoList);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //添加覆盖物
    private void addOverlay(List<Info> infoList) {
        mBaiduMap.clear();
        LatLng latLng = null;
        Marker marker;
        OverlayOptions options;
        for(Info info: infoList){
            //经纬度
            latLng = new LatLng(info.getLatitude(), info.getLongitude());
            //图标
            options = new MarkerOptions().position(latLng).icon(mMarker).zIndex(5);

            marker = (Marker) mBaiduMap.addOverlay(options);
            Bundle bundle = new Bundle();
            bundle.putSerializable("info", info);
            marker.setExtraInfo(bundle);
        }

        //将地图中心点移动到最后一个的位置上
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.setMapStatus(msu);
    }

    //定位到我的位置
    private void positionToMyLocation() {
        LatLng latLng = new LatLng(mLatitude, mLongitude);
        //设置地图新中心点
//        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        //设置地图中心点以及缩放级别
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngZoom(latLng, 15.0f);
        mBaiduMap.animateMapStatus(msu);
    }

    //实现BDLocationListener接口，接收异步返回的定位结果
    private class MyLocationListener implements BDLocationListener{
        //BDLocation类，封装了定位SDK的定位结果
        @Override
        public void onReceiveLocation(BDLocation location) {
            //定位数据
            MyLocationData data = new MyLocationData.Builder()
                    .direction(mCurrentX)                   //定位方向
                    .accuracy(location.getRadius())         //定位精度
                    .latitude(location.getLatitude())       //纬度
                    .longitude(location.getLongitude())     //经度
                    .build();
            //设置定位数据, 只有先允许定位图层后设置数据才会生效，参见 setMyLocationEnabled(boolean)
            mBaiduMap.setMyLocationData(data);

            //设置自定义图标
            MyLocationConfiguration config = new MyLocationConfiguration(
                    mLocationMode,      //MyLocationConfiguration.LocationMode.NORMAL,
                    true,               //
                    mIconLocation       //
            );
            mBaiduMap.setMyLocationConfigeration(config);

            //更新经纬度
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();

            if(isFirstIn){
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng); //设置地图中心点
                mBaiduMap.animateMapStatus(msu);
                isFirstIn = false;

                if(isOpenDebug) {
                    //要得到地址的话，需要在LocationClientOption中将setIsNeedAddress设置为true
                    Toast.makeText(MainActivity.this, location.getAddrStr(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

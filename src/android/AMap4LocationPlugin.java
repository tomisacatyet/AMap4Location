package com.tomisacat.cordova;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tomisacat on 16/1/7.
 */
public class AMap4LocationPlugin extends CordovaPlugin implements AMapLocationListener {

    private static Context mContext;

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;

    private CallbackContext mainCallbackContext;


    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        mContext = cordova.getActivity().getApplicationContext();
        init();
    }

    public void init(){
        locationClient = new AMapLocationClient(mContext);
        locationOption = new AMapLocationClientOption();
        // 设置定位模式为高精度模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置定位监听
        locationClient.setLocationListener(this);
        // 单次定位
        locationOption.setOnceLocation(true);
        // 设置是否需要显示地址信息
        locationOption.setNeedAddress(true);
        /**
         * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
         * 注意：只有在高精度模式下的单次定位有效，其他方式无效
         */
        locationOption.setGpsFirst(true);

        // 设置发送定位请求的时间间隔,最小值为2000，如果小于2000，按照2000算
        locationOption.setInterval(Long.valueOf(2000));

    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.mainCallbackContext = callbackContext;
        if ("location".equals(action)) {
            locationClient.startLocation();
            return true;
        }else if("stop".equals(action)) {
            if(locationClient.isStarted()){
                locationClient.stopLocation();
            }
            return true;
        }
        return false;  // Returning false results in a "MethodNotFound" error.
    }


    // 定位监听
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        try {
            if (null != amapLocation) {
                JSONObject resultObject = new JSONObject();
                //返回的状态码
                int statusCode = amapLocation.getErrorCode();
                if(statusCode==0){
					//获取经度
					resultObject.put("longitude",amapLocation.getLongitude());
					//获取纬度
					resultObject.put("latitude",amapLocation.getLatitude());
					//获取地址
					resultObject.put("formattedAddress",amapLocation.getAddress());
                    //城市名称
                    resultObject.put("cityName",amapLocation.getCity());
                    //城市编码
                    resultObject.put("cityCode",amapLocation.getCityCode());
                    //省份编码
                    resultObject.put("provinceName",amapLocation.getProvince());
                    //地区
                    resultObject.put("districtName",amapLocation.getDistrict());
					//获取区域编码
					resultObject.put("adcode",amapLocation.getAdCode());
                    //路或街道
                    resultObject.put("street",amapLocation.getStreet());
					//获取门牌号
					resultObject.put("number",amapLocation.getStreetNum());
					
                    mainCallbackContext.success(resultObject);
                }else{
                    //错误编码
                    resultObject.put("errorCode",amapLocation.getErrorCode());
                    //错误信息
                    resultObject.put("errorInfo",amapLocation.getErrorInfo());
                    mainCallbackContext.error(resultObject);
                }
            }else{
                JSONObject resultObject = new JSONObject();
                //错误编码
                resultObject.put("errorCode",amapLocation.getErrorCode());
                //错误信息
                resultObject.put("errorInfo",amapLocation.getErrorInfo());
                mainCallbackContext.error(resultObject);
            }
        }catch (Exception ex){
            Log.e("AMapLocationPlugin",ex.toString());
        }finally{
          if(locationClient != null && locationClient.isStarted()){
              locationClient.stopLocation();
          }
        }

    }
}

package com.df.imei;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    String str="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.ll);


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            str=DeviceIdUtil.getDeviceId(this);
            textView.setText("获取IMEI信息："+str);
        }
        else
        {
            if(PermissionChecker.getInstance().requestReadPhoneState(this))
            {
                str=DeviceIdUtil.getDeviceId(this);
                textView.setText("获取IMEI信息："+str);
            }
        }







    }

    public  String GetDeviceUUId()
    {
        String serial = null;

        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +

                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +

                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +

                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +

                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +

                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +

                Build.USER.length() % 10; //13 位

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                }
                serial = Build.getSerial();
            } else {
                serial = Build.SERIAL;
            }
            UUID uuid = UUID.randomUUID();
            String deviceId = uuid.toString().replace("-", "");
            m_szDevIDShort+=deviceId;
            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            //serial需要一个初始化
            serial = "serial"; // 随便一个初始化
        }
        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    public void getP(TextView txtView){
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        Class clazz = telephonyManager.getClass();
        Method getImei=null;
        try {
            getImei=clazz.getDeclaredMethod("getImei",int.class);//(int slotId)
            String strValue=getImei.invoke(telephonyManager, 0).toString(); //卡1
            if (!strValue.equals(""))
            {
                txtView.append(Html.fromHtml("卡1：<B><font color='red'>"+strValue+"</font></B>"));
                txtView.append("\r\n");
            }
            String strValue1= getImei.invoke(telephonyManager, 1).toString(); // 卡2
            if (!strValue1.equals(""))
            {
                txtView.append(Html.fromHtml("卡2：<B><font color='green'>"+strValue1+"</font></B>"));
            }
//            Log.e(TAG, "IMEI1 : "+getImei.invoke(telephonyManager, 0)); //卡1
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.Q)
    public void JudgeSIM(Context context,TextView v) {
        String imei = "";
        try {
            //TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
            imei= Settings.System.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
            v.setText("Android:"+imei);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
//        Log.d("卡槽数量：" + phoneCount);
//        Log.d("当前SIM卡数量：" + activeSubscriptionInfoCount);


    public String getIMEI(Context context) {
        String imei = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                imei = tm.getDeviceId();
            } else {
                Method method = tm.getClass().getMethod("getImei");
                imei = (String) method.invoke(tm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imei;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        try {
            str=DeviceIdUtil.getDeviceId(this);
            textView.setText("获取IMEI信息："+str);
        }
        catch (Exception e){
            textView.append("\r\n");
            textView.append("Error:"+e.toString());
        }

       /* switch (requestCode) {
            case READ_PHONE_STATE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
//                    insertDummyContact();
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    String deviceId = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
//                    Logger.d(TAG, "-------> IMEI:" + deviceId);

                } else {
                    // Permission Denied
                    Toast.makeText(this, getString(R.string.permission_deny), Toast.LENGTH_SHORT)
                            .show();
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }*/
    }
}

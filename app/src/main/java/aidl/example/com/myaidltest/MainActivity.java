package aidl.example.com.myaidltest;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.aidl.IMyAidlInterface;

public class MainActivity extends AppCompatActivity {

    private Button bind,unbind,hello;
    private IMyAidlInterface myService;// 服务
    private String appName = "unknown";
    private String TAG = "AIDL";

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG,"onServiceConnected call ");
            myService = IMyAidlInterface.Stub.asInterface(service);// 获取服务对象
        }// 连接服务

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG,"onServiceDisconnected call ");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bind = (Button) findViewById(R.id.bind_service);
        hello = (Button) findViewById(R.id.hello);
        unbind = (Button) findViewById(R.id.unbind_service);
        appName = getPackageName();

        // 我们没办法在构造Intent的时候就显式声明.
        Intent intent = new Intent("com.example.aidl.IMyAidlInterface");
        // 既然没有办法构建有效的component,那么给它设置一个包名也可以生效的
        intent.setPackage("aidl.example.com.aidlserver");// the service package
        // 绑定服务，可设置或触发一些特定的事件
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);


        bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("com.example.aidl.IMyAidlInterface");
                intent.setPackage("aidl.example.com.aidlserver");// the service package
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
                bind.setEnabled(false);
                hello.setEnabled(true);
                unbind.setEnabled(true);
            }
        });

        hello.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // AIDL服务调用代码如下：
                    String msg = myService.helloAndroidAIDL(appName + " : hello");
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        unbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unbindService(serviceConnection);
                bind.setEnabled(true);
                hello.setEnabled(false);
                unbind.setEnabled(false);
            }
        });

    }
}
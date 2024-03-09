package example.smart.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    private static final String MYTAG = "MainActivity";

    private static SocketServiceHex myservice;
    private ServiceMsgReceiver myServiceMsg;

    private static final int MAXLEN = 1024;
    private static final int DELAY = 100;
    private Timer timer;
    private Handler handler;
    private boolean bSerialLock = false;
    private int iSerialIn = 0;
    private int iSerialOut = 0;
    private byte[] bytesSerialRecBuff = new byte[MAXLEN];


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myservice = ((SocketServiceHex.LocalBinder) service).getService();
            SocketServiceHex.strMessageForDemo = MYTAG;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            myservice = null;
        }
    };

    // 接收service发送过来的广播，动作为service_msg
    public class ServiceMsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MYTAG)) {
                String msg = intent.getStringExtra("msg");
                try {
                    ReceiveData(msg.getBytes("ISO-8859-1"));
                } catch (Exception e) {
                    Log.i("Recv", "Error!");
                }
            }
        }
    }

    private void initEvent() {
        bSerialLock = false;
        iSerialIn = 0;
        iSerialOut = 0;
        // 注册接收服务
        myServiceMsg = new ServiceMsgReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MYTAG);
        registerReceiver(myServiceMsg, filter);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        }, 500, DELAY);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        scanData();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        // bind启动服务
        bindService(new Intent(this, SocketServiceHex.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    /// 接收数据到数据缓冲区内
    private void ReceiveData(byte[] bRecData) {
        int i;
        int iDataLen = bRecData.length;
        if (bSerialLock == false) {
            bSerialLock = true;
            if (iSerialIn + iDataLen <= MAXLEN) {
                for (i = 0; i < iDataLen; i++) {
                    bytesSerialRecBuff[iSerialIn + i] = bRecData[i];
                }
                iSerialIn += iDataLen;
            } else if (iSerialIn + iDataLen == MAXLEN) {
                for (i = 0; i < iDataLen; i++) {
                    bytesSerialRecBuff[iSerialIn + i] = bRecData[i];
                }
                iSerialIn = 0;
            } else {
                for (i = iSerialIn; i < MAXLEN; i++) {
                    bytesSerialRecBuff[i] = bRecData[i - iSerialIn];
                }
                for (i = 0; i < iDataLen - MAXLEN + iSerialIn; i++) {
                    bytesSerialRecBuff[i] = bRecData[i + MAXLEN - iSerialIn];
                }
                iSerialIn = iDataLen - MAXLEN + iSerialIn;
            }
            bSerialLock = false;
        }
    }

    //返回后面第iNum有效数据的位置
    private int dataOutLocation(int iMove) {
        int ret = 0;
        if (iSerialOut + iMove < MAXLEN) {
            ret = iSerialOut + iMove;
        } else if (iSerialOut + iMove > MAXLEN) {
            ret = iSerialOut + iMove - MAXLEN;
        }
        return ret;
    }

    // 从缓冲区内读出有效数据
    private void scanData() {
        if (bSerialLock == false) {
            bSerialLock = true;
            int iValidLen, iPacketLen;
            while (iSerialIn != iSerialOut) {
                if (bytesSerialRecBuff[iSerialOut] == DataConstants.HEAD_RET) {// 判断是否为包头
                    iValidLen = validReceiveLen();// 包含有效数据长度
                    if (iValidLen < 10) { // 有效长度太短
                        bSerialLock = false;
                        return;
                    }
                    iPacketLen = bytesSerialRecBuff[dataOutLocation(1)] & 0xFF;
                    if (iValidLen < iPacketLen) { // 包不完整
                        bSerialLock = false;
                        return;
                    }
                    if (iPacketLen > 9 && iPacketLen < 50) { // 数据长度正常
                        byte[] buf = new byte[iPacketLen];
                        for (int i = 0; i < iPacketLen; i++) {// 读出包并进行校验和计算
                            buf[i] = bytesSerialRecBuff[dataOutLocation(i)];
                        }
                        if (DataFormat.checkCRC(buf)) {
                            Log.w("bufLen:" + String.valueOf(validReceiveLen()), DataFormat.bytes2HexString(buf));
                            dataDispose(buf);
                            iSerialOut = dataOutLocation(iPacketLen);
                            bSerialLock = false;
                            return;
                        }
                    }
                }
                iSerialOut = dataOutLocation(1);
            }
            bSerialLock = false;
        }
    }

    // 读取当前缓冲区中的数据
    private int validReceiveLen() {
        if (iSerialOut < iSerialIn) {
            return (iSerialIn - iSerialOut);
        } else if (iSerialOut > iSerialIn) {
            return (MAXLEN - iSerialOut + iSerialIn);
        }
        return 0;
    }

    @Override
    protected void onDestroy() {
        // TODO 自动生成的方法存根
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        //unbindService(mConnection);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initEvent();

        Button btn_on = (Button)findViewById(R.id.btn_B_on);
        Button btn_off = (Button)findViewById(R.id.btn_B_off);

        Button btn1_on = (Button)findViewById(R.id.btn_M_on);
        Button btn1_off = (Button)findViewById(R.id.btn_M_off);


        btn_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendControlCmd (DataConstants.DEVTYPE_PWM, DataConstants.SENSOR_PWM_ALARMLIGHT,
                        DataConstants.INDEX_FIRST, DataConstants.VALUETYPE_BOOL,(byte) 0x01);
            }
        });
        btn_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendControlCmd (DataConstants.DEVTYPE_PWM, DataConstants.SENSOR_PWM_ALARMLIGHT,
                DataConstants.INDEX_FIRST, DataConstants.VALUETYPE_BOOL,(byte) 0x00);
            }
        });

        btn1_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendControlCmd1 (DataConstants.DEVTYPE_485, DataConstants.SENSOR_485_MP3,
                        DataConstants.INDEX_FIRST, DataConstants.VALUETYPE_ARRAY, (byte) 0x02, (byte) 0x00);
            }
        });
        btn1_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendControlCmd1 (DataConstants.DEVTYPE_485, DataConstants.SENSOR_485_MP3,
                DataConstants.INDEX_FIRST, DataConstants.VALUETYPE_ARRAY, (byte) 0x00, (byte) 0x00);
            }
        });


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(MainActivity.this).setIcon(R.mipmap.ic_launcher).setTitle("退出提示").setMessage("确认退出吗？").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialoginterface, int i) {
                    finish();
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialoginterface, int i) {
                }
            }).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    // 处理接收的数据包
    private void dataDispose(byte[] Packet) {
        if (Packet[2] == DataConstants.DEVTYPE_IO && Packet[6] == DataConstants.CMD_INTERVAL){
            byte[] data = new byte[6];
            for (int i = 0; i < data.length; i++) {
                data[i] = Packet[8 + i];
            }
            switch (Packet[4]) {
                case 0x08:
                    showButton(data);
                    break;
                case DataConstants.SENSOR_IO_INFRARED:
                    showInfrared(data);
                    break;
                case DataConstants.SENSOR_IO_SMOG:
                    showSmog(data);
                    break;
                case(byte)0x02:
                    showGas(data);
                    break;
            }
        }
    }

    private void showButton(byte[] Packet){
        TextView txt_data =(TextView)findViewById(R.id.txt_J);
        if (Packet[5] == 0x01) {
            txt_data.setText("紧急按钮：有呼叫");
        }
        else if (Packet[5] == 0x00){
            txt_data.setText("紧急按钮：无呼叫");
        }
    }

    private void showInfrared(byte[] Packet){
        TextView txt_data =(TextView)findViewById(R.id.txt_H);
        if (Packet[5] == 0x01) {
            txt_data.setText("红外对射值：有阻拦告警");
        }
        else if (Packet[5] == 0x00){
            txt_data.setText("红外对射值：无阻拦告警");
        }
    }

    private void showSmog(byte[] Packet){
        TextView txt_data =(TextView)findViewById(R.id.txt_Y);
        if (Packet[5] == 0x01) {
            txt_data.setText("烟雾监测：有烟雾告警");
        }
        else if (Packet[5] == 0x00){
            txt_data.setText("烟雾监测：无烟雾告警");
        }
    }

    private void showGas(byte[] Packet) {
        TextView txt_data = (TextView) findViewById(R.id.txt_R);
        if (Packet[5] == 0x01) {
            txt_data.setText("燃气值：有燃气告警");
        } else if (Packet[5] == 0x00) {
            txt_data.setText("燃气值：无燃气告警");
        }
    }

    private static void sendControlCmd (byte dev_type,byte addr,byte index,byte data_type,byte arg){
        byte[] bytesSend = new byte[16];
        bytesSend[0] = DataConstants.HEAD;;
        bytesSend[1] = (byte) bytesSend.length;
        bytesSend[2] = dev_type;
        bytesSend[3] = (byte) 0x00;
        bytesSend[4] = addr;
        bytesSend[5] = index;
        bytesSend[6] = DataConstants.CMD_CTRL;
        bytesSend[7] = data_type;
        bytesSend[13] = arg;
        DataFormat.setCRC(bytesSend); myservice.socketSend(bytesSend);
    }

    private static void sendControlCmd1 (byte dev_type, byte addr, byte index,byte data_type, byte arg1, byte arg2) {
        byte[] bytesSend = new byte[16];
        bytesSend[0] = DataConstants.HEAD;
        bytesSend[1] = (byte) bytesSend.length;
        bytesSend[2] = dev_type;
        bytesSend[3] = (byte) 0x00;
        bytesSend[4] = addr;
        bytesSend[5] = index;
        bytesSend[6] = DataConstants.CMD_CTRL;
        bytesSend[7] = data_type;
        bytesSend[12] = arg1;
        bytesSend[13] = arg2;
        DataFormat.setCRC(bytesSend);myservice.socketSend(bytesSend);
    }
}


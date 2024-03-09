package example.smart.demo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServiceHex extends Service {
    private static final int TIMER4RELINK = 5000;
    public static Socket socketDemo;
    public static DataInputStream inDemo;
    public static DataOutputStream outDemo;
    public static boolean bSocketflagDemo;
    public static String strMessageForDemo;
    private byte[] recvbuffer = new byte[1024];
    private boolean IsRun = true;
    private byte[] dataSend;
    private ExecutorService mThreadPool;

    private String sDemoIp = "";
    private String sDemoPort = "";

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public SocketServiceHex getService() {
            return SocketServiceHex.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private Timer mTimer;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    socketTest();
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), "Demo服务器(" + sDemoIp +":"+ sDemoPort + ")连接成功！！！", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(getApplicationContext(), "Demo服务器(" + sDemoIp +":"+ sDemoPort + ")连接失败！！！", Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreate() {
        //Android 4.0+ the socketDemo communication must be added
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());

        bSocketflagDemo = false;
        strMessageForDemo = "SocketServiceHex";

        //create threadpool
        mThreadPool = Executors.newCachedThreadPool();
        socketConnect();
        socketRecv();

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = mHandler.obtainMessage();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }
        }, 200, TIMER4RELINK);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socketDisconnect();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        IsRun = false;
        bSocketflagDemo = false;
    }

    public void sendMsgtoActivty(String msg) {
        Intent intent = new Intent(strMessageForDemo);
        intent.putExtra("msg", msg);
        sendBroadcast(intent);
    }

    //Thread for disconnect socketDemo
    public void socketDisconnect() {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (bSocketflagDemo) {
                    try {
                        inDemo.close();
                        outDemo.close();
                        socketDemo.close();
                    } catch (Exception e) {
                    }
                }
            }
        });
    }

    //Thread for connect socketDemo
    public void socketConnect() {
        socketDisconnect();
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);

                    sDemoIp = SettingConstants.DemoIp;
                    sDemoPort = SettingConstants.DemoPort;

                    socketDemo = new Socket();
                    socketDemo.setSoTimeout(100);
                    SocketAddress isa = new InetSocketAddress(sDemoIp, Integer.parseInt(sDemoPort));
                    socketDemo.connect(isa, 100);
                    inDemo = new DataInputStream(socketDemo.getInputStream());
                    outDemo = new DataOutputStream(socketDemo.getOutputStream());
                    bSocketflagDemo = true;

                    Message msg = mHandler.obtainMessage();
                    msg.what = 2;
                    mHandler.sendMessage(msg);
                } catch (Exception e) {
                    bSocketflagDemo = false;

                    Message msg = mHandler.obtainMessage();
                    msg.what = 3;
                    mHandler.sendMessage(msg);
                }
            }
        });
    }

    //Thread for test socketDemo
    public void socketTest() {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (bSocketflagDemo) {
                    try {
                        //socketDemo.sendUrgentData(0xFF);//connected test
                        outDemo.write(0xFF);
                    } catch (Exception e) {
                        socketConnect();
                    }
                } else {
                    socketConnect();
                }
            }
        });
    }

    //Thread for send socketDemo
    public void socketSend(byte[] data) {
        dataSend = data;
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (bSocketflagDemo) {
                    try {
                        //outDemo.writeBytes(new String(dataSend, "ISO-8859-1"));
                        outDemo.write(dataSend, 0, dataSend.length);
                    } catch (Exception e) {
                    }
                }
            }
        });
    }

    //Thread for receive socketDemo
    public void socketRecv() {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
            while (IsRun) {
                try {
                    if (bSocketflagDemo) {
                        int iCount = inDemo.read(recvbuffer);
                        if (iCount != -1) {
                            byte[] data = new byte[iCount];
                            for (int i = 0; i < iCount; i++) {
                                data[i] = recvbuffer[i];
                            }
                            String str = new String(data, "ISO-8859-1");
                            sendMsgtoActivty(str);
                        }
                    }
                    Thread.sleep(100);
                } catch (Exception e) {

                }
            }
            }
        });
    }
}

package example.smart.demo;

/**
 * Created by Administrator on 2019/6/13.
 */

public class DataConstants {
    //包头
    public static final byte HEAD = (byte) 0xCC;                //发送包头
    public static final byte HEAD_RET = (byte) 0xBB;            //接收包头
    //命令类型
    public static final byte CMD_CTRL = (byte) 0x01;            //控制命令
    public static final byte CMD_QUERY = (byte) 0x02;           //查询命令
    public static final byte CMD_INTERVAL = (byte) 0x03;        //定时收集传感器数据
    public static final byte CMD_SETLINKAGE = (byte) 0x04;      //设置联动
    public static final byte CMD_QUERYLINKAGE = (byte) 0x05;    //查询联动
    //设备类型
    public static final byte DEVTYPE_IO = (byte) 0x01;          //IO类
    public static final byte DEVTYPE_PWM = (byte) 0x02;         //PWM类
    public static final byte DEVTYPE_485 = (byte) 0x03;         //485类
    //数据类型
    public static final byte VALUETYPE_BOOL = (byte) 0x01;      //布尔类型
    public static final byte VALUETYPE_INT = (byte) 0x02;       //整型类型
    public static final byte VALUETYPE_FLOAT = (byte) 0x03;     //浮点类型
    public static final byte VALUETYPE_ARRAY = (byte) 0x04;     //数组类型
    public static final byte VALUETYPE_STRING = (byte) 0x05;    //字符串类型
    //索引编号
    public static final byte INDEX_FIRST = (byte) 0x00;         //索引号0
    public static final byte INDEX_SECOND = (byte) 0x01;        //索引号1
    public static final byte INDEX_THIRD = (byte) 0x02;         //索引号2
    public static final byte INDEX_FOURTH = (byte) 0x03;        //索引号3
    //传感器
    public static final byte SENSOR_IO_SMOG = (byte) 0x01;              //火焰|烟雾
    public static final byte SENSOR_IO_GAS = (byte) 0x02;               //燃气
    public static final byte SENSOR_IO_RAIN = (byte) 0x04;              //雨雪
    public static final byte SENSOR_IO_SHAKE = (byte) 0x05;             //震动
    public static final byte SENSOR_IO_INFRARED = (byte) 0x06;          //红外警戒
    public static final byte SENSOR_IO_DOPPLER = (byte) 0x07;           //多普勒
    public static final byte SENSOR_IO_BUTTON = (byte) 0x08;            //紧急按钮
    public static final byte SENSOR_IO_DOORCONTACT = (byte) 0x09;       //门磁
    public static final byte SENSOR_IO_SOILTEMP = (byte) 0x0B;          //土壤温度
    public static final byte SENSOR_IO_VOICE = (byte) 0x11;             //声音
    public static final byte SENSOR_IO_ILLUMINATION = (byte) 0x12;      //光照
    public static final byte SENSOR_IO_TRAFFICLIGHT = (byte) 0x13;      //交通灯
    public static final byte SENSOR_IO_LEVER = (byte) 0x14;             //舵机

    public static final byte SENSOR_PWM_LAMP = (byte) 0x01;             //12V调光灯
    public static final byte SENSOR_PWM_VALVE = (byte) 0x02;            //12V电磁阀
    public static final byte SENSOR_PWM_POWERONLOCK = (byte) 0x03;      //12V电磁锁
    public static final byte SENSOR_PWM_ALARMLIGHT = (byte) 0x05;       //12V告警灯
    public static final byte SENSOR_PWM_FAN = (byte) 0x06;              //12V风扇
    public static final byte SENSOR_PWM_SOCKET = (byte) 0x07;           //插座

    public static final byte SENSOR_485_ILLUMINATION = (byte) 0x01;     //光照
    public static final byte SENSOR_485_TEMP = (byte) 0x02;             //温度
    public static final byte SENSOR_485_HUMI = (byte) 0x03;             //湿度
    public static final byte SENSOR_485_RFID = (byte) 0x06;             //RFID门禁
    public static final byte SENSOR_485_ELECMETER = (byte) 0x07;        //电表
    public static final byte SENSOR_485_MP3 = (byte) 0x09;              //MP3播放器
    public static final byte SENSOR_485_CO2 = (byte) 0x0A;              //二氧化碳
    public static final byte SENSOR_485_FPLOCK = (byte) 0x1B;           //指纹锁
    public static final byte SENSOR_485_NOISE = (byte) 0x24;            //噪声
    public static final byte SENSOR_485_AIRQUALITY = (byte) 0x26;       //空气质量
    public static final byte SENSOR_485_WINDDIRECTION = (byte) 0x27;    //风向
    public static final byte SENSOR_485_WINDSPEED = (byte) 0x28;        //风速
    public static final byte SENSOR_485_PH = (byte)0x29;                //PH
    public static final byte SENSOR_485_SOILTEMP = (byte)0x2A;          //土壤温度
    public static final byte SENSOR_485_SOILHUMI = (byte)0x2B;          //土壤湿度
    public static final byte SENSOR_485_GASMETER = (byte) 0x2C;         //燃气表
    public static final byte SENSOR_485_900M = (byte) 0x2D;             //900M读卡器
    public static final byte SENSOR_485_MULTICHANNELRFID = (byte) 0x2E; //多通道读卡器
    public static final byte SENSOR_485_3IN1AIRPRESSURE = (byte) 0x2F;  //大气压力(三合一)
    public static final byte SENSOR_485_3IN1TEMP = (byte) 0x30;         //温度(三合一)
    public static final byte SENSOR_485_3IN1HUMI = (byte) 0x31;         //湿度(三合一)
    public static final byte SENSOR_485_8RELAYS = (byte) 0xA0;          //标准8路继电器
    public static final byte SENSOR_485_4RELAYS = (byte) 0xA0;          //标准8路继电器
    /************ 无线传感 结束************/
}

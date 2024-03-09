package example.smart.demo;

/**
 * Created by Administrator on 2019/4/11.
 */

public class DataFormat {
    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
                .byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
                .byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    public static byte[] hexString2Bytes(String src) {
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < tmp.length / 2; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    public static String bytes2HexString(byte[] bytes) {
        String ret = "";
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    public static int char2int(char c) {
        if (c >= '0' && c <= '9')
            return (int) (c - '0');
        else if (c <= 'f' && c >= 'a')
            return (int) (c - 'a' + 10);
        else if (c <= 'F' && c >= 'A')
            return (int) (c - 'A' + 10);
        return -1;
    }

    // 异或校验
    public static byte xorVerify(byte[] BytesData) {
        int i, uSum = BytesData[1];
        for (i = 2; i < BytesData.length - 1; i++) {
            uSum ^= BytesData[i];
        }
        return (byte) uSum;
    }

    // CRC校验
    public static boolean checkCRC(byte[] BytesFrame) {
        int Preset_Value = 0xFFFF;
        int Polynomial = 0xA001;
        for (int i = 0; i < BytesFrame.length - 2; i++) {
            Preset_Value ^= (BytesFrame[i] & 0xff);
            for (int j = 0; j < 8; j++) {
                if ((Preset_Value & 0x0001) != 0) {
                    Preset_Value = (Preset_Value >> 1) ^ Polynomial;
                } else {
                    Preset_Value = (Preset_Value >> 1);
                }
            }
        }
        byte retH = (byte) ((Preset_Value & 0xFF00) >> 8);
        byte retL = (byte) (Preset_Value & 0xFF);
        if (retL == BytesFrame[BytesFrame.length - 2]
                && retH == BytesFrame[BytesFrame.length - 1])
            return true;
        else
            return false;
    }

    // 设置校验
    public static void setCRC(byte[] BytesFrame) {
        int Preset_Value = 0xFFFF;
        int Polynomial = 0xA001;
        for (int i = 0; i < BytesFrame.length - 2; i++) {
            Preset_Value ^= (BytesFrame[i] & 0xff);
            for (int j = 0; j < 8; j++) {
                if ((Preset_Value & 0x0001) != 0) {
                    Preset_Value = (Preset_Value >> 1) ^ Polynomial;
                } else {
                    Preset_Value = (Preset_Value >> 1);
                }
            }
        }
        BytesFrame[BytesFrame.length - 2] = (byte) (Preset_Value & 0xFF);
        BytesFrame[BytesFrame.length - 1] = (byte) ((Preset_Value & 0xFF00) >> 8);
    }
}

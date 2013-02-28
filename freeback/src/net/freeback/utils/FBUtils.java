package net.freeback.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

public class FBUtils {

    public static java.sql.Date strToDate(String dateString) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            java.util.Date utilDate = formatter.parse(dateString);
            return new java.sql.Date(utilDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    static public char[] encode(byte[] data) {
        char[] out = new char[((data.length + 2) / 3) * 4];

        for (int i = 0, index = 0; i < data.length; i += 3, index += 4) {
            boolean quad = false;
            boolean trip = false;
            int val = (0xFF & (int) data[i]);
            val <<= 8;
            if ((i + 1) < data.length) {
                val |= (0xFF & (int) data[i + 1]);
                trip = true;
            }
            val <<= 8;
            if ((i + 2) < data.length) {
                val |= (0xFF & (int) data[i + 2]);
                quad = true;
            }
            out[index + 3] = alphabet[(quad ? (val & 0x3F) : 64)];
            val >>= 6;
            out[index + 2] = alphabet[(trip ? (val & 0x3F) : 64)];
            val >>= 6;
            out[index + 1] = alphabet[val & 0x3F];
            val >>= 6;
            out[index + 0] = alphabet[val & 0x3F];
        }
        return out;
    }

    /**
     * ��base64�������ݽ����ԭʼ���
     */
    static public byte[] decode(char[] data) {
        int len = ((data.length + 3) / 4) * 3;
        if (data.length > 0 && data[data.length - 1] == '=')
            --len;
        if (data.length > 1 && data[data.length - 2] == '=')
            --len;
        byte[] out = new byte[len];
        int shift = 0;
        int accum = 0;
        int index = 0;
        for (int ix = 0; ix < data.length; ix++) {
            int value = codes[data[ix] & 0xFF];
            if (value >= 0) {
                accum <<= 6;
                shift += 6;
                accum |= value;
                if (shift >= 8) {
                    shift -= 8;
                    out[index++] = (byte) ((accum >> shift) & 0xff);
                }
            }
        }
        if (index != out.length)
            throw new Error("miscalculated datahelper length!");
        return out;
    }

    static private char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();

    static private byte[] codes = new byte[256];

    static {
        for (int i = 0; i < 256; i++)
            codes[i] = -1;
        for (int i = 'A'; i <= 'Z'; i++)
            codes[i] = (byte) (i - 'A');
        for (int i = 'a'; i <= 'z'; i++)
            codes[i] = (byte) (26 + i - 'a');
        for (int i = '0'; i <= '9'; i++)
            codes[i] = (byte) (52 + i - '0');
        codes['+'] = 62;
        codes['/'] = 63;
    }

    public byte[] read(ByteBuffer buffer) {
        int size = buffer.getInt();
        byte[] b = new byte[size];
        buffer.get(b);

        return b;
    }

    public static int byteToInt(byte[] b) {
        int mask = 0xff;
        int temp = 0;
        int n = 0;
        for (int i = 0; i < 4; i++) {
            n <<= 8;
            temp = b[i] & mask;
            n |= temp;
        }
        return n;
    }

    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) ((i >>> 24) & 0xFF);
        result[1] = (byte) ((i >>> 16) & 0xFF);
        result[2] = (byte) ((i >>> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

    public static String byteToString(byte[] b) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            stringBuffer.append((char) b[i]);
        }
        return stringBuffer.toString();
    }

    public static String serialize(Object obj) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objOutputStream = new ObjectOutputStream(
                outputStream);
        objOutputStream.writeObject(obj);
        objOutputStream.flush();
        objOutputStream.close();
        return new String(encode(outputStream.toByteArray()));
    }

    public static Object deserialize(String inputString) throws IOException,
            ClassNotFoundException {
        byte[] buffer = decode(inputString.toCharArray());
        ByteArrayInputStream byteArrayInput = new ByteArrayInputStream(buffer);
        ObjectInputStream inputStream = new ObjectInputStream(byteArrayInput);
        return inputStream.readObject();
    }

    public static void writeFile(String fileName, String base64File)
            throws IOException {
        byte[] data = decode(base64File.toCharArray());
        FileOutputStream fos = new FileOutputStream(fileName);
        fos.write(data);
        fos.flush();
        fos.close();
    }

    public static String readFile(String fileName) throws IOException {
        String result = "";
        File file = new File(fileName);
        FileInputStream fs = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fs.read(data);
        result = new String(encode(data));
        return result;
    }

    public static void writeObject(String fileFullName, Object serializeObj) {
        try {
            String path = fileFullName.substring(0,
                    fileFullName.lastIndexOf("/"));
            createDirectory(path);
            String inputString = serialize(serializeObj);
            writeFile(fileFullName, inputString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object readObject(String fileName) throws IOException,
            ClassNotFoundException {
        return deserialize(readFile(fileName));
    }

    public static Object readObject(File file) {
        FileInputStream fs;
        try {
            fs = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fs.read(data);
            fs.close();
            return deserialize(new String(encode(data)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists())
            file.delete();
    }

    public static void createDirectory(String dirName) {
        File file = new File(dirName);
        if (file.exists())
            return;
        file.mkdirs();
    }

    public static String getFileName(String fullFileName){
        int start = fullFileName.lastIndexOf("/");
        String photoFileName =fullFileName.substring(start + 1, fullFileName.length() - start - 1);
        return photoFileName;
    }


    /**
     * �õ�����ǰ��ʱ��
     */

    public static Date getDateBefore(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        return now.getTime();
    }

    public static Date stringToDate() {
        try {
            return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2009-08-08 06:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new java.util.Date();
    }

    public static Date stringToDate(String strDate) {
        try {
            return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);
        } catch (ParseException e) {
            return stringToDate();
        }
    }

    public static String dateToString(java.util.Date date) {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public static String FileNameByNow() {
        return new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
    }

    public static String convertToYmd(Date date) {
        return new java.text.SimpleDateFormat("yyyyMMdd").format(date);
    }

    public static String convertToYmd() {
        return new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
    }

    // ���key��ȡvalue
    public static String readValue(String filePath, String key) {
        Properties props = new Properties();
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(filePath));
            props.load(in);
            String value = props.getProperty(key);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // д��properties��Ϣ
    public static void writeProperties(String filePath, String parameterName,
                                       String parameterValue) {
        Properties prop = new Properties();
        try {
            InputStream fis = new FileInputStream(filePath);
            prop.load(fis);
            OutputStream fos = new FileOutputStream(filePath);
            prop.setProperty(parameterName, parameterValue);
            prop.store(fos, "Update '" + parameterName + "' value");
        } catch (IOException e) {
            System.err.println("Visit " + filePath + " for updating "
                    + parameterName + " value error");
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println(String.format("%03d", 1));
        // IoBuffer in = IoBuffer.allocate(20).setAutoExpand(true);
        // Charset charset = Charset.forName("utf-8");
        // in.putInt(66);
        // in.putInt(124567890);
        // String str = "ABCDE";
        // in.putString(str, charset.newEncoder());
        // in.rewind();
        // System.out.println(in.getInt());
        // System.out.println(in.getInt());
        // System.out.println(in.getString(2, charset.newDecoder()));
        // System.out.println(in.getString(4, charset.newDecoder()));
        // //���ܳ�base64
        // String strSrc = "zhaotyzhangchunyan";
        // String strOut = new String(FileBase64.encode(strSrc.getBytes()));
        // System.out.println(strOut);
        //
        // String strOut2 = new String(FileBase64.decode(strOut.toCharArray()));
        // System.out.println(strOut2);
        //
        // MobEmail mEmail = new MobEmail();
        // mEmail.setCategory(1);
        // mEmail.setEmail("lcsoftware@126.com");
        // mEmail.setId(1111);
        // mEmail.setPerson(1);
        // mEmail.setUpdatetime(new Date());
        // String str = FileBase64.serialize(mEmail);
        // MobEmail sEmail = (MobEmail)FileBase64.deserialize(str);
        // System.out.println(sEmail.getEmail());
        // System.out.println(sEmail.getCategory());
    }

}

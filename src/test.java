import java.io.*;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;

import javafx.util.Pair;


public class test {

    public static void main(String args[]) throws IOException, InterruptedException {
//        byte[] b = {1, 0, 1, 1};
//        BitSet bb = BitSet.valueOf(b);
////        for (int i = 0; i < 4 * 8; i+=8){
////            for (int j = 7; j >= 0; j--){
////                if(bb.get(i + j)){
////                    System.out.print("1");
////                }else{
////                    System.out.print("0");
////                }
////            }
////        }
//        System.out.println(bb);

//        File fff = new File("t.png");
//        FileInputStream fileInputStream = new FileInputStream(fff);
//        int byteLength = (int) fff.length();
//        byte[] filecontent = new byte[byteLength];
//        fileInputStream.read(filecontent, 0, byteLength);
//        fileInputStream.close();
//
//        SF s = new SF();
//
//        byte[] encoded = s.encode(filecontent);
//
//
//        fff = new File("tt.png");
//        FileOutputStream fileOutputStream = new FileOutputStream(fff);
//        byteLength = (int) fff.length();
//        fileOutputStream.write(s.decode(encoded, s.getDictionary()));
//        fileOutputStream.close();

        Repetition rp = new Repetition(3);
        byte[] b = new byte[]{1,2};
        byte [] encode = rp.encode(b);
        System.out.println(Arrays.toString(encode));
        System.out.println(Arrays.toString(rp.decode(encode, null)));
    }
}

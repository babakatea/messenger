import java.io.*;
import java.util.HashMap;

import javafx.util.Pair;


public class test {

    public static void main(String args[]) throws IOException, InterruptedException {
//        System.out.println('1');
//        PythonInterpreter python = new PythonInterpreter();
//        System.out.println('2');
//        int number1 = 10;
//        int number2 = 32;
//        python.execfile("python_code/repetition.py");

        File fff = new File("kek.txt");
        FileInputStream fileInputStream = new FileInputStream(fff);
        int byteLength = (int) fff.length();
        byte[] filecontent = new byte[byteLength];
        fileInputStream.read(filecontent, 0, byteLength);
        fileInputStream.close();

        SFE s = new SFE();

        Pair<byte[], HashMap<String, Byte>> encoded = s.encode(filecontent);


        fff = new File("kek_t.txt");
        FileOutputStream fileOutputStream = new FileOutputStream(fff);
        byteLength = (int) fff.length();
        fileOutputStream.write(s.decode(encoded.getKey(), encoded.getValue()));
        fileOutputStream.close();


//        python.set("number1", new PyInteger(number1));
//        python.set("number2", new PyInteger(number2));
//        python.exec("number3 = number1+number2");
//        PyObject number3 = python.get("number3");
//        System.out.println("val : "+number3.toString());
    }
}

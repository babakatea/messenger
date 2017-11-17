import java.io.UnsupportedEncodingException;
import java.util.*;

public class LZW  {

    public static byte[] compress(byte[] uncompressed) throws UnsupportedEncodingException {
        // Build the dictionary.
        String input = new String(uncompressed,"UTF-8");
        int dictSize = 256;
        Map<String,Integer> dictionary = new HashMap<String,Integer>();

        /*Filling dict with ascii chars*/
        for (int i = 0; i < 256; i++)
            dictionary.put("" + (char)i, i);

        String w = "";
        List<Integer> result = new ArrayList<Integer>();

        for (char c : input.toCharArray()) {
            String wc = w + c;
            //if input contains word from a dict,then just remember it until unknown input
            if (dictionary.containsKey(wc))
                w = wc;
            //otherwise append dist to previous one to res
            else {
                result.add(dictionary.get(w));
                // Add wc to the dictionary.
                dictionary.put(wc, dictSize++);

                w = "" + c;
            }

        }

        // Output the code for w.
        if (!w.equals(""))
            result.add(dictionary.get(w));

        byte[] output=list_to_bytearr(result);

        return output;
    }


    public static byte[] decompress(byte[] compressed) throws UnsupportedEncodingException {
        // Build the dictionary.
        List<Integer> input = bytearr_to_list(compressed);

        int dictSize = 256;
        Map<Integer,String> dictionary = new HashMap<Integer,String>();
        for (int i = 0; i < 256; i++)
            dictionary.put(i, "" + (char)i);

        String w = "" + (char)(int)input.remove(0);
        StringBuffer result = new StringBuffer(w);
        for (int k : input) {
            String entry;
            if (dictionary.containsKey(k))
                entry = dictionary.get(k);
            else if (k == dictSize)
                entry = w + w.charAt(0);
            else
                throw new IllegalArgumentException("Bad input k: " + k);

            result.append(entry);

            // Add w+entry[0] to the dictionary.
            dictionary.put(dictSize++, w + entry.charAt(0));

            w = entry;
        }

        String out_str = result.toString();

        return out_str.getBytes("UTF-8");
    }

    //Func to convert list of int to bytearr
    public static byte[] list_to_bytearr(List<Integer> input) throws UnsupportedEncodingException{
        byte[] output ;
        Integer i = 0;
        String ref = "";
        String output_str = "";
        //First convert list to string
        for (int k:input){
                ref = ""+(char)k;
            output_str += ref;
            i++;
        }
        //Then to bytearr
        output = output_str.getBytes("UTF-8");

        return  output;
    }

    public static List<Integer> bytearr_to_list(byte[] input) throws UnsupportedEncodingException{
        List<Integer> output = new ArrayList<Integer>();

        String input_str = new String(input,"UTF-8");

        for (Integer i=0;i<input_str.length();i++){


               output.add( (int)input_str.charAt(i));

        }

        return  output;
    }

}


import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

public class LZ77  {

    private char refprefix;
    private int refintBase;
    private int refintFloorCode;
    private int refintCeilCode;
    private int maxStrDist;
    private int minStringLength;
    private int maxStringLength;
    private int defaultWindowLength;
    private int maxWindowLength;



    public LZ77() {

        refprefix = '`';
        refintBase = 96;
        refintFloorCode = (int) ' ';
        refintCeilCode = refintFloorCode + refintBase;
        maxStrDist = (int) Math.pow(refintBase, 2) - 1;
        minStringLength = 5;
        maxStringLength = (int) Math.pow(refintBase, 1) - 1
                + minStringLength;
        defaultWindowLength = 144;
        maxWindowLength = maxStrDist + minStringLength;
    }



    public byte[] compress(byte[] input) throws UnsupportedEncodingException {

        /*Giving the input as byte array converting it to a string*/
        String data = new String(input,"UTF-8");
        //System.out.println(data);

        int windowLength = defaultWindowLength;


        String compressed = "";

        int pos = 0;
        int lastPos = data.length() - minStringLength;

        /*Main algorithm*/
        while (pos < lastPos) {

            int searchStart = Math.max(pos - windowLength, 0);
            int matchLength = minStringLength;
            boolean foundMatch = false;
            int bestMatchDistance = maxStrDist;
            int bestMatchLength = 0;
            String newCompressed = null;

            while ((searchStart + matchLength) < pos) {

                int sourceWindowEnd = Math.min(searchStart + matchLength, data
                        .length());

                int targetWindowEnd = Math
                        .min(pos + matchLength, data.length());

                /*Founding strings in Lookahead Buffer and in search buffer*/
                String m1 = data.substring(searchStart, sourceWindowEnd);
                String m2 = data.substring(pos, targetWindowEnd);

                /*Compare them*/
                boolean isValidMatch = m1.equals(m2)
                        && matchLength < maxStringLength;

                if (isValidMatch) {

                    matchLength++;
                    foundMatch = true;

                } else {

                    int realMatchLength = matchLength - 1;

                    if (foundMatch && (realMatchLength > bestMatchLength)) {
                        bestMatchDistance = pos - searchStart - realMatchLength;
                        bestMatchLength = realMatchLength;
                    }

                    matchLength = minStringLength;
                    searchStart++;
                    foundMatch = false;
                }
            }

            /*If found match*/
            if (bestMatchLength != 0) {

                /*Instead of writing <dist,length,var> in string we are transforming dist and length to char*/
                newCompressed = refprefix
                        + encodeReferenceInt(bestMatchDistance, 2)
                        + encodeReferenceLength(bestMatchLength);

                pos += bestMatchLength;


            }

            /*Else just write a new var in result str*/
            else {

                if (data.charAt(pos) != refprefix) {
                    newCompressed = "" + data.charAt(pos);
                } else {
                    newCompressed = "" + refprefix + refprefix;
                }

                pos++;
            }
            compressed += newCompressed;
        }

        compressed = compressed + data.substring(pos).replaceAll("/`/g", "``");

        /*Now convert string back to byte arrray*/
        byte[] output = compressed.getBytes("UTF-8");

        return output;


    }


    public byte[] decompress(byte[] input) throws UnsupportedEncodingException{
        String data = new String(input,"UTF-8");
        String decompressed = "";
        int pos = 0;

        while (pos < data.length()) {

            char currentChar = data.charAt(pos);

            /*If not a refprefix, than just copy char to result output*/
            if (currentChar != refprefix) {

                decompressed += currentChar;
                pos++;

            } else {

                /*Else decode encoded substring*/
                char nextChar = data.charAt(pos + 1);

                if (nextChar != refprefix) {

                    int distance = decodeRefInt(data.substring(pos + 1,
                            pos + 3), 2);

                    int length = decodeRefLen(data.substring(pos + 3,
                            pos + 4));

                    int start = decompressed.length() - distance - length;
                    int end = start + length;
                    decompressed += decompressed.substring(start, end);
                    pos += minStringLength - 1;

                } else {

                    decompressed += refprefix;
                    pos += 2;
                }
            }
        }


        byte[] output = decompressed.getBytes("UTF-8");

        return output;

    }



    private String encodeReferenceInt(int value, int width) {

        if ((value >= 0) && (value < (Math.pow(refintBase, width) - 1))) {

            String encoded = "";

            while (value > 0) {
                char c = (char) ((value % refintBase) + refintFloorCode);
                encoded = "" + c + encoded;
                value = (int) Math.floor(value / refintBase);
            }

            int missingLength = width - encoded.length();

            for (int i = 0; i < missingLength; i++) {
                char c = (char) refintFloorCode;
                encoded = "" + c + encoded;
            }

            return encoded;

        } else {

            throw new IllegalArgumentException("Reference int out of range: "
                    + value + " (width = " + width + ")");
        }
    }

    private String encodeReferenceLength(int length) {

        return encodeReferenceInt(length - minStringLength, 1);
    }

    private int decodeRefInt(String data, int width) {

        int value = 0;

        for (int i = 0; i < width; i++) {

            value *= refintBase;

            int charCode = (int) data.charAt(i);

            if ((charCode >= refintFloorCode)
                    && (charCode <= refintCeilCode)) {

                value += charCode - refintFloorCode;

            } else {

                throw new RuntimeException(
                        "Wrong char in reference int: " + charCode);
            }
        }

        return value;
    }

    private int decodeRefLen(String data) {

        return decodeRefInt(data, 1) + minStringLength;
    }


    public static String ByteToString(byte[] input){
        String result = "";

        for ( int i = 0; i < input.length; i++){
            if ( input[i] == 1)
            { result+= "1"; }
            else
            { result+="0"; }
        }

        return result;
    }

    public static byte[] StringToByte(String message){
        byte[] byteArray = new byte[ message.length() ];
        for ( int i = 0; i < message.length(); i++){
            if ( message.charAt(i) == '1' )
            { byteArray[i] = 1; }
            else
            { byteArray[i] = 0; }
        }
        return byteArray;
    }


}


import javafx.util.Pair;
import org.python.antlr.ast.Str;

import java.util.*;

public class SFE {
    final int BYTE_SIZE = 8;

    public SFE() {
    }

    private static String floatToBinaryString(double n) {
        String val = "";    // Setting up string for result
        int LIM = 16;
        while (n > 0 && val.length() < LIM) {
            // While the fraction is greater than zero (not equal or less than zero)
            double r = n * 2; // Multiply current fraction (n) by 2
            if (r >= 1) {      // If the ones-place digit >= 1
                val += "1";       // Concat a "1" to the end of the result string (val)
                n = r - 1;        // Remove the 1 from the current fraction (n)
            } else {              // If the ones-place digit == 0
                val += "0";       // Concat a "0" to the end of the result string (val)
                n = r;            // Set the current fraction (n) to the new fraction
            }
        }
        while (val.length() < LIM) {
            val += "0";

        }
        return val;          // return the string result with all appended binary values
    }

    class ReverseValueMapComparator implements Comparator<Byte> {

        Map map;

        public ReverseValueMapComparator(Map<Byte, Integer> map) {
            this.map = map;
        }

        @Override
        public int compare(Byte o1, Byte o2) {
            int c = -((Integer) map.get(o2)).compareTo((Integer) map.get(o1));
            return c == 0 ? 1 : c;
        }
    }

    public TreeMap<Byte, Double> getTable(byte input[]) {
        HashMap<Byte, Integer> reference = new HashMap<>();
        double tableSize = 0.0;
        for (byte b : input) {
            int count = reference.get(b) == null ? 0 : reference.get(b);
            reference.put(b, count + 1);
            tableSize += 1.0;
        }

        TreeMap<Byte, Double> frequencies = new TreeMap<>(new ReverseValueMapComparator(reference));

        Set<Byte> bytes = reference.keySet();
        for (byte b : reference.keySet()) {
            frequencies.put(b, reference.get(b) / tableSize);
        }


        return frequencies;
    }

    private Pair<HashMap<Byte, String>, HashMap<String, Byte>> getDictionaries(byte[] input) {
        TreeMap<Byte, Double> table = getTable(input);
        HashMap<Byte, String> dictionary = new HashMap<>();
        HashMap<String, Byte> decompressingDict = new HashMap<>();

        LinkedList<Byte> codes = new LinkedList<>(table.keySet());
        LinkedList<Double> probabilities = new LinkedList<>(table.values());
        double sum = 0.0;
        int i = 0;
        for (double prob : probabilities) {
            int significantBits = (int) Math.abs(Math.log(1 / prob) / Math.log(2.0)) + 1;

            byte b = codes.get(i);
            String s = floatToBinaryString(sum + prob * 0.5).substring(0, significantBits);
            dictionary.put(b, s);
            decompressingDict.put(s, b);

            sum += prob;
            i++;
        }
        return new Pair<>(dictionary, decompressingDict);
    }

    public Pair<byte[], HashMap<String, Byte>> encode(byte[] input) {
        Pair<HashMap<Byte, String>, HashMap<String, Byte>> dicts = getDictionaries(input);
        HashMap<Byte, String> dict = dicts.getKey();
        HashMap<String, Byte> decompressingDict = dicts.getValue();

        String codedStr = "";
        for (byte anInput : input) {
            codedStr += dict.get(anInput);
        }

        int paddingLen = 8 - codedStr.length() % 8;
        codedStr = String.format("%0" + paddingLen + "d", 1) + codedStr;

        byte[] coded = new byte[codedStr.length() / 8];
        for (int i = 0; i < codedStr.length(); i += 8) {
            String s = codedStr.substring(i, i + 8);
            if (s.charAt(0) == '1') {
                s = "-" + s.substring(1);
            }

            coded[i / 8] += Byte.parseByte(s, 2);
        }

        return new Pair<>(coded, decompressingDict);
    }

    public byte[] decode(byte[] input, HashMap<String, Byte> dict) {
        ArrayList<Byte> decoded = new ArrayList<>();
        String codes = "";

        for (byte b : input) {

            String bs = "";
            if (b < 0) {
                b = (byte) Math.abs(b);
                bs = "1";
            }
            String rawBs = String.format("%8s", Integer.toBinaryString(b)).replace(' ', '0');
            bs += rawBs.substring(bs.equals("1") ? 1 : 0);

            codes += bs;
        }


        codes = codes.substring(codes.indexOf("1") + 1); // Delete padding

        String code = "";
        for (int i = 0; i < codes.length(); i++) {
            code += codes.charAt(i);
            if (dict.containsKey(code)) {
                decoded.add(dict.get(code));
                code = "";
            }
        }

        Byte[] bbytes = decoded.toArray(new Byte[1]);
        byte[] bytes = new byte[bbytes.length];
        for (int i = 0; i < bbytes.length; i++) bytes[i] = bbytes[i];

        return bytes;
    }
}
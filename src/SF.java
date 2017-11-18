import java.util.*;
import java.util.Map.Entry;

public class SF implements IAlgorithm {
    private HashMap<String, Byte> decodingDictionary;

    public byte[] encode(byte[] input) {
        List<Byte> byteList = new ArrayList<>();
        HashMap<Byte, Double> freq = calculateFrequencies(input);

        for (Entry<Byte, Double> entry : freq.entrySet()) {
            byteList.add(entry.getKey());
        }
        HashMap<Byte, String> dic = new HashMap<>();

        prepareCodes(dic, byteList, true);

        String coded = "";
        for (Byte b : input) {
            coded += dic.get(b);
        }

        int paddingLen = 8 - coded.length() % 8;
        coded = String.format("%0" + paddingLen + "d", 1) + coded;

        BitSet bCoded = new BitSet(coded.length());
        int i = 0;
        for (char c : coded.toCharArray()) {
            if (c == '1') {
                bCoded.flip(i);
            }
            i++;
        }

        decodingDictionary = reverseDictionary(dic);
        return bCoded.toByteArray();
    }

    public byte[] decode(byte[] input, Object oDict) {
        HashMap<String, Byte> dict = (HashMap<String, Byte>) oDict;

        BitSet bCoded = BitSet.valueOf(input);
        LinkedList<Byte> bDecoded = new LinkedList<>();

        int i = 0;
        while (!bCoded.get(i)) {
            i++;
        }

        String code = "";
        for (i++; i < input.length * 8; i++) {
            code += bCoded.get(i) ? '1' : '0';
            if (dict.containsKey(code)) {
                bDecoded.add(dict.get(code));
                code = "";
            }
        }

        Byte[] bbytes = bDecoded.toArray(new Byte[1]);
        byte[] bytes = new byte[bbytes.length];
        for (i = 0; i < bbytes.length; i++) {
            bytes[i] = bbytes[i];
        }

        return bytes;
    }

    @Override
    public Object getDictionary() {
        return decodingDictionary;
    }

    /**
     * Recursive algorithm to prepare codes for each byte
     * based on Shannon-Fano algorithm.
     *
     * @param result   - all bytes corresponded to its codes
     * @param byteList - list of all bytes, service variable for recursion
     * @param up       - to move up list or down list, service variable for recursion
     */
    private void prepareCodes(HashMap<Byte, String> result, List<Byte> byteList, boolean up) {
        String bit = "";
        if (!result.isEmpty()) {
            bit = (up) ? "0" : "1";
        }

        for (Byte b : byteList) {
            String s = (result.get(b) == null) ? "" : result.get(b);
            result.put(b, s + bit);
        }

        if (byteList.size() >= 2) {
            int separator = (int) Math.floor((float) byteList.size() / 2.0);

            List<Byte> upList = byteList.subList(0, separator);
            prepareCodes(result, upList, true);
            List<Byte> downList = byteList.subList(separator, byteList.size());
            prepareCodes(result, downList, false);
        }
    }

    /**
     * Calculates frequencies of different bytes in input.
     *
     * @param input bytes to count frequencies of
     * @return map with byte corresponded to thw amount of its entries to input
     */
    private HashMap<Byte, Double> calculateFrequencies(byte[] input) {
        HashMap<Byte, Double> freqs = new HashMap<>();

        for (byte b : input) {
            if (freqs.containsKey(b)) {
                freqs.put(b, freqs.get(b) + 1.0);
            } else {
                freqs.put(b, 1.0);
            }
        }
        return freqs;
    }

    /**
     * Reverse encoding dictionary to decoding one.
     *
     * @param dict to be reversed
     * @return reversed dictionary
     */
    private static HashMap<String, Byte> reverseDictionary(HashMap<Byte, String> dict) {
        HashMap<String, Byte> reversedDict = new HashMap<>();
        for (Entry<Byte, String> entry : dict.entrySet()) {
            reversedDict.put(entry.getValue(), entry.getKey());
        }
        return reversedDict;
    }
}
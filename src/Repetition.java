import java.util.BitSet;

public class Repetition implements IAlgorithm {
    private int repetition;

    public Repetition(int repetition) {
        this.repetition = repetition;
    }

    /**
     * Function that converts input to bits, then repeats bits N times and returns it as byte array.
     * @param input array of bytes
     * @return repeated bits converted to array of bytes
     */
    public byte[] encode(byte[] input) {
        BitSet codedBits = new BitSet();
        BitSet bitsToCode = BitSet.valueOf(input);
        for (int i = 0; i < input.length * 8; i++) {
            if (bitsToCode.get(i)) {
                int start = i * repetition;
                codedBits.flip(start, start + repetition);
            }
        }
        return codedBits.toByteArray();
    }

    @Override
    public Object getDictionary() {
        return null;
    }

    /**
     * Function that transform array of bytes in bits, then returns initial input as array of bytes.
     * @param input array of bytes
     * @param redundantParam dictionary used only by compressing algorithms
     * @return bits converted to array of bytes
     */
    public byte[] decode(byte[] input, Object redundantParam) {
        BitSet decodedBits = new BitSet();
        BitSet bitsToDecode = BitSet.valueOf(input);
        int k = 0;
        for (int i = 0; i <= input.length * 8; i += 1) {
            if (i % repetition == 0) {
                if (k > repetition / 2) {
                    decodedBits.flip(i / repetition - 1);
                }
                k = 0;
            }

            if (bitsToDecode.get(i)) {
                k++;
            }
        }

        return decodedBits.toByteArray();
    }
}
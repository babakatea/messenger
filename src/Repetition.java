import java.util.BitSet;

public class Repetition {
    private int repetition;

    public Repetition(int repetition) {
        this.repetition = repetition;
    }

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

    public byte[] decode(byte[] input) {
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
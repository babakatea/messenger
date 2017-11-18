public interface IAlgorithm {
    byte[] encode(byte[] data);

    byte[] decode(byte[] data, Object dictionary);

    Object getDictionary();
}

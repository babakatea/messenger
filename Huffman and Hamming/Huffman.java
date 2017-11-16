

import com.sun.imageio.spi.RAFImageInputStreamSpi;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.util.Arrays.*;


public class Huffman implements Algorithm {
    /* SizeOfWord is equal to number of bits reqred to encode one symbol */
    private Integer SizeOfWord;

    private boolean asciiInput;

    /* Nessesary for correct work with text in asci */
    private boolean asci;

    /* dictionary = {word : encodedWord} */
    private Map<String, String> dictionary = new HashMap<>();

    /* HashMap stores frequencies of character of message { word : freq } */
    private Map<String, Integer> frequency = new HashMap<>();

    /* Initial message  */
    public byte[] input;

    private Huffman(){}

    /* Constructor
     * Gets sizeOfWord as the first argument, array of bytes as second
     * if input is an array of ASCII code, third argument is true, else is false */
    public Huffman(int size, byte[] arr, boolean ascii){
        input = arr;
        SizeOfWord = size;
        asciiInput = false;

        if ( size == 1 ){
            input = AsciiToBinary(input);
            SizeOfWord = 8;
            asciiInput = true;
        }
    }

    /* Member allows to change the amount of bits required on encoding one symbol */
    public void changeWordSize(int s){
        SizeOfWord = s;
    }

    /* Member returns array of bits of an encoded message */
    public byte[] execute(){
        frequency.clear();
        dictionary.clear();

        byte[] word = {};
        for ( int i = 0; i < input.length; i+=SizeOfWord){
            if (i + SizeOfWord <= input.length){
                /* Word is a array of bits of length 'SizeOfWord' */
                word = copyOfRange( input, i, (i + SizeOfWord) );
            }

            /* If word already've been added to the frequency HashMap, increase frequency by one */
            if ( frequency.containsKey( ByteToString(word) ) ){
                int freq = frequency.get( ByteToString(word) );
                frequency.put( ByteToString(word) ,freq + 1);
            }
            /* If word is appeared first time, add word to frequency and set it's frequency to 1 */
            else { frequency.put( ByteToString(word), 1); }
        }

        /* Calling member to build a Huffman tree */
        Node root = buildTree(frequency);

        /* Calling member to fill the dictionary */
        buildCode(root, "");

        /* Calling member create encoded message */
        byte[] code = encode( ByteToString(input) );

        return code;
    }

    /* Member returns dictionary (I can't really remember why it's like this, but it works) */
    public HashMap<String, String> getDictionary(){

        HashMap<String, String> decodeDict = new HashMap<>();
        for ( String key : dictionary.keySet() ){
            decodeDict.put( dictionary.get(key), key );
        }

        return decodeDict;
    }

    /* Member creates encoded message by substituting initial words by encoded words from dictionary */
    private byte[] encode(String input){
        String word = "";
        String message = "";
        for ( int i = 0; i < input.length(); i++) {
            word+=input.charAt(i);
            if ( dictionary.containsKey(word) ){
                message+= dictionary.get(word);
                word = "";
            }
        }
        dictionary.put("size", Integer.toString(SizeOfWord));
        StringToByte("Size: " + dictionary.get("size"));
        byte[] encodedMessage = StringToByte(message);
        return encodedMessage;
    }

    /* Member gets encodedMessage as first argument and dictionary as second argument
     * Returns decoded message as array of bytes */
    public byte[] decompress( byte[] input, HashMap<String, String> dict ){
        if ( dict.get("size") == "1" ) {
            SizeOfWord = 8;
            asciiInput = true;
        }
        else {
//            SizeOfWord = Integer.valueOf(dict.get("size"));
        }

        String decodedMessage = decode( ByteToString(input), dict );
        if (asciiInput){
            return binaryToAscii( decodedMessage );
        }
        else {
            return StringToByte( decodedMessage );
        }

    }

    /* Member called from decompress member and decodes the message */
    private String decode(String encodedMessage, HashMap<String, String> dict ){
        String word = "";
        String message = "";
        String size = dict.get("size");
        for ( int i = 0; i < encodedMessage.length(); i++ ){
            word+=encodedMessage.charAt(i);
            if ( dict.containsKey(word) ){
                message+= dict.get(word);
                word = "";
            }
        }

        return message;
    }

    private byte[] binaryToAscii(String bin){
        byte[] res = new byte[bin.length()/SizeOfWord];
        for (int i = 0; i < bin.length(); i+=SizeOfWord){
            String word = "";
            for ( int j = i; j < i+SizeOfWord; j++ ){
                word += bin.charAt(j);
            }
            res[i/SizeOfWord] = Byte.parseByte(word, 2);
        }

        return res;
    }

    private byte[] AsciiToBinary(byte[] in){

        byte[] bytes = in;
        StringBuilder binary = new StringBuilder();
        for (byte b : bytes)
        {
            int val = b;
            for (int i = 0; i < SizeOfWord; i++)
            {
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
        }

        return StringToByte( binary.toString() );
    }

    /* Member to cast byte array as a string */
    private String ByteToString(byte[] input){
        String result = "";

        for (int i = 0; i < input.length; i++) {
            if (input[i] == 1) {
                result += "1";
            }
            else {
                result += "0";
            }
        }

        return result;
    }

    /* Member to cast string to a byte array */
    private byte[] StringToByte(String message){
        byte[] byteArray = new byte[message.length()];
        for (int i = 0; i < message.length(); i++) {
            if (message.charAt(i) == '1') {
                byteArray[i] = 1;
            }
            else {
                byteArray[i] = 0;
            }
        }
        return byteArray;
    }


    /* Member building Huffmans tree
    *  As argument gets a frequencies of chars from message  */
    private Node buildTree(Map<String, Integer> freq){

        Comparator<Node> comparator = new NodeComparator();
        PriorityQueue<Node> pq = new PriorityQueue<>(comparator);

        /* For each word created node and it's added to the Priority Queue */
        for ( String key : freq.keySet()){
            Node n = new Node(key, freq.get(key), null, null);
            pq.add(n);
        }

        /* From two nodes with smallest frequencies created another node and 've been put back to Queue */
        while ( pq.size() != 1 ){
            Node left = pq.poll();
            Node right = pq.poll();
            Node parent = new Node("\0", left.getFreq() + right.getFreq(), left, right);
            pq.add(parent);
        }

        /* Returns root of the tree (last node in Queue) */
        return pq.poll();
    }

    /* Member creates code for each word using Huffman tree
     * Gets root of the tree as first argument, second argument is requared as a code word
     * Each time function is called, 0 or 1 is added to 's' untill leaf is reached, and 's' assigned as a code word for a char in leaf node */
    private void buildCode (Node x, String s){
        /* Untill 'x' node is not a leaf, all member again and add bit ti codeword */
        if ( !x.isLeaf() )
        {
            buildCode(x.left, s+"0");
            buildCode(x.right, s+"1");
        }
        /* if 'x' node is a leaf, add word form node as a key in dictionary and 's' codeword as a value */
        else{
            dictionary.put(x.word, s);
        }
    }

    /* Comparator class for a priority Queue */
    private class NodeComparator implements Comparator<Node>{
        @Override
        public int compare(Node n1, Node n2){
            /* Returns -1 if frequency of n1 node is less than n2's frequency */
            if ( n1.getFreq() < n2.getFreq() )
            { return -1; }

            /* Returns -1 if frequency of n1 node is bigger than n2's frequency */
            if ( n1.getFreq() > n2.getFreq() )
            { return 1; }

            /* If frequency of n1 node is equal to n2's frequency, return 0 */
            return 0;
        }
    }

    /* Class Node for a Huffman tree */
    private class Node {

        private String word; // word which should be encoded
        private Integer frequency; // frequency of this word
        private Node left, right; // It's offsprings

        /* Constructor for a Node class
         * 'w' is word to encode, 'freq' is frequency of this word, 'l' and 'r' it's offsprings */
        Node( String w, int freq, Node l, Node r ){
            word = w;
            frequency = freq;
            left = l;
            right = r;
        }

        /* Method returns true if node is leaf ( left and right are equal to null)
         * if not, returns false */
        private boolean isLeaf(){
            if (( left == null ) || ( right == null ))
            { return true; }
            return false;
        }

        /* Method returns frequency of a node */
        private int getFreq(){
            return frequency;
        }

    }
    
}

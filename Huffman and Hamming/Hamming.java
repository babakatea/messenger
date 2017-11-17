
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import static java.lang.Math.decrementExact;
import static java.lang.Math.pow;



public class Hamming implements Algorithm{

    /* If binaryInput is false, all byte number will be converted into binary */
    boolean asciiInput;

    public byte[] alg_input;

    /*  Hamming(4,7) is implemented, so SizeOfWord = 4, SizeOfEncodedWord = 7
     *  NumberOfProtectionBits = 3 */
    private static final int SizeOfWord = 4;
    private static final int SizeOfEncodedWord = 7;
    private static final int NumberOfProtectionBits = 3;

    /* if element of list is 1, bit with the same index
     * is summed to create protection bit */
    private static final byte[] FirstBitSum = { 1, 1, 0, 1 };
    private static final byte[] SecondBitSum = { 1, 0, 1, 1 };
    private static final byte[] ThirdBitSum = { 0, 1, 1, 1 };


    private byte[][] checkMatrixTrans = new byte[SizeOfEncodedWord][NumberOfProtectionBits];
    private byte[][] generatorMatrix = new byte[SizeOfWord][SizeOfEncodedWord];

    /* Constructor
     * As first parameter gets array of bytes
      * Second argument should be true if input is an ASCII codewords, false if just bits*/
    public Hamming(byte[] arr, boolean ascii){
        asciiInput = ascii;
        alg_input = arr;

        /* Filling the Transpond Checking Matrix */
        for (int i = 0; i < SizeOfEncodedWord; i++){
            int value = i + 1;
            for ( int j = 0; j < NumberOfProtectionBits; j++){
                checkMatrixTrans[i][j] = (byte)(value % 2);
                if ( checkMatrixTrans[i][j] == 0 ) { value /= 2; }
                else { value = (value - 1) / 2; }
            }
        }

        /* First 3 collums of the generation Matrinx
         * shows wich bits will be summed to calculate protections bits */
        generatorMatrix = appendCol(generatorMatrix, FirstBitSum, 0);
        generatorMatrix = appendCol(generatorMatrix, SecondBitSum, 1);
        generatorMatrix = appendCol(generatorMatrix, ThirdBitSum, 2);

        /* Filling the generation Matrix */
        for ( int i = 0; i < SizeOfWord; i++ ){
            for ( int j = 0; j < SizeOfWord; j++ ){
                if ( i == j ) { generatorMatrix[i][NumberOfProtectionBits + j] = 1; }
                else { generatorMatrix[i][NumberOfProtectionBits + j] = 0; }
            }
        }
    }

    public void isAscii(boolean f){
        asciiInput = f;
    }

    /* Member assign collum in matrix at collum #numberOfCollum */
    private byte[][] appendCol(byte[][] matrix, byte[] column, int numberOfColumn)
    {
        for ( int i = 0; i < SizeOfWord; i++)
        {
            matrix[i][numberOfColumn] = column[i];
        }

        return matrix;
    }

    /* Member gets array of bytes as an input and returns encoded message as array of bytes */
    public byte[] execute() {

        String encodedString = "";
        byte[] word;

        if (asciiInput == false) {
            for (int i = 0; i < alg_input.length; i += SizeOfWord) {
            /* In loop 4 bits are given to createEncodedWord member to create codeword */
                word = Arrays.copyOfRange(alg_input, i, i + SizeOfWord);
                encodedString += createEncodedWord(word);
            }
        }
        else{
            for (int i = 0; i < alg_input.length; i += 1) {
            /* In loop 4 bits are given to createEncodedWord member to create codeword */
                word = Arrays.copyOfRange(alg_input, i, i + 1);
                word = AsciiToBinary(word);
                encodedString += createEncodedWord( Arrays.copyOfRange(word,0, 4) );
                encodedString += createEncodedWord( Arrays.copyOfRange(word,4, 8) );
            }
        }

        byte[] encodedMessage = StringToByte(encodedString);

        return encodedMessage;
    }


    /* Member returns encoded Word */
    private String createEncodedWord(byte[] word ){

        String encodedWord = "";
        byte nextBit;
        for (int j = 0; j < SizeOfEncodedWord; j++){
            nextBit = 0;
            for ( int i = 0; i < SizeOfWord; i++ ){
                /* bits of encoded word are calculated by multiplication generator Matrix and the word */
                nextBit += word[i]*generatorMatrix[i][j];
            }
            encodedWord +=Integer.toString((int)nextBit % 2);
        }

        byte[] enWord = StringToByte(encodedWord);

        /* third and second bits are swapped so second protection bit will be on 2^2 position */
        byte c = enWord[2];
        enWord[2] = enWord[3];
        enWord[3] = c;

        return ByteToString(enWord);
    }

    /* Member gets encoded Message as an argument */
    public byte[] decode(byte[] encodedMessage){

        int numberOfWords = encodedMessage.length / SizeOfEncodedWord;
        String decodedString = "";
        byte[] word;
        for (int i = 0; i < encodedMessage.length; i += SizeOfEncodedWord) {
            word = Arrays.copyOfRange(encodedMessage, i, i + SizeOfEncodedWord);
            /* decodeWord function gets an encodedWord as an argument and returns decodedWord */
            decodedString += decodeWord(word);
        }

        byte[] dencodedMessage = binaryToAscii(decodedString);
        return dencodedMessage;
    }

    /* Method gets encoded word and returns decoded Word
     * If in word only one error, it will be corrected */
    private String decodeWord( byte[] word ){

        boolean errorDetected = false;
        int[] sympthom = new int[NumberOfProtectionBits];
        int value;

        /* calculating of a symptons for an encoded word
         * by multiplying encoded word on Transponed checking Matrix */
        for ( int j = 0; j < NumberOfProtectionBits; j++){
            value = 0;
            for ( int i = 0; i < SizeOfEncodedWord; i++){
                value += word[i]*checkMatrixTrans[i][j];
            }
            if ( value%2 != 0 ) { errorDetected = true; }
            sympthom[j] = value%2;
        }

        if ( errorDetected ){

            /* error Position can be calculated via converting sympthom form binary to decimal */
            int errorPos = getErrorPos( sympthom );
            /* if error position less or equal than 7 and bigger or equal than 0, error can be corrected */
            if ( errorPos >= 0 || errorPos <= 7 ){
                if (word[errorPos - 1] == 1) {
                    word[errorPos - 1] = 0;
                } else {
                    word[errorPos - 1] = 1;
                }
            }
        }

        String initialWord = "";
        for ( int i = 0; i < SizeOfEncodedWord; i++){
            if ( i != 0 && i != 1 && i != 3 ){
                initialWord += word[i];
            }
        }

        return initialWord;
    }

    /* member gets number in binary and returns in decimal */
    private int getErrorPos(int[] binNumber){

        int decPos = 0;
        for (int i = 0; i < NumberOfProtectionBits; i++){
            decPos += binNumber[i] * pow(2, i);
        }
        return decPos;
    }

    /* Method make String from byte Array */
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

    /* Method make byte Array from String */
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

    /* Method returns bits sequence of input in ASCII code
     * (Represents all ascii codes in binary) */
    private byte[] AsciiToBinary(byte[] in){

        byte[] bytes = in;
        StringBuilder binary = new StringBuilder();
        for (byte b : bytes)
        {
            int val = b;
            for (int i = 0; i < 8; i++)
            {
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
        }

        return StringToByte( binary.toString() );
    }

    /* Method returns Array of ASCII codes from bits sequence */
    private byte[] binaryToAscii(String bin){
        byte[] res = new byte[bin.length()/8];
        for (int i = 0; i < bin.length(); i+=8){
            String word = "";
            for ( int j = i; j < i+8; j++ ){
                word += bin.charAt(j);
            }
            res[i/8] = Byte.parseByte(word, 2);
        }

        return res;
    }

}



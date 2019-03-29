import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    private static String message_path = "D:\\AES_Assignment\\AES_files\\message_long";
    private static String cipher_path = "D:\\AES_Assignment\\AES_files\\output_check\\cipher";
    //public static String key_path = "C:\\Users\\shachar wild\\Downloads\\AES_files\\output_check\\keys_found";
    private static String key_path = "D:\\AES_Assignment\\AES_files\\key_long";
    private static String output_path = "D:\\AES_Assignment\\AES_files\\output_check";
    private static byte[] state = new byte[16];
    private static List<byte[]> cipher_blocks = new ArrayList<>(); //will contain all cipher blocks
    private static String instruction = "e";
    private static boolean runFromCMD = false;



    public static void main(String[] args) {

        if(runFromCMD) {
            for (int arg_index = 0; arg_index < args.length; arg_index++) {
                if (args[arg_index].equals("-b") || args[arg_index].equals("-e") || args[arg_index].equals("-d"))
                    instruction = args[arg_index].substring(1);
                else if ((args[arg_index].equals("-i") || args[arg_index].equals("-m")) && (arg_index + 1 < args.length))
                    message_path = args[arg_index + 1];
                else if (args[arg_index].equals("-o") && arg_index + 1 < args.length)
                    output_path = args[arg_index + 1];
                else if (args[arg_index].equals("-k") && arg_index + 1 < args.length)
                    key_path = args[arg_index + 1];
                else if (args[arg_index].equals("-c") && arg_index + 1 < args.length)
                    cipher_path = args[arg_index + 1];
            }
        }

        //encrypt
        if (instruction.equals("e")) {
            byte[] message = readMessage(message_path);
            assert message != null;
            int num_blocks = message.length / 16; //each block contains 16 bytes

            byte[] key = getKey(key_path);

            int start_index = 0;
            int end_index = 16;
            for (int i = 0; i < num_blocks; i++) { //perform AES on each block
                state = Arrays.copyOfRange(message, start_index, end_index);
                start_index += 16;
                end_index += 16;

                encrypt_AES_3(key);
                writeOutput("cipher");
            }
        }



        //decrypt
        if (instruction.equals("d")) {
            byte[] cipher = readMessage(cipher_path);
            assert cipher != null;
            int num_blocks = cipher.length / 16; //each block contains 16 bytes

            byte[] key = getKey(key_path);

            int start_index = 0;
            int end_index = 16;
            for (int i = 0; i < num_blocks; i++) { //perform AES on each block
                state = Arrays.copyOfRange(cipher, start_index, end_index);
                start_index += 16;
                end_index += 16;

                decrypt_AES_3(key);
                writeOutput("message");
            }

            cipher_blocks = new ArrayList<>();
        }

        //break code (find 3 keys)
        if (instruction.equals("b")) {
            byte[] M =readMessage(message_path);
            byte[] C =readMessage(cipher_path);

            findKeys(M,C);

            cipher_blocks = new ArrayList<>();

        }
        if (instruction.equals("check_break")){
            byte[]keys=getKey(key_path);
            check_break(keys);

        }
    }

    //write cipher/message to output directory
    private static void writeOutput(String action){
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(output_path + "\\" + action);
            int start=0;

            for (byte[] toWrite : cipher_blocks) {
                fos.write(toWrite);
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
          byte [] delete = readMessage("C:\\Users\\shachar wild\\Downloads\\AES_files\\output_check\\message");
    }



    /**
     * AES 3 encrypt algorithm
     */
    private static void encrypt_AES_3(byte[] key){
        for (int j=0; j<3; j++){ //perform AES 3 times on each block
            byte[] K = makeKey(key,j);

            encrypt_AES(K); //call AES 3 times (each time with a different key)
        }

        cipher_blocks.add(state); //add the new cipher block
    }


    private static void encrypt_AES(byte[] K){
        ShiftRows();
        AddRoundKey(K);
    }



    /**
     * AES 3 decrypt algorithm
     */
    private static void decrypt_AES_3(byte[] key){
        int key_index=2;
        for (int j=0; j<3; j++){ //perform AES 3 times on each block
            byte[] K = makeKey(key,key_index);
            key_index--;
            decrypt_AES(K); //call AES 3 times (each time with a different key)
        }

        cipher_blocks.add(state); //add the new cipher block
    }

    private static void decrypt_AES(byte[] K){
        AddRoundKey(K);
        reverse_ShiftRows();
    }


    private static byte[] makeKey(byte[] keys, int round){
        //generate key
        byte[] K = new byte[16];
        int index=0;
        for (int i=round*16; i<round*16+16; i++){
            K[index] = keys[i];
            index++;
        }

        return K;
    }

    private static byte[] readMessage(String path){

        Path fileLocation = Paths.get(path);

        try {
            return Files.readAllBytes(fileLocation);
        }

        catch(Exception ignored){

        }
        return null; //if couldn't read.
    }


    private static byte[] getKey(String path){

        Path fileLocation = Paths.get(path);

        try {
            return Files.readAllBytes(fileLocation);
        }

        catch(Exception ignored){

        }
        return null; //if couldn't read.
    }



    private static void ShiftRows(){
        byte [] temp = new byte[16];

        temp[0] = state[0];
        temp[1] = state[5];
        temp[2] = state[10];
        temp[3] = state[15];

        temp[4] = state[4];
        temp[5] = state[9];
        temp[6] = state[14];
        temp[7] = state[3];

        temp[8] = state[8];
        temp[9] = state[13];
        temp[10] = state[2];
        temp[11] = state[7];

        temp[12] = state[12];
        temp[13] = state[1];
        temp[14] = state[6];
        temp[15] = state[11];

        System.arraycopy(temp, 0, state, 0, 16);

    }

    private static void reverse_ShiftRows(){
        byte [] temp = new byte[16];

        temp[0] = state[0];
        temp[1] = state[13];
        temp[2] = state[10];
        temp[3] = state[7];

        temp[4] = state[4];
        temp[5] = state[1];
        temp[6] = state[14];
        temp[7] = state[11];

        temp[8] = state[8];
        temp[9] = state[5];
        temp[10] = state[2];
        temp[11] = state[15];

        temp[12] = state[12];
        temp[13] = state[9];
        temp[14] = state[6];
        temp[15] = state[3];

        System.arraycopy(temp, 0, state, 0, 16);

    }

    private static void AddRoundKey(byte[] roundKey) {
        for (int i=0; i<16; i++){
            state[i] ^= roundKey[i];
        }
    }

    private static void findKeys(byte[] M, byte[] C){

        //take first block of M and C
        M = Arrays.copyOfRange(M, 0, 16);
        C = Arrays.copyOfRange(C, 0, 16);

        state = M;

        //create two random keys
        byte[] K1 = new byte[16];
        new Random().nextBytes(K1);

        byte[] K2 = new byte[16];
        new Random().nextBytes(K2);

        encrypt_AES(K1);
        encrypt_AES(K2);

        byte [] temp = new byte[16];

        //K3 is C XOR the message that we've message we've gotten using the two first encrypts
        byte[] K3 = new byte[16];
        ShiftRows();
        for (int i=0; i<16 ;i++){
            state[i] ^= C[i];
            K3 = state;
        }
        state=temp; // return state to its previous value

        cipher_blocks.add(K1);
        cipher_blocks.add(K2);
        cipher_blocks.add(K3);

        //write the 3 keys to a file
        writeOutput("keys_found");
    }

    private static void check_break(byte[] keys){
        byte [] cypher = readMessage(cipher_path);

        byte[] K1 = Arrays.copyOfRange(keys, 0, 16);
        byte[] K2 = Arrays.copyOfRange(keys, 16, 32);
        byte[] K3 = Arrays.copyOfRange(keys, 32, 48);

        state = readMessage(message_path);

        ShiftRows();
        AddRoundKey(K1);

        ShiftRows();
        AddRoundKey(K2);

        ShiftRows();
        AddRoundKey(K3);

    }

}



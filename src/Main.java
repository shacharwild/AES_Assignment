import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {

    public static String message_path = "C:\\Users\\shachar wild\\Downloads\\AES_files\\message_long";
    public static String cipher_path = "C:\\Users\\shachar wild\\Downloads\\AES_files\\cipher";
    public static String key_path = "C:\\Users\\shachar wild\\Downloads\\AES_files\\key_long";
    public static String output_path = "C:\\Users\\shachar wild\\Downloads\\AES_files\\output_check";
    public static byte[] state = new byte[16];
    public static List<byte[]> cipher_blocks = new ArrayList<byte[]>(); //will contain all cipher blocks
    public static String instruction = "d";
    public static byte[] first_message ;


    public static void main(String[] args) {

        //encrypt
        if (instruction == "e") {
            byte[] message = readMessage(message_path);
            first_message=message;
            String s = new String (message);
            int num_blocks = message.length / 16; //each block contains 16 bytes

            byte[] key = getKey(key_path);

            int start_index = 0;
            int end_index = 16;
            for (int i = 0; i < num_blocks; i++) { //perform AES on each block
                byte[] block = Arrays.copyOfRange(message, start_index, end_index);
                state = block;
                start_index += 16;
                end_index += 16;

                encrypt_AES_3(key);
                writeOutput("cipher");
            }
        }



        //decrypt
        if (instruction == "d") {
            byte[] cipher = readMessage(cipher_path);
            int num_blocks = cipher.length / 16; //each block contains 16 bytes

            byte[] key = getKey(key_path);

            int start_index = 0;
            int end_index = 16;
            for (int i = 0; i < num_blocks; i++) { //perform AES on each block
                byte[] block = Arrays.copyOfRange(cipher, start_index, end_index);
                state = block;
                start_index += 16;
                end_index += 16;

                decrypt_AES_3(key);
                String s = new String (state);
                writeOutput("message");
            }

            cipher_blocks = new ArrayList<>();
        }
    }

    //write cipher/message to output directory
    public static void writeOutput(String action){
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(output_path + "\\" + action);
            int start=0;

            for (int i = 0; i < cipher_blocks.size(); i++) {
                byte[] toWrite = cipher_blocks.get(i);
                fos.write(toWrite);
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * AES 3 encrypt algorithm
     */
    public static void encrypt_AES_3(byte[]key){
        for (int j=0; j<3; j++){ //perform AES 3 times on each block
            byte[] K = makeKey(key);
            encrypt_AES(K); //call AES 3 times (each time with a different key)
        }

        cipher_blocks.add(state); //add the new cipher block
    }


    public static void encrypt_AES(byte[]K){
        ShiftRows();
        AddRoundKey(K);
    }



    /**
     * AES 3 decrypt algorithm
     */
    public static void decrypt_AES_3(byte[]key){
        for (int j=0; j<3; j++){ //perform AES 3 times on each block
            byte[] K = makeKey(key);
            decrypt_AES(K); //call AES 3 times (each time with a different key)
        }

        cipher_blocks.add(state); //add the new cipher block
    }

    public static void decrypt_AES(byte[]K){
        AddRoundKey(K);
        reverse_ShiftRows();
    }


    public static byte[] makeKey(byte[] keys){
        //generate key
        byte[] K = new byte[16];
        for (int i=0; i<16; i++){
            K[i] = keys[i];
        }
        return K;
    }

    public static byte[] readMessage(String path){

        Path fileLocation = Paths.get(path);

        try {
            byte [] message = Files.readAllBytes(fileLocation);
            return message;
        }

        catch(Exception e){

        }
        return null; //if couldn't read.
    }


    public static byte[] getKey(String path){

        Path fileLocation = Paths.get(path);

        try {
            byte [] key = Files.readAllBytes(fileLocation);
            return key;
        }

        catch(Exception e){

        }
        return null; //if couldn't read.
    }

   /*
    public static char[] convertToChars(byte [] message){
        try {

            String text1 = new String(message);   // if the charset is UTF-8 (Find out)

            char[] chars = text1.toCharArray();
            return chars;
        }
        catch(Exception e){

        }
        return null;
    }
*/

    public static void ShiftRows(){
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

        for (int i=0; i<16; i++){
            state[i] = temp[i];
        }

    }

    public static void reverse_ShiftRows(){
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

        for (int i=0; i<16; i++){
            state[i] = temp[i];
        }

    }

    public static void AddRoundKey(byte[] roundKey ) {
        for (int i=0; i<16; i++){
            state[i] ^= roundKey[i];
        }
    }

}



import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    public static String message_path = "C:\\Users\\shachar wild\\Downloads\\AES_files\\message_short";
    public static String cipher_path = "C:\\Users\\shachar wild\\Downloads\\AES_files\\cipher";
    public static String key_path = "C:\\Users\\shachar wild\\Downloads\\AES_files\\key_long";
    public static String output_path = "C:\\Users\\shachar wild\\Downloads\\AES_files\\output_check";
    public static byte[] state = new byte[16];
    public static List<byte[]> cipher_blocks = new ArrayList<byte[]>(); //will contain all cipher blocks
    public static String instruction = "b";



    public static void main(String[] args) {

        //encrypt
        if (instruction == "e") {
            byte[] message = readMessage(message_path);
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
                writeOutput("message");
            }

            cipher_blocks = new ArrayList<>();
        }

        //break code (find 3 keys)
        if (instruction == "b") {
            byte[] M =readMessage(message_path);
            byte[] C =readMessage(cipher_path);

            findKeys(M,C);

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
          byte [] delete = readMessage("C:\\Users\\shachar wild\\Downloads\\AES_files\\output_check\\message");
    }



    /**
     * AES 3 encrypt algorithm
     */
    public static void encrypt_AES_3(byte[]key){
        for (int j=0; j<3; j++){ //perform AES 3 times on each block
            byte[] K = makeKey(key,j);

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
        int key_index=2;
        for (int j=0; j<3; j++){ //perform AES 3 times on each block
            byte[] K = makeKey(key,key_index);
            key_index--;
            decrypt_AES(K); //call AES 3 times (each time with a different key)
        }

        cipher_blocks.add(state); //add the new cipher block
    }

    public static void decrypt_AES(byte[]K){
        AddRoundKey(K);
        reverse_ShiftRows();
    }


    public static byte[] makeKey(byte[] keys,int round){
        //generate key
        byte[] K = new byte[16];
        int index=0;
        for (int i=round; i<16+round; i++){
            K[index] = keys[i];
            index++;
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

    public static void findKeys(byte[] M, byte[] C){

        //take first block of M and C
        M = Arrays.copyOfRange(M, 0, 16);
        C = Arrays.copyOfRange(C, 0, 16);

        state = M;

        //create two random keys
        byte[] K1 = new byte[20];
        new Random().nextBytes(K1);

        byte[] K2 = new byte[20];
        new Random().nextBytes(K2);

        encrypt_AES(K1);
        encrypt_AES(K2);

        byte [] temp = new byte[16];

        //K3 is C XOR the message that we've message we've gotten using the two first encrypts
        byte[] K3 = new byte[16];
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

}



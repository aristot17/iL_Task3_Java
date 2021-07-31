import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Scanner;
import java.util.Random;

public class Game {
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException {

        int len = args.length;
        int duplicates = 0;
        for (int i = 0; i < len; i++){
            for (int j=i+1; j< len; j++){
                if (args[i].equals(args[j])){duplicates++; break;}
            }
        }
        if (len < 3 || len % 2 == 0){
            System.out.println("Wrong number of moves!");
        }
        else if (duplicates != 0){
            System.out.println("Moves must be unique!");
        }
        else {
            Random random = new Random();
            int comp_move = random.nextInt(len);
            int usr_move = -1;
            System.out.println("Pseudo-random integer: " + comp_move);
            String key = key128();

            Mac sha256_Hmac = Mac.getInstance("HmacSHA256");
            sha256_Hmac.init(new SecretKeySpec(key.getBytes(), "HmacSHA256"));
            byte[] hmac = sha256_Hmac.doFinal("example".getBytes());
            System.out.println("HMAC:\n" + DatatypeConverter.printHexBinary(hmac));

            Scanner in = new Scanner(System.in);
            int cond=1;
            while (cond == 1) {
                System.out.println("Available moves:");
                for (int i = 0; i < len; i++) {
                    System.out.println(i + 1 + " - " + args[i]);
                }
                System.out.println("0 - exit");
                System.out.println("Enter your move: ");
                if (!in.hasNextInt()) {
                    System.out.println("Please insert available number!");
                    in.next();
                }
                else if (in.hasNextInt()) {
                    usr_move = in.nextInt() - 1;
                    if (usr_move < -1 || usr_move > len - 1) {
                        System.out.println("Please insert correct chose!");
                    }
                    else if (usr_move == -1) {
                        System.out.println("Goodbye!");
                        return;
                    }
                    else {
                        System.out.println("Your move: " + args[usr_move]);
                        cond=0;
                    }
                }
            }
            in.close();

            System.out.println("Computer move: " + args[comp_move]);


            if (comp_move == usr_move){
                System.out.println("Draw!");
            }
            else if ((comp_move > usr_move && comp_move <= usr_move + len / 2) ||
                    (comp_move + len  > usr_move && comp_move + len <= usr_move + len / 2)){
                System.out.println("Computer Win!");
            }
            else {
                System.out.println("You Win!");
            }

            System.out.println("HMAC key:\n"+ key);

        }

    }

    private static String key128() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        SecureRandom random = new SecureRandom();
        keyGen.init(random);
        SecretKey secretKey = keyGen.generateKey();
        return new BigInteger(1, secretKey.getEncoded()).toString(16);
    }

}

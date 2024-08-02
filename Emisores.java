import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.io.*;
import java.net.*;
import java.util.Random;

public class Emisores {
    private static final String CRC32_ALGORITHM = "CRC32";
    private static final String HAMMING_ALGORITHM = "Hamming";
    private static final int NUM_ITERATIONS = 10000;

    public static int[] XOR(int[] a, int[] b) {
        int[] result = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = (a[i] != 0) ^ (b[i] != 0) ? 1 : 0;
        }

        return result;
    }

    public static int getInitialIndex(int[] array) {
        int i = 0;
        boolean indexFound = false;
        while (i < array.length && !indexFound) {
            if (array[i] == 1) {
                indexFound = true;
            } else
                i += 1;
        }

        return i;
    }

    public static int[] mergeArrays(int[] a, int[] b) {
        int[] result = new int[a.length + b.length];
        int i = 0;
        for (int j = 0; j < a.length; j++) {
            result[i] = a[i];
            i += 1;
        }

        for (int j = 0; j < b.length; j++) {
            result[i] = b[j];
            i += 1;
        }

        return result;
    }

    public static String calculateCRC(String trama) {
        int size = 33;
        int[] binaryData = new int[trama.length() + size - 1];
        for (int i = 0; i < trama.length(); i++) {
            binaryData[i] = Integer.parseInt(String.valueOf(trama.charAt(i)));
        }

        int[] CRC_32 = { 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1,
                1 };

        int start = 0;
        int end = size;
        int[] xorResult = { 0 };
        int[] result = Arrays.copyOfRange(binaryData, 0, size);
        while (end < binaryData.length + 1) {
            xorResult = XOR(result, CRC_32);
            int index = getInitialIndex(xorResult);
            int padding = index;
            start = end;
            end = end + padding;
            int[] temp1 = Arrays.copyOfRange(xorResult, index, xorResult.length);
            int[] temp2 = Arrays.copyOfRange(binaryData, start, end);
            result = mergeArrays(temp1, temp2);
        }
        int[] appendedData = new int[xorResult.length + (binaryData.length - start)];
        int i = 0;
        for (i = 0; i < xorResult.length; i++) {
            appendedData[i] = xorResult[i];
        }
        for (int j = start; j < binaryData.length; j++) {
            appendedData[i] = binaryData[j];
            i += 1;
        }
        String resultingTrama = "";
        int[] tempResult = Arrays.copyOfRange(appendedData, appendedData.length - size + 1, appendedData.length);
        for (i = 0; i < tempResult.length; i++)
            resultingTrama += tempResult[i];
        return trama + resultingTrama;
    }

    public static boolean checkInput(String input) {
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) != '0' && input.charAt(i) != '1') {
                return false;
            }
        }
        return true;
    }

    public static String HammingEncoder(String input) {
        int m = input.length();
        int r = 0;

        while (Math.pow(2, r) < m + r + 1) {
            r++;
        }

        int totalLength = m + r;
        char[] encoded = new char[totalLength];

        for (int i = 0, j = 0; i < totalLength; i++) {
            if (isPowerOfTwo(i + 1)) {
                encoded[i] = '0'; 
            } else {
                encoded[i] = input.charAt(j);
                j++;
            }
        }

        for (int i = 0; i < r; i++) {
            int parityPosition = (int) Math.pow(2, i);
            encoded[parityPosition - 1] = calculateParity(encoded, parityPosition);
        }

        return new String(encoded);
    }

    private static boolean isPowerOfTwo(int num) {
        return (num & (num - 1)) == 0;
    }

    private static char calculateParity(char[] encoded, int parityPosition) {
        int parity = 0;
        for (int i = parityPosition - 1; i < encoded.length; i += 2 * parityPosition) {
            for (int j = i; j < i + parityPosition && j < encoded.length; j++) {
                if (encoded[j] == '1') {
                    parity ^= 1;
                }
            }
        }
        return (parity == 0) ? '0' : '1';
    }

    private static String encodeCRC32(String message) {
        return calculateCRC(message);
    }

    private static String encodeHamming(String message) {
        return HammingEncoder(message);
    }

    private static String applyNoise(String message, double errorProbability) {
        System.out.println(message);
        if (errorProbability < 0 || errorProbability > 1) {
            throw new IllegalArgumentException("La probabilidad debe estar en el rango de 0 a 1");
        }
        Random rand = new Random();
        // StringBuilder noisyMessage = new StringBuilder(message);
        StringBuilder noisyMessage = new StringBuilder();

        int counter = 0;
        for (int i = 0; i < message.length(); i++) {
            char bit = message.charAt(i);
            char flippedBit = bit;

            if (rand.nextDouble() < errorProbability) {
                flippedBit = (bit == '0') ? '1' : '0';
                counter += 1;
            }
            noisyMessage.append(flippedBit);

        }
        return noisyMessage.toString();
    }

    private static String textToBinary(String text) {
        StringBuilder binary = new StringBuilder();
        for (char character : text.toCharArray()) {
            binary.append(String.format("%8s", Integer.toBinaryString(character)).replaceAll(" ", "0"));
        }
        return binary.toString();
    }

     private static List<String> readWordsFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            return br.lines().collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            Socket socket = new Socket("localhost", 12345);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            List<String> words = readWordsFromFile("words.txt");

            System.out.println("Ingrese el mensaje a enviar:");
            String message = scanner.nextLine();

            System.out.println("Seleccione el algoritmo \n1. CRC32 \n2. Hamming");
            int algorithmChoice = scanner.nextInt();
            scanner.nextLine();  // Limpiar el buffer

            String algorithm = "";

            System.out.println("Ingrese la probabilidad de error (entre 0 y 1)");
            System.out.print("> ");
            double errorProbability = Double.parseDouble(scanner.nextLine());

            String encodedMessage = "";
            // CODE FOR ANALYTICS - COMMENTED DUE TO TEST
            // for (String word : words) {
            //     String binaryMessage = textToBinary(word);
            //     String encodedMessage = (algorithmChoice == 1) ? encodeCRC32(binaryMessage) : encodeHamming(binaryMessage);
            //     String noisyMessage = applyNoise(encodedMessage, errorProbability);

            //     // Opcional: Imprimir el mensaje para verificación
            //     System.out.println("Mensaje enviado: " + noisyMessage);

            //     // Enviar al servidor
            //     out.println(algorithm);
            //     out.println(noisyMessage);
            // }
            if (algorithmChoice == 1) {
                algorithm = CRC32_ALGORITHM;
                encodedMessage = encodeCRC32(message);
            } else if (algorithmChoice == 2) {
                algorithm = HAMMING_ALGORITHM;
                encodedMessage = encodeHamming(textToBinary(message));
            } else {
                System.out.println("Selección no válida.");
                scanner.close();
                return;
            }

            String noisyMessage = applyNoise(encodedMessage, errorProbability);

            System.out.println("Mensaje enviado: " + noisyMessage);

            out.println(algorithm);
            out.println(noisyMessage);

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}

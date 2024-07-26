import java.util.Arrays;
import java.util.Scanner;

public class CRCEmisor {

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

    public static void printArray(int[] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i]);
        }
        System.out.println("");
    }

    public static String calculateCRC(String trama) {
        int size = 33;
        int[] binaryData = new int[trama.length() + size - 1];
        for (int i = 0; i < trama.length(); i++) {
            binaryData[i] = Integer.parseInt(String.valueOf(trama.charAt(i)));
        }

        int[] CRC_32 = { 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1,
                1 };
        // int [] CRC_32 = {1,0,0,1};

        int start = 0;
        int end = size;
        // boolean finished = false;
        int[] xorResult = { 0 };
        int[] result = Arrays.copyOfRange(binaryData, 0, size);
        while (end < binaryData.length + 1) {
            xorResult = XOR(result, CRC_32);
            int index = getInitialIndex(xorResult);
            int padding = index;
            start = end;
            end = end + padding;
            // System.out.println(start);
            // System.out.println(end);
            int[] temp1 = Arrays.copyOfRange(xorResult, index, xorResult.length);
            int[] temp2 = Arrays.copyOfRange(binaryData, start, end);
            result = mergeArrays(temp1, temp2);
            // System.out.println("--------------------------------");
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

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingrese el mensaje en binario");
        System.out.print("> ");
        String trama = scanner.nextLine();
        boolean allGood = checkInput(trama);
        if (!allGood) {
            System.out.println("Error: Ingrese correctamente la trama.");
        } else
            System.out.println("Resultado: " + calculateCRC(trama));

        scanner.close();
    }
}
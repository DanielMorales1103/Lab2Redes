import java.util.Scanner;

public class HammingCode {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese una cadena binaria: ");
        String input = scanner.nextLine();
        
        String encodedMessage = encodeHamming(input);
        System.out.println("El mensaje codificado es: " + encodedMessage);
        scanner.close();
    }

    public static String encodeHamming(String input) {
        int m = input.length();
        int r = 0;

        // Encontrar el número de bits de paridad necesarios
        while (Math.pow(2, r) < m + r + 1) {
            r++;
        }

        int totalLength = m + r;
        char[] encoded = new char[totalLength];

        // Colocar los bits de datos en sus posiciones correspondientes
        for (int i = 0, j = 0; i < totalLength; i++) {
            if (isPowerOfTwo(i + 1)) {
                encoded[i] = '0'; // Temporales bits de paridad
            } else {
                encoded[i] = input.charAt(j);
                j++;
            }
        }

        // Calcular los bits de paridad
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
}

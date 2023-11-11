import java.util.Scanner;

public class Lab {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите число в десятичной системе счисления:");
        int value = scanner.nextInt();
        String result = "";

        while (true) {
            int temp = value % (-10);
            value /= (-10);

            if (temp < 0) {
                temp += 10;
                value++;
            }

            switch (temp) {
                case 0 -> result = "0" + result;
                case 1 -> result = "1" + result;
                case 2 -> result = "2" + result;
                case 3 -> result = "3" + result;
                case 4 -> result = "4" + result;
                case 5 -> result = "5" + result;
                case 6 -> result = "6" + result;
                case 7 -> result = "7" + result;
                case 8 -> result = "8" + result;
                case 9 -> result = "9" + result;
            }

            if (value == 0) {
                break;
            }
        }

        System.out.println("Результат: \n" + result);
    }
}

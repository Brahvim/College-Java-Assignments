package com.brahvim.college_assignments;

import java.util.Scanner;

public class App {

    public static void main(final String[] p_args) {
        System.out.print("Enter a number to sum the digits of: ");

        final Scanner sc = new Scanner(System.in);
        final long userInput = sc.nextLong();
        sc.close();

        long sum = 0;
        long number = userInput;

        while (number != 0) {
            sum += number % 10;
            number /= 10;
        }

        System.out.println(String.format("Total: `%d`.", sum));
    }

}

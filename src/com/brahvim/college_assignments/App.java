package com.brahvim.college_assignments;

import java.util.Scanner;

public class App {

    protected static final double[] NUMBERS = { 9, 8, 2, 0, 2, 4 };

    static class NoSuchNumberException extends RuntimeException {

        public NoSuchNumberException(final double p_faultyNumber) {
            super(String.format("%nBOOM! Number `%f` was not in the list!%nError stacktrace:", p_faultyNumber));
        }

    }

    public static void main(final String[] p_args) {
        System.out.print("Enter one of the right number or this program's method stack explodes!: ");

        final Scanner sc = new Scanner(System.in);
        final double userInput = sc.nextDouble();
        sc.close();

        System.out.println("PAY ATTENTION!");

        boolean willExplode = true;
        for (final double d : App.NUMBERS)
            if (userInput != d)
                willExplode = false;

        if (willExplode)
            throw new NoSuchNumberException(userInput);

        System.out.println("YES! We made it. Go home now...");
    }

}

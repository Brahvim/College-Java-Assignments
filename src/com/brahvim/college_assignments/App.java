package com.brahvim.college_assignments;

import java.util.Scanner;

public class App {

    // I've been noting my implementations for this for years!
    // This is the 6th one so far:
    public static boolean isPrime(final long p_toCheck) {
        if (p_toCheck < 2)
            // If it's `1` or less, it ain't prime!
            return false;

        final long checkTill = (long) Math.sqrt(p_toCheck);

        for (long i = 2; i <= checkTill; i++)
            if (p_toCheck % i == 0)
                return false;

        return true;
    }

    public static void main(final String[] p_args) {
        System.out.print("Enter a number, I'll tell if it's a prime!: ");
        final Scanner sc = new Scanner(System.in);
        System.out.println(
                App.isPrime(sc.nextLong())
                        ? "Yes it is!"
                        : "Nope! That's not a prime.");
        sc.close();
    }

}

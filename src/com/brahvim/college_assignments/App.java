package com.brahvim.college_assignments;

import java.util.Scanner;

public class App {

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
        System.out.println("Gimme a range of numbers, I'll show you the primes.");

        final Scanner sc = new Scanner(System.in);

        System.out.print("What's the first number in this range?: ");
        final long start = sc.nextLong();

        System.out.print("What's the *last* number in this range?: ");
        final long end = sc.nextLong() + 1;
        sc.close();

        System.out.println("The primes are:");

        for (long i = end; true; i--) {
            if (!App.isPrime(i))
                continue;

            for (long j = start; j < i; j++) {
                if (!App.isPrime(j))
                    continue;

                System.out.print(j);
                System.out.print(", ");
            }

            System.out.print(i);
            System.out.println('.');
            break;
        }
    }

}

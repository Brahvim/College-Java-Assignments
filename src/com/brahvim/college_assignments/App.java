package com.brahvim.college_assignments;

import java.util.Scanner;

public class App {

    public static void main(final String[] p_args) {
        System.out.print("Enter the number to display the multiplication table for: ");

        final Scanner sc = new Scanner(System.in);
        final long multiplier = sc.nextLong();

        System.out.print("Till when?: ");
        final long tableEnd = sc.nextLong() + 1;

        sc.close();

        for (int i = 1; i < tableEnd; i++)
            System.out.println(String.format("`%d` * `%d` = `%d`.", multiplier, i, multiplier * i));
    }

}

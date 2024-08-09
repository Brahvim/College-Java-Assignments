package com.brahvim.college_assignments;

public class App {

    public static void main(final String[] p_args) {
        System.out.println(String.format(
                "Number of parameters: `%d`.", p_args.length));

        for (int i = 0; i < p_args.length; i++)
            System.out.println(String.format(
                    "Argument number `%d`: `%s`.", i, p_args[i]));
    }

}

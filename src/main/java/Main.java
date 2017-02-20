import models.ApiResponse;
import network.Fetcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter printer = new PrintWriter(System.out, true);

        while (true) {
            try {
                printer.println("Enter from currency:");
                String from = scanner.readLine();
                if (from == null) {
                    return;
                }

                printer.println("Enter to currency:");
                String to = scanner.readLine();
                if (to == null) {
                    return;
                }

                from = from.toUpperCase().trim();
                to = to.toUpperCase().trim();

                ApiResponse data = Fetcher.fetchCurrency(from, to);

                if (data == null) {
                    printer.println("Sorry :( Your input is invalid\n");
                    continue;
                }

                printer.println(String.format(Locale.US, "%s -> %s : %.3f\n", from, to, data.getRates().getValue()));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

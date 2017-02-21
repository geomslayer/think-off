package com.geomslayer;

import com.geomslayer.models.ApiResponse;
import com.geomslayer.utils.Fetcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.concurrent.*;

public class Main {

    private static final ExecutorService service;

    static {
        service = Executors.newSingleThreadExecutor();
    }

    public static void main(String[] args) {
        BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter printer = new PrintWriter(System.out, true);

        StatusBar statusBar = new StatusBar(printer);

        while (true) {
            try {
                printer.println("Enter from currency:");
                String from = scanner.readLine();
                if (from == null) {
                    finish();
                }

                printer.println("Enter to currency:");
                String to = scanner.readLine();
                if (to == null) {
                    finish();
                }

                from = from.toUpperCase().trim();
                to = to.toUpperCase().trim();

                ApiResponse response = null;

                // Async call, but in the main thread we don't have another work
                // so just get data right here
                Callable<ApiResponse> loader = new Fetcher.Loader(from, to);
                Future<ApiResponse> futureResponse = service.submit(loader);
                try {
                    statusBar.show();
                    response = futureResponse.get();
                } catch (ExecutionException | InterruptedException e) {
                    finish();
                } finally {
                    statusBar.hide();
                }

                if (response == null) {
                    continue;
                }

                if (response.getMessage() != null) {
                    printer.println(response.getMessage());
                }
                if (response.getRates() == null || response.getRates() == null) {
                    continue;
                }
                printer.println(String.format(Locale.US, "Exchange rates for %s is", response.getDate()));
                printer.println(String.format(Locale.US, "%s 1.000 == %s %.3f\n", from, to, response.getRates().getValue()));
                printer.print("Continue? y/n ");
                printer.flush();

                String reply = scanner.readLine();
                if (reply == null) {
                    printer.println();
                    finish();
                }

                reply = reply.trim().toLowerCase();
                if (!reply.equals("") && !reply.equals("y")) {
                    finish();
                }
                printer.println();

            } catch (IOException e) {
                System.out.println("Couldn't connect to your keyboard");
                finish();
            }
        }
    }

    private static void finish() {
        service.shutdown();
        System.exit(0);
    }

}

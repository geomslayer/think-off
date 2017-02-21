package com.geomslayer;

import java.io.PrintWriter;

// shows loading process while it is needed, of course async
public class StatusBar {
    private PrintWriter printer;
    private boolean working;

    public StatusBar(PrintWriter printer) {
        this.printer = printer;
    }

    public void show() {
        working = true;
        new Thread(() -> {
            int cnt = 0;
            try {
                while (working) {
                    printer.print("\rLoading " + getPoints(cnt++ % 4));
                    printer.flush();
                    Thread.sleep(250);
                }
            } catch (InterruptedException e) {}
        }).start();
    }

    public void hide() {
        working = false;
        printer.print("\r");
        printer.flush();
    }

    private String getPoints(int cnt) {
        String res = "";
        for (int i = 0; i < cnt; ++i) {
            res += ".";
        }
        return res;
    }

}

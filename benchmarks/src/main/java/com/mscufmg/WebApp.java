package com.mscufmg.benchmarks;

import io.javalin.Javalin;

public class WebApp {
    public static void main(String[] args) {
        Javalin app = Javalin.create().enableDebugLogging().start(7000);
        app.get("/", ctx -> ctx.result("Hello World"));
    }
}

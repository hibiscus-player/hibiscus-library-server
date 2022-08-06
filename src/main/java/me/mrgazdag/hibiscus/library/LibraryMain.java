package me.mrgazdag.hibiscus.library;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.RoutingHandler;
import me.mrgazdag.hibiscus.library.coreapi.RestCoreApi;

import java.nio.file.FileSystems;

public class LibraryMain {
    public static final int DEFAULT_PORT = 80;
    public static final String DEFAULT_CORE_URL = "http://hibiscus-player.ddns.net";
    public static void main(String[] args) {
        Integer port = null;
        String coreUrl = null;
        String rootFolder = null;
        for (int i = 0; i < args.length; i++) {
            String c = args[i];
            switch (c) {
                case "--port":
                    if (i + 1 < args.length) {
                        try {
                            port = Integer.parseInt(args[i + 1]);
                            i++;
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid port \"" + args[i + 1] + "\"!");
                            System.exit(-1);
                        }
                    } else {
                        System.err.println("No port specified");
                    }
                    break;
                case "--coreUrl":
                    if (i + 1 < args.length) {
                        coreUrl = args[i + 1];
                        i++;
                    } else {
                        System.err.println("No core url specified");
                    }
                    break;
                case "--rootFolder":
                    if (i + 1 < args.length) {
                        rootFolder = args[i + 1];
                        i++;
                    } else {
                        System.err.println("No root folder specified");
                    }
                    break;
            }
        }
        if (port == null) {
            port = DEFAULT_PORT;
            System.out.println("Using default port " + port);
        } else {
            System.out.println("Using port " + port);
        }
        if (coreUrl == null) {
            coreUrl = DEFAULT_CORE_URL;
            System.out.println("Using default core URL \"" + coreUrl + "\"");
        } else {
            System.out.println("Using core URL \"" + coreUrl + "\"");
        }
        if (rootFolder == null) {
            rootFolder = ".";
        } else {
            System.out.println("Using root folder \"" + rootFolder + "\"");
        }

        RoutingHandler handler = Handlers.routing();
        Undertow server = Undertow.builder()
                .addHttpListener(port, "0.0.0.0")
                .setHandler(handler)
                .setServerOption(UndertowOptions.ENABLE_HTTP2, true)
                .build();

        LibraryServer lib = new LibraryServer(FileSystems.getDefault().getPath(rootFolder), new RestCoreApi(coreUrl));
        lib.hookInto(handler);
        server.start();
        System.out.println("Library Server started");
    }
}

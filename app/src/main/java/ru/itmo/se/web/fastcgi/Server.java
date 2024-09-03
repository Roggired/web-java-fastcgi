package ru.itmo.se.web.fastcgi;

import com.fastcgi.FCGIInterface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Properties;

public class Server {
    public static void main(String[] args) throws IOException {
        var fcgiInterface = new FCGIInterface();
        while (fcgiInterface.FCGIaccept() >= 0) {
            var method = FCGIInterface.request.params.getProperty("REQUEST_METHOD");
            if (method == null) {
                System.out.println(errorResult("Unsupported HTTP method: null"));
                continue;
            }

            if (method.equals("GET")) {
                var queryString = FCGIInterface.request.params.getProperty("QUERY_STRING");
                if (queryString != null && queryString.equals("debug=1")) {
                    var paramsDump = FCGIInterface.request
                            .params
                            .entrySet()
                            .stream()
                            .map((entry) -> "%s: %s".formatted(entry.getKey().toString(), entry.getValue().toString()))
                            .reduce("", (acc, el) -> acc + "\n" + el);
                    System.out.println(echoPage(paramsDump));
                } else {
                    System.out.println(getHelloPage());
                }
                continue;
            }

            if (method.equals("POST")) {
                var contentType = FCGIInterface.request.params.getProperty("CONTENT_TYPE");
                if (contentType == null) {
                    System.out.println(errorResult("Content-Type is null"));
                    continue;
                }

                if (!contentType.equals("application/x-www-form-urlencoded")) {
                    System.out.println(errorResult("Content-Type is not supported"));
                    continue;
                }

                var requestBody = simpleFormUrlEncodedParsing(readRequestBody());
                var xStr = requestBody.get("x");
                var yStr = requestBody.get("y");
                if (xStr == null || yStr == null) {
                    System.out.println(errorResult("X and Y must be provided as x-www-form-urlencoded params"));
                    continue;
                }

                int x, y;
                try {
                    x = Integer.parseInt(xStr.toString());
                } catch (NumberFormatException e) {
                    System.out.println(errorResult("X must be an integer"));
                    continue;
                }
                try {
                    y = Integer.parseInt(yStr.toString());
                } catch (NumberFormatException e) {
                    System.out.println(errorResult("Y must be an integer"));
                    continue;
                }

                System.out.println(sumPage(x, y, x + y));
                continue;
            }

            System.out.println(errorResult("Unsupported HTTP method: " + method));
        }
    }

    private static Properties simpleFormUrlEncodedParsing(String requestBodyStr) {
        var props = new Properties();
        Arrays.stream(requestBodyStr.split("&"))
                .forEach(keyValue -> props.setProperty(keyValue.split("=")[0], keyValue.split("=")[1]));
        return props;
    }

    private static String readRequestBody() throws IOException {
        FCGIInterface.request.inStream.fill();

        var contentLength = FCGIInterface.request.inStream.available();
        ByteBuffer buffer = ByteBuffer.allocate(contentLength);
        var readBytes = FCGIInterface.request.inStream.read(buffer.array(), 0, contentLength);

        var requestBodyRaw = new byte[readBytes];
        buffer.get(requestBodyRaw);
        buffer.clear();

        return new String(requestBodyRaw, StandardCharsets.UTF_8);
    }

    private static String errorResult(String error) {
        return """
                HTTP/1.1 400 Bad Request
                Content-Type: text/html
                Content-Length: %d
                
                
                %s
                """.formatted(error.getBytes(StandardCharsets.UTF_8).length, error);
    }

    private static String getHelloPage() {
        var content = """
                    <html>
                        <head>
                            <title>Java FastCGI Hello World</title>
                        </head>
                        <body>
                            <h1>From Java FastCGI:</h1>
                            <p>Hello, World!</p>
                        </body>
                    </html>
                    """;
        return """
                HTTP/1.1 200 OK
                Content-Type: text/html
                Content-Length: %d
                
                
                %s
                """.formatted(content.getBytes(StandardCharsets.UTF_8).length, content);
    }

    private static String echoPage(String echo) {
        var content = """
                <h1>Received request body</h1>
                <p>
                %s
                </p>
                """.formatted(echo);
        return """
                HTTP/1.1 200 OK
                Content-Type: text/html
                Content-Length: %d
                
                
                %s
                """.formatted(content.getBytes(StandardCharsets.UTF_8).length, content);
    }

    private static String sumPage(int x, int y, int sum) {
        var content = """
                <h1>Calculated sum</h1>
                <p>%d + %d = %d</p>
                """.formatted(x, y, sum);
        return """
                HTTP/1.1 200 OK
                Content-Type: text/html
                Content-Length: %d
                
                
                %s
                """.formatted(content.getBytes(StandardCharsets.UTF_8).length, content);
    }
}

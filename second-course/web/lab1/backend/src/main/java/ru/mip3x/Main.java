package ru.mip3x;
import com.fastcgi.FCGIInterface;

import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String args[]) {
        FCGIInterface fcgiInterface = new FCGIInterface();
        while (fcgiInterface.FCGIaccept() >= 0) {

            var content = """
              <html>
                <head><title>Java FastCGI Test App</title></head>
                <body><h1>JAVA #4</h1></body>
              </html>""";
            var httpResponse = """
              HTTP/1.1 228 OK
              Content-Type: text/html
              Content-Length: %d
              
              %s
              """.formatted(content.getBytes(StandardCharsets.UTF_8).length, content);
            System.out.println(httpResponse);
        }
    }
}

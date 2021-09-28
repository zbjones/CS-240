package Handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.*;

/**
 * Abstract class to contain several useful methods the various handlers use.
 */
public abstract class Handler {

    protected boolean isPostRequest(HttpExchange exchange) {
        return exchange.getRequestMethod().equalsIgnoreCase("post");
    }

    protected boolean isGetRequest(HttpExchange exchange) {
        return exchange.getRequestMethod().equalsIgnoreCase("get");
    }

    protected String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    protected void writeToOutputStream(String string, OutputStream outputStream) throws IOException {
        OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
        streamWriter.write(string);
        streamWriter.close();
    }
}

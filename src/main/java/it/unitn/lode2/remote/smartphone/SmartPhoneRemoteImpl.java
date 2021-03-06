package it.unitn.lode2.remote.smartphone;

import it.unitn.lode2.remote.Remote;
import it.unitn.lode2.remote.RemoteCommand;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Tiziano on 07/10/2015.
 */
public class SmartPhoneRemoteImpl implements Remote {

    private String host="127.0.0.1";
    private Integer port=80;
    private Boolean terminate=Boolean.FALSE;
    private Map<RemoteCommand, Supplier<byte[]>> byteSuppliers = new HashMap<>();
    private Map<RemoteCommand, Function<String, byte[]>> byteFunctions = new HashMap<>();
    private Map<RemoteCommand, Supplier<String>> suppliers = new HashMap<>();
    private Map<RemoteCommand, Function<String, String>> functions = new HashMap<>();

    ServerSocket serverSocket;

    private SecureRandom random = new SecureRandom();


    Task<Void> receiverTast = new Task<Void>() {
        @Override
        protected Void call() throws Exception {
            serverSocket = new ServerSocket(port);
            while(!terminate){
                Socket acceptSocket = serverSocket.accept();

                Task<Boolean> task = new Task() {
                    @Override
                    protected Boolean call() throws Exception {
                        return acceptSocketThread(acceptSocket);
                    }
                };

                Thread thread = new Thread(task);
                thread.start();


            }
            return null;
        }
    };
    private String token;

    private Boolean checkSession(String tokenToCheck){
        return tokenToCheck.equals(token);
    }

    private Boolean acceptSocketThread(Socket accept) throws IOException {
        BufferedReader commandReader = new BufferedReader(new InputStreamReader(accept.getInputStream()));
        String line = commandReader.readLine();
        List<String> params = Arrays.asList(line.split(" "));
        String command = params.get(0);
        if( !"LOGIN".equals(command) && !checkSession(params.get(params.size()-1)) ){
            writeString(accept, "DENY\n");
            return Boolean.FALSE;
        }
        String param = null;
        if (params.size() > 1) {
            param = params.get(1);
        }
        RemoteCommand remoteCommand = RemoteCommand.valueOf(command);
        if (param != null && functions.containsKey(remoteCommand)) {
            Function<String, String> function = functions.get(remoteCommand);
            String response = function.apply(param);
            writeString(accept, response);
        } else if (suppliers.containsKey(remoteCommand)) {
            Supplier<String> supplier = suppliers.get(remoteCommand);
            String response = supplier.get();
            writeString(accept, response);
        } else if (param != null && byteFunctions.containsKey(remoteCommand)) {
            Function<String, byte[]> function = byteFunctions.get(remoteCommand);
            byte[] response = function.apply(param);
            writeBytes(accept, response);
        } else if (byteSuppliers.containsKey(remoteCommand)) {
            Supplier<byte[]> supplier = byteSuppliers.get(remoteCommand);
            byte[] response = supplier.get();
            writeBytes(accept, response);
        } else {
            writeString(accept, "NO\n");
        }
        accept.close();
        return Boolean.TRUE;
    }

    private void writeString(Socket accept, String response) throws IOException {
        DataOutputStream stringResponseWriter = new DataOutputStream(accept.getOutputStream());
        stringResponseWriter.writeBytes(response);
        stringResponseWriter.flush();
        stringResponseWriter.close();
    }

    private void writeBytes(Socket accept, byte[] response) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(response.length);
        byteArrayOutputStream.write(response, 0, response.length);
        byteArrayOutputStream.writeTo(accept.getOutputStream());
        byteArrayOutputStream.flush();
        byteArrayOutputStream.close();
    }


    public SmartPhoneRemoteImpl(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void start() {
        Thread thread = new Thread(receiverTast);
        //thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void stop() {
        terminate = Boolean.TRUE;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void setCommandHandler(RemoteCommand command, EventHandler<ActionEvent> handler) {
        Supplier<String> supplier = () -> {
            Platform.runLater(() -> handler.handle(null));
            return "OK\n";
        };
        suppliers.put(command, supplier);
    }

    @Override
    public void setCommandHandler(RemoteCommand command, Function<String, String> function) {
        functions.put(command, function);
    }

    @Override
    public void setCommandHandler(RemoteCommand command, Supplier<String> supplier) {
        suppliers.put(command, supplier);
    }

    @Override
    public void setCommandByteHandler(RemoteCommand command, Function<String, byte[]> function) {
        byteFunctions.put(command, function);
    }

    @Override
    public void setCommandByteHandler(RemoteCommand command, Supplier<byte[]> supplier) {
        byteSuppliers.put(command, supplier);
    }

    @Override
    public String initializeToken() {
        token = new BigInteger(130, random).toString(32);
        return token;
    }
}

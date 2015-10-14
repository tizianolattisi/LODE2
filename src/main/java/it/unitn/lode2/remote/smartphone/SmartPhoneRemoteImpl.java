package it.unitn.lode2.remote.smartphone;

import it.unitn.lode2.remote.Remote;
import it.unitn.lode2.remote.RemoteCommand;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
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

    Task<Void> receiverTast = new Task<Void>() {
        @Override
        protected Void call() throws Exception {
            ServerSocket socket = new ServerSocket(port);
            Socket accept = socket.accept();
            BufferedReader commandReader = new BufferedReader(new InputStreamReader(accept.getInputStream()));
            DataOutputStream stringResponseWriter = new DataOutputStream(accept.getOutputStream());
            ObjectOutputStream bytesResponseWriter = new ObjectOutputStream(accept.getOutputStream());
            while(!terminate){
                String line = commandReader.readLine();
                List<String> params = Arrays.asList(line.split(" "));
                String command = params.get(0);
                String param = null;
                if (params.size() > 1) {
                    param = params.get(1);
                }
                RemoteCommand remoteCommand = RemoteCommand.valueOf(command);
                if (param != null && functions.containsKey(remoteCommand)) {
                    Function<String, String> function = functions.get(remoteCommand);
                    String response = function.apply(param);
                    stringResponseWriter.writeBytes(response);
                } else if (suppliers.containsKey(remoteCommand)) {
                    Supplier<String> supplier = suppliers.get(remoteCommand);
                    String response = supplier.get();
                    stringResponseWriter.writeBytes(response);
                } else if (param != null && byteFunctions.containsKey(remoteCommand)) {
                    Function<String, byte[]> function = byteFunctions.get(remoteCommand);
                    byte[] response = function.apply(param);
                    bytesResponseWriter.write(response);
                } else if (byteSuppliers.containsKey(remoteCommand)) {
                    Supplier<byte[]> supplier = byteSuppliers.get(remoteCommand);
                    byte[] response = supplier.get();
                    bytesResponseWriter.write(response);
                } else {
                    stringResponseWriter.writeChars("NO\n");
                }
            }
            return null;
        }
    };

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
    }


    @Override
    public void setCommandHandler(RemoteCommand command, EventHandler<ActionEvent> handler) {
        /*
        Function<String, String> function = actionEventEventHandler -> {
            Platform.runLater(() -> handler.handle(null));
            return "OK\n";
        };
        functions.put(command, function);
        */
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
}

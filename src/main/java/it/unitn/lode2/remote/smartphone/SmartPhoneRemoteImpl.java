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
    private Map<RemoteCommand, Supplier<String>> suppliers = new HashMap<>();
    private Map<RemoteCommand, Function<String, String>> functions = new HashMap<>();

    Task<Void> receiverTast = new Task<Void>() {
        @Override
        protected Void call() throws Exception {
            ServerSocket socket = new ServerSocket(port);
            Socket accept = socket.accept();
            BufferedReader commandReader = new BufferedReader(new InputStreamReader(accept.getInputStream()));
            DataOutputStream responseWriter = new DataOutputStream(accept.getOutputStream());
            while(!terminate){
                String line = commandReader.readLine();
                System.out.println(line);
                List<String> params = Arrays.asList(line.split(" "));
                String command = params.get(0);
                String param = null;
                if (params.size() > 1) {
                    param = params.get(1);
                }
                String response = "NO\n";
                RemoteCommand remoteCommand = RemoteCommand.valueOf(command);
                if (param != null && functions.containsKey(remoteCommand)) {
                    Function function = functions.get(remoteCommand);
                    response = (String) function.apply(param);
                } else if (suppliers.containsKey(remoteCommand)) {
                    Supplier<String> supplier = suppliers.get(remoteCommand);
                    response = supplier.get();
                }
                responseWriter.writeBytes(response);
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
}

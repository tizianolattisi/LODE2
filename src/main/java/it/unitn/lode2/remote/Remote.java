package it.unitn.lode2.remote;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Tiziano on 07/10/2015.
 */
public interface Remote {

    void start();

    void stop();

    void setCommandHandler(RemoteCommand command, EventHandler<ActionEvent> handler);

    void setCommandHandler(RemoteCommand command, Function<String, String> function);

    void setCommandHandler(RemoteCommand command, Supplier<String> supplier);

    void setCommandByteHandler(RemoteCommand command, Function<String, byte[]> function);

    void setCommandByteHandler(RemoteCommand command, Supplier<byte[]> supplier);

}

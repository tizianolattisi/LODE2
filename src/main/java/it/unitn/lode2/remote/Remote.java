package it.unitn.lode2.remote;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.util.function.Function;

/**
 * Created by Tiziano on 07/10/2015.
 */
public interface Remote {

    void start();

    void setCommandHandler(RemoteCommand command, EventHandler<ActionEvent> handler);

    void setCommandHandler(RemoteCommand command, Function<String, String> function);

}

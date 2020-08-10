package org.gradle;

import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.Connection;
import org.openmuc.j60870.ConnectionEventListener;
import org.openmuc.j60870.Server;
import org.openmuc.j60870.ServerEventListener;
import org.openmuc.j60870.ie.IeQuality;
import org.openmuc.j60870.ie.IeScaledValue;
import org.openmuc.j60870.ie.IeSingleCommand;
import org.openmuc.j60870.ie.InformationElement;
import org.openmuc.j60870.ie.InformationObject;

public class IEC104Ghost {
	public class ServerListener implements ServerEventListener {

        public class ConnectionListener implements ConnectionEventListener {

            private final Connection connection;
            private final int connectionId;
            private boolean selected = false;

            public ConnectionListener(Connection connection, int connectionId) {
                this.connection = connection;
                this.connectionId = connectionId;
            }

            @Override
            public void newASdu(ASdu aSdu) {

                try {
                    switch (aSdu.getTypeIdentification()) {
                    // interrogation command
                    case C_IC_NA_1:
                        connection.sendConfirmation(aSdu);
                        println("Got interrogation command. Will send scaled measured values.\n");

                        connection.send(new ASdu(ASduType.M_ME_NB_1, true, CauseOfTransmission.SPONTANEOUS, false,
                                false, 0, aSdu.getCommonAddress(),
                                new InformationObject(1, new InformationElement[][] {
                                        { new IeScaledValue(-32768), new IeQuality(true, true, true, true, true) },
                                        { new IeScaledValue(10), new IeQuality(true, true, true, true, true) },
                                        { new IeScaledValue(200), new IeQuality(true, true, true, true, true) } })));

                        break;
                        
                    case C_SC_NA_1:
                        InformationObject informationObject = aSdu.getInformationObjects()[0];
                        IeSingleCommand singleCommand = (IeSingleCommand) informationObject
                                .getInformationElements()[0][0];

                        if (informationObject.getInformationObjectAddress() != 5000) {
                            break;
                        }
                        if (singleCommand.isSelect()) {
                            println("Got single command with select true. Select command.");
                            selected = true;
                        }
                        else if (!singleCommand.isSelect() && selected) {
                            println("Got single command with select false. Execute selected command.");
                            selected = false;
                        }
                        else {
                            println("Got single command with select false. But no command is selected, no execution.");
                        }
                        connection.sendConfirmation(aSdu);
                        break;
                        
                    default:
                        println("Got unknown request: ", aSdu.toString(), ". Will not confirm it.\n");
                    }

                } catch (EOFException e) {
                    println("Will quit listening for commands on connection (" + connectionId,
                            ") because socket was closed.");
                } catch (IOException e) {
                    println("Will quit listening for commands on connection (" + connectionId, ") because of error: \"",
                            e.getMessage(), "\".");
                }

            }

            @Override
            public void connectionClosed(IOException e) {
                println("Connection (" + connectionId, ") was closed. ", e.getMessage());
            }

        }

        @Override
        public void connectionIndication(Connection connection) {

            int myConnectionId = connectionIdCounter++;
            println("A client has connected using TCP/IP. Will listen for a StartDT request. Connection ID: "
                    + myConnectionId);

            try {
                connection.waitForStartDT(new ConnectionListener(connection, myConnectionId), 5000);
            } catch (InterruptedIOException e) {
                // ignore: nothing to do
            } catch (IOException e) {
                println("Connection (" + myConnectionId, ") interrupted while waiting for StartDT: ", e.getMessage(),
                        ". Will quit.");
                return;
            }
            println("Started data transfer on connection (" + myConnectionId, ") Will listen for incoming commands.");

        }

        @Override
        public void serverStoppedListeningIndication(IOException e) {
            println("Server has stopped listening for new connections : \"", e.getMessage(), "\". Will quit.");
        }

        @Override
        public void connectionAttemptFailed(IOException e) {
            println("Connection attempt failed: ", e.getMessage());

        }

    }

    private int connectionIdCounter = 1;

    public static void main(String[] args) {
        new IEC104Ghost().start();
    }

    public void start() {
        Server server = Server.builder().build();

        try {
            server.start(new ServerListener());
        } catch (IOException e) {
            println("Unable to start listening: \"", e.getMessage(), "\". Will quit.");
        }
    }

    private void println(String... strings) {
        StringBuilder sb = new StringBuilder();
        for (String string : strings) {
            sb.append(string);
        }
        System.out.println(sb.toString());
    }

}

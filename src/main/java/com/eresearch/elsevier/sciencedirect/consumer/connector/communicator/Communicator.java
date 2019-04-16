package com.eresearch.elsevier.sciencedirect.consumer.connector.communicator;

import java.net.URI;

public interface Communicator {

    String communicateWithElsevier(URI uri);
}

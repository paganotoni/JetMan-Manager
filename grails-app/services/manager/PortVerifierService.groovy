package manager

class PortVerifierService {

  public Boolean isPortAvailable( String portNumber ) {
    boolean portTaken = false;
    ServerSocket socket = null;

    try {
      socket = new ServerSocket(portNumber.toInteger());
    } catch (IOException e) {
      portTaken = true;
    } finally {
      // Clean up
      if (socket != null) socket.close();
    }

    return !portTaken && Instance.countByPort( portNumber ) == 0
  }
}

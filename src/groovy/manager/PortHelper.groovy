package manager


class PortHelper {

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

  public Integer findAvailablePort(int startPort,int endPort) {
    Integer result

    for(int port : startPort..endPort ){
      if( Instance.countByPort(port) == 0 && isPortAvailable(port.toString())){
        result = port
        break;
      }
    }

    return result
  }
}

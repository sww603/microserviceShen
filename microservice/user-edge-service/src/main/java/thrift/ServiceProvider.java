package thrift;

import com.imooc.thrift.user.UserService;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by sww_6 on 2019/3/4.
 */
@Component
public class ServiceProvider {

  @Value("${thrift.user.ip}")
  private String serverIp;

  @Value("${thrift.user.port}")
  private int serverPort;

  private enum ServiceType {
    USER,
    MESSAGE,
  }

  public UserService.Client getUserService() {

    return getService(serverIp, serverPort, ServiceType.USER);
  }
  public UserService.Client getService(String ip,int port,ServiceType serviceType) {
    TSocket socket = new TSocket(serverIp, serverPort, 3000);
    TTransport transport=new TFramedTransport(socket);
    try {
      transport.open();
    } catch (TTransportException e) {
      e.printStackTrace();
      return null;
    }
    TProtocol protocol=new TBinaryProtocol(transport);
    UserService.Client client=new UserService.Client(protocol);
    return client;
  }
}

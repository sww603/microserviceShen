package com.imooc.user.thrift;

import com.imooc.thrift.user.UserService;
import com.imooc.thrift.user.UserService.Processor;
import javax.annotation.PostConstruct;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol.Factory;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Created by sww_6 on 2019/3/2.
 */
@Configuration
public class ThriftService {
  @Value("${server.port}")
  private int serverPort;

  @Autowired
  private UserService.Iface userService;

  @PostConstruct
  public void startThriftServer(){
    TProcessor processor = new Processor<>(userService);
    TNonblockingServerSocket socket = null;
    try {
      socket = new TNonblockingServerSocket(serverPort);
    } catch (TTransportException e) {
      e.printStackTrace();
    }
    TNonblockingServer.Args args = new TNonblockingServer.Args(socket);
    args.processor(processor);
    args.transportFactory(new TFramedTransport.Factory());
    args.protocolFactory(new Factory());
    TServer server = new TNonblockingServer(args);
    server.serve();
  }
}

package com.tjy.modbus;

import com.tjy.util.ByteAndHexConverter;
import com.serotonin.modbus4j.base.BaseMessageParser;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.ip.IpMessageResponse;
import com.serotonin.modbus4j.ip.encap.EncapMessageParser;
import com.serotonin.modbus4j.ip.encap.EncapMessageRequest;
import com.serotonin.modbus4j.ip.encap.EncapWaitingRoomKeyFactory;
import com.serotonin.modbus4j.ip.xa.XaMessageParser;
import com.serotonin.modbus4j.ip.xa.XaMessageRequest;
import com.serotonin.modbus4j.ip.xa.XaWaitingRoomKeyFactory;
import com.serotonin.modbus4j.msg.ModbusRequest;
import com.serotonin.modbus4j.msg.ModbusResponse;
import com.serotonin.modbus4j.sero.messaging.EpollStreamTransport;
import com.serotonin.modbus4j.sero.messaging.MessageControl;
import com.serotonin.modbus4j.sero.messaging.OutgoingRequestMessage;
import com.serotonin.modbus4j.sero.messaging.Transport;
import com.serotonin.modbus4j.sero.messaging.WaitingRoomKeyFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class ModbusS {
    private final int port;
    private final boolean encapsulated;

    private final ExecutorService executorService;
    private ServerSocket serverSocket;
    private final Map<String, MyHandler> socketMap = new ConcurrentHashMap<>();
    
    private final MyEPoll ePoll;

    public ModbusS(int port, boolean encapsulated, MyEPoll ePoll) {
        this.port = port;
        this.encapsulated = encapsulated;
        this.ePoll = ePoll;
        executorService = Executors.newSingleThreadExecutor();
    }

    public void init() throws ModbusInitException {
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
		        try {
		            serverSocket = new ServerSocket(port);
		            while (true) {
		            	System.out.println("开始while循环accept...");
		                Socket socket = serverSocket.accept();
		                MyHandler handler = new MyHandler(socket);
		                executorService.execute(handler);
		            }
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
			}
		}).start();
    }

    public void destroy() {
    	System.out.println("destroy");
        try {
            serverSocket.close();
            Iterator<MyHandler> it = socketMap.values().iterator();
            while (it.hasNext()) {
                final MyHandler handler = it.next();
                handler.kill();
                it.remove();
            }
            socketMap.clear();
            ePoll.destroy();
            executorService.shutdown();
            executorService.awaitTermination(3, TimeUnit.SECONDS);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ModbusResponse send(ModbusRequest request, String mac) throws Exception {
        MyHandler handler = socketMap.get(mac);
        if (handler != null) {
            return handler.send(request);
        } else {
            throw new Exception("没有该mac地址的设备");
        }
    }

    public void start(String mac) throws Exception {
        MyHandler handler = socketMap.get(mac);
        if (handler != null) {
            handler.start();
        } else {
            throw new Exception("没有该mac地址的设备");
        }
    }

    public void stop(String mac) throws Exception {
        MyHandler handler = socketMap.get(mac);
        if (handler != null) {
            handler.stop();
        } else {
            throw new Exception("没有该mac地址的设备");
        }
    }

    private class MyHandler implements Runnable {
        private final Socket socket;
        private Transport transport;
        private MessageControl conn;
        private short wait_time = 0;
        private boolean initialized = false;
        private boolean running = false;

        private MyHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            byte[] buf = new byte[6];
            String mac = null;
            try {
                InputStream is = socket.getInputStream();
                while (wait_time < 1000) {
                    if (is.available() == 0) {
                        synchronized (this) {
                            try {
                                wait(50);
                                wait_time += 50;
                            } catch (InterruptedException e) {
                                // no op
                            }
                        }
                        continue;
                    }
                    int readCount = is.read(buf);
                    System.out.println("mac地址：读取了" + readCount + "个字节");
                    mac = ByteAndHexConverter.bytesToHexString(buf);
                    break;
                }
                if (mac != null && mac.length() != 0) {
                	System.out.println("wait_time = " + wait_time);
                    socketMap.put(mac, this);
                    System.out.println("mac地址为" + mac);
                } else {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private synchronized ModbusResponse send(ModbusRequest request) throws ModbusTransportException {
            if (initialized && running) {
                OutgoingRequestMessage ipRequest;
                if (encapsulated) {
                    ipRequest = new EncapMessageRequest(request);
                } else {
                    ipRequest = new XaMessageRequest(request, 0);
                }
                IpMessageResponse ipResponse;
                try {
                    ipResponse = (IpMessageResponse) conn.send(ipRequest);
                    if (ipResponse == null) {
                        return null;
                    }
                    return ipResponse.getModbusResponse();
                } catch (IOException e) {
                    throw new ModbusTransportException(e, request.getSlaveId());
                }
            } else {
                throw new ModbusTransportException("handler.initialized = " + String.valueOf(initialized) +
                        ", handler.running = " + String.valueOf(running), request.getSlaveId());
            }
        }

        private void start() {
            if (!initialized) {
                BaseMessageParser ipMessageParser;
                WaitingRoomKeyFactory waitingRoomKeyFactory;
                try {
                    transport = new EpollStreamTransport(socket.getInputStream(), socket.getOutputStream(), ePoll);
                    if (encapsulated) {
                        ipMessageParser = new EncapMessageParser(true);
                        waitingRoomKeyFactory = new EncapWaitingRoomKeyFactory();
                    } else {
                        ipMessageParser = new XaMessageParser(true);
                        waitingRoomKeyFactory = new XaWaitingRoomKeyFactory();
                    }
                    conn = new MessageControl();
//                conn.setTimeout();
//                conn.setRetries();
//                conn.setDiscardDataDelay();
                    conn.DEBUG = true;
                    conn.start(transport, ipMessageParser, null, waitingRoomKeyFactory);
                    initialized = true;
                    running = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (!running) {
                try {
                    transport.setConsumer(conn);
                    running = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private synchronized void stop() {
            if (initialized) {
                transport.removeConsumer();
                running = false;
            }
        }

        private synchronized void kill() {
            stop();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            initialized = false;
        }
    }
}


package com.tjy.modbus;

import com.serotonin.modbus4j.sero.epoll.InputStreamEPollWrapper;
import com.serotonin.modbus4j.sero.epoll.Modbus4JInputStreamCallback;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class MyEPoll implements InputStreamEPollWrapper {
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Map<InputStream, InputStreamHandler> map = new ConcurrentHashMap<>();

    @Override
    public void add(InputStream in, Modbus4JInputStreamCallback inputStreamCallback) {
        if (map.get(in) == null) {
            InputStreamHandler handler = new InputStreamHandler(in, inputStreamCallback);
            map.put(in, handler);
            executorService.execute(handler);
        }
    }

    @Override
    public void remove(InputStream in) {
        InputStreamHandler handler = map.get(in);
        if (handler != null) {
            handler.stop();
        }
        map.remove(in);
    }

    public void destroy() {
        Iterator<InputStreamHandler> it = map.values().iterator();
        while (it.hasNext()) {
            it.next().stop();
            it.remove();
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class InputStreamHandler implements Runnable {
        private final InputStream in;
        private final Modbus4JInputStreamCallback inputStreamCallback;
        private volatile boolean running = true;

        private InputStreamHandler(InputStream in, Modbus4JInputStreamCallback inputStreamCallback) {
            this.in = in;
            this.inputStreamCallback = inputStreamCallback;
        }

        @Override
        public void run() {
            running = true;
            byte[] buf = new byte[1024];
            int readCount;
            try {
                while (running) {
                    try {
                        if (in.available() == 0) {
                            synchronized (this) {
                                try {
                                    wait(50);
                                } catch (InterruptedException e) {
                                    // no op
                                }
                            }
                            continue;
                        }

                        readCount = in.read(buf);
                        inputStreamCallback.input(buf, readCount);
                    } catch (IOException e) {
                        inputStreamCallback.ioException(e);
                        if (StringUtils.equals(e.getMessage(), "Stream closed."))
                            break;
                        if (StringUtils.contains(e.getMessage(), "nativeavailable"))
                            break;
                    }
                }
            } finally {
                running = false;
            }
        }

        private void stop() {
            running = false;
            synchronized (this) {
                notify();
            }
        }
    }
}


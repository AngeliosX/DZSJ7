package packserver;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.nio.channels.SelectionKey.*;
import static java.nio.channels.SelectionKey.OP_READ;
import static java.nio.charset.StandardCharsets.UTF_8;

public class Server {
    public static void main(String[] args) {
        new Server().start();
    }

    final String ADR = "localhost";
    final int PORT = 3030;
    private byte[] ba;
    public static List<String> clients = new ArrayList<String>();

    final void start() {
        System.out.println("[SERVER]");
        try (Selector selector = Selector.open();
             ServerSocketChannel socket = ServerSocketChannel.open()
        ) {
            InetSocketAddress socketAddress = new InetSocketAddress(ADR, PORT);
            socket.bind(socketAddress);
            socket.configureBlocking(false);
            socket.register(selector, OP_ACCEPT);
            //
            do {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isAcceptable()) {
                        SocketChannel client = socket.accept();
                        client.configureBlocking(false);
                        System.out.println("server> connected: "
                                + client.getRemoteAddress());
                        client.register(selector, OP_READ);

                    } else if (key.isReadable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(256);
                        client.read(buffer);
                        String line = new String(buffer.array()).trim();
                        System.out.println("client> " + line);
                        ba = line.getBytes(UTF_8);
                        if (line.equalsIgnoreCase("exit")) {
                            client.close();
                        } else {
                            client.register(selector, OP_WRITE);
                        }
                    } else if (key.isWritable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.wrap(ba);
                        client.write(buffer);
                    }
                }
            } while (true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package packclient;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        new Client().start();
    }

    final String ADR = "localhost";
    final int PORT = 3030;

    final void start() {
        System.out.println("[CLIENT]");
        InetSocketAddress address = new InetSocketAddress(ADR, PORT);
        try (SocketChannel client = SocketChannel.open(address)) {
            Scanner sc = new Scanner(System.in);
            ByteBuffer buf = ByteBuffer.allocate(256);
            String line;
            while (true) {
                System.out.print("client> ");
                line = sc.nextLine();
                buf.clear();
                buf.put(line.getBytes()).flip();
                client.write(buf);
                if (line.equalsIgnoreCase("exit")){
                    break;
                }
                buf.clear();
                client.read(buf);
                buf.flip();
                System.out.println("server> " +
                        new String(buf.array(), buf.position(), buf.limit()));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

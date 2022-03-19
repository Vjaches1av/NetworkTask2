import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Scanner;

public final class SimpleServer {

    public SimpleServer() {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Сервер успешно запущен!");

            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println("Установлено соединение: " + getIPAndPort(socket));

                    Scanner scanner = new Scanner(socket.getInputStream(), Charset.forName("cp866"));
                    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true, Charset.forName("cp866"));

                    createThread(socket, scanner, writer).start();
                } catch (IOException e) {
                    System.err.println("Ошибка установки соединения!");
                }
            }
        } catch (IOException e) {
            System.err.println("Не удалось запустить сервер!");
        }
    }

    private @NotNull String getIPAndPort(@NotNull Socket socket) {
        return socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
    }

    private Thread createThread(Socket socket, Scanner scanner, @NotNull PrintWriter writer) {
        writer.println("Привет, я Сервер! Как тебя зовут?");
        return new Thread(() -> {
            int i = 0;
            boolean done = false;
            String firstName = "Тайный Друг";
            while (true) {
                if (scanner.hasNext()) {
                    String input = scanner.nextLine();
                    switch (i++) {
                        case 0: {
                            firstName = input;
                            writer.println("Ты ребенок?");
                            break;
                        }
                        case 1: {
                            if ("Да".equalsIgnoreCase(input)) {
                                writer.println("Добро пожаловать в детскую зону, " + firstName + "! " +
                                        "Играй и веселись! Пока ;)");
                            } else {
                                writer.println("Добро пожаловать в зону для взрослых, " + firstName + "! " +
                                        "Хорошего отдыха! Пока ;)");
                            }
                            done = true;
                            break;
                        }
                    }
                    if (done) {
                        try {
                            socket.close();
                            System.out.println("Соединение разорвано: " + getIPAndPort(socket));
                            break;
                        } catch (IOException e) {
                            writer.println("Ты отличный собеседник. Пообщаемся еще?");
                            i = 0;
                        }
                    }

                }
            }
        });
    }

    public static void main(String[] args) {
        new SimpleServer();
    }
}

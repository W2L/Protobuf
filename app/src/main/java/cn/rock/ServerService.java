package cn.rock;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(() -> startServer()).start();
        return super.onStartCommand(intent, flags, startId);
    }

private void startServer() {
    try {
        ServerSocket serverSocket = new ServerSocket(9998);
        while (true) {
            Socket socket = serverSocket.accept();
            InputStream inputStream = socket.getInputStream();
            StudentProto.Student student = StudentProto.Student.parseFrom(inputStream);
            System.out.println("客户端: " + student);
            socket.close();
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}

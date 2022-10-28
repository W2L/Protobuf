package cn.rock;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ClientActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protobuf);

        startService(new Intent(this, ServerService.class));

        //创建实体
        StudentProto.Student student = StudentProto.Student.newBuilder().setAge(12).setSex(StudentProto.Student.Sex.FEMALE).addCourse("English").build();
        ((TextView) findViewById(R.id.student)).setText(student.toString());

        //实体转ByteString
        ByteString byteString = student.toByteString();
        ((TextView) findViewById(R.id.byteString)).setText(byteString.toString());

        //ByteString转实体
        try {
            student = StudentProto.Student.parseFrom(byteString);
            ((TextView) findViewById(R.id.student1)).setText(student.toString());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        //ByteString处理字节数组
        new Thread(() -> {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tjxb);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] bytes = baos.toByteArray();
            ByteString tmpByteString = ByteString.copyFrom(bytes);
            byte[] tmpBytes = tmpByteString.toByteArray();
            Bitmap tmpBitmap = BitmapFactory.decodeByteArray(tmpBytes, 0, bytes.length);
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.post(() -> imageView.setImageBitmap(tmpBitmap));
        }).start();

        final StudentProto.Student stu = student;
        new Handler().postDelayed(() -> new Thread(() -> connectServer(stu)).start(), 2000L);
    }

private void connectServer(StudentProto.Student student) {
    try {
        Socket socket = new Socket("0.0.0.0", 9998);
        OutputStream os = socket.getOutputStream();
        student.writeTo(os);
        socket.shutdownOutput();
        socket.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}
package com.qwh.tcpclient;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button btn_connect;
    private Button btn_disconnect;
    private Button btn_send;
    private TextView tv_state;
    private EditText et_ip;
    private EditText et_port;
    private EditText et_send;
    private Button btn_clear;
    private TextView tv_receive;
    private String TAG_log;
    /**
     * socket data receive
     * data(byte[]) analyze
     */
    private TcpClient.OnDataReceiveListener dataReceiveListener = new TcpClient.OnDataReceiveListener() {
        @Override
        public void onConnectSuccess() {
            Log.i(TAG_log, "onDataReceive connect success");
            tv_state.setText("已连接");
        }

        @Override
        public void onConnectFail() {
            Log.e(TAG_log, "onDataReceive connect fail");
            tv_state.setText("未连接");
        }

        @Override
        public void onDataReceive(byte[] buffer, int size, int requestCode) {
            //获取有效长度的数据
            byte[] data = new byte[size];
            System.arraycopy(buffer, 0, data, 0, size);

            final String oxValue = HexUtil.bytes2Hex(data);
            Log.i(TAG_log, "onDataReceive requestCode = " + requestCode + ", content = " + oxValue);

//            tv_receive.setText(tv_receive.getText().toString() + oxValue + "\n");
            tv_receive.setText( oxValue + "\n");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TAG_log = this.getLocalClassName();
        btn_connect = findViewById(R.id.button);
        btn_disconnect = findViewById(R.id.button2);
        btn_send = findViewById(R.id.button3);
        btn_clear = findViewById(R.id.button4);
        tv_state = findViewById(R.id.textView2);
        tv_receive = findViewById(R.id.textView8);
        et_ip = findViewById(R.id.editTextTextPersonName);
        et_port = findViewById(R.id.editTextTextPersonName2);
        et_send = findViewById(R.id.editTextTextPersonName3);
        tv_receive.setMovementMethod(ScrollingMovementMethod.getInstance());
        initListener();
        initDataReceiver();
    }

    private void initListener() {
        //socket connect

        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = et_ip.getText().toString();
                String port = et_port.getText().toString();

                if (TextUtils.isEmpty(ip)) {
                    Toast.makeText(MainActivity.this, "IP地址为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(port)) {
                    Toast.makeText(MainActivity.this, "端口号为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                connect(ip, Integer.parseInt(port));
            }
        });

        //socket disconnect
        btn_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });

        //socket send
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TcpClient.getInstance().isConnect()) {
                    byte[] data = et_send.getText().toString().getBytes();
                    send(data);
                } else {
                    Toast.makeText(MainActivity.this, "尚未连接，请连接Socket", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //clear receive
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_receive.setText("");
            }
        });
    }

    /**
     * socket data receive
     */
    private void initDataReceiver() {
        TcpClient.getInstance().setOnDataReceiveListener(dataReceiveListener);
    }

    /**
     * socket connect
     */
    private void connect(String ip, int port) {
        TcpClient.getInstance().connect(ip, port);
    }

    /**
     * socket disconnect
     */
    private void disconnect() {
        TcpClient.getInstance().disconnect();
        tv_state.setText("未连接");
    }

    /**
     * socket send
     */
    private void send(byte[] data) {
        TcpClient.getInstance().sendByteCmd(data, 1001);
    }

    @Override
    protected void onDestroy() {
        TcpClient.getInstance().disconnect();
        super.onDestroy();
    }
}
package com.example.joljak;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    TextView connectMachine;
    private static final int REQUEST_ENABLE_BT = 10; // 블루투스 활성화 상태
    private BluetoothAdapter bluetoothAdapter; // 블루투스 어댑터
    private Set<BluetoothDevice> devices; // 블루투스 디바이스 데이터 셋
    private BluetoothDevice bluetoothDevice; // 블루투스 디바이스
    private BluetoothSocket bluetoothSocket = null; // 블루투스 소켓
    private OutputStream outputStream = null; // 블루투스에 데이터를 출력하기 위한 출력 스트림
    private InputStream inputStream = null; // 블루투스에 데이터를 입력하기 위한 입력 스트림
    private Thread workerThread = null; // 문자열 수신에 사용되는 쓰레드
    private byte[] readBuffer; // 수신 된 문자열을 저장하기 위한 버퍼
    private int readBufferPosition; // 버퍼 내 문자 저장 위치
    private TextView textViewReceive; // 수신 된 데이터를 표시하기 위한 텍스트 뷰
    private TextView ID;//신원 정보
    private TextView UserAddress;//주소 정보
    private TextView CareId;//보호자 신원 정보
    private TextView CarePhone;//보호자 전화번호
    private EditText editTextSend; // 송신 할 데이터를 작성하기 위한 에딧 텍스트
    private Button buttonSend; // 송신하기 위한 버튼
    private Button Emergency;//긴급 전화
    private List<CareId> care;
    private UserId user;
    CareId care1;
    final static int ACT_SUB=0;
    final static int ACT_COMPLETE=1;
    String sfName="myFile";
    //할일을 하지 않았을때 백그라운드에 표시
    //클라이언트한테 전송
    Button emergenctButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ID=(TextView)findViewById(R.id.mainUser);
        UserAddress=(TextView)findViewById(R.id.mainAddress);
        CareId=findViewById(R.id.MainCare);
        CarePhone=findViewById(R.id.MainCarePhone);
        care1=new CareId();
        care=new ArrayList<>();
        emergenctButton=(Button)findViewById(R.id.EmergencyButton);
        SharedPreferences sf=getSharedPreferences(sfName,0);
        String str=sf.getString("ID","");
        ID.setText(str);
        str=sf.getString("USERADDRESS","");
        UserAddress.setText(str);
        str=sf.getString("CAREID","");
        CareId.setText(str);
        str=sf.getString("CAREPHONE","");
        CarePhone.setText(str);
        //처음시작이라면 사용자 정보 보호자정보 입력(데이터 입력이없어도 처음시작)
        //textViewReceive=(TextView)findViewById(R.id.receive);
        //bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // 블루투스 어댑터를 디폴트 어댑터로 설정
        emergenctButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tel="01085338348";//여기 전화번호 바꿔줘야함
                String text="응급 상황 발생 위치 :";
                SmsManager smsManager=SmsManager.getDefault();
                smsManager.sendTextMessage(tel,null,text,null,null);
                Intent intent=new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:01085338348"));
                try {
                    startActivity(intent);
                }catch(Exception e){
                    e.printStackTrace();;
                }
            }
        });



        /*if(bluetoothAdapter == null) { // 디바이스가 블루투스를 지원하지 않을 때
            // 여기에 처리 할 코드를 작성하세요.
        }

        else { // 디바이스가 블루투스를 지원 할 때
            if(bluetoothAdapter.isEnabled()) { // 블루투스가 활성화 상태 (기기에 블루투스가 켜져있음)
                selectBluetoothDevice(); // 블루투스 디바이스 선택 함수 호출
            }

            else { // 블루투스가 비 활성화 상태 (기기에 블루투스가 꺼져있음)
                // 블루투스를 활성화 하기 위한 다이얼로그 출력
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                // 선택한 값이 onActivityResult 함수에서 콜백된다.
                startActivityForResult(intent, REQUEST_ENABLE_BT);
            }
            BluetoothProfile.ServiceListener mProfileListenr=new BluetoothProfile.ServiceListener() {
                @Override
                public void onServiceConnected(int profile, BluetoothProfile proxy) {
                    if(profile==BluetoothProfile.A2DP){
                        boolean deviceconnected=false;
                        BluetoothA2dp btA2dp=(BluetoothA2dp)proxy;
                        List<BluetoothDevice> a2dpconnected=btA2dp.getConnectedDevices();
                        if(a2dpconnected.size() !=0){
                            for(BluetoothDevice device : a2dpconnected){
                                if(device.getName().contains("AirPods")){
                                    deviceconnected=true;
                                    textViewReceive.setText(device.getName());

                                }
                            }
                        }
                        if(!deviceconnected){
                            textViewReceive.setText("연결안됨");
                        }
                        bluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP,btA2dp);
                    }
                }

                @Override
                public void onServiceDisconnected(int profile) {

                }
            };
            Context context=getApplicationContext();
            bluetoothAdapter.getProfileProxy(context,mProfileListenr,BluetoothProfile.A2DP);
        }*/
    }
    @Override
    protected void onActivityResult(int requestcode,int resultcode,Intent data){
        switch(requestcode){
            case ACT_SUB:
                if(resultcode== Activity.RESULT_OK) {
                    user.name = data.getStringExtra("UserID");
                    user.Address = data.getStringExtra("UserAddress");
                    care1 = new CareId(data.getStringExtra("CareName"), data.getStringExtra("CarePhone"));
                    care.add(care1);
                    ID.setText(user.name);
                    UserAddress.setText(user.Address);
                    CareId.setText(care1.CareId);
                    CarePhone.setText(care1.Phone);
                }
                break;
            case ACT_COMPLETE:
                //여기에 이제 설정된것들을 넣으면됨
                user.name=data.getStringExtra("UserID");
                user.Address=data.getStringExtra("UserAddress");
                care1=new CareId(data.getStringExtra("CareName"),data.getStringExtra("CarePhone"));
                care.add(care1);
                ID.setText(user.name);
                UserAddress.setText(user.Address);
                CareId.setText(care1.CareId);
                CarePhone.setText(care1.Phone);
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outstate){//일시정지될때 저장
        super.onSaveInstanceState(outstate);

    }
    @Override
    protected void onStop(){//어플이 종료되기전 데이터 저장(사용자 보호자 인적사항등을 저장하기 위해 사용)
        super.onStop();
        SharedPreferences sf=getSharedPreferences(sfName,0);
        SharedPreferences.Editor editor=sf.edit();
        if(user!=null) {
            editor.putString("ID", user.name);
            editor.putString("USERADDRESS", user.Address);
            editor.putString("CAREID", care1.CareId);
            editor.putString("CAREPHONE", care1.Phone);
            editor.commit();
        }
    }
   // @Override
    protected void onPause(Bundle savedInsatanceState){//일시정지 되었을때
        super.onPause();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.mainmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent=null;
        switch(item.getItemId()){
            case  R.id.GPS:
                intent=new Intent(MainActivity.this,GPSActivity.class);
                startActivity(intent);
                break;
            case R.id.Condition:
                intent=new Intent(MainActivity.this,ConditionActivity.class);
                startActivity(intent);
                break;
            case R.id.simbak:
                intent=new Intent(MainActivity.this,simbakActivity.class);
                startActivity(intent);
                break;
            case R.id.write://디비부분에 저장 희망 이게 제일 중요함 만약 어그러지면 다 망함
                intent=new Intent(MainActivity.this,SaveID.class);
                startActivityForResult(intent,ACT_SUB);
                intent=getIntent();
                user=new UserId();
                user.name=intent.getStringExtra("UserID");
                user.Address=intent.getStringExtra("UserAddress");
                care1=new CareId(intent.getStringExtra("CareName"),intent.getStringExtra("CarePhone"));
                care.add(care1);
                ID.setText(user.name);
                UserAddress.setText(user.Address);
                CareId.setText(care1.CareId);
                CarePhone.setText(care1.Phone);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void selectBluetoothDevice() {
        // 이미 페어링 되어있는 블루투스 기기를 찾습니다.
        devices = bluetoothAdapter.getBondedDevices();
        // 페어링 된 디바이스의 크기를 저장
        int pariedDeviceCount = devices.size();
        // 페어링 되어있는 장치가 없는 경우
        if(pariedDeviceCount == 0) {
            // 페어링을 하기위한 함수 호출
        }
        // 페어링 되어있는 장치가 있는 경우
        else {
            // 디바이스를 선택하기 위한 다이얼로그 생성
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("페어링 되어있는 블루투스 디바이스 목록");
            // 페어링 된 각각의 디바이스의 이름과 주소를 저장
            List<String> list = new ArrayList<>();
            // 모든 디바이스의 이름을 리스트에 추가
            for(BluetoothDevice bluetoothDevice : devices) {
                list.add(bluetoothDevice.getName());
            }
            list.add("취소");
            // List를 CharSequence 배열로 변경
            final CharSequence[] charSequences = list.toArray(new CharSequence[list.size()]);
            list.toArray(new CharSequence[list.size()]);
            // 해당 아이템을 눌렀을 때 호출 되는 이벤트 리스너
            builder.setItems(charSequences, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 해당 디바이스와 연결하는 함수 호출
                    connectDevice(charSequences[which].toString());

                }
            });
            // 뒤로가기 버튼 누를 때 창이 안닫히도록 설정
            builder.setCancelable(false);
            // 다이얼로그 생성
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

    }
    public void connectDevice(String deviceName) {
        // 페어링 된 디바이스들을 모두 탐색
        for(BluetoothDevice tempDevice : devices) {
            // 사용자가 선택한 이름과 같은 디바이스로 설정하고 반복문 종료
            if(deviceName.equals(tempDevice.getName())) {
                bluetoothDevice = tempDevice;
                break;
            }
        }
        // UUID 생성
        UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        // Rfcomm 채널을 통해 블루투스 디바이스와 통신하는 소켓 생성
        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
            // 데이터 송,수신 스트림을 얻어옵니다.
            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();
            // 데이터 수신 함수 호출
            receiveData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void receiveData() {
        final Handler handler = new Handler();
        // 데이터를 수신하기 위한 버퍼를 생성
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        // 데이터를 수신하기 위한 쓰레드 생성
        workerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(Thread.currentThread().isInterrupted()) {
                    try {
                        // 데이터를 수신했는지 확인합니다.
                        int byteAvailable = inputStream.available();
                        // 데이터가 수신 된 경우
                        if(byteAvailable > 0) {
                            // 입력 스트림에서 바이트 단위로 읽어 옵니다.
                            byte[] bytes = new byte[byteAvailable];
                            inputStream.read(bytes);
                            // 입력 스트림 바이트를 한 바이트씩 읽어 옵니다.
                            for(int i = 0; i < byteAvailable; i++) {
                                byte tempByte = bytes[i];
                                // 개행문자를 기준으로 받음(한줄)
                                if(tempByte == '\n') {
                                    // readBuffer 배열을 encodedBytes로 복사
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    // 인코딩 된 바이트 배열을 문자열로 변환
                                    final String text = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            // 텍스트 뷰에 출력
                                            textViewReceive.append(text + "\n");
                                            textViewReceive.setText(text+"\n");
                                        }
                                    });
                                } // 개행 문자가 아닐 경우
                                else {
                                    readBuffer[readBufferPosition++] = tempByte;
                                }

                            }

                        }

                    } catch (IOException e) {

                        e.printStackTrace();

                    }

                    try {

                        // 1초마다 받아옴

                        Thread.sleep(1000);

                    } catch (InterruptedException e) {

                        e.printStackTrace();

                    }

                }

            }

        });

        workerThread.start();

    }

}//여가까지가 수신

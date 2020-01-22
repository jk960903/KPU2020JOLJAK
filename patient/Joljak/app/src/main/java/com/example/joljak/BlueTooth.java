package com.example.joljak;
import android.app.AlertDialog;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
//
public class BlueTooth extends Application {
    public static final int REQUEST_ENABLE_BT=10;//블루투스 활성화 상태
    public BluetoothAdapter bluetoothAdapter;//블루투스 어댑터
    public Set<BluetoothDevice> devices;//블루투스 디바이스 데이터 셋
    public BluetoothDevice  bluetoothDevice;//블루투스 디바이스
    public BluetoothSocket bluetoothSocket=null;//블루투스 소켓
    public OutputStream outputStream =null;//블루투스 데이터 출력위한 출력 스트림
    public InputStream inputStream=null;//블루투스 데이터를 입력하기 위한 입력 스트림
    public Thread workerThread=null;
    byte[] readBuffer;//수신된 문자열 입력버퍼
    public int readBufferPosition;//버퍼내 문자 저장위치
    String mTmp;
    String mStrDlimiter = "\n";
    String rcvData;
    char mCharDelimiter =  '\n';
    public void checkBluetooth(){
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter==null){
            //미지원
        }
        else{
            if(!bluetoothAdapter.isEnabled()){
                //비활성화 상태
                Intent EnableIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //여기 다음을 이제 메인에서 다뤄야함
            }
            else{
                //활성화 상태
                selectDevice();
            }
        }
    }
    public void selectDevice(){
        devices=bluetoothAdapter.getBondedDevices();//BoundeDevice는 페어링된 장치 목록얻어오는 함수
        final int paredevicecount=devices.size();
        if(paredevicecount==0){
            //페어링장치가 없음
        }
        else{
          //  AlertDialog.Builder builder=new AlertDialog.Builder(getApplicationContext());
            //builder.setTitle("블루투스 장치 선택");
            //각 디바이스들은 이름과 다른주소 페어링된 디바이스들을 표시
            List<String> listItems=new ArrayList<String>();
            for(BluetoothDevice device : devices){
                listItems.add(device.getName());

            }
            listItems.add("취소");
            final CharSequence[] items= listItems.toArray(new CharSequence[listItems.size()]);
            listItems.toArray(new CharSequence[listItems.size()]);

            /*builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if(item==paredevicecount){
                        //연결할 장치안하고 취소를 눌렀을때
                    }
                    else{
                        connectToSelctedDevice(items[item].toString());
                        mTmp=items[item].toString();
                    }
                }
            });
            builder.setCancelable(false);//뒤로가기 사용 금지)
            AlertDialog alert=builder.create();
            alert.show();*/
        }
    }
    void connectToSelctedDevice(String selectedDeviceName){
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        devices=bluetoothAdapter.getBondedDevices();
        bluetoothDevice=getDeviceFromBondedList(selectedDeviceName);
        UUID uuid=java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        try{
            //소켓생성
            bluetoothSocket=bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
            //송수신 스트림얻고
            outputStream=bluetoothSocket.getOutputStream();
            inputStream=bluetoothSocket.getInputStream();

            beginListenForData();//수신준비
        }catch (Exception e){

        }
    }
    void beginListenForData(){
        final Handler handler=new Handler();

        readBufferPosition=0;
        readBuffer=new byte[1024];


        //수신 스레드
        workerThread=new Thread(new Runnable() {
            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted()){
                    //다른 스레드에서 블로킹 전까지 읽을수 있는 문자열 개수 반환
                    try{
                    int byteAvailable=inputStream.available();
                    if(byteAvailable>0){
                        byte[] packetBytes=new byte[byteAvailable];

                        inputStream.read(packetBytes);
                        for(int i=0; i<byteAvailable; i++){
                            byte b=packetBytes[i];
                            if(b==mCharDelimiter){
                                byte[] encodedBytes=new byte[readBufferPosition];
                                System.arraycopy(readBuffer,0,encodedBytes,0,encodedBytes.length);//복사할 배열,시작점,복사된배열,붙이기 시작점,복사할개수
                                final String data=new String(encodedBytes,"US-ASCII");
                                readBufferPosition=0;
                                Log.i("RevData","first");
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.i("RevData","second");
                                        rcvData=rcvData+data+mStrDlimiter;
                                    }
                                });
                            }
                            else{
                                readBuffer[readBufferPosition++]=b;
                            }
                        }
                    }
                }catch(Exception e){

                    }

                }
            }

        });

    }
    BluetoothDevice getDeviceFromBondedList(String name) {

        // BluetoothDevice : 페어링 된 기기 목록을 얻어옴.

        BluetoothDevice selectedDevice = null;

        // getBondedDevices 함수가 반환하는 페어링 된 기기 목록은 Set 형식이며,

        // Set 형식에서는 n 번째 원소를 얻어오는 방법이 없으므로 주어진 이름과 비교해서 찾는다.

        for(BluetoothDevice deivce : devices) {

            // getName() : 단말기의 Bluetooth Adapter 이름을 반환

            if(name.equals(deivce.getName())) {

                selectedDevice = deivce;

                break;

            }

        }

        return selectedDevice;

    }
}

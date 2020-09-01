package com.example.joljak;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.Manifest;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
//이거 확인
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import static com.example.joljak.MainActivity.ACT_SUB;

public class GPSActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    // 여기액티비티는 블루투스 연계하고 블루투스에 대한 데이터값 핸들링 해야함;;

    private GoogleApiClient googleApiClient = null;
    private GoogleMap googleMap = null;
    private Marker currentparker = null;
    private static final String TAG = "googlemap_example";
    private AppCompatActivity mActivity;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초
    boolean askPermissionOnceAgain = false;
    boolean mRequestingLocationUpdates = false;
    Location mCurrentLocation;
    boolean mMoveMapByUser = true;
    boolean mMoveMapByAPI = true;
    LatLng currentPositon;
    public static String defaultUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    Handler handler = new Handler();
    LocationRequest locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(UPDATE_INTERVAL_MS).setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);
    //setinterval=위치가 업데이트 되는 주기
    //setfastestinterval위치 획득후 업데이트 되는 주기
    //setpriority4가지 설정값 high->배터리 소모 고려없이 가장 정확도 최우선
    //low-저전력 고려 정확도가 떨어짐,no 배터리 소모없이 위치정보 획득,balancedpower-전력과 정확도의 밸런스 고려
    //setsmallestdisplacement-최소 거리 이동시 갱신가능
    //locationrequest가 null이되는 경우 위치활성화를 끄거나 비활성화하면 이전 캐시가 지워져 마지막 위치라도 못구함
    //위치권한을 비활성화 시키고 다시 활성화 시켜도 바로 못가져오고 위치 업데이트 요청을 해야함

    //전역변수
    private double prelat, prelon;;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        View actionbarView;
        ImageButton menu_btn;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar =getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(false);

        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        actionbarView = getLayoutInflater().inflate(R.layout.toolbar, null);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(actionbarView, layout);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.d(TAG, "onCreate");
        mActivity = this;
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addApi(LocationServices.API).build();
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (googleApiClient.isConnected()) {
            Log.d(TAG, "onResume:call startLocationUpdates");
            if (!mRequestingLocationUpdates) startLocationUpdates();
        }
        //앱 정보에서 퍼미션 허가했는지 확인
        if (askPermissionOnceAgain) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askPermissionOnceAgain = false;

                checkPermission();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.GPS:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.write:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(final GoogleMap map) {//지도의 초기세팅
        googleMap = map;
        //런타임 퍼미션 요청 or 활성 요청 대화상자 보이기전 초기위치로 이동
        setDefaultLocation();
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);//자기위치버튼활성화
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));//카메라 이동할수있게 활성화
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                mMoveMapByAPI = true;//위치에 따른 카메라 이동 활성화
                return true;
            }
        });
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //맵클릭
            }
        });
        googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                if (mMoveMapByUser == true && mRequestingLocationUpdates) {
                    mMoveMapByAPI = false;//위치에 따른 카메라 이동 비활성화
                }
                mMoveMapByUser = true;//사람이 움직일수 있게해줌
            }
        });
        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {

            }
        });

    }

    private void startLocationUpdates() {
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            mRequestingLocationUpdates = true;
            googleMap.setMyLocationEnabled(true);
        }
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        mRequestingLocationUpdates = false;
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mRequestingLocationUpdates == false) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                } else {
                    //퍼미션을 가지고 있고 혅위치 업데이트함수 호출
                    startLocationUpdates();
                    googleMap.setMyLocationEnabled(true);//현재위치 활성화
                }
            } else {
                //현재위치 업데이트
                startLocationUpdates();
                googleMap.setMyLocationEnabled(true);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        //연결 거절
        if (cause == CAUSE_NETWORK_LOST) {
            //네트워크 연결 안됨
        } else if (cause == CAUSE_SERVICE_DISCONNECTED) {
            //구글 플레이 서비스 연결 실패
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //연결 실패
        setDefaultLocation();//디폴트 위치로 설정
    }

    @Override
    public void onLocationChanged(Location location) {//GPS 위치가 변경되었을때 오버라이딩
        currentPositon = new LatLng(location.getLatitude(), location.getLongitude());//위도와 경도 얻어옴
        String markerTitle = getCurrentAddress(currentPositon);
        String markserSnippet = "위도:" + String.valueOf(location.getLatitude()) + "경도 :" + String.valueOf(location.getLongitude());
        setCurrentLocation(location, markerTitle, markserSnippet);
        mCurrentLocation = location;//최근위치 변경
        prelat = mCurrentLocation.getLatitude();
        prelon = mCurrentLocation.getLongitude();
    }

    @Override
    protected void onStart() {
        if (googleApiClient != null && googleApiClient.isConnected() == false) {
            //연결이 안됨
            googleApiClient.connect();
        }
        super.onStart();

    }

    @Override
    protected void onStop() {
        if (mRequestingLocationUpdates) {
            stopLocationUpdates();
        }
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    private String getCurrentAddress(LatLng latlng) {//현재 주소 반환
        //지오코더 GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1);


        } catch (IOException ioException) {
            return "지오코더 서비스 사용 불가";
        } catch (IllegalArgumentException illegalArgumentException) {//부적절 인자를 받았을때
            return "잘못된 GPS 좌표";
        }
        if (addresses == null || addresses.size() == 0) {
            return "주소 미발견";
        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {
        mMoveMapByUser = false;
        //사용자가 맵움직일수 없게 변경
        if (currentparker != null) currentparker.remove();
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);//마커의 위치
        markerOptions.title(markerTitle);//마커를 눌렀을때 정보창에 표시되는 문자열
        markerOptions.snippet(markerSnippet);//제목 아래에 표시되는 추가 텍스트
        markerOptions.draggable(true);//마커이동가능
        //이에 추가로 Icon//visibility/Flat or Billboard(평면 또는 빌보드 방향)
        //lotation(회전),zindex(그리기순서),Tag
        currentparker = googleMap.addMarker(markerOptions);
        if (mMoveMapByAPI) {
            Log.d(TAG, "setCurrentLocation:mGoogleMap moveCamera" + location.getLatitude() + " " + location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);//카메라 위치변경
            googleMap.moveCamera(cameraUpdate);
        }

    }

    public void setDefaultLocation() {
        mMoveMapByUser = false;//사용자가 이동 불가
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);//위치 서울로 설정(GPS  안켜져있거나 인터넷 연결이 안되 구글 api를 가져올수 없음)
        String marketTitle = "위치정보 가져오기 불가";
        String markterSnippet = "위치 퍼미션과 GPS 활성 요부 확인";

        if (currentparker != null) currentparker.remove();//마커가 있다면 마커 제거
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(marketTitle);
        markerOptions.snippet(markterSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentparker = googleMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        googleMap.moveCamera(cameraUpdate);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {
        boolean fineLocationRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED && fineLocationRationale)
            showDialogForPermission("위치정보 승인 해야함");
        else if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED && !fineLocationRationale) {
            showDialogForPermissionSettings("위치 정보 승인 거부");
        } else if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            //퍼미션 허용
            if (googleApiClient.isConnected() == false) {
                //googleapi와 연결이 안되어있다면
                googleApiClient.connect();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permsRequestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.length > 0) {
            boolean permissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

            if (permissionAccepted) {
                if (googleApiClient.isConnected() == false) {
                    //연결이 안되었을때
                    googleApiClient.connect();
                }
            } else {
                checkPermission();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GPSActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        });
        builder.create().show();
    }

    private void showDialogForPermissionSettings(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GPSActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                askPermissionOnceAgain = true;
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + mActivity.getPackageName()));
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.create().show();
    }

    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GPSActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치서비스 필요합니다.\n" + "위치 설정을 수정하실건가요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();

    }

    @Override
    protected void onActivityResult(int requestcode, int resultcode, Intent data) {//GPS 켰는지 안켰는지 검사
        super.onActivityResult(requestcode, resultcode, data);

        switch (requestcode) {
            case GPS_ENABLE_REQUEST_CODE:

                if (checkLocationServicesStatus()) {
                    //GPS켰는지 검사
                    if (googleApiClient.isConnected() == false) {
                        googleApiClient.connect();
                    }
                    return;
                }
                break;
        }
    }

}

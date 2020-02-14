#include <SoftwareSerial.h>

SoftwareSerial bt(3,2);
void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  bt.begin(9600);

}

void loop() {
  // put your main code here, to run repeatedly:
  if(bt.available()){
    Serial.write(bt.read());
  }
  if(Serial.available()){
    bt.write(Serial.read());
  }

}

//http://blog.naver.com/PostView.nhn?blogId=sisosw&logNo=221518483163&categoryNo=8&parentCategoryNo=0&viewDate=&currentPage=1&postListTopCurrentPage=1&from=search

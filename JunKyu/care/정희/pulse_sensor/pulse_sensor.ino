#include <SoftwareSerial.h>

SoftwareSerial bt(3,2); //블루투스 통신 값 설정
int pulsePin = A0;                 // Pulse Sensor purple wire connected to analog pin A0

// 인터럽트함수에서 수정하는 내용은 변수형 앞에 volatile를 붙인다.
volatile int BPM;                   // 심박수 A0번으로 출력
volatile int Signal;                // BPM으로 바뀌기 전 심박수 받아오는 값
volatile int IBI = 600;             // 비트 사이의 시간 간격을 유지하는 int
volatile boolean Pulse = false;     // "True" when User's live heartbeat is detected. "False" when not a "live beat". 
volatile boolean QS = false;        // 비트 값 찾을 경우 true로 변경

static boolean serialVisual = true;   
// serial 모니터에 값 보일때 사용

volatile int rate[10];                      //마지막 10 개의 IBI 값을 보유하는 배열
volatile unsigned long sampleCounter = 0;          // used to determine pulse timing
volatile unsigned long lastBeatTime = 0;           // used to find IBI
volatile int P = 512;                      //신호의 최대값 찾기
volatile int T = 512;                     // 신호의 최소값 찾기
volatile int thresh = 525;                // 심박 순간
volatile int amp = 100;                   // 펄스 파형 진폭 유지
volatile boolean firstBeat = true;        
volatile boolean secondBeat = false;      // BPM 배열에 사용

void setup()
{
  Serial.begin(115200);   //serial 모니터
  bt.begin(9600);         //블루투스
  interruptSetup();                 //2ms 마다 인터럽트 발생 심박 측정    
}


void loop(){
  if (QS == true) // 심박수를 찾은 경우
    {    
     serialOutputWhenBeatHappens();   //시리얼 확인 + 앱에 BPM 수신
      QS = false; 
    }
    delay(1000);
}


void interruptSetup()
{     
  TCCR2A = 0x02;     //모드설정 : CTC 모드 -> 타이머 카운터 모드
  TCCR2B = 0x06;     // 
  OCR2A = 0X7C;      // CTC 모드 설정시, 124와 같아질 때 인터럽트 발생 
  TIMSK2 = 0x02;     // 사용할 타이머 인터럽트 ENABLE.
  sei();                                                                 
} 


void serialOutputWhenBeatHappens()
{    
 if (serialVisual == true) //  
   {            
     if(BPM<50||BPM>130){
     // Serial.println("BPM..");//시리얼 확인용
      bt.println(BPM); //안드로이드로 보내기 // 좀 더 보완할 예정
     }
     else{
      // Serial.println(BPM); //시리얼 확인용
      bt.println(BPM); //안드로이드로 보내기
     }
   }
 else
   {
     sendDataToSerial('B',BPM);   
     sendDataToSerial('Q',IBI);  
   }   
}

void sendDataToSerial(char symbol, int data )
{
   Serial.print(symbol);
   Serial.println(data);                
}

ISR(TIMER2_COMPA_vect) //인터럽트가 실행될 때 넘어오는 함수
// Timer2가 124로 카운트 될 때 트리거 됨
{  
  cli();                                      // 외부 영향 없애기
  Signal = analogRead(pulsePin);              // 심박수 읽음(핵심) 
  sampleCounter += 2;                         // ms 단위
  int N = sampleCounter - lastBeatTime;       // IBI 측정 후 경과 시간
                                             
  if(Signal < thresh && N > (IBI/5)*3) // 최저점 찾는 함수
    {      
      if (Signal < T) 
      {                        
        T = Signal; 
      }
    }

  if(Signal > thresh && Signal > P) //최고점 찾는 함수
    {         
      P = Signal;                             
    }                                        

  
  if (N > 250)
  {                                  
    if ( (Signal > thresh) && (Pulse == false) && (N > (IBI/5)*3) )
      {        
        Pulse = true;                             
        IBI = sampleCounter - lastBeatTime;         // 비트 시간 ms로 측정
        lastBeatTime = sampleCounter;               // 다음 펄스 시간 
  
        if(secondBeat)
        {                       
          secondBeat = false;                 
          for(int i=0; i<=9; i++) 
          {          
               
            rate[i] = IBI;                      
          }
        }
  
        if(firstBeat) // 처음 비트를 찾은 경우 버린다.
        {                         
          firstBeat = false;                   
          secondBeat = true;                   
          sei();                               
          return;                              // IBI 값 신뢰X 버림
        }   
      // 합계값은 10개로 유지
      word runningTotal = 0;                  // 10개 누적값 초기화

      for(int i=0; i<=8; i++)
        {                
          
          rate[i] = rate[i+1];                  // 가장 오랜된 값 버림 
          runningTotal += rate[i];              
        }

      rate[9] = IBI;                          // 가장 최신 IBI값 넣음
      runningTotal += rate[9];                
      runningTotal /= 10;                     // IBI값 평균
      BPM = 60000/runningTotal;  // 1분의 BPM 측정
      QS = true;                                                            
    }                       
  }

  if (Signal < thresh && Pulse == true) //신호가 하강 상태가 되는 경우
    {  
      Pulse = false;                      
      amp = P - T;                           // 진폭 구하기
      thresh = amp/2 + T;                    // thresh 재설정
      P = thresh;                            
      T = thresh;
    }

  if (N > 2500)
    {                           //2.5초 이상 비트가 측정되지 않으면 초기화          
      thresh = 512;                         
      P = 512;                               
      T = 512;                              
      lastBeatTime = sampleCounter;                 
      firstBeat = true;                      
      secondBeat = false;                    
    }

  sei();                                   // enable interrupts when youre done! 다시 활성화
}// end isr

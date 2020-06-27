void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:
  analogWrite(9, 200);                                     
  delay(3000);
  Serial.println("motor 200");                                                    
  analogWrite(9, 100);                                      
  delay(3000);    
  Serial.println("motor 100");                                                  
  analogWrite(9, 0);                                          
  delay(3000);                                                 
}

// This #include statement was automatically added by the Particle IDE.
#include <InternetButton.h>

InternetButton b = InternetButton();
bool is_running;
int seq_cnt;
String sequenceStr;
String inputString;
int inputCnt;


void setup() {
    b.begin();
    is_running = true;
    seq_cnt = 0;
    sequenceStr = "";
    inputString = "";
    inputCnt = 0;
    Particle.function("sequence", game);
}

void loop() {
   
    userInput();
    
}

int randomNumberGenerator(int max) {
  return random(1, max+1);
}


void check(){
    if(inputCnt == 4){
        if(seq_cnt == 4){
            if(inputCnt == seq_cnt){
                if(sequenceStr == inputString){
                    Particle.publish("Result", "You Win!!",60,PUBLIC);
                } else {
                    Particle.publish("Result", "You Lose!!",60,PUBLIC);
                }
            }
        }
    }
}
void userInput(){
    if(b.buttonOn(1)){
       if(inputCnt < 4){
           delay(500);
           inputString = inputString + "1";
           inputCnt = inputCnt + 1;
           Particle.publish("inputString", inputString);
            Particle.publish("userInput", inputString,60,PUBLIC);
       }
    }
    if(b.buttonOn(2)){
        if(inputCnt < 4){
            delay(500);
           inputString = inputString + "2";
           inputCnt = inputCnt + 1;
           Particle.publish("inputString", inputString);
            Particle.publish("userInput", inputString,60,PUBLIC);
       }
    }
    if(b.buttonOn(3)){
        if(inputCnt < 4){
            delay(500);
           inputString = inputString + "3";
           inputCnt = inputCnt + 1;
           Particle.publish("inputString", inputString);
            Particle.publish("userInput", inputString,60,PUBLIC);
       }
    }
    if(b.buttonOn(4)){
        if(inputCnt < 4){
            delay(500);
           inputString = inputString + "4";
           inputCnt = inputCnt + 1;
       }
    }
    check();
}

int game(String command){
    startup();
    if(command == "start"){
        sequenceStr = "";
        inputString = "";
        inputCnt = 0;
        seq_cnt = 0;
       // Particle.publish("Running status", String(is_running));
        for (int i = 1; i <= 4; i++){
            if(is_running == true){
                b.allLedsOff();
                delay(1000);
                int r_num = randomNumberGenerator(4);
                    
            //    Particle.publish("Random", String(r_num));
                if(r_num == 1) {
                    b.ledOn(1, 255, 0, 0);
                    b.ledOn(2, 255, 0, 0);
                    sequenceStr = sequenceStr + "1";
                } else if(r_num == 2) {
                    b.ledOn(3, 0, 255, 0);
                    b.ledOn(4, 0, 255, 0);
                    b.ledOn(5, 0, 255, 0);
                    sequenceStr = sequenceStr + "2";
                } else if(r_num == 3) {
                    b.ledOn(6, 0, 0, 255);
                    b.ledOn(7, 0, 0, 255);
                    b.ledOn(8, 0, 0, 255);
                    sequenceStr = sequenceStr + "3";
                } else {
                    b.ledOn(9, 254, 128, 64);
                    b.ledOn(10, 254, 128, 64);
                    b.ledOn(11, 254, 128, 64);
                    sequenceStr = sequenceStr + "4";
                }
                seq_cnt = seq_cnt + 1;
            }
            
            delay(2000);
           // Particle.publish("Sequence", sequence);
            b.allLedsOff();
            
        }
        
        Particle.publish("count", String(seq_cnt));
        if(seq_cnt == 4){
                Particle.publish("sequenceString", sequenceStr,60,PUBLIC);
        }
    } else {
        is_running = false;
        
    }
    return 1;
}

void startup(){
    for(int i = 0; i < 3; i++){
        b.allLedsOn(0,20,20);
        delay(500);
        b.allLedsOff();
        delay(500);
    }
    b.allLedsOn(0,20,20);
    b.playSong("E5,8,G5,8,E6,8,C6,8,D6,8,G6,8");
    b.allLedsOff();
    delay(3000);
    b.ledOn(1, 65, 65, 65);
    delay(500);
    b.ledOn(10, 65, 65, 65);
    delay(1000);
    b.allLedsOff();
    delay(1000);
}


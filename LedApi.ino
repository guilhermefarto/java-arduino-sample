
// ACENDER;11
// APAGAR;11
// ACENDER;11?100
// ACENDER
// APAGAR

int portas[] = {9, 10, 11};

void setup() {
  Serial.begin(9600);

  for (int i=0; i<sizeof(portas); i++) {
    pinMode(portas[i], OUTPUT);
    digitalWrite(portas[i], LOW);
  }

  Serial.println("Setup() is okay!");
}

void loop() {
}

void executar(String operacao, int porta, int brilho) {
  enviarDados(true);
  
  if (porta > -1 && brilho > -1) {
    analogWrite(porta, brilho);
  } else {
    if (porta > -1) {
      digitalWrite(porta, operacao.equals("ACENDER") ? HIGH : LOW);
    } else {
      for(int i=0; i<sizeof(portas); i++) {
        digitalWrite(portas[i], operacao.equals("ACENDER") ? HIGH : LOW);
      }
    }
  }
}

String operacao = "";
int porta = -1;
int brilho = -1;

void serialEvent() {
  if (Serial.available()) {
    String command = Serial.readString();
  
    operacao = command.substring(0, command.indexOf(";"));
  
    porta = -1;
    brilho = -1;
  
    if (command.indexOf("?") != -1) {
      porta = command.substring(command.indexOf(";") + 1, command.indexOf("?")).toInt();
      brilho = command.substring(command.indexOf("?") + 1).toInt();
    } else {
      if(command.indexOf(";") != -1) {
        porta = command.substring(command.indexOf(";") + 1).toInt();
        brilho = -1;
      }
    }
  
    executar(operacao, porta, brilho);
  }
}

void enviarDados(boolean isEnviar) {
  if (isEnviar) {
    Serial.print(operacao);
    Serial.print(" | ");
    Serial.print(porta);
    Serial.print(" | ");
    Serial.println(brilho);
  }
}

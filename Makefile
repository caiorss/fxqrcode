
# Scala compiler 
SCALAC := scalac 

TARGET := fxqrcode.jar
SRC := $(wildcard src/*.scala)

# Building dependencies 
DEPS := com.google.zxing/core/2.2,com.google.zxing/javase/2.2


# ================ R U L E S =================== #

all: main 

main: $(TARGET)

$(TARGET): $(SRC)
	jarget exec $(DEPS) -- scalac $(SRC) 


# Start Scala REPL with dependencies in CLASSPATH
repl:
	jarget exec $(DEPS) -- scala 

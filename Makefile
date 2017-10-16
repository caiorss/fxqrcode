
#================ V A R I A B L E S =============#

# Scala compiler 
CC := fsc  

TARGET      := bin/fxqrcode.jar
TARGET_UBER := bin/fxqrcode-app.jar 
SRC         := $(wildcard src/*.scala)

# Building dependencies 
DEPS := com.google.zxing/core/2.2,com.google.zxing/javase/2.2


# ================ R U L E S =================== #

all: main 

uber: $(TARGET_UBER)

main: $(TARGET)


# Force building target if scala compiler daemon (fsc)
# throws errors such as: "Compile server encountered fatal condition: null".
#
force: $(SRC)
	jarget exec $(DEPS) -- scalac $(SRC) -d $(TARGET)

$(TARGET): $(SRC)
	mkdir -p bin
	jarget exec $(DEPS) -- fsc $(SRC) -d $(TARGET)

$(TARGET_UBER): $(TARGET)
	jarget uber -scala -m $(TARGET) -o $(TARGET_UBER) -p com.google.zxing/core/2.2 com.google.zxing/javase/2.2

run: $(TARGET)
	jarget exec $(DEPS) -- scala $(TARGET)

# Start Scala REPL with dependencies in CLASSPATH
repl:
	jarget exec $(DEPS) -- scala 

clean:
	rm -rf $(TARGET) $(TARGET_UBER)

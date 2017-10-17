
#================ V A R I A B L E S =============#

# Scala compiler 
CC := fsc  

TARGET      := bin/fxqrcode.jar
TARGET_UBER := bin/fxqrcode-uber.jar
TARGET_PROG := bin/fxqrcode-proguard.jar # Pro-guard build 
SRC         := $(wildcard src/*.scala)

# Run-time Dependencies 
DEPS := com.google.zxing/core/2.2,com.google.zxing/javase/2.2

# Building dependencies
#
# If jarget is already in the $PATH variable set 
JARGET     := ~/bin/jarget 

# ================ R U L E S =================== #

all: main 

# Development build 
main: $(TARGET)

# Production build - scala run-time library is packed with application.
uber: $(TARGET_UBER)

# Production build optimized with proguard 
guard: $(TARGET_PROG)

# Force building target if scala compiler daemon (fsc)
# throws errors such as: "Compile server encountered fatal condition: null".
#
force: $(SRC)
	jarget exec $(DEPS) -- scalac $(SRC) -d $(TARGET)

$(TARGET): $(SRC) $(JARGET)
	mkdir -p bin
	$(JARGET) exec $(DEPS) -- fsc $(SRC) -d $(TARGET)

$(TARGET_UBER): $(TARGET) $(JARGET)
	$(JARGET) uber -scala -m $(TARGET) -o $(TARGET_UBER) -p com.google.zxing/core/2.2 com.google.zxing/javase/2.2

$(TARGET_PROG): $(TARGET_UBER) config.pro 
	java -jar proguard.jar @config.pro 

run: $(TARGET)
	$(JARGET) exec $(DEPS) -- scala $(TARGET)

# Start Scala REPL with dependencies in CLASSPATH
repl:
	$(JARGET) exec $(DEPS) -- scala 

install: $(TARGET_PROG)
	cp $(TARGET_PROG) ~/Desktop/fxqrcode.jar 

clean:
	rm -rf bin/*.jar 

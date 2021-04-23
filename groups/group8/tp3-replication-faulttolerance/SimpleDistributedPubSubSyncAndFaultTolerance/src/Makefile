COMPILER = javac -Xlint
BINARIES = $(EXEC_PROG)

SOURCES := $(shell find -name '*.java')

OBJECTS = $(SOURCES:.java=.class)

%.class: %.java
	$(COMPILER) -d . -classpath . $<

all: $(OBJECTS)
	@echo Build Completed

broker:
	java appl.Broker

client1:
	java appl.OneAppl

client2:
	java appl.OneApplTwo

client3:
	java appl.OneApplThree

.PHONY : clean
clean:
	find . -type f -name '*.class' -delete
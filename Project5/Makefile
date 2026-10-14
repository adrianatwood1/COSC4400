JFLAGS=-cp .
JAR=jar
JAVA=java
JAVAC=javac
JAVACC=javacc

PARSEDIR=Parse
MJ_PARSER=MiniJavaParser

DEFAULT_JAVACCGEN = $(PARSEDIR)/SimpleCharStream.java \
    $(PARSEDIR)/ParseException.java \
    $(PARSEDIR)/Token.java \
    $(PARSEDIR)/TokenMgrError.java

MJ_JAVACCGEN = $(DEFAULT_JAVACCGEN) \
    $(PARSEDIR)/$(MJ_PARSER).java \
    $(PARSEDIR)/$(MJ_PARSER)Constants.java \
    $(PARSEDIR)/$(MJ_PARSER)TokenManager.java

# Project 4 - Semantic Analyzer
ASTSRC = $(wildcard Absyn/*.java)
TYPESSRC = $(wildcard Types/*.java)
SEMANTSRC = $(wildcard Semant/*.java)

PROJECTSRC = $(ASTSRC) $(TYPESSRC) $(SEMANTSRC)

all:    $(PARSEDIR)/$(MJ_PARSER).java $(PROJECTSRC:.java=.class)

$(PARSEDIR)/$(MJ_PARSER).java: $(PARSEDIR)/$(MJ_PARSER).jj
	cd $(PARSEDIR); ${JAVACC} $(MJ_PARSER).jj;
	${JAVAC} ${JFLAGS} ${MJ_JAVACCGEN}

%.class : %.java
	${JAVAC} ${JFLAGS} $<

clean:
	rm -f $(PROJECTSRC:.java=.class) \
		$(MJ_JAVACCGEN) $(MJ_JAVACCGEN:.java=.class)\
		'$(PARSEDIR)/$(MJ_PARSER)$$JJCalls.class' \
		'$(PARSEDIR)/$(MJ_PARSER)$$LookaheadSuccess.class' 

realclean: clean
	rm -f */*.class *~ */*~ *.jar
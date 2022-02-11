JAVA_CP = "lib/jena/lib/*:."
SIMPSONS_FILE = oblig/2/simpsons.ttl

java = java -cp $(JAVA_CP)
javac = javac -cp $(JAVA_CP)

%.class: %.java
        @$(javac) $<

output.ttl: Simpsons.class
        @$(java) $(basename $(<F)) $(SIMPSONS_FILE) $@

run_test: Test.class
        @$(java) $(basename $(<F)) $(SIMPSONS_FILE) $@

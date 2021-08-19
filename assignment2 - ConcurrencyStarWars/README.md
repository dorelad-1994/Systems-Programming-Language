I built a concurrency system are uses Thread-safe MessageBus and Microservices that complex applications are composed of small and independent services that are able to communicate with each other using messages. I simulate a battle with Star-Wars character are represent by Threads.


#run this script in Terminal/Command:#

tar -xf assignment2.tar.gz

mvn compile

mvn test

mvn exec:java -Dexec.mainClass="bgu.spl.mics.application.Main" -Dexec.args="in1.json out1.json"

mvn exec:java -Dexec.mainClass="bgu.spl.mics.application.Main" -Dexec.args="in2.json out2.json"

mvn exec:java -Dexec.mainClass="bgu.spl.mics.application.Main" -Dexec.args="in3.json out3.json"

mvn exec:java -Dexec.mainClass="bgu.spl.mics.application.Main" -Dexec.args="in4.json out4.json"

mvn exec:java -Dexec.mainClass="bgu.spl.mics.application.Main" -Dexec.args="in5.json out5.json"

The scenarios and their requested output :

Scenario1. Case: the json you were supplied with.

Expected output:

There are 2 attacks.

HanSolo and C3PO finish their tasks ~1000 milliseconds one after the other.

All threads terminate ~4000 milliseconds later.

Scenario2. Case: a single AttackEvent.

Expected output:

There is 1 attack.

HanSolo and C3PO finish their tasks ~1000 milliseconds one after the other. This is also acceptable if one of the threads finishes in time “0”.

R2D2 deactivates ~2000 milliseconds later.

All threads terminate ~2000 milliseconds later.

Scenario3. Case: deadlock.

Expected output:

There are 2 attacks.

HanSolo and C3PO finish their tasks ~1000 milliseconds one after the other.

R2D2 deactivates ~2000 milliseconds later.

All threads terminate ~2000 milliseconds later.

Scenario4. Case: resources are acquired for a long time by one thread, the other thread

handles several events.

Expected output:

There are 8 attacks.

HanSolo and C3PO finish their tasks ~2000 milliseconds one after the other.

R2D2 deactivates ~2000 milliseconds later.

All threads terminate ~2000 milliseconds later.

Scenario5. Case: threads alternately acquire the resources needed by the other thread.

Expected output:

There are 5 attacks.

HanSolo and C3PO finish their tasks ~2000 milliseconds one after the other.

R2D2 deactivates ~2000 milliseconds later.

All threads terminate ~2000 milliseconds later.
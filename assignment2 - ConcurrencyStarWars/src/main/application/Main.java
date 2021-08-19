package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import java.io.*;
import java.util.concurrent.CountDownLatch;

/**
 * This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {

    //to ensure that leia starts sending attacks only after the others finished initializing
    public static CountDownLatch countDownLatch = new CountDownLatch(4);

    public static void main(String[] args) {

        countDownLatch = new CountDownLatch(4);

        Input input = input(args[0]);

        Ewoks ewoks = Ewoks.getInstance();
        ewoks.addEwoks(input.getEwoks());

        HanSoloMicroservice han = new HanSoloMicroservice();
        Thread hanThread = new Thread(han);
        hanThread.start();

        C3POMicroservice c3po = new C3POMicroservice();
        Thread c3poThread = new Thread(c3po);
        c3poThread.start();

        R2D2Microservice r2d2 = new R2D2Microservice(input.getR2D2());
        Thread r2d2Thread = new Thread(r2d2);
        r2d2Thread.start();

        LandoMicroservice lando = new LandoMicroservice(input.getLando());
        Thread landoThread = new Thread(lando);
        landoThread.start();

        LeiaMicroservice leia = new LeiaMicroservice(input.getAttacks());
        Thread leiaThread = new Thread(leia);
        leiaThread.start();

        try {
            leiaThread.join();
            hanThread.join();
            c3poThread.join();
            r2d2Thread.join();
            landoThread.join();
        } catch (InterruptedException x) {}

        output(args[1]);

    }

    private static Input input(String path) {
        Gson gson = new Gson();
        Input input = new Input();
        try (Reader reader = new FileReader(path)) {
            input = gson.fromJson(reader, Input.class);
        } catch (IOException x) {
            System.out.println("I/O Exception input");
        }
        return input;
    }

    private static void output(String path) {
        Gson gson = new Gson();
        try {
            FileWriter fileWriter = new FileWriter(path);
            gson.toJson(Diary.getInstance(),fileWriter);
            fileWriter.close();
        } catch (IOException x) {
            System.out.println("I/O Exception output");
        }

    }
}

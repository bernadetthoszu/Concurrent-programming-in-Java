package Task1;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PigMent {

    /* Global Constants */
    static final int TIC_MIN   = 50;  // Tickrate minimum time (ms)
    static final int TIC_MAX   = 200; // Tickrate maximum time (ms)
    static final int FEED      = 20;  // Mass gained through photosynthesis
    static final int BMR       = 10;  // Mass lost due to basal metabolic rate
    static final int MAX_POP   = 10;  // Maximum number of concurent pigs
    static final int INIT_POP  = 3;   // size of initial pig population
    static final int INIT_MASS = 20;  // starting mass of initial pigs


    // don't forget to make the pigs scream edgy stuff:
    // pigSay("Holy crap, I just ate light!");
    // pigSay("This vessel can no longer hold the both of us!");
    // pigSay("Beware world, for I'm here now!");
    // pigSay("Bless me, Father, for I have sinned.");
    // pigSay("I have endured unspeakable horrors. Farewell, world!");
    // pigSay("Look on my works, ye Mighty, and despair!");


    // globally accessible variables go here (id, pigPool, openArea)
    // explicit locks and conditions also go here (Task2)
    static final Random rand = new Random();

    static final AtomicInteger id = new AtomicInteger(0);
    static final Object microphone = new Object();
    static final Object apocalypse = new Object();
    static final AtomicBoolean isOver = new AtomicBoolean(false);
    static final ExecutorService pigPool = Executors.newFixedThreadPool(MAX_POP);

    /* Implementing Awesomeness (a.k.a. the pigs) */
    static class PhotoPig implements Runnable {

        /* Take this, USA! */
        final int id;

        /* Watch your lines, piggie! */
        int mass;

        /* Sweet dreams (are made of this) */
        void pigSleep() {
            int sleepTime = rand.nextInt(TIC_MIN, TIC_MAX + 1);
            try{
                Thread.sleep(sleepTime);
            } catch (InterruptedException ie){
                ie.printStackTrace();
            }
        }

        /* Ensuring free speech */
        void pigSay(String msg) {
            synchronized (microphone){
                System.out.println("<Pig#" + id + "," + mass + "kg>: " + msg);
            }
        }

        /* Here comes the esoteric stuff */
        boolean eatLight() {
            // sunbathing
            pigSleep();
            mass += FEED;
            pigSay("Holy crap, I just ate light!");

            mass -= mass / BMR;

            if (mass / BMR > FEED / 2){
                pigSay("This vessel can no longer hold the both of us!");
                if (PigMent.id.get() < MAX_POP) {
                    pigPool.submit(new PhotoPig(this.mass / 2));
                }
                mass /= 2;
            }
            return true; // TODO: replace this placeholder (task1 and task2)
        }

        /* Hey, this ain't vegan! */
        boolean aTerribleThingToDo() {
            return true; // TODO: replace this placeholder (task2)
        }

        /* Every story has a beginning */
        public PhotoPig(int mass) {
            this.mass = mass;
            this.id   = PigMent.id.getAndIncrement();
            if (PigMent.id.get() == MAX_POP){
                synchronized (apocalypse){
                    apocalypse.notify();
                    isOver.set(true);
                }
            }
        }

        /* Live your life, piggie! */
        @Override public void run() {
            pigSay("Beware world, for I'm here now!");
            while (!isOver.get() && eatLight()){

            }
            pigSay("I have endured unspeakable horrors. Farewell, world!");
            return; // TODO: replace this placeholder (task1 and task2)
        }
    }

    /* Running the simulation */
    public static void main(String[] args) {
        for (int i = 0; i < INIT_POP; i++){
            pigPool.submit(new PhotoPig(INIT_MASS));
        }

        synchronized (apocalypse){
            try{
                apocalypse.wait();
            }catch (InterruptedException ie){
                ie.printStackTrace();
            }
            pigPool.shutdownNow();
        }
    }
}

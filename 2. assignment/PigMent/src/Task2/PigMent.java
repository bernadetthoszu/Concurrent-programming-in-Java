package Task2;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
    static final ExecutorService pigPool = Executors.newFixedThreadPool(MAX_POP);
    static final BlockingQueue<PhotoPig> openArea = new PriorityBlockingQueue<>(MAX_POP, (a,b) -> a.mass - b.mass);
    static final Lock gimmeTheMike = new ReentrantLock();
    static final Lock labourWard = new ReentrantLock();
    static final Condition birthControl = labourWard.newCondition();

    /* Implementing Awesomeness (a.k.a. the pigs) */
    static class PhotoPig implements Runnable {

        /* Take this, USA! */
        final int id;

        /* Watch your lines, piggie! */
        int mass;

        /* Sweet dreams (are made of this) */
        void pigSleep() throws InterruptedException{
            int sleepTime = rand.nextInt(TIC_MIN, TIC_MAX + 1);
            Thread.sleep(sleepTime);

        }

        /* Ensuring free speech */
        void pigSay(String msg) {
            gimmeTheMike.lock();
            try{
                System.out.println("<Pig#" + id + "," + mass + "kg>: " + msg);
            }finally{
                gimmeTheMike.unlock();
            }
        }

        /* Here comes the esoteric stuff */
        boolean eatLight() throws InterruptedException {
            // sunbathing
            openArea.offer(this);
            pigSleep();
            if (!openArea.remove(this)){
                return false;
            }
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
            return true;
        }

        /* Hey, this ain't vegan! */
        boolean aTerribleThingToDo() throws InterruptedException {
            PhotoPig prey = openArea.peek();
            openArea.offer(this);
            pigSleep();
            if (!openArea.remove(this)){
                return false;
            }
            if (openArea.remove(prey)){
                this.mass += prey.mass / 2;
                pigSay("Bless me, Father, for I have sinned.");
            }
            return true;
        }

        /* Every story has a beginning */
        public PhotoPig(int mass) {
            this.mass = mass;
            this.id   = PigMent.id.getAndIncrement();
            labourWard.lock();
            try{
                if (PigMent.id.get() == MAX_POP){
                    birthControl.signal();
                }
            } finally {
                labourWard.unlock();
            }

        }

        /* Live your life, piggie! */
        @Override public void run() {
            pigSay("Beware world, for I'm here now!");
            try{
                while (aTerribleThingToDo() && eatLight()){

                }
                pigSay("I have endured unspeakable horrors. Farewell, world!");
            }catch (InterruptedException ie){
                pigSay("Look on my works, ye Mighty, and despair!");
            }
        }
    }

    /* Running the simulation */
    public static void main(String[] args) {
        for (int i = 0; i < INIT_POP; i++){
            pigPool.submit(new PhotoPig(INIT_MASS));
        }

        labourWard.lock();
        try {
            while (id.get() < MAX_POP){
                birthControl.await();
            }
            pigPool.shutdownNow();
        } catch (InterruptedException ie){
            ie.printStackTrace();
        } finally {
            labourWard.unlock();
        }
    }
}

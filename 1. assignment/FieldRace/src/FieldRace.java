import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class FieldRace {
    private static final int PLAYER_COUNT = 12;
    private static final int CHECKPOINT_COUNT = 5;
    private static final int NUMBER_OF_THREADS = PLAYER_COUNT + CHECKPOINT_COUNT + 1;
    private static final Random random = new Random();
    private static final ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static final AtomicBoolean isOn = new AtomicBoolean(true);
    private static final ConcurrentHashMap<Integer, Integer> scores = new ConcurrentHashMap<>();
    private static final ArrayList<AtomicInteger> checkpointScores = new ArrayList<>(PLAYER_COUNT);
    private static final List<BlockingQueue<AtomicInteger>> checkpointQueues = Collections.synchronizedList(new ArrayList<>(CHECKPOINT_COUNT));

    public static void main(String[] args) {

        //initialize scores, checkpointScores, queues
        for (int i = 0; i < PLAYER_COUNT; i ++){  // each player starts with overall score 0
            scores.put(i, 0);
        }
        for (int i = 0; i < PLAYER_COUNT; i ++){ //a variable for each player, in which the person receives his/her score at the checkpoint they are at
            checkpointScores.add(new AtomicInteger(0));
        }
        for (int i = 0; i < CHECKPOINT_COUNT; i ++){ // a queue for each checkpoint, the queues contain scores that need to be tackled
            checkpointQueues.add(new ArrayBlockingQueue<AtomicInteger>(PLAYER_COUNT));
        }

        //we start the player-threads, the checkpoint-threads and the "+1" thread which logs the stats.
        for (int i = 0; i < PLAYER_COUNT; i ++){  // each player starts with overall score 0
            int index = i;
            executor.submit(() -> { playerAction(index); });
        }
        for (int i = 0; i < CHECKPOINT_COUNT; i ++){  // each player starts with overall score 0
            int index = i;
            executor.submit(() -> { checkpointAction(index); });
        }
        Thread logger = new Thread(()->{
            while(isOn.get()){
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                printScores();
            }
        });
        executor.submit(logger);

        //the main program waits 10 seconds
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " got interrupted");
            e.printStackTrace();
        }
        //isOn is set to false - the race is over
        isOn.set(false);
        //we stop the ExecutorService
        executor.shutdown();
        try{
            executor.awaitTermination(3, TimeUnit.SECONDS);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        executor.shutdownNow();

        //lof final results
        System.out.println("FINAL SCORES:");
        printScores();
    }

    //The repetitive actions of the checkpoints
    private static void checkpointAction(int checkpoint){
        while(isOn.get()){
            AtomicInteger checkpointScore = null;
            try{
                checkpointScore = checkpointQueues.get(checkpoint).poll(2000, TimeUnit.MILLISECONDS);
            }catch(InterruptedException ie){
                ie.printStackTrace();
            }
            if(checkpointScore == null)
                continue;
            checkpointScore.set(random.nextInt(10, 101));
            synchronized (checkpointScore){
                checkpointScore.notify();
            }
        }
    }
    //The repetitive actions of the contestants/players
    private static void playerAction(int player){
        while(isOn.get()){

            int checkpoint = random.nextInt(0 ,CHECKPOINT_COUNT);

            try{
                Thread.sleep(random.nextInt(500, 2000));
            }catch(InterruptedException ie){
                ie.printStackTrace();
            }

            AtomicInteger checkpointScore = checkpointScores.get(player);
            checkpointQueues.get(checkpoint).offer(checkpointScore);

            synchronized (checkpointScore){
                while(checkpointScore.get() == 0 && isOn.get()){
                    try{
                        checkpointScore.wait(3000);
                    }catch(InterruptedException ie){
                        ie.printStackTrace();
                    }
                }
                if(isOn.get()) {
                    System.out.println("Player " + player + " got " + checkpointScore.get() + " points at checkpoint " + checkpoint);
                    scores.put(player, scores.get(player) + checkpointScore.get());
                }
                checkpointScore.set(0);
            }
        }
    }

    class SelectionSort {
        public static void sort(ArrayList<Pair> arr) {
            int n = arr.size();

            // One by one move boundary of unsorted subarray
            for (int i = 0; i < n - 1; i++) {
                // Find the maximum element in unsorted array
                int max_idx = i;
                for (int j = i + 1; j < n; j++)
                    if (arr.get(j).getB() > arr.get(max_idx).getB())
                        max_idx = j;

                // Swap the found minimum element with the first
                // element
                Pair temp = arr.get(max_idx);
                arr.set(max_idx, arr.get(i));
                arr.set(i, temp);
            }
        }
    }

    private static void printScores(){
        ArrayList<Pair> pairs = new ArrayList<>();
        scores.forEach((person, score) -> {
            pairs.add(new Pair(person, score));
        });
        SelectionSort.sort(pairs);

        int n = pairs.size();
        System.out.print("\nScores: [");
        for (int i = 0; i < n-1 ; i++){
            System.out.print(pairs.get(i).getA() + "=" + pairs.get(i).getB() + ", ");
        }
        System.out.println(pairs.get(n-1).getA() + "=" + pairs.get(n-1).getB() + "]\n");
    }
}

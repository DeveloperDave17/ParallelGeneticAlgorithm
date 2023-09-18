package factoryLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class FactoryPopulation {
  // All factory layouts in this simulation are Rectanguler 
  private final int ROW_SIZE_DEFAULT = 16;
  private final int COL_SIZE_DEFAULT = 16;

  private final int GENERATION_TARGET_AMOUNT_DEFAULT = 5000;
  private final double MUTATION_RATE_DEFAULT = 0.1;

  private final int POP_MAX_DEFAULT = 1000;

  private final int STARTING_POP_DEFAULT = 200;
  
  private final int NUM_STATIONS_DEFAULT = 4;


  private final int ROWSIZE;
  private final int COLSIZE;

  private final int GENERATION_TARGET_AMOUNT;

  List<Double> affinityPool;
  
  // These values will directly affect the speed and quality of reproduction
  private final double MUTATION_RATE;
  private final int POP_MAX;
  private final int STARTING_POP;

  private final int NUM_STATIONS;

  private Station[] stations;

  private List<Double> affinityList = Collections.synchronizedList(new ArrayList<>());

  private ConcurrentHashMap<Double,Factory> factoryMap = new ConcurrentHashMap<>();

  double highestAffinity;

  boolean simulationStillGoing;

  public FactoryPopulation(int rowSize, int colSize, int generationTargetAmount, double mutationRate, int popMax, int startingPop, int numStations) {
    this.ROWSIZE = rowSize;
    this.COLSIZE = colSize;
    this.GENERATION_TARGET_AMOUNT = generationTargetAmount;
    this.MUTATION_RATE = mutationRate;
    this.POP_MAX = popMax;
    this.STARTING_POP = startingPop;
    this.NUM_STATIONS = numStations;
  }

  public FactoryPopulation() {
    this.ROWSIZE = ROW_SIZE_DEFAULT;
    this.COLSIZE = COL_SIZE_DEFAULT;
    this.GENERATION_TARGET_AMOUNT = GENERATION_TARGET_AMOUNT_DEFAULT;
    this.MUTATION_RATE = MUTATION_RATE_DEFAULT;
    this.POP_MAX = POP_MAX_DEFAULT;
    this.STARTING_POP = STARTING_POP_DEFAULT;
    this.NUM_STATIONS = NUM_STATIONS_DEFAULT;
  }

  public void runSimulation() throws InterruptedException {
    generateStations();
    createStartingPopulation();
    highestAffinity = 0;
    simulationStillGoing = true;
    for (int i = 0; i < affinityList.size(); i++) {
      if (affinityList.get(i) > highestAffinity) highestAffinity = affinityList.get(i);
    }
    FactoryView factoryView = new FactoryView(COLSIZE, ROWSIZE);
    factoryView.displayStations(getHighestAfinityFactory());
    factoryView.display();
    ExecutorService displayExecutor = Executors.newFixedThreadPool(1);
    displayExecutor.submit(() -> {
      while(getSimulationStillGoing()) {
        try {
          Thread.sleep(500);
        } catch(Exception e) {
          e.printStackTrace();
        }
        factoryView.displayStations(getHighestAfinityFactory());
      }
    });
    for (int currentGeneration = 0; currentGeneration < GENERATION_TARGET_AMOUNT; currentGeneration++) {
      generateAffinityPool();
      ExecutorService pool = Executors.newCachedThreadPool();
      for (int currentTaskCount = 0; currentTaskCount < 64; currentTaskCount++)
        pool.submit(() -> reproduce());
      pool.shutdown();
      pool.awaitTermination(10, TimeUnit.SECONDS);
      for (int i = 0; i < affinityList.size(); i++) {
        if (affinityList.get(i) > highestAffinity) highestAffinity = affinityList.get(i);
      }
      removePopulationSurplus();
    }
    System.out.println("Simulation Is Over");
    simulationStillGoing = false;
  }

  private void generateStations() {
    Random rand = new Random();
    int RBGUpperLim = 256;
    stations = new Station[NUM_STATIONS];
    for (int i = 0; i < NUM_STATIONS; i++) {
      stations[i] = new Station(rand.nextInt(RBGUpperLim), rand.nextInt(RBGUpperLim), rand.nextInt(RBGUpperLim));
    }
  }

  private void createStartingPopulation() {
    for (int factoryNum = 0; factoryNum < STARTING_POP; factoryNum++) {
      Factory currenFactory = new Factory(ROWSIZE, COLSIZE);
      Random random = new Random();
      for (int i = 0; i < ROWSIZE; i++) {
        for (int j = 0; j < COLSIZE; j++) {
          if (random.nextInt(100) < 90) currenFactory.setIndex(i, j, stations[random.nextInt(NUM_STATIONS)]);
          else currenFactory.createGap(i, j);
        }
      }
      factoryMap.put(currenFactory.getAffinity(), currenFactory);
      affinityList.add(currenFactory.getAffinity());
    }
    
  }

  private void removePopulationSurplus() {
    PriorityQueue<Double> affinityPriorities = new PriorityQueue<>(affinityList);
    int populationSurplus = affinityList.size() - POP_MAX;
    for (int i = 0; i < populationSurplus; i++) {
      affinityList.remove(affinityPriorities.poll());
    }
  }

  private void generateAffinityPool() {
    double affinityTotal = 0;
    for (int i = 0; i < affinityList.size(); i++) {
      affinityTotal += affinityList.get(i);
    }

    affinityPool = new ArrayList<>();
    for (int i = 0; i < affinityList.size(); i++) {
      for (int j = 0; j < (affinityList.size() * (affinityList.get(i) / affinityTotal)); j++)
        affinityPool.add(affinityList.get(i));
    }
  }

  private Factory[] selectFacortyMates() {
    Random random = new Random();
    Factory[] factoriesToBreed = new Factory[2];
    factoriesToBreed[0] = factoryMap.get(affinityPool.get(random.nextInt(affinityPool.size())));
    factoriesToBreed[1] = factoryMap.get(affinityPool.get(random.nextInt(affinityPool.size())));
    return factoriesToBreed;
  }

  private void reproduce() {
    Factory[] factoryMates = selectFacortyMates();
    double totalAffinity = factoryMates[0].getAffinity() + factoryMates[1].getAffinity();
    ThreadLocalRandom random = ThreadLocalRandom.current();
    Factory babyFactory = new Factory(ROWSIZE, COLSIZE);

    if (random.nextDouble() < (totalAffinity / factoryMates[0].getAffinity())) {
      babyFactory.copyTopLeftQuadrant(factoryMates[0]);
    } else {
      babyFactory.copyTopLeftQuadrant(factoryMates[1]);
    }

    if (random.nextDouble() < (totalAffinity / factoryMates[0].getAffinity())) {
      babyFactory.copyTopRightQuadrant(factoryMates[0]);
    } else {
      babyFactory.copyTopRightQuadrant(factoryMates[1]);
    }

    if (random.nextDouble() < (totalAffinity / factoryMates[0].getAffinity())) {
      babyFactory.copyBottomLeftQuadrant(factoryMates[0]);
    } else {
      babyFactory.copyBottomLeftQuadrant(factoryMates[1]);
    }

    if (random.nextDouble() < (totalAffinity / factoryMates[0].getAffinity())) {
      babyFactory.copyBottomRightQuadrant(factoryMates[0]);
    } else {
      babyFactory.copyBottomRightQuadrant(factoryMates[1]);
    }

    if (random.nextDouble() < MUTATION_RATE)
      babyFactory.mutate();

    factoryMap.put(babyFactory.getAffinity(), babyFactory);
    affinityList.add(babyFactory.getAffinity());
  }

  public Factory getHighestAfinityFactory() {
    return factoryMap.get(highestAffinity);
  }

  public boolean getSimulationStillGoing() {
    return simulationStillGoing;
  }

}

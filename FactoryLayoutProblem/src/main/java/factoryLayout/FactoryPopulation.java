package factoryLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class FactoryPopulation {
  // All factory layouts in this simulation are Rectanguler 
  private final int ROW_SIZE_DEFAULT = 8;
  private final int COL_SIZE_DEFAULT = 8;

  private final int GENERATION_TARGET_AMOUNT_DEFAULT = 100;
  private final double MUTATION_RATE_DEFAULT = 0.1;

  private final int POP_MAX_DEFAULT = 1000;

  private final int STARTING_POP_DEFAULT = 200;
  
  private final int NUM_STATIONS_DEFAULT = 2;


  private final int ROWSIZE;
  private final int COLSIZE;

  private final int GENERATION_TARGET_AMOUNT;
  private int generationCount;
  
  // These values will directly affect the speed and quality of reproduction
  private final double MUTATION_RATE;
  private final int POP_MAX;
  private final int STARTING_POP;

  private final int NUM_STATIONS;

  private Station[] stations;

  private List<Double> affinityList = new ArrayList<>();

  private ConcurrentHashMap<Double,Factory> factoryMap = new ConcurrentHashMap<>();

  public FactoryPopulation(int rowSize, int colSize, int generationTargetAmount, double mutationRate, int popMax, int startingPop, int numStations) {
    this.ROWSIZE = rowSize;
    this.COLSIZE = colSize;
    this.GENERATION_TARGET_AMOUNT = generationTargetAmount;
    this.generationCount = 0;
    this.MUTATION_RATE = mutationRate;
    this.POP_MAX = popMax;
    this.STARTING_POP = startingPop;
    this.NUM_STATIONS = numStations;
  }

  public FactoryPopulation() {
    this.ROWSIZE = ROW_SIZE_DEFAULT;
    this.COLSIZE = COL_SIZE_DEFAULT;
    this.GENERATION_TARGET_AMOUNT = GENERATION_TARGET_AMOUNT_DEFAULT;
    this.generationCount = 0;
    this.MUTATION_RATE = MUTATION_RATE_DEFAULT;
    this.POP_MAX = POP_MAX_DEFAULT;
    this.STARTING_POP = STARTING_POP_DEFAULT;
    this.NUM_STATIONS = NUM_STATIONS_DEFAULT;
  }

  public void runSimulation() {
    generateStations();
    createStartingPopulation();
    double highestAffinity = 0;
    for (int i = 0; i < affinityList.size(); i++) {
      System.out.println(affinityList.get(i));
      if (affinityList.get(i) > highestAffinity) highestAffinity = affinityList.get(i);
    }
    factoryMap.get(highestAffinity).display();
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

}

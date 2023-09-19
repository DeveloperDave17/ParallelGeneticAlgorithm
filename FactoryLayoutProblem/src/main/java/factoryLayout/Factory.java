package factoryLayout;

import java.util.concurrent.ThreadLocalRandom;

public class Factory {
  
  private Station[][] factoryLayout;
  private double affinity;
  private Station[] possibleStations;

  public Factory(int rowSize, int colSize, Station[] possibleStations) {
    factoryLayout = new Station[rowSize][colSize];
    affinity = 0;
    this.possibleStations = possibleStations;
  }

  // Create Deep Copy
  public Factory(Factory factory) {
    for (int i = 0; i < factory.getRowLength(); i++) {
      for (int j = 0; j < factory.getColLength(); j++) {
        factoryLayout[i][j] = factory.getIndex(i, j);
      }
    }
    affinity = 0;
  }

  public Station getIndex(int i, int j) {
    return factoryLayout[i][j];
  }

  public void setIndex(int i, int j, Station station) {
    factoryLayout[i][j] = station;
  }

  public int getRowLength() {
    return factoryLayout.length;
  }

  public int getColLength() {
    return factoryLayout[0].length;
  }

  public void createGap(int i, int j) {
    factoryLayout[i][j] = null;
  }

  // Only called on Members of the gene pool or already mutated children
  public double getAffinity() {
    if (affinity == 0) {
      this.affinity = calcTotalAffinity();
    }
    return affinity;
  }

  public double calcTotalAffinity() {
    double affinity = 0;
    int[] stationCounters = new int[possibleStations.length];
    for (int i = 0; i < stationCounters.length; i++) stationCounters[i] = 0;
    // Iterate through every station
    for (int currentRow = 0; currentRow < factoryLayout.length; currentRow++) {
      for (int currentCol = 0; currentCol < factoryLayout[0].length; currentCol++) {
        Station currentStation = factoryLayout[currentRow][currentCol];
        for (int i = 0; i < possibleStations.length; i++) {
          if (possibleStations[i] == currentStation) stationCounters[i] += 1;
        }
        // Compare the station to every station that hasn't been compared to this one yet.
        if (currentStation != null) {
          for (int targetRow = 0; targetRow < factoryLayout.length; targetRow++) {
            for (int targetCol = 0; targetCol < factoryLayout[0].length; targetCol++) {
              if ((currentRow != targetRow || currentCol != targetCol) && factoryLayout[targetRow][targetCol] != null) affinity += calcAffinity(currentStation, factoryLayout[targetRow][targetCol], currentRow, currentCol, targetRow, targetCol);
            }
          }
        }
      }
    }

    for (int i = 0; i < stationCounters.length; i++) {
      if (stationCounters[i] > ((factoryLayout.length * factoryLayout[0].length) * (1.0 / stationCounters.length))) {
        double tooManyStation = stationCounters[i] - ((factoryLayout.length * factoryLayout[0].length) * (1.0 / stationCounters.length));
        for (int stationCount = 0; stationCount < tooManyStation; stationCount++) {
          affinity += -100;
        }
      }
    }

    return affinity;
  }

  public double calcAffinity(Station station1, Station station2, int firstRow, int firstCol, int secondRow, int secondCol) {
    double distance = Math.sqrt(Math.pow(firstRow - secondRow, 2) + Math.pow(firstCol - secondCol, 2));
    if (station1 == station2) return (2 * possibleStations.length) / distance;
    else return (-2 / possibleStations.length) / distance;
  }

  public void mutate() {
    ThreadLocalRandom random = ThreadLocalRandom.current();
    for (int mutation = 1; mutation <= 5; mutation++) {
      int row = random.nextInt(factoryLayout.length);
      int col = random.nextInt(factoryLayout[0].length);
      // + 1 for gap value
      int randomStationDecider = random.nextInt(possibleStations.length + 1);
      Station randomStation;
      if (randomStationDecider == (possibleStations.length + 1)) randomStation = null;
      else randomStation = possibleStations[randomStationDecider];
      factoryLayout[row][col] = randomStation;
    }
  }

  public void display() {
    for (int i = 0; i < factoryLayout.length; i++) {
      for (int j = 0; j < factoryLayout[0].length; j++) {
        if (factoryLayout[i][j] != null) factoryLayout[i][j].display();
        else System.out.print("GAP ");
      }
      System.out.println();
    }
  }

  public void copyTopLeftQuadrant(Factory parentFactory) {
    for (int i = 0; i < factoryLayout.length / 2; i++) {
      for (int j = 0; j < factoryLayout[0].length / 2; j++) {
        factoryLayout[i][j] = parentFactory.getIndex(i, j);
      }
    }
  }

  public void copyTopRightQuadrant(Factory parentFactory) {
    for (int i = 0; i < factoryLayout.length / 2; i++) {
      for (int j = factoryLayout[0].length / 2; j < factoryLayout[0].length; j++) {
        factoryLayout[i][j] = parentFactory.getIndex(i, j);
      }
    }
  }

  public void copyBottomLeftQuadrant(Factory parentFactory) {
    for (int i = factoryLayout.length / 2; i < factoryLayout.length; i++) {
      for (int j = 0; j < factoryLayout[0].length / 2; j++) {
        factoryLayout[i][j] = parentFactory.getIndex(i, j);
      }
    }
  }

  public void copyBottomRightQuadrant(Factory parentFactory) {
    for (int i = factoryLayout.length / 2; i < factoryLayout.length; i++) {
      for (int j = factoryLayout[0].length / 2; j < factoryLayout[0].length; j++) {
        factoryLayout[i][j] = parentFactory.getIndex(i, j);
      }
    }
  }


}

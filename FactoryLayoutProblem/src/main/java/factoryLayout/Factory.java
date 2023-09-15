package factoryLayout;

import java.util.concurrent.ThreadLocalRandom;

public class Factory {
  
  private Station[][] factoryLayout;
  private double affinity;

  public Factory(int rowSize, int colSize) {
    factoryLayout = new Station[rowSize][colSize];
    affinity = 0;
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
    for (int currentRow = 0; currentRow < factoryLayout.length; currentRow++) {
      for (int currentCol = 0; currentCol < factoryLayout[0].length; currentCol++) {
        Station currentStation = factoryLayout[currentRow][currentCol];
        if (currentStation != null) {
          for (int targetRow = 0; targetRow < factoryLayout.length; targetRow++) {
            for (int targetCol = 0; targetCol < factoryLayout.length; targetCol++) {
              if ((currentRow != targetRow || currentCol != targetCol) && factoryLayout[targetRow][targetCol] != null) affinity += calcAffinity(currentStation, factoryLayout[targetRow][targetCol], currentRow, currentCol, targetRow, targetCol);
            }
          }
        }
      }
    }
    return affinity;
  }

  public double calcAffinity(Station station1, Station station2, int firstRow, int firstCol, int secondRow, int secondCol) {
    double distance = Math.sqrt(Math.pow(firstRow - secondRow, 2) + Math.pow(firstCol - secondCol, 2));
    if (station1 == station2) return 5 / distance;
    else return -1 / distance;
  }

  public void mutate() {
    ThreadLocalRandom random = ThreadLocalRandom.current();
    for (int mutation = 1; mutation <= 15; mutation++) {
      int row = random.nextInt(factoryLayout.length);
      int col = random.nextInt(factoryLayout[0].length);
      int row2 = random.nextInt(factoryLayout.length);
      int col2 = random.nextInt(factoryLayout[0].length);
      factoryLayout[row][col] = factoryLayout[row2][col2];
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

package factoryLayout;

import java.util.Random;

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
    Random random = new Random();
    for (int mutation = 1; mutation <= 5; mutation++) {
      int row = random.nextInt();
      int col = random.nextInt();
      int row2 = random.nextInt();
      int col2 = random.nextInt();
      factoryLayout[row][col] = factoryLayout[row2][col2];
    }
  }

}

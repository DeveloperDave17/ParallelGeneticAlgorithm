package factoryLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;

public class FactoryView {
  
  final JFrame factoryFrame;

  final int DEFAULT_STATION_SIZE = 50;

  public FactoryView(int width, int height) {
    factoryFrame = new JFrame("Best Factory Layout");
    int taskBarHeight = Toolkit.getDefaultToolkit().getScreenInsets(factoryFrame.getGraphicsConfiguration()).top;
    factoryFrame.setSize(DEFAULT_STATION_SIZE * width, DEFAULT_STATION_SIZE * height + taskBarHeight);
    factoryFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    factoryFrame.setResizable(false);
  }

  public void displayStations(Factory factory) { 
    factoryFrame.getContentPane().removeAll();
    DrawStations stations = new DrawStations(factory);
    factoryFrame.getContentPane().add(stations);
    factoryFrame.revalidate();
    factoryFrame.repaint();
  }

  public void display() {
    factoryFrame.setVisible(true);
  }

  private class DrawStations extends JPanel {

    Factory factory;

    public DrawStations(Factory factory) {
      this.factory = factory;
    }


    public void drawStations(Graphics graphics) {
      Graphics2D graphics2d = (Graphics2D) graphics;
      for (int row = 0; row < factory.getRowLength(); row++) {
        for (int col = 0; col < factory.getColLength(); col++) {
          Station station = factory.getIndex(row, col);
          Color stationColor;
          if (station == null) stationColor = new Color(0, 0, 0);
          else stationColor = new Color(station.getR(), station.getG(), station.getB());
          graphics2d.setColor(stationColor);
          graphics2d.fillRect(col * DEFAULT_STATION_SIZE, row * DEFAULT_STATION_SIZE, DEFAULT_STATION_SIZE, DEFAULT_STATION_SIZE);
        }
      }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
      super.paintComponent(graphics);
      drawStations(graphics);
    }
  }
}

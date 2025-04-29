package cs113.trolley;

import javafx.scene.paint.Color;

class TrolleyRoute {
    private String fromStation;
    private String toStation;
    private int weight;
    private Color color;

    public TrolleyRoute(String fromStation, String toStation, int weight, Color color) {
        this.fromStation = fromStation;
        this.toStation = toStation;
        this.weight = weight;
        this.color = color;
    }

    public String getFromStation() {
        return fromStation;
    }

    public String getToStation() {
        return toStation;
    }

    public int getWeight() {
        return weight;
    }

    public Color getColor() {
        return color;
    }
}
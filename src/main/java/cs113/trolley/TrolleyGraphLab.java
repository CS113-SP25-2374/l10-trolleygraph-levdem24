// trolleyGraph Lab for IntelliJ and JavaFX
// Introduction to Graph Theory through trolley Map Visualization

package cs113.trolley;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.*;

public class TrolleyGraphLab extends Application {

    // The Graph represents the trolley system
    private TrolleyGraph trolleyGraph;

    // UI components
    private Canvas mapCanvas;
    private GraphicsContext gc;
    private TextArea outputConsole;
    private ComboBox<String> startStationCombo;
    private ComboBox<String> endStationCombo;
    private ToggleGroup algorithmToggle;

    // Constants for visualization
    private static final int STATION_RADIUS = 15;
    private static final int CANVAS_WIDTH = 800;
    private static final int CANVAS_HEIGHT = 600;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Initialize the trolley graph
        trolleyGraph = new TrolleyGraph();

        // Create sample data - students can modify this
        createSampletrolleySystem();

        // Set up the UI
        BorderPane root = new BorderPane();

        // Canvas for drawing the trolley map
        mapCanvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        gc = mapCanvas.getGraphicsContext2D();

        // Control Panel
        VBox controlPanel = createControlPanel();

        // Output Console
        outputConsole = new TextArea();
        outputConsole.setEditable(false);
        outputConsole.setPrefHeight(150);

        // Add components to the layout
        root.setCenter(mapCanvas);
        root.setRight(controlPanel);
        root.setBottom(outputConsole);
        BorderPane.setMargin(controlPanel, new Insets(10));
        BorderPane.setMargin(outputConsole, new Insets(10));

        // Create the scene
        Scene scene = new Scene(root, 1000, 700);

        primaryStage.setTitle("Trolley Graph Lab");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Draw the initial trolley map
        drawtrolleyMap();

        // Update the station combo boxes
        updateStationCombos();
    }

    private VBox createControlPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(200);

        // Station management section
        Label stationLabel = new Label("Station Management");
        stationLabel.setStyle("-fx-font-weight: bold");

        TextField stationNameField = new TextField();
        stationNameField.setPromptText("Station Name");

        TextField xCoordField = new TextField();
        xCoordField.setPromptText("X Coordinate");

        TextField yCoordField = new TextField();
        yCoordField.setPromptText("Y Coordinate");

        Button addStationButton = new Button("Add Station");
        addStationButton.setMaxWidth(Double.MAX_VALUE);
        addStationButton.setOnAction(e -> {
            try {
                String name = stationNameField.getText();
                int x = Integer.parseInt(xCoordField.getText());
                int y = Integer.parseInt(yCoordField.getText());

                if (name != null && !name.trim().isEmpty()) {
                    trolleyGraph.addStation(name, x, y);
                    stationNameField.clear();
                    xCoordField.clear();
                    yCoordField.clear();
                    drawtrolleyMap();
                    updateStationCombos();
                    outputConsole.appendText("Added station: " + name + "\n");
                }
            } catch (NumberFormatException ex) {
                outputConsole.appendText("Invalid coordinates. Please enter numbers.\n");
            }
        });

        // Route management section
        Label routeLabel = new Label("Route Management");
        routeLabel.setStyle("-fx-font-weight: bold");

        startStationCombo = new ComboBox<>();
        startStationCombo.setPromptText("From Station");
        startStationCombo.setMaxWidth(Double.MAX_VALUE);

        endStationCombo = new ComboBox<>();
        endStationCombo.setPromptText("To Station");
        endStationCombo.setMaxWidth(Double.MAX_VALUE);

        TextField routeWeightField = new TextField();
        routeWeightField.setPromptText("Distance/Time");

        ColorPicker routeColorPicker = new ColorPicker(Color.RED);
        routeColorPicker.setMaxWidth(Double.MAX_VALUE);

        Button addRouteButton = new Button("Add Route");
        addRouteButton.setMaxWidth(Double.MAX_VALUE);
        addRouteButton.setOnAction(e -> {
            String from = startStationCombo.getValue();
            String to = endStationCombo.getValue();

            try {
                int weight = Integer.parseInt(routeWeightField.getText());
                Color routeColor = routeColorPicker.getValue();

                if (from != null && to != null && !from.equals(to)) {
                    trolleyGraph.addRoute(from, to, weight, routeColor);
                    drawtrolleyMap();
                    routeWeightField.clear();
                    outputConsole.appendText("Added route: " + from + " → " + to + " (Weight: " + weight + ")\n");
                }
            } catch (NumberFormatException ex) {
                outputConsole.appendText("Invalid weight. Please enter a number.\n");
            }
        });

        // Traversal section
        Label traversalLabel = new Label("Graph Traversal");
        traversalLabel.setStyle("-fx-font-weight: bold");

        algorithmToggle = new ToggleGroup();

        RadioButton bfsButton = new RadioButton("BFS");
        bfsButton.setToggleGroup(algorithmToggle);
        bfsButton.setSelected(true);
        bfsButton.setUserData("BFS");

        RadioButton dfsButton = new RadioButton("DFS");
        dfsButton.setToggleGroup(algorithmToggle);
        dfsButton.setUserData("DFS");

        RadioButton dijkstraButton = new RadioButton("Dijkstra");
        dijkstraButton.setToggleGroup(algorithmToggle);
        dijkstraButton.setUserData("DIJKSTRA");

        HBox radioBox = new HBox(10, bfsButton, dfsButton, dijkstraButton);

        Button traverseButton = new Button("Find Path");
        traverseButton.setMaxWidth(Double.MAX_VALUE);
        traverseButton.setOnAction(e -> {
            String from = startStationCombo.getValue();
            String to = endStationCombo.getValue();

            if (from != null && to != null) {
                String algorithm = algorithmToggle.getSelectedToggle().getUserData().toString();
                List<String> path = traverseGraph(from, to, algorithm);

                // Clear previous paths
                drawtrolleyMap();

                // Draw the path
                if (path != null && path.size() > 1) {
                    drawPath(path);

                    // Display the path in the console
                    outputConsole.appendText("Path using " + algorithm + ": ");
                    for (int i = 0; i < path.size(); i++) {
                        outputConsole.appendText(path.get(i));
                        if (i < path.size() - 1) {
                            outputConsole.appendText(" → ");
                        }
                    }
                    outputConsole.appendText("\n");
                } else {
                    outputConsole.appendText("No path found using " + algorithm + "\n");
                }
            }
        });

        // Add all components to the panel
        panel.getChildren().addAll(
                stationLabel,
                stationNameField, xCoordField, yCoordField, addStationButton,
                new Separator(),
                routeLabel,
                startStationCombo, endStationCombo, routeWeightField,
                routeColorPicker, addRouteButton,
                new Separator(),
                traversalLabel,
                radioBox, traverseButton
        );

        return panel;
    }

    private void updateStationCombos() {
        Set<String> stations = trolleyGraph.getStationNames();
        startStationCombo.getItems().clear();
        endStationCombo.getItems().clear();
        startStationCombo.getItems().addAll(stations);
        endStationCombo.getItems().addAll(stations);
    }

    private void drawtrolleyMap() {
        // Clear the canvas
        gc.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());

        // Draw all routes (edges)
        for (TrolleyRoute route : trolleyGraph.getRoutes()) {
            TrolleyStation from = trolleyGraph.getStationByName(route.getFromStation());
            TrolleyStation to = trolleyGraph.getStationByName(route.getToStation());

            if (from != null && to != null) {
                gc.setStroke(route.getColor());
                gc.setLineWidth(3);
                gc.strokeLine(from.getX(), from.getY(), to.getX(), to.getY());

                // Draw weight value in the middle of the line
                double midX = (from.getX() + to.getX()) / 2;
                double midY = (from.getY() + to.getY()) / 2;
                gc.setFill(Color.WHITE);
                gc.fillOval(midX - 10, midY - 10, 20, 20);
                gc.setFill(Color.BLACK);
                gc.fillText(String.valueOf(route.getWeight()), midX - 5, midY + 5);
            }
        }

        // Draw all stations (nodes)
        for (TrolleyStation station : trolleyGraph.getStations()) {
            gc.setFill(Color.WHITE);
            gc.fillOval(station.getX() - STATION_RADIUS, station.getY() - STATION_RADIUS,
                    STATION_RADIUS * 2, STATION_RADIUS * 2);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeOval(station.getX() - STATION_RADIUS, station.getY() - STATION_RADIUS,
                    STATION_RADIUS * 2, STATION_RADIUS * 2);

            // Draw station name
            gc.setFill(Color.BLACK);
            gc.fillText(station.getName(), station.getX() - STATION_RADIUS,
                    station.getY() - STATION_RADIUS - 5);
        }
    }

    private void drawPath(List<String> path) {
        if (path.size() <= 1) return;

        gc.setStroke(Color.LIME);
        gc.setLineWidth(5);

        for (int i = 0; i < path.size() - 1; i++) {
            TrolleyStation from = trolleyGraph.getStationByName(path.get(i));
            TrolleyStation to = trolleyGraph.getStationByName(path.get(i + 1));

            if (from != null && to != null) {
                gc.strokeLine(from.getX(), from.getY(), to.getX(), to.getY());
            }
        }

        // Highlight start and end stations
        TrolleyStation start = trolleyGraph.getStationByName(path.get(0));
        TrolleyStation end = trolleyGraph.getStationByName(path.get(path.size() - 1));

        if (start != null) {
            gc.setFill(Color.GREEN);
            gc.fillOval(start.getX() - STATION_RADIUS, start.getY() - STATION_RADIUS,
                    STATION_RADIUS * 2, STATION_RADIUS * 2);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeOval(start.getX() - STATION_RADIUS, start.getY() - STATION_RADIUS,
                    STATION_RADIUS * 2, STATION_RADIUS * 2);
        }

        if (end != null) {
            gc.setFill(Color.RED);
            gc.fillOval(end.getX() - STATION_RADIUS, end.getY() - STATION_RADIUS,
                    STATION_RADIUS * 2, STATION_RADIUS * 2);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeOval(end.getX() - STATION_RADIUS, end.getY() - STATION_RADIUS,
                    STATION_RADIUS * 2, STATION_RADIUS * 2);
        }
    }

    private List<String> traverseGraph(String start, String end, String algorithm) {
        switch (algorithm) {
            case "BFS":
                return trolleyGraph.breadthFirstSearch(start, end);
            case "DFS":
                return trolleyGraph.depthFirstSearch(start, end);
            case "DIJKSTRA":
                return trolleyGraph.dijkstra(start, end);
            default:
                return null;
        }
    }

    private void createSampletrolleySystem() {
        // Add some sample stations
        trolleyGraph.addStation("Central", 400, 300);
        trolleyGraph.addStation("North", 400, 100);
        trolleyGraph.addStation("South", 400, 500);
        trolleyGraph.addStation("East", 600, 300);
        trolleyGraph.addStation("West", 200, 300);
        trolleyGraph.addStation("Northeast", 550, 150);
        trolleyGraph.addStation("Northwest", 250, 150);
        trolleyGraph.addStation("Southeast", 550, 450);
        trolleyGraph.addStation("Southwest", 250, 450);

        // Add some sample routes
        trolleyGraph.addRoute("Central", "North", 3, Color.RED);
        trolleyGraph.addRoute("Central", "South", 4, Color.RED);
        trolleyGraph.addRoute("Central", "East", 2, Color.BLUE);
        trolleyGraph.addRoute("Central", "West", 2, Color.BLUE);
        trolleyGraph.addRoute("North", "Northeast", 1, Color.GREEN);
        trolleyGraph.addRoute("North", "Northwest", 1, Color.GREEN);
        trolleyGraph.addRoute("East", "Northeast", 2, Color.ORANGE);
        trolleyGraph.addRoute("East", "Southeast", 2, Color.ORANGE);
        trolleyGraph.addRoute("South", "Southeast", 1, Color.PURPLE);
        trolleyGraph.addRoute("South", "Southwest", 1, Color.PURPLE);
        trolleyGraph.addRoute("West", "Northwest", 2, Color.BROWN);
        trolleyGraph.addRoute("West", "Southwest", 2, Color.BROWN);
    }
}
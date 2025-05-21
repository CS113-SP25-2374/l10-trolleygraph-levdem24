package cs113.trolley;

import javafx.scene.paint.Color;

import java.util.*;

class TrolleyGraph {
    private List<TrolleyStation> stations;
    private List<TrolleyRoute> routes;

    public TrolleyGraph() {
        stations = new ArrayList<>();
        routes = new ArrayList<>();
    }

    // Add a new station (node) to the graph
    public void addStation(String name, int x, int y) {
        if (getStationByName(name) == null) {
            stations.add(new TrolleyStation(name, x, y));
        }
    }

    // Get a station by its name
    public TrolleyStation getStationByName(String name) {
        for (TrolleyStation station : stations) {
            if (station.getName().equals(name)) {
                return station;
            }
        }
        return null;
    }

    // Get all station names
    public Set<String> getStationNames() {
        Set<String> names = new HashSet<>();
        for (TrolleyStation station : stations) {
            names.add(station.getName());
        }
        return names;
    }

    // Add a new route (edge) between two stations
    public void addRoute(String fromStation, String toStation, int weight, Color color) {
        if (getStationByName(fromStation) != null && getStationByName(toStation) != null) {
            routes.add(new TrolleyRoute(fromStation, toStation, weight, color));
        }
    }

    // Get all stations
    public List<TrolleyStation> getStations() {
        return stations;
    }

    // Get all routes
    public List<TrolleyRoute> getRoutes() {
        return routes;
    }

    // Get adjacent stations
    public List<String> getAdjacentStations(String stationName) {
        List<String> adjacents = new ArrayList<>();
        for (TrolleyRoute route : routes) {
            if (route.getFromStation().equals(stationName)) {
                adjacents.add(route.getToStation());
            }
            if (route.getToStation().equals(stationName)) {
                adjacents.add(route.getFromStation());
            }
        }
        return adjacents;
    }

    public int getRouteWeight(String fromStation, String toStation) {
        for (TrolleyRoute route : routes) {
            if ((route.getFromStation().equals(fromStation) && route.getToStation().equals(toStation)) ||
                    (route.getFromStation().equals(toStation) && route.getToStation().equals(fromStation))) {
                return route.getWeight();
            }
        }
        return -1;
    }

    public List<String> breadthFirstSearch(String start, String end) {
        Queue<String> queue = new LinkedList<>();
        Map<String, String> parentMap = new HashMap<>();
        Set<String> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (current.equals(end)) {
                return reconstructPath(parentMap, start, end);
            }
            for (String neighbor : getAdjacentStations(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parentMap.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }
        return null;
    }

    public List<String> depthFirstSearch(String start, String end) {
        Set<String> visited = new HashSet<>();
        Map<String, String> parentMap = new HashMap<>();
        dfsHelper(start, end, visited, parentMap);
        return reconstructPath(parentMap, start, end);
    }

    private boolean dfsHelper(String current, String end, Set<String> visited, Map<String, String> parentMap) {
        if (current.equals(end)) {
            return true;
        }
        visited.add(current);
        for (String neighbor : getAdjacentStations(current)) {
            if (!visited.contains(neighbor)) {
                parentMap.put(neighbor, current);
                if (dfsHelper(neighbor, end, visited, parentMap)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<String> dijkstra(String start, String end) {
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> parentMap = new HashMap<>();
        Set<String> visited = new HashSet<>();
        PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        for (TrolleyStation station : stations) {
            distances.put(station.getName(), Integer.MAX_VALUE);
        }
        distances.put(start, 0);
        queue.add(start);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (!visited.add(current)) continue;

            if (current.equals(end)) {
                return reconstructPath(parentMap, start, end);
            }

            for (String neighbor : getAdjacentStations(current)) {
                if (!visited.contains(neighbor)) {
                    int weight = getRouteWeight(current, neighbor);
                    int newDist = distances.get(current) + weight;
                    if (newDist < distances.get(neighbor)) {
                        distances.put(neighbor, newDist);
                        parentMap.put(neighbor, current);
                        queue.add(neighbor);
                    }
                }
            }
        }
        return null;
    }

    private List<String> reconstructPath(Map<String, String> parentMap, String start, String end) {
        List<String> path = new ArrayList<>();
        String current = end;
        while (current != null) {
            path.add(0, current);
            current = parentMap.get(current);
        }
        return path.get(0).equals(start) ? path : null;
    }
}

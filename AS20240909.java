package javaapplication5;

import java.io.*;
import java.util.*;

public class AS20240909 {

    static final int MAX_CITIES = 30;
    static final int MAX_DELIVERIES = 50;
    static final double FUEL_PRICE = 310.0;

    static String[] cityNames = new String[MAX_CITIES];
    static int cityCount = 0;
    static int[][] cityDistances = new int[MAX_CITIES][MAX_CITIES];

    static String[] fromCity = new String[MAX_DELIVERIES];
    static String[] toCity = new String[MAX_DELIVERIES];
    static double[] deliveryDistance = new double[MAX_DELIVERIES];
    static double[] deliveryTime = new double[MAX_DELIVERIES];
    static double[] deliveryCost = new double[MAX_DELIVERIES];
    static int deliveryCount = 0;

    static String[] vehicleType = {"Van", "Truck", "Lorry"};
    static int[] vehicleCap = {1000, 5000, 10000};
    static double[] ratePerKm = {30, 40, 80};
    static double[] speed = {60, 50, 45};
    static double[] efficiency = {12, 6, 4};

    static Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        loadCitiesAndDistances();
        loadDeliveries();

        while (true) {
            System.out.println("\n========== LOGISTICS MANAGEMENT SYSTEM ==========");
            System.out.println("1. Add City");
            System.out.println("2. Rename City");
            System.out.println("3. Remove City");
            System.out.println("4. Manage Distances");
            System.out.println("5. New Delivery Request");
            System.out.println("6. View Reports");
            System.out.println("7. Find Least-Cost Route");
            System.out.println("8. Save & Exit");
            System.out.print("Enter your choice: ");
            int choice = getInt();

            switch (choice) {
                case 1 -> addNewCity();
                case 2 -> renameCity();
                case 3 -> removeCity();
                case 4 -> manageDistances();
                case 5 -> handleDelivery();
                case 6 -> showReport();
                case 7 -> findLeastCostRoute();
                case 8 -> {
                    saveCitiesAndDistances();
                    saveDeliveries();
                    System.out.println("Data saved. Exiting... Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again!");
            }
        }
    }

    // ---------------- CITY MANAGEMENT ----------------

    static void addNewCity() {
        if (cityCount >= MAX_CITIES) {
            System.out.println("City limit reached!");
            return;
        }
        System.out.print("Enter new city name: ");
        String name = in.nextLine();
        for (int i = 0; i < cityCount; i++) {
            if (cityNames[i].equalsIgnoreCase(name)) {
                System.out.println("City already exists!");
                return;
            }
        }
        cityNames[cityCount++] = name;
        System.out.println("City added successfully!");
    }

    static void renameCity() {
        showCityList();
        System.out.print("Enter city index to rename: ");
        int idx = getInt();
        if (idx < 0 || idx >= cityCount) {
            System.out.println("Invalid index!");
            return;
        }
        System.out.print("Enter new name: ");
        cityNames[idx] = in.nextLine();
        System.out.println("City renamed successfully!");
    }

    static void removeCity() {
        showCityList();
        System.out.print("Enter city index to remove: ");
        int idx = getInt();
        if (idx < 0 || idx >= cityCount) {
            System.out.println("Invalid index!");
            return;
        }
        for (int i = idx; i < cityCount - 1; i++) {
            cityNames[i] = cityNames[i + 1];
            for (int j = 0; j < cityCount; j++) {
                cityDistances[i][j] = cityDistances[i + 1][j];
                cityDistances[j][i] = cityDistances[j][i + 1];
            }
        }
        cityCount--;
        System.out.println("City removed successfully!");
    }

    static void showCityList() {
        System.out.println("\n--- City List ---");
        for (int i = 0; i < cityCount; i++) {
            System.out.println(i + " - " + cityNames[i]);
        }
    }

    // ---------------- DISTANCE MANAGEMENT ----------------

    static void manageDistances() {
        if (cityCount < 2) {
            System.out.println("Add at least 2 cities first!");
            return;
        }
        showCityList();
        System.out.print("Enter source city index: ");
        int src = getInt();
        System.out.print("Enter destination city index: ");
        int dest = getInt();
        if (src == dest) {
            System.out.println("Same city! Distance = 0");
            return;
        }
        System.out.print("Enter distance (km): ");
        int dist = getInt();
        cityDistances[src][dest] = dist;
        cityDistances[dest][src] = dist;
        System.out.println("Distance updated successfully!");
    }

    // ---------------- DELIVERY HANDLING ----------------

    static void handleDelivery() {
        if (cityCount < 2) {
            System.out.println("Add cities first!");
            return;
        }

        showCityList();
        System.out.print("From city index: ");
        int from = getInt();
        System.out.print("To city index: ");
        int to = getInt();
        if (from == to) {
            System.out.println("Source and destination cannot be same!");
            return;
        }

        System.out.print("Enter weight (kg): ");
        double weight = getDouble();

        System.out.println("Choose vehicle: 1=Van, 2=Truck, 3=Lorry");
        int vType = getInt() - 1;
        if (vType < 0 || vType > 2) {
            System.out.println("Invalid vehicle choice!");
            return;
        }
        if (weight > vehicleCap[vType]) {
            System.out.println("Weight exceeds vehicle capacity!");
            return;
        }

        double dist = findShortestDistance(from, to);
        if (dist == Double.MAX_VALUE) {
            System.out.println("No available route between these cities!");
            return;
        }

        double baseCost = dist * ratePerKm[vType] * (1 + weight / 10000);
        double fuelUsed = dist / efficiency[vType];
        double fuelCost = fuelUsed * FUEL_PRICE;
        double operationalCost = baseCost + fuelCost;
        double profit = baseCost * 0.25;
        double totalCharge = operationalCost + profit;
        double estTime = dist / speed[vType];

        System.out.println("\n==============================");
        System.out.println("DELIVERY COST ESTIMATION");
        System.out.println("------------------------------");
        System.out.println("From: " + cityNames[from]);
        System.out.println("To: " + cityNames[to]);
        System.out.println("Vehicle: " + vehicleType[vType]);
        System.out.println("Weight: " + weight + " kg");
        System.out.printf("Shortest Distance: %.2f km\n", dist);
        System.out.printf("Base Cost: %.2f LKR\n", baseCost);
        System.out.printf("Fuel Used: %.2f L\n", fuelUsed);
        System.out.printf("Fuel Cost: %.2f LKR\n", fuelCost);
        System.out.printf("Operational Cost: %.2f LKR\n", operationalCost);
        System.out.printf("Profit: %.2f LKR\n", profit);
        System.out.printf("Customer Charge: %.2f LKR\n", totalCharge);
        System.out.printf("Estimated Time: %.2f hours\n", estTime);
        System.out.println("==============================");

        fromCity[deliveryCount] = cityNames[from];
        toCity[deliveryCount] = cityNames[to];
        deliveryDistance[deliveryCount] = dist;
        deliveryTime[deliveryCount] = estTime;
        deliveryCost[deliveryCount] = totalCharge;
        deliveryCount++;
    }

    // ---------------- LEAST COST ROUTE ----------------

    static void findLeastCostRoute() {
        if (cityCount < 2) {
            System.out.println("Not enough cities!");
            return;
        }
        showCityList();
        System.out.print("Enter starting city index: ");
        int from = getInt();
        System.out.print("Enter destination city index: ");
        int to = getInt();

        double shortest = findShortestDistance(from, to);
        if (shortest == Double.MAX_VALUE) {
            System.out.println("No route found between these cities!");
        } else {
            System.out.printf("Shortest distance between %s and %s is: %.2f km\n",
                    cityNames[from], cityNames[to], shortest);
        }
    }

    // Brute-force path checking (only up to 4 cities)
    static double findShortestDistance(int start, int end) {
        if (cityDistances[start][end] > 0) return cityDistances[start][end];

        double shortest = Double.MAX_VALUE;
        boolean[] visited = new boolean[cityCount];

        visited[start] = true;
        shortest = dfsShortest(start, end, visited, 0, shortest, 0);
        return shortest;
    }

    static double dfsShortest(int current, int target, boolean[] visited, double totalDist, double best, int depth) {
        if (depth > 4) return best;
        if (current == target) return Math.min(best, totalDist);

        for (int next = 0; next < cityCount; next++) {
            if (!visited[next] && cityDistances[current][next] > 0) {
                visited[next] = true;
                best = dfsShortest(next, target, visited,
                        totalDist + cityDistances[current][next], best, depth + 1);
                visited[next] = false;
            }
        }
        return best;
    }

    // ---------------- REPORTS ----------------

    static void showReport() {
        if (deliveryCount == 0) {
            System.out.println("No deliveries available!");
            return;
        }

        double totalDist = 0, totalTime = 0, totalRevenue = 0;
        double longest = 0, shortest = Double.MAX_VALUE;

        for (int i = 0; i < deliveryCount; i++) {
            totalDist += deliveryDistance[i];
            totalTime += deliveryTime[i];
            totalRevenue += deliveryCost[i];
            if (deliveryDistance[i] > longest) longest = deliveryDistance[i];
            if (deliveryDistance[i] < shortest) shortest = deliveryDistance[i];
        }

        System.out.println("\n=========== PERFORMANCE REPORT ===========");
        System.out.println("Total Deliveries: " + deliveryCount);
        System.out.printf("Total Distance Covered: %.2f km\n", totalDist);
        System.out.printf("Average Delivery Time: %.2f hours\n", totalTime / deliveryCount);
        System.out.printf("Total Revenue: %.2f LKR\n", totalRevenue);
        System.out.printf("Longest Route: %.2f km\n", longest);
        System.out.printf("Shortest Route: %.2f km\n", shortest);
        System.out.println("==========================================");
    }

    // ---------------- FILE HANDLING ----------------

    static void loadCitiesAndDistances() {
        try (BufferedReader br = new BufferedReader(new FileReader("routes.txt"))) {
            cityCount = Integer.parseInt(br.readLine());
            for (int i = 0; i < cityCount; i++) {
                cityNames[i] = br.readLine();
            }
            for (int i = 0; i < cityCount; i++) {
                String[] parts = br.readLine().split(" ");
                for (int j = 0; j < cityCount; j++) {
                    cityDistances[i][j] = Integer.parseInt(parts[j]);
                }
            }
            System.out.println("City data loaded successfully!");
        } catch (Exception e) {
            System.out.println("No previous city data found (new start).");
        }
    }

    static void saveCitiesAndDistances() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("routes.txt"))) {
            pw.println(cityCount);
            for (int i = 0; i < cityCount; i++) {
                pw.println(cityNames[i]);
            }
            for (int i = 0; i < cityCount; i++) {
                for (int j = 0; j < cityCount; j++) {
                    pw.print(cityDistances[i][j] + " ");
                }
                pw.println();
            }
            System.out.println("City and distance data saved.");
        } catch (Exception e) {
            System.out.println("Error saving routes.txt");
        }
    }

    static void loadDeliveries() {
        try (BufferedReader br = new BufferedReader(new FileReader("deliveries.txt"))) {
            deliveryCount = Integer.parseInt(br.readLine());
            for (int i = 0; i < deliveryCount; i++) {
                fromCity[i] = br.readLine();
                toCity[i] = br.readLine();
                deliveryDistance[i] = Double.parseDouble(br.readLine());
                deliveryTime[i] = Double.parseDouble(br.readLine());
                deliveryCost[i] = Double.parseDouble(br.readLine());
            }
            System.out.println("Delivery history loaded successfully!");
        } catch (Exception e) {
            System.out.println("No previous deliveries found.");
        }
    }

    static void saveDeliveries() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("deliveries.txt"))) {
            pw.println(deliveryCount);
            for (int i = 0; i < deliveryCount; i++) {
                pw.println(fromCity[i]);
                pw.println(toCity[i]);
                pw.println(deliveryDistance[i]);
                pw.println(deliveryTime[i]);
                pw.println(deliveryCost[i]);
            }
            System.out.println("Deliveries saved successfully.");
        } catch (Exception e) {
            System.out.println("Error saving deliveries.txt");
        }
    }

    // ---------------- HELPERS ----------------

    static int getInt() {
        while (true) {
            try {
                return Integer.parseInt(in.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Enter valid number: ");
            }
        }
    }

    static double getDouble() {
        while (true) {
            try {
                return Double.parseDouble(in.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Enter valid number: ");
            }
        }
    }
}

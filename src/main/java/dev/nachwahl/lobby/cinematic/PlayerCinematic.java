package dev.nachwahl.lobby.cinematic;

import com.destroystokyo.paper.Title;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.RunnableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerCinematic {

    private List<PathPoint> cameraPoints = new ArrayList<>();
    private int currentPointIndex = 0;

    private Plugin plugin;

    private double totalTicks;

    private double totalDistance = 0;
    private boolean useDefinedTime = true;

    public PlayerCinematic(Plugin plugin){
        this.plugin = plugin;
    }

    public void startCinematic(Player player, List<PathPoint> cameraPoints,double totalTicks, boolean isLinear) {
        this.cameraPoints = cameraPoints;
        this.totalTicks = totalTicks;
        for (int i = 1; i < cameraPoints.size(); i++) {
            Location currentPoint = cameraPoints.get(i).getLocation();
            Location previousPoint = cameraPoints.get(i - 1).getLocation();
            double distanceBetweenPoints = currentPoint.distance(previousPoint);
            totalDistance += distanceBetweenPoints;
        }
        runNextInterpolation(player, isLinear);
    }

    private void runNextInterpolation(Player player, boolean isLinear) {
        GameMode previousGameMode = player.getGameMode();
        player.setGameMode(GameMode.SPECTATOR);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,9999,2,false,false));
        if (currentPointIndex < cameraPoints.size() - 1) {
            CompletableFuture<Void> future;
            PathPoint originPoint = cameraPoints.get(currentPointIndex);
            PathPoint targetPoint = cameraPoints.get(currentPointIndex + 1);
            if(isLinear) {
                future = runLinearInterpolationTask(player, originPoint, targetPoint, true);
            } else {
                //future = runSplineInterpolationTask(player, cameraPoints);
                future = runLinearInterpolationTask(player, originPoint, targetPoint, false);
            }

            // Füge eine Aktion hinzu, die nach Abschluss des CompletableFuture ausgeführt wird
            future.thenRun(() -> {
                // Inkrementiere den Index und führe die nächste Interpolation aus
                currentPointIndex++;
                runNextInterpolation(player, isLinear);
            });
        } else {
            // Kamerafahrt abgeschlossen
            player.removePotionEffect(PotionEffectType.SLOW);
            player.setGameMode(previousGameMode);
            player.sendMessage("§aDie Kamerafahrt wurde abgeschlossen!");
        }
    }

    private CompletableFuture<Void> runLinearInterpolationTask(Player player,PathPoint originPoint, PathPoint targetPoint, boolean linear) {
        String title = originPoint.getTitle();
        String subtitle = originPoint.getSubtitle();
        String chatMessage = originPoint.getChatMessage();
        Sound sound = originPoint.getSound();

        if(title != null && subtitle != null) {
            Title.builder().title(title).subtitle(subtitle).fadeIn(20).fadeOut(30).build().send(player);
        }
        if(chatMessage != null) player.sendMessage(chatMessage);
        if(sound != null) player.playSound(player, sound,70,0);

        CompletableFuture<Void> future = new CompletableFuture<>();
        Location originLocation = originPoint.getLocation();
        Location targetLocation = targetPoint.getLocation();
        // Länge des Vektors, um die Entfernung zwischen den Kamerapunkten zu erhalten
        double distanceBetweenPoints = targetLocation.distance(originLocation);
        double ticks = targetPoint.getTicksToPoint();

        if(totalTicks == 0) {
            useDefinedTime = false;
            // wenn keine ticks angegeben wird distanz zwischen punkten berechnet und diese mit 10 multipliziert
            if (ticks == 0 || ticks == 20.0) {
                totalTicks = distanceBetweenPoints * 10; // 40 normal value
            }else{
                totalTicks = ticks;
            }
        }

        new BukkitRunnable() {
            double currentTick = 0;
            final World world = player.getWorld();

            @Override
            public void run() {
                // Berechne die Ticks, die für diese Kamerapunkte benötigt werden
                double ticksForThisSegment = (distanceBetweenPoints / totalDistance) * totalTicks;

                if (!useDefinedTime) {
                    ticksForThisSegment = totalTicks;
                }
                System.out.println("Totalticks: "+ totalTicks+", Ticks-per-segment: "+ticksForThisSegment);
                if (currentTick <= ticksForThisSegment) {
                    double t = currentTick / ticksForThisSegment;
/*
                    double x = lerp(originLocation.getX(), targetLocation.getX(), t);
                    double y = lerp(originLocation.getY(), targetLocation.getY(), t);
                    double z = lerp(originLocation.getZ(), targetLocation.getZ(), t);

                    float yaw = (float) lerp(originLocation.getYaw(), targetLocation.getYaw(), t);
                    float pitch = (float) lerp(originLocation.getPitch(), targetLocation.getPitch(), t);

                    Location interpolatedLocation = new Location(world, x, y, z, yaw, pitch);

 */
                    Location interpolatedLocation = interpolate(linear,originLocation, targetLocation, t); //kubic
                    player.teleport(interpolatedLocation);

                    currentTick++;
                } else {
                    // Interpolation abgeschlossen
                    future.complete(null);
                    this.cancel();
                }
            }

        }.runTaskTimer(plugin, 0, 1);

        return future;
    }

    private double lerp(double start, double end, double t) {
        return start + t * (end - start);
    }


    public static long convertToTicks(String timeString) {
        // Verwende reguläre Ausdrücke, um die Zeiteinheiten zu extrahieren
        Pattern pattern = Pattern.compile("(\\d+)([smh])");
        Matcher matcher = pattern.matcher(timeString);

        if (matcher.matches()) {
            int amount = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2).toLowerCase();
            // Konvertiere die Zeiteinheit in Ticks
            switch (unit) {
                case "s":
                    return amount * 20L; // 20 Ticks pro Sekunde
                case "m":
                    return amount * 20L * 60L; // 20 Ticks pro Sekunde * 60 Sekunden pro Minute
                case "h":
                    return amount * 20L * 60L * 60L; // 20 Ticks pro Sekunde * 60 Sekunden pro Minute * 60 Minuten pro Stunde
            }
        }else{
            return Integer.parseInt(timeString);
        }

        // Standardwert: 0 Ticks (Ungültige Eingabe)
        return 0;
    }

    private Location interpolate(boolean linear,Location start, Location end, double t) {
        double x;
        double y;
        double z;
        float yaw;
        float pitch;

        if(linear){
            x = lerp(start.getX(), end.getX(), t);
            y = lerp(start.getY(), end.getY(), t);
            z = lerp(start.getZ(), end.getZ(), t);
            yaw = (float) lerp(start.getYaw(), end.getYaw(), t);
            pitch = (float) lerp(start.getPitch(), end.getPitch(), t);
        }else {
            double[] coefficients = calculateSplineCoefficients(start, end);
            double t2 = t * t;
            double t3 = t2 * t;
            x = coefficients[0] + coefficients[1] * t + coefficients[2] * t2 + coefficients[3] * t3;
            y = coefficients[4] + coefficients[5] * t + coefficients[6] * t2 + coefficients[7] * t3;
            z = coefficients[8] + coefficients[9] * t + coefficients[10] * t2 + coefficients[11] * t3;
            yaw = (float) interpolate(start.getYaw(), end.getYaw(), t);
            pitch = (float) interpolate(start.getPitch(), end.getPitch(), t);
        }

        World world = start.getWorld();
        return new Location(world, x, y, z, yaw, pitch);
    }

    private double interpolate(double start, double end, double t) {
        return start + (end - start) * t;
    }

    private double[] calculateSplineCoefficients(Location start, Location end) {
        double[] coefficients = new double[12];
        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();
        double dz = end.getZ() - start.getZ();

        coefficients[0] = start.getX();
        coefficients[1] = 0;
        coefficients[2] = 3 * dx;
        coefficients[3] = -2 * dx;

        coefficients[4] = start.getY();
        coefficients[5] = 0;
        coefficients[6] = 3 * dy;
        coefficients[7] = -2 * dy;

        coefficients[8] = start.getZ();
        coefficients[9] = 0;
        coefficients[10] = 3 * dz;
        coefficients[11] = -2 * dz;

        return coefficients;
    }

    private CompletableFuture<Void> runSplineInterpolationTask(Player player, List<PathPoint> cameraPoints) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        int totalPoints = cameraPoints.size();
        int controlPointsPerSegment = 2; // Anzahl der Steuerpunkte pro Segment (hier 2 für kubische Bézier-Spline)

        BukkitTask runnable = new BukkitRunnable() {
            int currentIndex = 0;
            int currentTick = 0;
            final World world = player.getWorld();

            @Override
            public void run() {
                System.out.println("currentIndex: " + currentIndex);
                System.out.println("currentTick: " + currentTick);
                System.out.println("totalPoints: " + totalPoints);

                if (currentIndex < totalPoints - 1) {
                    PathPoint startPoint = cameraPoints.get(currentIndex);
                    PathPoint endPoint = cameraPoints.get(Math.min(currentIndex + 1, totalPoints - 1));

                    System.out.println("StartPoint: " + startPoint);
                    System.out.println("EndPoint: " + endPoint);

                    double t = 1.0 * currentTick / (startPoint.getTicksToPoint() - 1);

                    System.out.println("t: " + t);

                    List<PathPoint> controlPoints = getControlPoints(cameraPoints, currentIndex, controlPointsPerSegment);

                    double x = cubicBezierSplineInterpolate(t,
                            controlPoints.stream().map(p -> p.getLocation().getX()).toArray(Double[]::new));

                    double y = cubicBezierSplineInterpolate(t,
                            controlPoints.stream().map(p -> p.getLocation().getY()).toArray(Double[]::new));

                    double z = cubicBezierSplineInterpolate(t,
                            controlPoints.stream().map(p -> p.getLocation().getZ()).toArray(Double[]::new));

                    float yaw = (float) cubicBezierSplineInterpolate(t, (double) startPoint.getLocation().getYaw(), (double) endPoint.getLocation().getYaw());
                    float pitch = (float) cubicBezierSplineInterpolate(t, (double) startPoint.getLocation().getPitch(), (double) endPoint.getLocation().getPitch());

                    Location interpolatedLocation = new Location(world, x, y, z, yaw, pitch);
                    player.teleport(interpolatedLocation);

                    // Erhöhe den Index, um zum nächsten Punkt zu gelangen
                    currentIndex++;
                    currentTick = 0;
                } else {
                    // Interpolation abgeschlossen
                    future.complete(null);
                    this.cancel();
                }
                currentTick++;
            }
        }.runTaskTimer(plugin, 0, 1);

        // Weise die Instanz des BukkitRunnable zur späteren Verwendung zu
        future.whenComplete((result, throwable) -> {
            runnable.cancel();
        });

        return future;
    }

    private List<PathPoint> getControlPoints(List<PathPoint> cameraPoints, int currentIndex, int numPoints) {
        int startIndex = Math.max(currentIndex - numPoints + 1, 0);
        int endIndex = Math.min(currentIndex + numPoints, cameraPoints.size());

        return cameraPoints.subList(startIndex, endIndex);
    }

    private double cubicBezierSplineInterpolate(double t, Double... values) {
        int n = values.length - 1;
        double result = 0;

        for (int i = 0; i <= n; i++) {
            result += binomialCoefficient(n, i) * Math.pow(1 - t, n - i) * Math.pow(t, i) * values[i];
        }

        return result;
    }

    private long binomialCoefficient(int n, int k) {
        if (k < 0 || k > n) {
            return 0;
        }

        if (k == 0 || k == n) {
            return 1;
        }

        return binomialCoefficient(n - 1, k - 1) + binomialCoefficient(n - 1, k);
    }
}

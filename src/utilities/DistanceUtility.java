package utilities;

public final class DistanceUtility {
    private DistanceUtility() {}

    public static double calculate(int[] point_A, int[] point_B) {
        double x_component = Math.pow(point_B[0] - point_A[0], 2);
        double y_component = Math.pow(point_B[1] - point_A[1], 2);

        return Math.sqrt(x_component + y_component);
    }
}

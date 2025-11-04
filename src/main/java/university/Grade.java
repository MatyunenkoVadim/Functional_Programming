package university;

public enum Grade {
    // DATA
    A(5.0), B(4.0), C(3.0), D(5.0), F(1.0);
    private final double points;

    // ACTION
    Grade(double p) {
        this.points = p;
    }

    // CALCULATION
    public double points() {
        return points;
    }
}

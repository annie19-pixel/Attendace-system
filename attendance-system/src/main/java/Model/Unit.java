package Model;

public class Unit {
    private final int unitId;
    private final String unitCode;
    private final String unitName;

    public Unit(int unitId, String unitCode, String unitName) {
        this.unitId = unitId;
        this.unitCode = unitCode;
        this.unitName = unitName;
    }

    public int getUnitId() {
        return unitId;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public String getUnitName() {
        return unitName;
    }

    @Override
    public String toString() {
        return unitCode + " - " + unitName;
    }
}

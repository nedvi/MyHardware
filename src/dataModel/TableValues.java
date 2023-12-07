package dataModel;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Trida reprezentujici hodnoty v tabulce
 *
 * @author Dominik Nedved, A22B0109P
 * @version 2023.06.26
 */
public class TableValues {
    private final SimpleStringProperty valueType;
    private final SimpleDoubleProperty value;

    public TableValues(String valueType, double value) {
        this.valueType = new SimpleStringProperty(valueType);
        this.value = new SimpleDoubleProperty(value);
    }

    public String getValueType() {
        return valueType.get();
    }

    public SimpleStringProperty valueTypeProperty() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType.set(valueType);
    }

    public double getValue() {
        return value.get();
    }

    public SimpleDoubleProperty valueProperty() {
        return value;
    }

    public void setValue(double value) {
        this.value.set(value);
    }
}

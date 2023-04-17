package Beta;

import javafx.scene.control.Label;

import java.awt.*;

public class Component {

    private final componentType componentType;

    public Component(componentType componentType) {
        this.componentType = componentType;
    }

    public enum componentType {
       CPU, GPU, DISK;

       public String toString() {
           switch(this) {
               case CPU:
                    return "CPU";
           }

           return  "Undefined";
       }

        public String getSymbol() {
            switch(this) {
                case CPU:
                    return "\uF2D6";
            }
            return "\u0000";
        }
    }

    public Label setDescription(Label description) {
        return description;
    }

    public componentType getComponentType() {
        return this.componentType;
    }

    @Override
    public String toString() {
        return (getComponentType().getSymbol() + getComponentType().toString());
    }
}

package test;

import engine.DrawManager;
import entity.Ship;
import entity.ShipMultipliers;

public class TestShip extends Ship {
    public TestShip(int positionX, int positionY,
                    String name, ShipMultipliers multipliers,
                    DrawManager.SpriteType spriteType) {
        super(positionX,positionY,name,multipliers,spriteType);
    }


}

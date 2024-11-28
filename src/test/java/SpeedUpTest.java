package test;

import engine.ItemManager;
import entity.Ship;
import entity.ShipMultipliers;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class SpeedUpTest {

    @Test
    public void TestSpeedUp() {
        //Initial speed settings
        ShipMultipliers shipMultipliers = new ShipMultipliers(2.0f,1f,750);
        TestShip testShip = new TestShip(0,0,"Test Ship", shipMultipliers, null);

        Assertions.assertEquals(2.0f, testShip.getMultipliers().speed());

        //Check the speed increase
        ItemManager itemManager = new ItemManager(testShip, null,null,800,600,0.0f);
        itemManager.useItem();

        Assertions.assertEquals(2.1f, testShip.getMultipliers().speed());


        //Iterative application testing
        for(int i = 1; i <= 20; i++) {
            itemManager.useItem();
        }

        //Maximum speed test
        Assertions.assertEquals(2.999999f, testShip.getMultipliers().speed(), 0.000001f);
    }

}

package engine;

import entity.Ship;
import entity.ShipMultipliers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemManagerTest {

    private Ship testShip;
    private ItemManager testItemManager;
    private Method operateSpeedUp;

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        ShipMultipliers testMultipliers = new ShipMultipliers(2.0f, 1.0f, 750);
        testShip = new Ship(0, 0, "Test Ship", testMultipliers, null);
        testItemManager = new ItemManager(testShip, null, null, null, 800, 600, 0.0f);

        operateSpeedUp = ItemManager.class.getDeclaredMethod("operateSpeedUp");
        operateSpeedUp.setAccessible(true);
    }

    @Test
    @DisplayName("Speed Up Test")
    public void testOperateSpeedUp() throws InvocationTargetException, IllegalAccessException {

        assertEquals(2.0f, testShip.getMultipliers().speed());

        //Check the speed increase
        operateSpeedUp.invoke(testItemManager);

        assertEquals(2.1f, testShip.getMultipliers().speed());

        //Iterative application testing
        for (int i = 1; i <= 20; i++) {
            operateSpeedUp.invoke(testItemManager);
        }

        //Maximum speed test
        assertEquals(2.999999f, testShip.getMultipliers().speed(), 0.000001f);
    }
}
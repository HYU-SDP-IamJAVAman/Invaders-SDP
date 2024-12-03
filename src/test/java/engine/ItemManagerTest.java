package engine;

import entity.AllyShip;
import entity.Ship;
import entity.ShipMultipliers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemManagerTest {

    private Ship testShip;
    private Set<AllyShip> testAllyShips;
    private ItemManager testItemManager;
    private Method operateSpeedUp;
    private Method operateAllyShip;

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        ShipMultipliers testMultipliers = new ShipMultipliers(2.0f, 1.0f, 750);
        testShip = new Ship(0, 0, "Test Ship", testMultipliers, null);
        testItemManager = new ItemManager(testShip, null, null, null, 800, 600, 0.0f);

        operateSpeedUp = ItemManager.class.getDeclaredMethod("operateSpeedUp");
        operateSpeedUp.setAccessible(true);

        operateAllyShip = ItemManager.class.getDeclaredMethod("operateAllyShip");
        operateAllyShip.setAccessible(true);
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

    @Test
    @DisplayName("AllyShip Test")
    public void testOperateAllyShip() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Set<AllyShip> testAllyShips = new HashSet<>();

        Field allyShipsField = ItemManager.class.getDeclaredField("allyShips");
        allyShipsField.setAccessible(true);
        allyShipsField.set(testItemManager, testAllyShips);

        // 초기 allyShips 상태 확인
        assertEquals(0, testAllyShips.size(), "AllyShips should initially be empty");

        // AllyShip 아이템 사용
        operateAllyShip.invoke(testItemManager);

        // AllyShip 생성 확인
        assertEquals(2, testAllyShips.size(), "Two AllyShips should be created after invoking operateAllyShip");
    }
}
package engine;

import entity.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Manages item drop and use.
 *
 * @author Seochan Moon
 * @author noturavrigk
 * @author specture258
 * @author javadocq
 * @author bamcasa
 * @author D0hunLee
 *
 */
public class ItemManager {
    /** Width of game screen. */
    private int WIDTH;
    /** Height of game screen. */
    private int HEIGHT;
    /** Item drop probability, (1 ~ 100). */
    private static final int ITEM_DROP_PROBABILITY = 30;
    /** Cooldown of Ghost */
    private static final int GHOST_COOLDOWN = 3000;
    /** Cooldown of Time-stop */
    private static final int TIMESTOP_COOLDOWN = 4000;
    /** Cooldown of AllyShip */
    private static final int ALLYSHIP_COOLDOWN = 10000;
    /** Max Speed */
    private final float MAX_SPEED = 3.0f;
    /** Random generator. */
    private final Random rand;
    /** Player's ship. */
    private final Ship ship;
    /** Formation of enemy ships. */
    private final EnemyShipFormation enemyShipFormation;
    /** Set of Barriers in game screen. */
    private final Set<Barrier> barriers;
    /** Set of Barriers in game screen. */
    private final Set<AllyShip> allyShips;
    /** Application logger. */
    private final Logger logger;
    /** Singleton instance of SoundManager */
    private final SoundManager soundManager = SoundManager.getInstance();
    /** Cooldown variable for Ghost */
    private Cooldown ghost_cooldown = Core.getCooldown(0);
    /** Cooldown variable for Time-stop */
    private Cooldown timeStop_cooldown = Core.getCooldown(0);
    /** Cooldown variable for AllyShip */
    private Cooldown allyShip_cooldown = Core.getCooldown(0);

    /** Check if the number of shot is max, (maximum 3). */
    private boolean isMaxShotNum;
    /** Number of bullets that player's ship shoot. */
    private int shotNum;
    /** Sound balance for each player*/
    private float balance;

    /** Types of item */
    public enum ItemType {
        Bomb,
        LineBomb,
        Barrier,
        Ghost,
        TimeStop,
        SpeedUp,
        AllyShip,
        MultiShot
    }

    /**
     * Constructor, sets the initial conditions.
     *
     * @param ship Player's ship.
     * @param enemyShipFormation Formation of enemy ships.
     * @param barriers Set of barriers in game screen.
     * @param balance 1p -1.0, 2p 1.0, both 0.0
     *
     */
    public ItemManager(Ship ship, EnemyShipFormation enemyShipFormation,
                       Set<Barrier> barriers, Set<AllyShip> allyShips,
                       int width, int height, float balance) {
        this.shotNum = 1;
        this.allyShips = allyShips;
        this.rand = new Random();
        this.ship = ship;
        this.enemyShipFormation = enemyShipFormation;
        this.barriers = barriers;
        this.logger = Core.getLogger();
        this.WIDTH = width;
        this.HEIGHT = height;
        this.balance = balance;
    }

    /**
     * Drop the item.
     *
     * @return Checks if the item was dropped.
     */
    public boolean dropItem() {
        return (rand.nextInt(101)) <= ITEM_DROP_PROBABILITY;
    }

    /**
     * Select item randomly.
     *
     * @return Item type.
     */
    private ItemType selectItemType() {
        ItemType[] itemTypes = ItemType.values();

        if (isMaxShotNum)
            return itemTypes[rand.nextInt(7)];

        return itemTypes[rand.nextInt(8)];
    }

    /**
     * Uses a randomly selected item.
     *
     * @return If the item is offensive, returns the score to add and the number of ships destroyed.
     *         If the item is non-offensive, returns null.
     */
    public Entry<Integer, Integer> useItem() {
        ItemType itemType = selectItemType();
        logger.info(itemType + " used");

        return switch (itemType) {
            case Bomb -> operateBomb();
            case LineBomb -> operateLineBomb();
            case Barrier -> operateBarrier();
            case Ghost -> operateGhost();
            case TimeStop -> operateTimeStop();
            case SpeedUp -> operateSpeedUp();
            case AllyShip -> operateAllyShip();
            case MultiShot -> operateMultiShot();

        };
    }

    /**
     * Operate Bomb item.
     *
     * @return The score to add and the number of ships destroyed.
     */
    private Entry<Integer, Integer> operateBomb() {
        this.soundManager.playSound(Sound.ITEM_BOMB, balance);

        int addScore = 0;
        int addShipsDestroyed = 0;

        List<List<EnemyShip>> enemyships = this.enemyShipFormation.getEnemyShips();
        int enemyShipsSize = enemyships.size();

        int maxCnt = -1;
        int maxRow = 0, maxCol = 0;

        for (int i = 0; i <= enemyShipsSize - 3; i++) {

            List<EnemyShip> rowShips = enemyships.get(i);
            int rowSize = rowShips.size();

            for (int j = 0; j <= rowSize - 3; j++) {

                int currentCnt = 0;

                for (int x = i; x < i + 3; x++) {

                    List<EnemyShip> subRowShips = enemyships.get(x);

                    for (int y = j; y < j + 3; y++) {
                        EnemyShip ship = subRowShips.get(y);

                        if (ship != null && !ship.isDestroyed())
                            currentCnt++;
                    }
                }

                if (currentCnt > maxCnt) {
                    maxCnt = currentCnt;
                    maxRow = i;
                    maxCol = j;
                }
            }
        }

        List<EnemyShip> targetEnemyShips = new ArrayList<>();
        for (int i = maxRow; i < maxRow + 3; i++) {
            List<EnemyShip> subRowShips = enemyships.get(i);
            for (int j = maxCol; j < maxCol + 3; j++) {
                EnemyShip ship = subRowShips.get(j);

                if (ship != null && !ship.isDestroyed())
                    targetEnemyShips.add(ship);
            }
        }

        if (!targetEnemyShips.isEmpty()) {
            for (EnemyShip destroyedShip : targetEnemyShips) {
                addScore += destroyedShip.getPointValue();
                addShipsDestroyed++;
                enemyShipFormation.destroy(destroyedShip, balance);
            }
        }

        return new SimpleEntry<>(addScore, addShipsDestroyed);
    }

    /**
     * Operate Line-bomb item.
     *
     * @return The score to add and the number of ships destroyed.
     */
    private Entry<Integer, Integer> operateLineBomb() {
        this.soundManager.playSound(Sound.ITEM_BOMB, balance);

        int addScore = 0;
        int addShipsDestroyed = 0;

        List<List<EnemyShip>> enemyShips = this.enemyShipFormation.getEnemyShips();

        int destroyRow = -1;

        for (List<EnemyShip> column : enemyShips) {
            for (int i = 0; i < column.size(); i++) {
                if (column.get(i) != null && !column.get(i).isDestroyed())
                    destroyRow = Math.max(destroyRow, i);
            }
        }

        if (destroyRow != -1) {
            for (List<EnemyShip> column : enemyShips) {
                if (column.get(destroyRow) != null && !column.get(destroyRow).isDestroyed()) {
                    addScore += column.get(destroyRow).getPointValue();
                    addShipsDestroyed++;
                    enemyShipFormation.destroy(column.get(destroyRow), balance);
                }
            }
        }

        return new SimpleEntry<>(addScore, addShipsDestroyed);
    }

    /**
     * Operate Barrier item.
     *
     * @return null
     */
    private Entry<Integer, Integer> operateBarrier() {
        this.soundManager.playSound(Sound.ITEM_BARRIER_ON, balance);

        int BarrierY = HEIGHT - 70;
        int middle = WIDTH / 2 - 39;
        int range = 200;
        this.barriers.clear();

        this.barriers.add(new Barrier(middle, BarrierY));
        this.barriers.add(new Barrier(middle - range, BarrierY));
        this.barriers.add(new Barrier(middle + range, BarrierY));
        logger.info("Barrier created at positions: (" + middle + ", " + (BarrierY) + "), ("
                + (middle - range) + ", " + (BarrierY) + "), ("
                + (middle + range) + ", " + (BarrierY) + ")");
        return null;
    }

    /**
     * Operate Ghost item.
     *
     * @return null
     */
    private Entry<Integer, Integer> operateGhost() {
        this.soundManager.playSound(Sound.ITEM_GHOST, balance);

        this.ship.setColor(Color.DARK_GRAY);
        this.ghost_cooldown = Core.getCooldown(GHOST_COOLDOWN);
        this.ghost_cooldown.reset();

        return null;
    }

    /**
     * Operate Time-stop item.
     *
     * @return null
     */
    private Entry<Integer, Integer> operateTimeStop() {
        this.soundManager.playSound(Sound.ITEM_TIMESTOP_ON, balance);

        this.timeStop_cooldown = Core.getCooldown(TIMESTOP_COOLDOWN);
        this.timeStop_cooldown.reset();

        return null;
    }

    /**
     * Operate Multi-shot item.
     *
     * @return null
     */
    private Entry<Integer, Integer> operateMultiShot() {
        if (this.shotNum < 3) {
            this.shotNum++;
            if (this.shotNum == 3) {
                this.isMaxShotNum = true;
            }
        }

        return null;
    }
    /**
     * Operate Speed-Up item.
     *
     * @return null
     */
    private Entry<Integer, Integer> operateSpeedUp() {
        ShipMultipliers currentMultipliers = ship.getMultipliers();
        float newSpeedMultiplier = currentMultipliers.speed() + 0.1f;

        if(newSpeedMultiplier >  MAX_SPEED) {
            logger.info("Speed-Up not applied. Speed multiplier is already at maximum: " + MAX_SPEED);
            return null;
        }
        ShipMultipliers updatedMultipliers = new ShipMultipliers(
                newSpeedMultiplier,
                currentMultipliers.bulletSpeed(),
                currentMultipliers.shootingInterval()
        );

        ship.updateMultipliers(updatedMultipliers);

        logger.info("Speed-Up activated. New speed multiplier: " + newSpeedMultiplier);
        return null;
    }

    private Entry<Integer, Integer> operateAllyShip() {
        if (!isAllyShipActive()) {
            int allyShipY = HEIGHT - 30;
            int middle = WIDTH / 2 - 39;
            int range = 200;

            this.allyShips.add(new AllyShip(middle - range + 26, allyShipY));
            this.allyShips.add(new AllyShip(middle + range + 26, allyShipY));
        }

        this.allyShip_cooldown = Core.getCooldown(ALLYSHIP_COOLDOWN);
        this.allyShip_cooldown.reset();

        return null;
    }

    /**
     * Checks if Ghost is active.
     *
     * @return True when Ghost is active.
     */
    public boolean isGhostActive() {
        return !this.ghost_cooldown.checkFinished();
    }

    /**
     * Checks if Time-stop is active.
     *
     * @return True when Time-stop is active.
     */
    public boolean isTimeStopActive() {
        return !this.timeStop_cooldown.checkFinished();
    }

    /**
     * Checks if AllyShip is active.
     *
     * @return True when AllyShip is active.
     */
    public boolean isAllyShipActive() {
        return !this.allyShip_cooldown.checkFinished();
    }

    /**
     * Returns the number of bullets that player's ship shoot.
     * @return Number of bullets that player's ship shoot.
     */
    public int getShotNum() {
        return this.shotNum;
    }
}
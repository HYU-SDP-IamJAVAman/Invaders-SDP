package engine;

import static org.junit.jupiter.api.Assertions.*;
import entity.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CoreTest {
    Wallet wallet;

    @BeforeEach
    public void setup() {
        wallet = new Wallet();
    }

    // EASY
    @Test
    public void testMaxLivesEasy() {
        // if livesLv is 1 test
        wallet.setLives_lv(1); //livesLv = 1
        Core.setLivesByDifficulty(0); // EASY
        assertEquals(3, Core.MAX_LIVES, "Easy difficulty is wallet.getLives_lv() + 2");

        // if livesLv is 3 test
        wallet.setLives_lv(3); // livesLv = 3
        Core.setLivesByDifficulty(0); // EASY
        assertEquals(5, Core.MAX_LIVES, "Easy difficulty is wallet.getLives_lv() + 2");

        // if livesLv is 4 test
        wallet.setLives_lv(4); // livesLv = 4
        Core.setLivesByDifficulty(0); // EASY
        assertEquals(6, Core.MAX_LIVES, "Easy difficulty is wallet.getLives_lv() + 2");
    }

    //NORMAL
    @Test
    public void testMaxLivesNormal() {
        // if livesLv is 1 test
        wallet.setLives_lv(1); // livesLv = 1
        Core.setLivesByDifficulty(1); // NORMAL
        assertEquals(2, Core.MAX_LIVES, "Normal difficulty is wallet.getLives_lv() + 1");

        // if livesLv is 2 test
        wallet.setLives_lv(2); // livesLv = 2
        Core.setLivesByDifficulty(1); // NORMAL
        assertEquals(3, Core.MAX_LIVES, "Normal difficulty is wallet.getLives_lv() + 1");

        // if livesLv is 3 test
        wallet.setLives_lv(3); // livesLv = 3
        Core.setLivesByDifficulty(1); // NORMAL
        assertEquals(3, Core.MAX_LIVES, "If livesLv > 2 on NORMAL difficulty, MAX_LIVES must be 3");
    }

    // HARD
    @Test
    public void testMaxLivesHard() {
        // if livesLv is 1 test
        wallet.setLives_lv(1); // livesLv = 1
        Core.setLivesByDifficulty(2); // HARD
        assertEquals(1, Core.MAX_LIVES, "On HARD difficulty, MAX_LIVES should always be 1");

        // if livesLv is 3 test
        wallet.setLives_lv(3); // livesLv = 3
        Core.setLivesByDifficulty(2); // HARD
        assertEquals(1, Core.MAX_LIVES, "On HARD difficulty, MAX_LIVES should always be 1");
    }
}

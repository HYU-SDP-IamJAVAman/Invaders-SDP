package entity;

import engine.Cooldown;
import engine.Core;
import engine.DrawManager.SpriteType;

import java.awt.*;
import java.util.Set;

public class AllyShip extends Entity{

    private static final int ALLYSHIP_BULLET_SPEED = -4;

    private Cooldown shootingCooldown;

    public AllyShip(final int positionX, final int positionY) {
        super(positionX, positionY, 13 * 2, 8 * 2, Color.GREEN);
        this.spriteType = SpriteType.Ship;
        this.shootingCooldown = Core.getVariableCooldown(1000, 1);
        this.shootingCooldown.reset();
    }

    public void shoot(final Set<Bullet> bullets) {
        if (this.shootingCooldown.checkFinished()) {
            this.shootingCooldown.reset();
            bullets.add(BulletPool.getBullet(this.getPositionX()
                    + this.width / 2, this.getPositionY(), ALLYSHIP_BULLET_SPEED));
        }
    }
}

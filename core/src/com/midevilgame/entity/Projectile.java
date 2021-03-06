package com.midevilgame.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.midevilgame.map.Map;

import java.util.Iterator;
import java.util.List;

public abstract class Projectile extends Entity {
    private final Entity shooter;
    private final float angle;
    private long spawnTime;

    public Projectile(Map map, Texture texture, Vector2 position, float width, float height, float angle, Entity shooter) {
        super(map, texture, position, width, height);
        this.angle = angle;
        this.shooter = shooter;
        getSprite().setRotation(this.angle);
    }

    abstract int getDamage();

    abstract void onDamage(LivingEntity target);

    @Override
    public void onSpawn() {
        spawnTime = System.currentTimeMillis();
    }

    @Override
    public void onCollide(List<Collidable> entities) {
        if (this.shooter instanceof Player) {
            entities.remove(this.shooter);
        }
        else if (this.shooter instanceof Enemy) {
            Iterator<Collidable> iterator = entities.iterator();
            while (iterator.hasNext()) {
                Collidable collider = iterator.next();
                if (collider instanceof Enemy) {
                    iterator.remove();
                }
            }
        }

        for (Collidable collidable : entities) {
            if (collidable instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) collidable;
                living.damage(getDamage());
                onDamage(living);
                break;
            }
            else if (!collidable.isPassable()) {
                remove();
                break;
            }
        }
    }

    @Override
    public void update() {
        super.update();

        double radians = this.angle / 180.0 * Math.PI;

        float x = (float) Math.cos(radians) * getSpeed();
        float y = (float) Math.sin(radians) * getSpeed();

        addX(x);
        addY(y);

        if (System.currentTimeMillis() - spawnTime > 20000) {
            remove();
        }
    }

    public abstract float getSpeed();
}

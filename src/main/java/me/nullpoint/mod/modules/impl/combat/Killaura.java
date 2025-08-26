package me.nullpoint.mod.modules.impl.combat;


import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;
import me.nullpoint.mod.modules.Module;
import me.nullpoint.mod.modules.settings.impl.BooleanSetting;
import me.nullpoint.api.utils.math.Timer;

public class Killaura extends Module {
    public static Killaura INSTANCE;
    public static List<LivingEntity> targets = new ArrayList<>();

    // 简易版Killaura的所有功能开关
    public final BooleanSetting Players = add(new BooleanSetting("Players", false));
    public final BooleanSetting Slime = add(new BooleanSetting("Slime", true));
    public final BooleanSetting Hostiles = add(new BooleanSetting("Hostiles", true));
    public final BooleanSetting Animals = add(new BooleanSetting("Animals", false));
    public final BooleanSetting Neutrals = add(new BooleanSetting("Neutrals", true));
    public final BooleanSetting OnlyAngryNeutrals = add(new BooleanSetting("OnlyAngryNeutrals", true , v -> Neutrals.getValue()));

    private final Timer attackTimer = new Timer();
    private static final double FIXED_RANGE_SQ = 5.0 * 5.0; // 固定攻击距离5.0的平方

    public Killaura() {
        super("Killaura", "Attacks entities in radius.", Category.Combat);
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (nullCheck()) {
            return;
        }
        if (attackTimer.passed(750)) {
            targets = getTargets();
            for (LivingEntity target : targets) {
                if (target == null) continue;
                mc.interactionManager.attackEntity(mc.player, target);
            }
            attackTimer.reset();
        }
    }

    private List<LivingEntity> getTargets() {
        List<LivingEntity> newTargets = new ArrayList<>();

        for (Entity entity : mc.world.getEntities()) {
            if (entity.squaredDistanceTo(mc.player) > FIXED_RANGE_SQ) continue;
            if (entity.getUuid().equals(mc.player.getUuid())) continue;
            if (isEnemy(entity)) {
                newTargets.add((LivingEntity) entity);
            }



        }
        return newTargets;
    }

    private boolean isEnemy(Entity entity) {
        if (!(entity instanceof LivingEntity)) return false;

        if (entity instanceof PlayerEntity && Players.getValue()) {
            return true;
        }
        if (entity instanceof MobEntity && Slime.getValue()) {
            if(entity instanceof SlimeEntity){
                return true;
            }
        }
        if (entity instanceof HostileEntity && Hostiles.getValue()) {
//            HostileEntity small = (HostileEntity) entity;
            if (!(entity instanceof EndermanEntity) && !(entity instanceof ZombifiedPiglinEntity) && !(entity instanceof IronGolemEntity ironGolem) && ! (entity instanceof PolarBearEntity polarBear)){
                return true;
            }
        }
        if (entity instanceof AnimalEntity && Animals.getValue()) {
            return true;
        }

        // 简易版中立生物逻辑
        if (Neutrals.getValue()) {
            // 攻击所有中立生物 (不包括敌对和动物)
            if (entity instanceof MobEntity && !(entity instanceof HostileEntity) && !(entity instanceof AnimalEntity) && !(entity instanceof SlimeEntity)) {
                return true;
            }
        }
        // 简易版只攻击被激怒的中立生物逻辑
        if (OnlyAngryNeutrals.getValue()) {
            if (isAngryNeutral(entity)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAngryNeutral(Entity entity) {
        if (entity instanceof WolfEntity wolf) {
            return wolf.isAttacking();
        }
        if (entity instanceof EndermanEntity enderman) {
            return enderman.isAttacking();
        }
        if (entity instanceof ZombifiedPiglinEntity piglin) {
            return piglin.isAttacking();
        }
        if (entity instanceof PolarBearEntity polarBear) {
            return polarBear.isAttacking();
        }
        if (entity instanceof IronGolemEntity ironGolem) {
            return ironGolem.isAttacking();
        }
        return false;
    }
}
package ru.metro.yadernaya_zima;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GasMaskItem extends Item {
    public GasMaskItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        if (head.isEmpty()) {
            if (!level.isClientSide) {
                ItemStack mask = held.copy();
                mask.setCount(1);
                player.setItemSlot(EquipmentSlot.HEAD, mask);
                if (!player.getAbilities().instabuild) held.shrink(1);
            }
            return InteractionResultHolder.sidedSuccess(held, level.isClientSide);
        }
        return InteractionResultHolder.pass(held);
    }
}

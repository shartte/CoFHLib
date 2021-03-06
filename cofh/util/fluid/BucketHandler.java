
package cofh.util.fluid;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import cofh.util.ItemHelper;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class BucketHandler {

    public static BucketHandler instance = new BucketHandler();

    private static boolean initialized;

    private static BiMap<Integer, Integer> buckets = HashBiMap.create();

    private BucketHandler() {

    }

    /**
     * This should only be called when the BucketHandler instance is registered to the Forge Event
     * Bus.
     */
    public static boolean initialize() {

        if (initialized) {
            return false;
        }
        initialized = true;
        return true;
    }

    /**
     * This should be checked by all mods making use of this class. If this returns true, then the
     * instance should NOT be registered again.
     */
    public static boolean isInitialized() {

        return initialized;
    }

    @ForgeSubscribe
    public void onBucketFill(FillBucketEvent event) {

        if (!event.current.getItem().equals(Item.bucketEmpty)) {
            return;
        }
        ItemStack bucket = fillBucket(event.world, event.target);

        if (bucket == null) {
            return;
        }
        event.result = bucket;
        event.setResult(Result.ALLOW);
    }

    public static boolean registerBucket(int blockId, int blockMeta, ItemStack bucket) {

        if (blockId <= 0 || blockMeta < 0 || bucket == null || buckets.containsKey(ItemHelper.getHashCode(blockId, blockMeta))
                || buckets.inverse().containsKey(ItemHelper.getHashCode(bucket))) {
            return false;
        }
        buckets.put(ItemHelper.getHashCode(blockId, blockMeta), ItemHelper.getHashCode(bucket));
        return true;
    }

    public static ItemStack fillBucket(World world, MovingObjectPosition pos) {

        int blockId = world.getBlockId(pos.blockX, pos.blockY, pos.blockZ);
        int blockMeta = world.getBlockMetadata(pos.blockX, pos.blockY, pos.blockZ);

        if (!buckets.containsKey(ItemHelper.getHashCode(blockId, blockMeta))) {
            return null;
        }
        world.setBlock(pos.blockX, pos.blockY, pos.blockZ, 0);
        int hashCode = buckets.get(ItemHelper.getHashCode(blockId, blockMeta));
        return new ItemStack(ItemHelper.getIDFromHashCode(hashCode), 1, ItemHelper.getMetaFromHashCode(hashCode));
    }

    public static boolean emptyBucket(World world, int x, int y, int z, ItemStack bucket) {

        if (!buckets.inverse().containsKey(ItemHelper.getHashCode(bucket))) {
            return false;
        }
        int hashCode = buckets.inverse().get(ItemHelper.getHashCode(bucket));
        world.setBlock(x, y, z, ItemHelper.getIDFromHashCode(hashCode), ItemHelper.getMetaFromHashCode(hashCode), 3);
        return true;
    }

}

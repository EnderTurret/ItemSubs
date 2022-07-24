package net.enderturret.itemsubs.block;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CollisionSupport {

	public static VoxelShape of(VoxelShape... shapes) {
		VoxelShape result = Shapes.empty();

		for (VoxelShape shape : shapes)
			result = Shapes.join(result, shape, BooleanOp.OR);

		return result;
	}

	public static VoxelShape rotate(VoxelShape shape, Direction from, Direction to) {
		final VoxelShape[] buffer = { shape, Shapes.empty() };

		final int times = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;
		for (int i = 0; i < times; i++) {
			buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1], Shapes.create(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
			buffer[0] = buffer[1];
			buffer[1] = Shapes.empty();
		}

		return buffer[0];
	}

	public static VoxelShape[] horizontal(VoxelShape src, Direction orig) {
		final VoxelShape[] shapes = new VoxelShape[4];

		for (int i = 0; i < shapes.length; i++) {
			final Direction dir = Direction.from2DDataValue(i);
			shapes[i] = dir == orig ? src : rotate(src, orig, dir);
		}

		return shapes;
	}
}
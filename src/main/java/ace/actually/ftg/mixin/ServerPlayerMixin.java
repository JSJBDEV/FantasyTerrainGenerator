package ace.actually.ftg.mixin;

import ace.actually.ftg.FTG;
import ace.actually.ftg.make.MapCell;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerMixin extends PlayerEntity {
	public ServerPlayerMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
		super(world, pos, yaw, gameProfile);
	}

	@Shadow public abstract ServerWorld getServerWorld();

	@Shadow public ServerPlayNetworkHandler networkHandler;

	@Inject(at = @At("HEAD"), method = "tick")
	private void init(CallbackInfo info) {
		if(getServerWorld().getRegistryKey().getValue().getNamespace().equals("ftg") && getServerWorld().getTimeOfDay()%60==0)
		{
			MapCell oldCell = FTG.ACTIVE_MAP.getLastMapCell(getUuidAsString());
			MapCell cell = FTG.ACTIVE_MAP.getMapCell(getBlockPos(),getUuidAsString());


			if(cell!=null)
			{
				if(oldCell!=null)
				{
					if(oldCell.getProvince()==cell.getProvince())
					{
						return;
					}
				}
				String province = FTG.ACTIVE_MAP.getStringEsoterically("provinces",cell.getProvince(),"fullName");
				String state = FTG.ACTIVE_MAP.getStringEsoterically("states",cell.getState(),"fullName");
				TitleS2CPacket packet = new TitleS2CPacket(Text.of(province));
				SubtitleS2CPacket packet2 = new SubtitleS2CPacket(Text.of(state));
				networkHandler.sendPacket(packet2);
				networkHandler.sendPacket(packet);
				System.out.println(province+" in "+state);
			}
		}
	}
}
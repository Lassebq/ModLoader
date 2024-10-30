package io.github.lassebq.modloader.mixin;

import java.io.DataInputStream;
import java.io.IOException;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.network.packet.DisconnectPacket;
import net.minecraft.network.packet.Packet;

@Mixin(DisconnectPacket.class)
public abstract class DisconnectPacketMixin extends Packet {
    @Shadow public String reason;

    // TODO mm does not support ModifyConstant
    // @ModifyConstant(
    //     method = "read(Ljava/io/DataInputStream;)V",
    //     constant = @Constant(
    //         intValue = 100
    //     )
    // )
    // public int replaceMaxLength(DataInputStream input, int cst) {
    //     return 1000;
    // }

    @Overwrite
    public void read(DataInputStream input) throws IOException {
      this.reason = readString(input, 1000);
    }
}

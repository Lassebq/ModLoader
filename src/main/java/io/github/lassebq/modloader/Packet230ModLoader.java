package io.github.lassebq.modloader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.PacketHandler;
import net.minecraft.network.packet.Packet;

public class Packet230ModLoader extends Packet {
    private static final int MAX_DATA_LENGTH = 65535;
    public int modId;
    public int packetType;
    public int[] dataInt = new int[0];
    public float[] dataFloat = new float[0];
    public String[] dataString = new String[0];

    public Packet230ModLoader() {
    }

    public void read(DataInputStream datainputstream) throws IOException {
        this.modId = datainputstream.readInt();
        this.packetType = datainputstream.readInt();
        int i = datainputstream.readInt();
        if (i > MAX_DATA_LENGTH) {
            throw new IOException(String.format("Integer data size of %d is higher than the max (%d).", i, MAX_DATA_LENGTH));
        } else {
            this.dataInt = new int[i];

            int k;
            for(k = 0; k < i; ++k) {
                this.dataInt[k] = datainputstream.readInt();
            }

            k = datainputstream.readInt();
            if (k > MAX_DATA_LENGTH) {
                throw new IOException(String.format("Float data size of %d is higher than the max (%d).", k, MAX_DATA_LENGTH));
            } else {
                this.dataFloat = new float[k];

                int i1;
                for(i1 = 0; i1 < k; ++i1) {
                    this.dataFloat[i1] = datainputstream.readFloat();
                }

                i1 = datainputstream.readInt();
                if (i1 > MAX_DATA_LENGTH) {
                    throw new IOException(String.format("String data size of %d is higher than the max (%d).", i1, MAX_DATA_LENGTH));
                } else {
                    this.dataString = new String[i1];

                    for(int j1 = 0; j1 < i1; ++j1) {
                        int k1 = datainputstream.readInt();
                        if (k1 > MAX_DATA_LENGTH) {
                            throw new IOException(String.format("String length of %d is higher than the max (%d).", k1, MAX_DATA_LENGTH));
                        }

                        byte[] abyte0 = new byte[k1];
                        datainputstream.read(abyte0, 0, k1);
                        this.dataString[j1] = new String(abyte0);
                    }

                }
            }
        }
    }

    public void write(DataOutputStream dataoutputstream) throws IOException {
        if (this.dataInt != null && this.dataInt.length > MAX_DATA_LENGTH) {
            throw new IOException(String.format("Integer data size of %d is higher than the max (%d).", this.dataInt.length, MAX_DATA_LENGTH));
        } else if (this.dataFloat != null && this.dataFloat.length > MAX_DATA_LENGTH) {
            throw new IOException(String.format("Float data size of %d is higher than the max (%d).", this.dataFloat.length, MAX_DATA_LENGTH));
        } else if (this.dataString != null && this.dataString.length > MAX_DATA_LENGTH) {
            throw new IOException(String.format("String data size of %d is higher than the max (%d).", this.dataString.length, MAX_DATA_LENGTH));
        } else {
            dataoutputstream.writeInt(this.modId);
            dataoutputstream.writeInt(this.packetType);
            int k;
            if (this.dataInt == null) {
                dataoutputstream.writeInt(0);
            } else {
                dataoutputstream.writeInt(this.dataInt.length);

                for(k = 0; k < this.dataInt.length; ++k) {
                    dataoutputstream.writeInt(this.dataInt[k]);
                }
            }

            if (this.dataFloat == null) {
                dataoutputstream.writeInt(0);
            } else {
                dataoutputstream.writeInt(this.dataFloat.length);

                for(k = 0; k < this.dataFloat.length; ++k) {
                    dataoutputstream.writeFloat(this.dataFloat[k]);
                }
            }

            if (this.dataString == null) {
                dataoutputstream.writeInt(0);
            } else {
                dataoutputstream.writeInt(this.dataString.length);

                for(k = 0; k < this.dataString.length; ++k) {
                    if (this.dataString[k].length() > MAX_DATA_LENGTH) {
                        throw new IOException(String.format("String length of %d is higher than the max (%d).", this.dataString[k].length(), MAX_DATA_LENGTH));
                    }

                    dataoutputstream.writeInt(this.dataString[k].length());
                    dataoutputstream.writeBytes(this.dataString[k]);
                }
            }

        }
    }

    public void handle(PacketHandler nethandler) {
        ModLoaderMp.HandleAllPackets(this);
    }

    public int getSize() {
        int i = 1;
        ++i;
        ++i;
        i += this.dataInt != null ? this.dataInt.length * 32 : 0;
        ++i;
        i += this.dataFloat != null ? this.dataFloat.length * 32 : 0;
        ++i;
        if (this.dataString != null) {
            for(int j = 0; j < this.dataString.length; ++j) {
                ++i;
                i += this.dataString[j].length();
            }
        }

        return i;
    }
}

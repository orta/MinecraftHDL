package minecrafthdl.synthesis;

import jdk.nashorn.internal.ir.Block;
import minecrafthdl.Demo;
import minecrafthdl.MHDLException;
import minecrafthdl.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

/**
 * Created by Francis on 11/12/2016.
 */
public class LogicGates {


    public static void main(String[] args) {
        IntermediateCircuit ic = new IntermediateCircuit();
        ic.loadGraph(Demo.create4bitmuxgraph());
        ic.printLayers();
    }

    public static Gate Input(String id){

        Gate gate = new Gate(1, 2, 1, 1, 1, 0, 0, new int[]{0});

        gate.is_io = true;
        String[] id_txt = extractSignText(id);

        TileEntitySign tes = new TileEntitySign();

        for (int i = 0 ; i < id_txt.length; i++){
            tes.signText[i] = new TextComponentString(id_txt[i]);
        }

        gate.te_map.put(new Vec3i(0, 1, 0), tes);

        gate.setBlock(0, 0, 0, Blocks.WOOL.getDefaultState());
        gate.setBlock(0, 1, 0, Blocks.STANDING_SIGN.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.STANDING_SIGN, "rotation"), 8));
        return gate;
    }

    public static Gate Output(String id){
        Gate gate = new Gate(1, 2, 1, 1, 1, 0, 0, new int[]{0});

        gate.is_io = true;
        String[] id_txt = extractSignText(id);

        TileEntitySign tes = new TileEntitySign();

        for (int i = 0 ; i < id_txt.length; i++){
            tes.signText[i] = new TextComponentString(id_txt[i]);
        }

        gate.te_map.put(new Vec3i(0, 1, 0), tes);

        gate.setBlock(0, 0, 0, Blocks.REDSTONE_LAMP.getDefaultState());
        gate.setBlock(0, 1, 0, Blocks.STANDING_SIGN.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.STANDING_SIGN, "rotation"), 0));
        return gate;
    }

    private static String[] extractSignText(String id){
        String[] txt = {"", "", ""};

        int i = 0;

        do {
            if (id.length() <= 15){
                txt[i] += id;
                break;
            } else {
                String line = id.substring(0, 15);
                id = id.substring(15, id.length());
                txt[i] += line;
                i++;
            }

        } while (i < 3);

        return  txt;
    }

    public static Gate NOT(){
        Gate gate = new Gate(1, 1, 3, 1, 1, 0, 0, new int[]{0});
        gate.setBlock(0, 0, 0, Blocks.WOOL.getDefaultState());
        gate.setBlock(0, 0, 1, Blocks.REDSTONE_TORCH.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.REDSTONE_TORCH, "facing"), EnumFacing.SOUTH));
        gate.setBlock(0, 0, 2, Blocks.REDSTONE_WIRE.getDefaultState());
        return gate;
    }

    public static Gate RELAY(){
        Gate gate = new Gate(1, 1, 3, 1, 1, 0, 0, new int[]{0});
        gate.setBlock(0, 0, 0, Blocks.REDSTONE_WIRE.getDefaultState());
        gate.setBlock(0, 0, 1, Blocks.UNPOWERED_REPEATER.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.UNPOWERED_REPEATER, "facing"), EnumFacing.NORTH));
        gate.setBlock(0, 0, 2, Blocks.REDSTONE_WIRE.getDefaultState());
        return gate;
    }

    public static Gate AND(int inputs) {
        if (inputs == 0) throw new MHDLException("Gate cannot have 0 inputs");
        int width;
        if (inputs == 1) width = 1;

        else width = (inputs * 2) - 1;

        Gate gate = new Gate(width, 2, 4, inputs, 1, 1, 0, new int[]{0});

        gate.setBlock(0, 0, 2, Blocks.REDSTONE_TORCH.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.REDSTONE_TORCH, "facing"), EnumFacing.SOUTH));
        gate.setBlock(0, 0, 3, Blocks.REDSTONE_WIRE.getDefaultState());

        for (int i = 0; i < width; i+=2) {
            gate.setBlock(i, 0, 0, Blocks.WOOL.getDefaultState());
            gate.setBlock(i, 0, 1, Blocks.WOOL.getDefaultState());
            gate.setBlock(i, 1, 0, Blocks.REDSTONE_TORCH.getDefaultState());
            gate.setBlock(i, 1, 1, Blocks.REDSTONE_WIRE.getDefaultState());

            if (i != width - 1) {
                gate.setBlock(i + 1, 0, 1, Blocks.WOOL.getDefaultState());
                if (i == 14) {
                    gate.setBlock(i + 1, 1, 1, Blocks.UNPOWERED_REPEATER.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.UNPOWERED_REPEATER, "facing"), EnumFacing.EAST));
                } else {
                    gate.setBlock(i + 1, 1, 1, Blocks.REDSTONE_WIRE.getDefaultState());
                }
            }
        }

        return gate;
    }



    public static Gate OR(int inputs) {
        if (inputs == 0) throw new MHDLException("Gate cannot have 0 inputs");
        int width;
        if (inputs == 1) width = 1;
        else width = (inputs * 2) - 1;

        Gate gate = new Gate(width, 2, 4, inputs, 1, 1, 0, new int[]{0});

        gate.setBlock(0, 0, 3, Blocks.REDSTONE_WIRE.getDefaultState());

        for (int i = 0; i < width; i+=2) {
            gate.setBlock(i, 0, 0, Blocks.WOOL.getDefaultState());
            gate.setBlock(i, 0, 1, Blocks.UNPOWERED_REPEATER.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.UNPOWERED_REPEATER, "facing"), EnumFacing.NORTH));
            gate.setBlock(i, 0, 2, Blocks.REDSTONE_WIRE.getDefaultState());
            if (i != width - 1) {
                if (i == 14) {
                    gate.setBlock(i + 1, 0, 2, Blocks.UNPOWERED_REPEATER.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.UNPOWERED_REPEATER, "facing"), EnumFacing.EAST));
                } else {
                    gate.setBlock(i + 1, 0, 2, Blocks.REDSTONE_WIRE.getDefaultState());
                }
            }
        }
        return gate;
    }

    public static Gate XOR(){
        Gate gate = new Gate(3, 2, 7, 2, 1, 1, 0, new int[]{0});

        gate.setBlock(0, 0, 0, Blocks.WOOL.getDefaultState());
        gate.setBlock(0, 0, 1, Blocks.REDSTONE_TORCH.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.REDSTONE_TORCH, "facing"), EnumFacing.SOUTH));
        gate.setBlock(0, 0, 2, Blocks.REDSTONE_WIRE.getDefaultState());
        gate.setBlock(0, 0, 3, Blocks.WOOL.getDefaultState());
        gate.setBlock(0, 0, 4, Blocks.REDSTONE_TORCH.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.REDSTONE_TORCH, "facing"), EnumFacing.SOUTH));
        gate.setBlock(0, 0, 5, Blocks.REDSTONE_WIRE.getDefaultState());

        gate.setBlock(0, 1, 0, Blocks.REDSTONE_TORCH.getDefaultState());
        gate.setBlock(0, 1, 3, Blocks.REDSTONE_WIRE.getDefaultState());

        gate.setBlock(1, 0, 0, Blocks.WOOL.getDefaultState());
        gate.setBlock(1, 1, 0, Blocks.REDSTONE_WIRE.getDefaultState());
        gate.setBlock(1, 0, 1, Blocks.WOOL.getDefaultState());
        gate.setBlock(1, 1, 1, Blocks.REDSTONE_WIRE.getDefaultState());
        gate.setBlock(1, 0, 2, Blocks.REDSTONE_TORCH.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.REDSTONE_TORCH, "facing"), EnumFacing.SOUTH));
        gate.setBlock(1, 0, 4, Blocks.REDSTONE_WIRE.getDefaultState());
        gate.setBlock(1, 0, 5, Blocks.REDSTONE_WIRE.getDefaultState());


        gate.setBlock(2, 0, 0, Blocks.WOOL.getDefaultState());
        gate.setBlock(2, 0, 1, Blocks.REDSTONE_TORCH.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.REDSTONE_TORCH, "facing"), EnumFacing.SOUTH));
        gate.setBlock(2, 0, 2, Blocks.REDSTONE_WIRE.getDefaultState());
        gate.setBlock(2, 0, 3, Blocks.WOOL.getDefaultState());
        gate.setBlock(2, 0, 4, Blocks.REDSTONE_TORCH.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.REDSTONE_TORCH, "facing"), EnumFacing.SOUTH));

        gate.setBlock(2, 1, 0, Blocks.REDSTONE_TORCH.getDefaultState());
        gate.setBlock(2, 1, 3, Blocks.REDSTONE_WIRE.getDefaultState());

        gate.setBlock(0, 0, 6, Blocks.UNPOWERED_REPEATER.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.UNPOWERED_REPEATER, "facing"), EnumFacing.NORTH));

        return gate;
    }

//    public static Gate XOR(){
//        Gate gate = new Gate(3, 2, 6, 2, 1, 1, 0, new int[]{0});
//        gate.setBlock(0, 0, 0, Blocks.WOOL.getDefaultState());
//        gate.setBlock(0, 0, 1, Blocks.UNPOWERED_REPEATER.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.UNPOWERED_REPEATER, "facing"), EnumFacing.NORTH));
//        gate.setBlock(0, 0, 2, Blocks.REDSTONE_WIRE.getDefaultState());
//        gate.setBlock(0, 0, 3, Blocks.WOOL.getDefaultState());
//        gate.setBlock(0, 0, 4, Blocks.REDSTONE_WIRE.getDefaultState());
//
//        gate.setBlock(0, 1, 0, Blocks.STICKY_PISTON.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.STICKY_PISTON, "facing"), EnumFacing.SOUTH));
//        gate.setBlock(0, 1, 1, Blocks.WOOL.getDefaultState());
//        gate.setBlock(0, 1, 3, Blocks.REDSTONE_WIRE.getDefaultState());
//
//
//        gate.setBlock(2, 0, 0, Blocks.WOOL.getDefaultState());
//        gate.setBlock(2, 0, 1, Blocks.UNPOWERED_REPEATER.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.UNPOWERED_REPEATER, "facing"), EnumFacing.NORTH));
//        gate.setBlock(2, 0, 2, Blocks.REDSTONE_WIRE.getDefaultState());
//        gate.setBlock(2, 0, 3, Blocks.WOOL.getDefaultState());
//        gate.setBlock(2, 0, 4, Blocks.REDSTONE_WIRE.getDefaultState());
//
//        gate.setBlock(2, 1, 0, Blocks.STICKY_PISTON.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.STICKY_PISTON, "facing"), EnumFacing.SOUTH));
//        gate.setBlock(2, 1, 1, Blocks.WOOL.getDefaultState());
//        gate.setBlock(2, 1, 3, Blocks.REDSTONE_WIRE.getDefaultState());
//
//        gate.setBlock(1, 0, 4, Blocks.REDSTONE_WIRE.getDefaultState());
//        gate.setBlock(1, 0, 2, Blocks.REDSTONE_WIRE.getDefaultState());
//        gate.setBlock(0, 0, 5, Blocks.UNPOWERED_REPEATER.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.UNPOWERED_REPEATER, "facing"), EnumFacing.NORTH));
//
//        return gate;
//    }

    public static Gate MUX() {
        Gate gate = new Gate(5, 2, 6, 3, 1, 1, 0, new int[]{0});

        gate.setBlock(0, 0, 0, Blocks.WOOL.getDefaultState());
        gate.setBlock(0, 0, 1, Blocks.WOOL.getDefaultState());
        gate.setBlock(0, 0, 2, Blocks.WOOL.getDefaultState());
        gate.setBlock(0, 0, 3, Blocks.REDSTONE_TORCH.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.REDSTONE_TORCH, "facing"), EnumFacing.SOUTH));
        gate.setBlock(0, 0, 4, Blocks.REDSTONE_WIRE.getDefaultState());
        gate.setBlock(0, 0, 5, Blocks.UNPOWERED_REPEATER.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.UNPOWERED_REPEATER, "facing"), EnumFacing.NORTH));

        gate.setBlock(1, 0, 2, Blocks.UNPOWERED_REPEATER.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.UNPOWERED_REPEATER, "facing"), EnumFacing.EAST));
        gate.setBlock(1, 0, 4, Blocks.REDSTONE_WIRE.getDefaultState());

        gate.setBlock(2, 0, 0, Blocks.WOOL.getDefaultState());
        gate.setBlock(2, 0, 1, Blocks.UNPOWERED_REPEATER.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.UNPOWERED_REPEATER, "facing"), EnumFacing.NORTH));
        gate.setBlock(2, 0, 2, Blocks.WOOL.getDefaultState());
        gate.setBlock(2, 0, 4, Blocks.REDSTONE_WIRE.getDefaultState());

        gate.setBlock(3, 0, 2, Blocks.REDSTONE_TORCH.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.REDSTONE_TORCH, "facing"), EnumFacing.EAST));
        gate.setBlock(3, 0, 4, Blocks.REDSTONE_TORCH.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.REDSTONE_TORCH, "facing"), EnumFacing.WEST));

        gate.setBlock(4, 0, 0, Blocks.WOOL.getDefaultState());
        gate.setBlock(4, 0, 1, Blocks.REDSTONE_TORCH.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.REDSTONE_TORCH, "facing"), EnumFacing.SOUTH));
        gate.setBlock(4, 0, 2, Blocks.REDSTONE_WIRE.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.REDSTONE_WIRE, "power"), 10));
        gate.setBlock(4, 0, 3, Blocks.REDSTONE_WIRE.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.REDSTONE_WIRE, "power"), 10));
        gate.setBlock(4, 0, 4, Blocks.WOOL.getDefaultState());

        gate.setBlock(0, 1, 0, Blocks.REDSTONE_TORCH.getDefaultState());
        gate.setBlock(0, 1, 1, Blocks.REDSTONE_WIRE.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.REDSTONE_WIRE, "power"), 10));
        gate.setBlock(0, 1, 2, Blocks.REDSTONE_WIRE.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.REDSTONE_WIRE, "power"), 10));

        return gate;
    }

    public static Gate LOW(){
        Gate gate = new Gate(1, 1, 1, 1, 1, 0, 0, new int[]{0});
        gate.setBlock(0, 0, 0, Blocks.WOOL.getDefaultState());
        return gate;
    }

    public static Gate HIGH(){
        Gate gate = new Gate(1, 1, 1, 1, 1, 0, 0, new int[]{0});
        gate.setBlock(0, 0, 0, Blocks.REDSTONE_TORCH.getDefaultState());
        return gate;
    }

    public static Gate D_LATCH() {
        Gate gate = new Gate(3, 1, 4, 2, 1, 1, 0, new int[]{0});

        gate.setBlock(0, 0, 0, Blocks.WOOL.getDefaultState());
        gate.setBlock(0, 0, 1, Blocks.UNPOWERED_REPEATER.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.UNPOWERED_REPEATER, "facing"), EnumFacing.NORTH));
        gate.setBlock(0, 0, 2, Blocks.UNPOWERED_REPEATER.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.UNPOWERED_REPEATER, "facing"), EnumFacing.NORTH));
        gate.setBlock(0, 0, 3, Blocks.WOOL.getDefaultState());

        gate.setBlock(1, 0, 2, Blocks.UNPOWERED_REPEATER.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.UNPOWERED_REPEATER, "facing"), EnumFacing.EAST));

        gate.setBlock(2, 0, 0, Blocks.WOOL.getDefaultState());
        gate.setBlock(2, 0, 1, Blocks.UNPOWERED_REPEATER.getDefaultState().withProperty(Utils.getPropertyByName(Blocks.UNPOWERED_REPEATER, "facing"), EnumFacing.NORTH));
        gate.setBlock(2, 0, 2, Blocks.REDSTONE_WIRE.getDefaultState());

        return gate;
    }
}

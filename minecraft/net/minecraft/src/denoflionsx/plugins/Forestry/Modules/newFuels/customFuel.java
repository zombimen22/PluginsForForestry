package net.minecraft.src.denoflionsx.plugins.Forestry.Modules.newFuels;

import forestry.api.fuels.EngineBronzeFuel;
import forestry.api.fuels.FuelManager;
import forestry.api.fuels.GeneratorFuel;
import forestry.api.liquids.LiquidStack;
import net.minecraft.src.ItemStack;
import net.minecraft.src.denoflionsx.API.API;
import net.minecraft.src.denoflionsx.core.ItemIDManager;
import net.minecraft.src.denoflionsx.core.core;
import net.minecraft.src.denoflionsx.denLib.Colors;
import net.minecraft.src.denoflionsx.denLib.denLib;
import net.minecraft.src.denoflionsx.items.PfFContainer;
import net.minecraft.src.denoflionsx.plugins.Core.EnumLiquidTextures;
import net.minecraft.src.denoflionsx.plugins.Forestry.EnumContainers;
import net.minecraft.src.denoflionsx.plugins.Forestry.LiquidContainerSystem;
import net.minecraft.src.denoflionsx.plugins.Forestry.LiquidFXHook;
import net.minecraft.src.denoflionsx.plugins.pluginBase;
import net.minecraft.src.mod_PluginsforForestry;

// This class is to automate the creation of basic fuel liquids and the
// associated containers.
// Special liquids like milk and radioactive waste won't work with this.
public class customFuel {

    private String name = "";
    private String nameLowerCaseNoSpaces = "";
    private String nameNoSpaces = "";
    private int MJt = 0;
    private int burnTime = 0;
    private PfFContainer fuel = null;
    private customFuelSolid fuelSolid = null;
    private int textures[] = new int[]{};
    private ItemIDManager IDs;
    private boolean enabled = true;
    private int ID1 = 0;
    private int ID2 = 0;
    private int MJt2 = 0;
    private int burnTime2 = 0;
    private int isSafeFuel = 1;
    private int color = Colors.Values.WHITE.getColor();
    public static int numOfContainers = 8;
    private int redCapMeta = 3;
    private pluginBase parent;
    // Peat's MJ value divided by EU value.
    public static final double conversionRate = 1.25;

    public customFuel(String name, int MJt, int burnTime, int textures[], ItemIDManager ItemIDs, int color, pluginBase parent) {

        if (name.equals("")){
            name = "Unnamed Liquid";
        }
        this.name = name;
        this.MJt = MJt;
        this.burnTime = burnTime;
        this.textures = textures;
        this.IDs = ItemIDs;
        this.nameNoSpaces = denLib.toNoSpaces(name);
        this.nameLowerCaseNoSpaces = denLib.toLowerCaseNoSpaces(name);
        this.color = color;
        this.parent = parent;
        this.createItems();

    }

    public static int[] populateSprites(int liquidtex) {
        int textures[] = new int[numOfContainers];
        textures[0] = liquidtex;
        textures[1] = EnumContainers.Containers.CAPSULE.getTexture();
        textures[2] = EnumContainers.Containers.CAN.getTexture();
        textures[3] = EnumContainers.Containers.CAPSULE_RED.getTexture();
        textures[4] = EnumContainers.Containers.BUCKET.getTexture();
        textures[5] = EnumContainers.Containers.BOTTLE.getTexture();
        textures[6] = EnumContainers.Containers.CELL.getTexture();
        textures[7] = EnumContainers.Containers.BARREL.getTexture();
        return textures;
    }

    private void createItems() {

        if (!this.enabled) {
            return;
        }
        String internalNames[] = generateInternals();
        String externalNames[] = generateExternals();
        this.createConfigOptions();
        this.getConfigOptions();
        // This is using my new system for doing colors through code.
        // The spritesheet was getting to be a clusterfuck.
        fuel = new PfFContainer(this.ID1, this.nameLowerCaseNoSpaces);
        for (int i = 0; i != (numOfContainers); i++) {
            // This is basically hardcoding meta values.
            String e = externalNames[i];
            String e2 = internalNames[i];
            if (i == this.redCapMeta) {
                e = e + "_Red";
                e2 = e2 + "_red";
            }
            // Meta map isn't really useful here, but the LiquidContainerSystem 
            // still uses it, so we have to properly populate it.
            fuel.metaMap.put(e, i);
            fuel.add(e2, fuel.metaMap.get(e), textures[i], externalNames[i]);
        }
        ItemStack fuelStack;
        fuel.setAllRenderColor(this.color);
        fuelStack = API.getItem(internalNames[0]);
        LiquidContainerSystem.create(fuel);
        int colors[] = new int[3];
        for (Colors.Values A : Colors.Values.values()){
            if (A.getColor() == this.color){
                colors[0] = A.getR();
                colors[1] = A.getG();
                colors[2] = A.getB();
            }
        }
        EnumLiquidTextures.Liquids Liquid = EnumLiquidTextures.Liquids.CITRUSJUICE.find(textures[0]);
        core.fx.add(LiquidFXHook.addTexture(colors[0],Liquid.getR(),colors[1],Liquid.getB(),colors[2],Liquid.getG(),this.textures[0],mod_PluginsforForestry.texture));
        FuelManager.bronzeEngineFuel.put(fuelStack.itemID, new EngineBronzeFuel(fuelStack, this.MJt, this.burnTime, this.isSafeFuel));
        GeneratorFuel.fuels.put(fuel.shiftedIndex, new GeneratorFuel(new LiquidStack(fuel.shiftedIndex, 1), convertToEU(this.MJt,this.burnTime), 1));
        fuelSolid = new customFuelSolid(this.ID2, this.name, this.MJt2, this.burnTime2, EnumContainers.Containers.BAR.getTexture(), false, this.color);
    }

    private String[] generateInternals() {

        String internalNames[] = new String[numOfContainers];
        int i = 0;
        for (String s : generateExternals()) {
            internalNames[i] = denLib.toLowerCaseNoSpaces(s);
            i++;
        }

        return internalNames;

    }

    private void createConfigOptions() {
        this.parent.config.addDefault(this.nameNoSpaces + "_Enabled=" + "true");
        this.parent.config.addDefault(this.nameNoSpaces + "_Liquid_ItemID=" + this.IDs.getItemIDs().get(0));
        this.parent.config.addDefault(this.nameNoSpaces + "_Liquid_MJt=" + this.MJt);
        this.parent.config.addDefault(this.nameNoSpaces + "_Liquid_BurnTime=" + this.burnTime);
        this.parent.config.addDefault(this.nameNoSpaces + "_Solid_ItemID=" + this.IDs.getItemIDs().get(1));
        this.parent.config.addDefault(this.nameNoSpaces + "_Solid_MJt=" + this.MJt);
        this.parent.config.addDefault(this.nameNoSpaces + "_Solid_BurnTime=" + this.burnTime);
    }

    private void getConfigOptions() {
        this.enabled = this.parent.getOptionBool(this.nameNoSpaces + "_Enabled");
        this.ID1 = this.parent.getOptionInt(this.nameNoSpaces + "_Liquid_ItemID");
        this.MJt = this.parent.getOptionInt(this.nameNoSpaces + "_Liquid_MJt");
        this.burnTime = this.parent.getOptionInt(this.nameNoSpaces + "_Liquid_BurnTime");
        this.ID2 = this.parent.getOptionInt(this.nameNoSpaces + "_Solid_ItemID");
        this.MJt2 = this.parent.getOptionInt(this.nameNoSpaces + "_Solid_MJt");
        this.burnTime2 = this.parent.getOptionInt(this.nameNoSpaces + "_Solid_BurnTime");
    }

    private String[] generateExternals() {

        String externalBase = this.name;
        String externalNames[] = new String[numOfContainers];
        externalNames[0] = externalBase;
        externalNames[1] = externalBase + " Capsule";
        externalNames[2] = externalBase + " Can";
        externalNames[3] = externalNames[1];
        externalNames[4] = externalBase + " Bucket";
        externalNames[5] = externalBase + " Bottle";
        externalNames[6] = externalBase + " Cell";
        externalNames[7] = externalBase + " Barrel";

        return externalNames;

    }

    public static int convertToEU(int MJt, int burnTime) {
        int totalEU = convertToEUTotal(MJt,burnTime);
        int EUs = totalEU / 60;
        int EUt = EUs / 20;
        return EUt;
    }

    public static int convertToEUTotal(int MJt, int burnTime) {
        int totalMJt = MJt * burnTime;
        core.print("MJt: " + MJt + "| Burntime: " + burnTime);
        core.print("TotalMJt: " + totalMJt);
        double totalEU = totalMJt * conversionRate;
        core.print("TotalEU: " + (int) totalEU + "| Conversion: " + conversionRate);
        return (int) totalEU;
    }

    public int getMJt() {
        return MJt;
    }

    public int getBurnTime() {
        return burnTime;
    }

    public String getName() {
        return name;
    }

    public ItemIDManager getIDs() {
        return IDs;
    }

    public String getNameNoSpaces() {
        return nameNoSpaces;
    }

    public pluginBase getParent() {
        return parent;
    }

    public int[] getTextures() {
        return textures;
    }

    public int getColor() {
        return color;
    }

    public PfFContainer getFuel() {
        return fuel;
    }

    public int getIsSafeFuel() {
        return isSafeFuel;
    }

    public String getNameLowerCaseNoSpaces() {
        return nameLowerCaseNoSpaces;
    }

    public static int getNumOfContainers() {
        return numOfContainers;
    }

    public int getRedCapMeta() {
        return redCapMeta;
    }

    public int getID1() {
        return ID1;
    }

    public int getID2() {
        return ID2;
    }

    public int getMJt2() {
        return MJt2;
    }

    public int getBurnTime2() {
        return burnTime2;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public customFuelSolid getFuelSolid() {
        return fuelSolid;
    }
}

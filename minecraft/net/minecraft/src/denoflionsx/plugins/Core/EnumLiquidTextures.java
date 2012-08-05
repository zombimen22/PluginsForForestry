package net.minecraft.src.denoflionsx.plugins.Core;

import net.minecraft.src.denoflionsx.items.Placeholder;

public class EnumLiquidTextures {

    public static enum Liquids {

        PEAT(3, 74, 60, 51), SUGARYPEAT(4, 181, 166, 155),
        CITRUSJUICE(5, 250, 197, 189), PUMPKINJUICE(6, 255, 210, 127),
        MELONJUICE(7, 255, 148, 152),
        RADIOACTIVEWASTE(2, 79, 156, 0), MILK(1);
        int index;
        int r;
        int b;
        int g;

        private Liquids(int index) {
            this.index = index;
            this.r = 0;
            this.b = 0;
            this.g = 0;
        }

        private Liquids(int index, int r, int b, int g) {
            this.index = index;
            this.r = r;
            this.b = b;
            this.g = g;
        }

        private Liquids() {
            this.index = Placeholder.Sprite.RADICAL_EDWARD.getIndex();
            this.r = 0;
            this.b = 0;
            this.g = 0;
        }

        public int getIndex() {
            return index;
        }

        public int getB() {
            return b;
        }

        public int getG() {
            return g;
        }

        public int getR() {
            return r;
        }
        
        public EnumLiquidTextures.Liquids find(int texture){
            for (EnumLiquidTextures.Liquids l : EnumLiquidTextures.Liquids.values()){
                if (l.getIndex() == texture){
                    return l;
                }
            }
            return null;
        }
        
        
    }
}

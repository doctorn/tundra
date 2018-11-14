package net.tundra.core.resources.sprites;

public class Font {
    private static final String CHARACTERS =
            " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_";

    private SpriteSheet sheet;

    public Font(SpriteSheet sheet) {
        this.sheet = sheet;
    }

    public Sprite getCharacter(char c) {
        int location =  CHARACTERS.indexOf(c);
        if (location != -1) {
            int x = location % sheet.getColumns();
            int y = location / sheet.getColumns();
            return sheet.getSprite(x, y);
        } else return sheet.getSprite(0, 0);
    }

    public int getCharacterWidth() {
        return sheet.getSpriteWidth();
    }
}

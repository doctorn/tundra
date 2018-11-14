package net.tundra.core.resources.sprites;

public class Animation {
    private SpriteSheet sheet;
    private int startX, startY, endX, endY;

    private boolean looping;
    private boolean playing = false, stopping = false;
    private int stoppingAt;

    private int time, currentTime = 0;
    private int x, y;
    private int currentFrame = 0;

    public Animation(SpriteSheet sheet, int startX, int startY, int endX, int endY, boolean looping, int frameRate) {
        this.sheet = sheet;
        this.startX = startX;
        this.startY = startY;
        this.x = startX;
        this.y = startY;
        this.endX = endX;
        this.endY = endY;
        this.looping = looping;
        time = (int) Math.floor(1000f / (float) frameRate);
    }

    public void start() {
        x = startX;
        y = startY;
        currentTime = 0;
        currentFrame = 0;
        stopping = false;
        playing = true;
    }

    public void setCurrentFrame(int frame) {
        currentFrame = frame;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void pause() {
        playing = false;
    }

    public void unpause() {
        playing = true;
    }

    public void stop() {
        playing = false;
        stopping = false;
    }

    public void stopAt(int frameNumber) {
        stopping = true;
        stoppingAt = frameNumber;
    }

    public void setLooping(boolean looping) {
        this.looping = looping;
    }

    public boolean isStopping() {
        return stopping;
    }

    public boolean isStopped() {
        return playing;
    }

    public void update(int delta) {
        if (playing) {
            currentTime += delta;
            while (currentTime > time) {
                currentTime = 0;
                currentFrame += 1;
                x += 1;
                if ((y == endY && x > endX) || x > sheet.getColumns()) {
                    x = 0;
                    y += 1;
                    if (y > endY) {
                        if (looping) {
                            x = startX;
                            y = startY;
                        } else stop();
                    }
                }
                if (stopping && stoppingAt == currentFrame) stop();
            }
        }
    }

    public Sprite currentFrame() {
        return sheet.getSprite(x, y);
    }
}

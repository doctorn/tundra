package net.tundra.core.audio;

import net.tundra.core.Game;
import net.tundra.core.TundraException;
import net.tundra.core.scene.Trackable;
import org.joml.Vector3f;

public class TrackingListener extends Listener {
    private Trackable tracked;
    private Vector3f  offset;

    public TrackingListener(float gain, Trackable tracked, Vector3f offset) {
        super(tracked.getPosition(), gain);
        this.tracked = tracked;
        this.offset  = offset;
    }

    public Trackable getTracked() {
        return tracked;
    }

    public void setTracked(Trackable tracked) {
        this.tracked = tracked;
    }

    public Vector3f getOffset() {
        return offset;
    }

    public void setOffset(Vector3f offset) {
        this.offset = offset;
    }

    @Override
    public void update(Game game, float delta) throws TundraException {
        setPosition(tracked.getPosition().add(offset));
        super.update(game, delta);
    }
}

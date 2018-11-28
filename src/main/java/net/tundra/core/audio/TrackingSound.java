package net.tundra.core.audio;

import net.tundra.core.Game;
import net.tundra.core.TundraException;
import net.tundra.core.scene.Trackable;
import org.joml.Vector3f;

public class TrackingSound extends Sound {
    private Trackable tracked;
    private Vector3f offset;

    public TrackingSound(String file, Vector3f velocity, float gain,
                         float pitch, boolean looping, Trackable tracked, Vector3f offset) throws TundraException {
        super(file, tracked.getPosition(), velocity, gain, pitch, looping);
        this.tracked = tracked;
        this.offset  = offset;
    }

    public TrackingSound(String file, Trackable tracked, Vector3f offset) throws TundraException {
        this(file, new Vector3f(0,0,0), 1, 1, false, tracked, offset);
    }

    public TrackingSound(String file, boolean looping, Trackable tracked, Vector3f offset) throws TundraException {
        this(file, new Vector3f(0,0,0), 1, 1, looping, tracked, offset);
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

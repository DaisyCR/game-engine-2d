package components;

import util.AssetPool;

import java.util.ArrayList;
import java.util.List;

public class AnimationState {
  public String title;
  public List<Frame> animationFrames = new ArrayList<>();

  private static Sprite defaultSprite = new Sprite();
  private transient float timeElapsed = 0f;
  private transient int currentSprite = 0;
  public boolean doesLoop = false;

  public void update(float deltaTime){
    if(currentSprite < animationFrames.size()){
      timeElapsed -= deltaTime;
      if(timeElapsed <= 0){
        if(currentSprite != animationFrames.size() - 1 || doesLoop){
          currentSprite = (currentSprite + 1) % animationFrames.size();
        }
        timeElapsed = animationFrames.get(currentSprite).frameTime;
      }
    }
  }

  public void addFrame(Sprite sprite, float frameTime){
    animationFrames.add(new Frame(sprite, frameTime));
  }

  public void setLoop(boolean doesLoop){
    this.doesLoop = doesLoop;
  }

  public Sprite getCurrentSprite(){
    if(currentSprite < animationFrames.size()){
      return animationFrames.get(currentSprite).sprite;
    }
    return defaultSprite;
  }

  public void refreshTextures() {
    for(Frame frame : animationFrames){
      frame.sprite.setTexture(AssetPool.getTexture(frame.sprite.getTexture().getFilepath()));
    }
  }
}

package components;

import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class StateMachine extends Component{
  private static class StateTrigger {

    public String state;
    public String trigger;
    public StateTrigger(){}

    public StateTrigger(String state, String trigger){
      this.state = state;
      this.trigger = trigger;
    }
    @Override
    public boolean equals(Object o) {
      if (o.getClass() != StateTrigger.class) return false;
      StateTrigger t2 = (StateTrigger)o;
      return t2.trigger.equals(this.trigger) && t2.state.equals(this.state);
    }

    @Override
    public int hashCode(){
      return Objects.hash(trigger, state);
    }

  }
  public HashMap<StateTrigger, String> stateTriggers = new HashMap<>();

  private List<AnimationState> animationStates = new ArrayList<>();
  private transient AnimationState currentState = null;
  private String defaultStateTitle = "";
  public void addState(AnimationState state){
    this.animationStates.add(state);
  }

  public void addStateTrigger(String start, String end, String onTrigger){
    this.stateTriggers.put(new StateTrigger(start, onTrigger), end);
  }

  public void setDefaultState(String animationTitle) {
    for(AnimationState state : animationStates){
      if (state.title.equals(animationTitle)) {
        defaultStateTitle = animationTitle;
        if(currentState == null){
          currentState = state;
          return;
        }
      }
    }
    System.out.println("Unable to find state '" + animationTitle + "' in set default state");
  }

  public void trigger(String trigger){
    for(StateTrigger stateTrigger : stateTriggers.keySet()){
      if(stateTrigger.state.equals(currentState.title) && stateTrigger.trigger.equals(trigger)){
        if(stateTriggers.get(stateTrigger) != null){
          int newStateIndex = -1;
          int index = 0;
          for(AnimationState animationState : animationStates){
            if(animationState.title.equals(stateTriggers.get(stateTrigger))){
              newStateIndex = index;
              break;
            }
            index++;
          }
          if(newStateIndex > -1){
            currentState = animationStates.get(newStateIndex);
          }
        }
        return;
      }
    }
    System.out.println("Unable to find trigger '" + trigger + "'");
  }

  public void refreshTextures(){
    for(AnimationState state : animationStates){
      state.refreshTextures();
    }
  }

  @Override
  public void start(){
    for(AnimationState state : animationStates){
      if(state.title.equals(defaultStateTitle)){
        currentState = state;
        break;
      }
    }
  }

  @Override
  public void update(float deltaTime){
    if(currentState != null){
      currentState.update(deltaTime);
      SpriteRenderer sprite = gameObject.getComponent(SpriteRenderer.class);
      if(sprite != null){
        sprite.setSprite(currentState.getCurrentSprite());
      }
    }
  }

  @Override
  public void editorUpdate(float deltaTime){
    if(currentState != null){
      currentState.update(deltaTime);
      SpriteRenderer sprite = gameObject.getComponent(SpriteRenderer.class);
      if(sprite != null){
        sprite.setSprite(currentState.getCurrentSprite());
      }
    }
  }

  @Override
  public void imGui(){
    int index = 0;
    for(AnimationState state : animationStates){
      ImString title = new ImString(state.title);
      ImGui.inputText("State: ", title);
      state.title = title.get();

      ImBoolean doesLoop = new ImBoolean(state.doesLoop);
      ImGui.checkbox("Does Loop: ", doesLoop);
      state.setLoop(doesLoop.get());

      for(Frame frame : state.animationFrames){
        float[] tmp = new float[1];
        tmp[0] = frame.frameTime;
        ImGui.dragFloat("Frame (" + index + ") Time: ", tmp, 0.01f );
        frame.frameTime = tmp[0];
        index++;
      }
    }
  }
}

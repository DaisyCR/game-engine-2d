package engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Component;
import components.ComponentDeserializer;
import components.SpriteRenderer;
import imgui.ImGui;
import util.AssetPool;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
    private static int ID_COUNTER = 0;
    private int uId = -1;
    private String name;
    private List<Component> components;
    public transient Transform transform;
    private boolean doSerialization = true;
    private boolean isDead = false;

    public GameObject(String name){
        this.name = name;
        this.components = new ArrayList<>();
        this.uId = ID_COUNTER++;
    }

    public static void init(int maxId){
        ID_COUNTER = maxId;
    }

    public void addComponent(Component c){
        c.generateId();
        this.components.add(c);
        c.gameObject = this;
    }

    public <T extends Component> void removeComponent(Class<T> componentClass){
        for(int i = 0; i < components.size(); i++){
            Component c = components.get(i);
            if( componentClass.isAssignableFrom(c.getClass()) ){
                components.remove(i);
                return;
            }
        }
    }

    public <T extends Component> T getComponent(Class<T> componentClass){
        for(Component c : components){
            if( componentClass.isAssignableFrom(c.getClass()) ){
                try {
                    return componentClass.cast(c);
                } catch (ClassCastException e){
                    e.printStackTrace();
                    assert false : "Error: Casting Component";
                }
            }
        }
        return null;
    }

    public List<Component> getAllComponents(){
        return this.components;
    }

    public void update(float deltaTime){
        for(int i = 0; i < components.size(); i++){
            components.get(i).update(deltaTime);
        }
    }

    public void editorUpdate(float deltaTime) {
        for(int i = 0; i < components.size(); i++){
            components.get(i).editorUpdate(deltaTime);
        }

    }

    public void start(){
        for(int i = 0; i < components.size(); i++){
            components.get(i).start();
        }
    }

    public void imGui(){
        for( Component c : components ){
            if(ImGui.collapsingHeader(c.getClass().getSimpleName())){
                c.imGui();
            }
        }
    }

    public int getuId(){
        return this.uId;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setNoSerialize() {
        this.doSerialization = false;
    }

    public boolean doSerialization() {
        return doSerialization;
    }

    public void destroy() {
        this.isDead = true;
        for(int i = 0; i < components.size(); i++){
            components.get(i).destroy();
        }
    }

  public GameObject copy() {
        //TODO clean this up later
      Gson gson = new GsonBuilder()
              .registerTypeAdapter(Component.class, new ComponentDeserializer())
              .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
              .create();
      String objAsJson = gson.toJson(this);
      GameObject newGo = gson.fromJson(objAsJson, GameObject.class);
      newGo.generateUid();
      for(Component c : components){
          c.generateId();
      }
      SpriteRenderer spr = newGo.getComponent(SpriteRenderer.class);
      if(spr != null && spr.getTexture() != null){
        spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilepath()));
      }
      return newGo;
  }

    private void generateUid() {
        this.uId = ID_COUNTER++;
    }
}

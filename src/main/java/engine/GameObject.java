package engine;

import components.Component;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
    private static int ID_COUNTER = 0;
    private int uId = -1;
    private String name;
    private List<Component> components;
    public Transform transform;
    private int zIndex;
    private boolean doSerialization = true;

    public GameObject(String name, Transform transform, int zIndex){
        this.name = name;
        this.components = new ArrayList<>();
        this.transform = transform;
        this.zIndex = zIndex;
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

    public void start(){
        for(int i = 0; i < components.size(); i++){
            components.get(i).start();
        }
    }

    public void imGui(){
        for( Component c : components ){
            c.imGui();
        }
    }

    public int zIndex(){
        return this.zIndex;
    }

    public int getuId(){
        return this.uId;
    }


    public void setNoSerialize() {
        this.doSerialization = false;
    }

    public boolean doSerialization() {
        return doSerialization;
    }
}

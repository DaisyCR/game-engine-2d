package util;

public enum Constants {
    GRID_WIDTH(0.25f), GRID_HEIGHT(0.25f);
    private final float value;

    Constants(float value){
        this.value = value;
    }

    public float getValue(){
        return value;
    }
}

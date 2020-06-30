package vurkus.concept.geometry;

//Helper code to deal with bounce angles.
public class Angle {

    public static float bounceFromVertical(float rotation) {
        if (rotation <= 180){
            return 180-rotation;
        }
        else if (rotation <= 360) {
            return 180 - rotation + 360;
        }

        return 0;
    }

    public static float bounceFromHorizontal(float rotation) {
        return 360-rotation;
    }

}
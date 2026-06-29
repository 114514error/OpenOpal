package wtf.opal.duck;

import net.minecraft.util.Hand;

public interface ClientPlayerEntityAccess {
    void opal$swingHandClientside(Hand hand);

    void opal$swingHandServerside(Hand hand);
}

package it.unitn.lode2.camera;


import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 * User: tiziano
 * Date: 06/11/14
 * Time: 19:03
 */
public abstract class AbstractCamera implements Camera {

    private Set<Capability> capabilities = EnumSet.noneOf(Capability.class);

    @Override
    public Boolean hasCapability(Capability capability) {
        return capabilities.contains(capability);
    }

    @Override
    public void setCapabilities(Capability... caps) {
        capabilities = EnumSet.copyOf(Arrays.asList(caps));
    }

    @Override
    public void addCapability(Capability capability) {
        capabilities.add(capability);
    }
}

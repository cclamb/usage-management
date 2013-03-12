package edu.unm.ece.informatics.rectifier.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import edu.unm.ece.informatics.rectifier.DocumentRectifier;

public class RectifierActivator implements BundleActivator {
    DocumentRectifier rectifier = new RubyRectifier(null, null);
    ServiceRegistration registration;

    public void start(BundleContext bundleContext) throws Exception {
        //Register the service with the container.
        //Register the Interface, implementation and possible properties
        registration = bundleContext.registerService(RubyRectifier.class.getName(), rectifier, null);
    }

    public void stop(BundleContext bundleContext) throws Exception {
        //When we stop, clean up the references.
        registration.unregister();
    }

}

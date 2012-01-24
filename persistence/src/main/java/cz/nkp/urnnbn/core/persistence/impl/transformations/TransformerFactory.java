/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.transformations;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Řehánek
 * @deprecated 
 */
public class TransformerFactory {

    private static final Logger logger = Logger.getLogger(TransformerFactory.class.getName());
    private static final Map<Class, ResultsetTransformer> transformers = new HashMap<Class, ResultsetTransformer>();

    public static ResultsetTransformer getTransformer(Class transformerClass) {
        ResultsetTransformer transformer = transformers.get(transformerClass);
        if (transformer == null) {
            try {
                transformer = (ResultsetTransformer) Class.forName(transformerClass.getCanonicalName()).newInstance();
                transformers.put(transformerClass, transformer);
                logger.log(Level.INFO, "Loaded transformer {0}", transformerClass.getCanonicalName());
            } catch (ClassNotFoundException ex) {
                logger.log(Level.SEVERE, "Failed to instantiate transformer " + transformerClass.getCanonicalName(), ex);
            } catch (InstantiationException ex) {
                logger.log(Level.SEVERE, "Failed to instantiate transformer" + transformerClass.getCanonicalName(), ex);
            } catch (IllegalAccessException ex) {
                logger.log(Level.SEVERE, "Failed to instantiate transformer" + transformerClass.getCanonicalName(), ex);
            }
        }
        return transformer;
    }
}

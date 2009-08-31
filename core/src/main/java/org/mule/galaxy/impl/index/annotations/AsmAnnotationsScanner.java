/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.mule.galaxy.impl.index.annotations;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.EmptyVisitor;

public class AsmAnnotationsScanner extends EmptyVisitor
{
    private final Log log = LogFactory.getLog(getClass());

    private List<AnnotationInfo> classAnnotations = new ArrayList<AnnotationInfo>();
    private List<AnnotationInfo> fieldAnnotations = new ArrayList<AnnotationInfo>();
    private List<AnnotationInfo> methodAnnotations = new ArrayList<AnnotationInfo>();
    private List<AnnotationInfo> paramAnnotations = new ArrayList<AnnotationInfo>();

    private AnnotationInfo currentAnnotation;

    private static final int PROCESSING_FIELD = 1;
    private static final int PROCESSING_METHOD = 2;
    private static final int PROCESSING_CLASS = 3;
    private static final int PROCESSING_PARAM = 4;

    private final BitSet currentlyProcessing = new BitSet(4);
    private String currentMethod;
    
    @Override
    public AnnotationVisitor visitParameterAnnotation(final int parameter, final String desc, final boolean visible)
    {
        currentAnnotation = new AnnotationInfo();
        currentAnnotation.className = getAnnotationClassName(desc);
        currentlyProcessing.set(PROCESSING_PARAM);
        if (log.isDebugEnabled())
        {
            log.debug("Parameter Annotation: " + getAnnotationClassName(desc));
        }
        return this;
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible)
    {
        // are we processing anything currently?
        if (currentlyProcessing.nextSetBit(0) < 0)
        {
            // no, just continue
            return this;
        }

        currentAnnotation = new AnnotationInfo();
        currentAnnotation.className = getAnnotationClassName(desc);
        if (currentlyProcessing.get(PROCESSING_METHOD))
        {
            currentAnnotation.method = currentMethod;
        }
        
        if (log.isDebugEnabled())
        {
            log.debug("Annotation: " + getAnnotationClassName(desc));
        }

        return this;
    }

    /**
     * This is the class entry.
     */
    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces)
    {
        currentlyProcessing.set(PROCESSING_CLASS);
    }

    /**
     * We get annotation values in this method, but have to track the current context.
     */
    @Override
    public void visit(String name, Object value)
    {
        if (currentAnnotation != null)
        {
            currentAnnotation.params.add(new AnnotationInfo.NameValue(name, value));
        }
        if (log.isDebugEnabled())
        {
            // won't really output nicely with multithreaded parsing
            log.debug("          : " + name + "=" + value);
        }
    }

    @Override
    public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value)
    {
        currentlyProcessing.set(PROCESSING_FIELD);
        return this;
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions)
    {
        currentlyProcessing.set(PROCESSING_METHOD);
        currentMethod = name;
        return this;
    }

    

    @Override
    public void visitEnd()
    {
        if (currentAnnotation != null)
        {
            if (currentlyProcessing.get(PROCESSING_CLASS))
            {
                classAnnotations.add(currentAnnotation);
            }
            if (currentlyProcessing.get(PROCESSING_FIELD))
            {
                fieldAnnotations.add(currentAnnotation);
            }
            else if (currentlyProcessing.get(PROCESSING_PARAM))
            {
                paramAnnotations.add(currentAnnotation);
            }
            else if (currentlyProcessing.get(PROCESSING_METHOD))
            {
                methodAnnotations.add(currentAnnotation);
            }
            currentAnnotation = null;
        }
        currentlyProcessing.clear();

    }

    public String getAnnotationClassName(String rawName)
    {
        return rawName.substring(1, rawName.length() - 1).replace('/', '.');
    }

    public List<AnnotationInfo> getClassAnnotations()
    {
        return classAnnotations;
    }

    public List<AnnotationInfo> getFieldAnnotations()
    {
        return fieldAnnotations;
    }

    public List<AnnotationInfo> getMethodAnnotations()
    {
        return methodAnnotations;
    }

    public List<AnnotationInfo> getParamAnnotations()
    {
        return paramAnnotations;
    }

}
